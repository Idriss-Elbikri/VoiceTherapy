package org.example.ai_voice.service;

import org.example.ai_voice.model.AnalysisResult;
import org.example.ai_voice.model.SpeechSession;
import org.example.ai_voice.model.SpeechSession.SessionStatus;
import org.example.ai_voice.model.VoiceInput;
import org.example.ai_voice.repository.SpeechSessionRepository;
import org.springframework.stereotype.Service;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import org.example.ai_voice.model.Exercise;
import org.example.ai_voice.model.SessionExercise;
import org.example.ai_voice.repository.ExerciseRepository;
import org.example.ai_voice.repository.SessionExerciseRepository;

@Service
public class VoiceAnalysisService {
    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Autowired
    private SpeechSessionRepository sessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String WHISPER_URL = "http://localhost:5005/transcribe";
    private static final String LLM_ANALYSIS_URL = "http://localhost:5006/analyze_transcript";

    public AnalysisResult analyze(VoiceInput input) {
        String fakeDiagnosis = "Bégaiement détecté léger";
        return new AnalysisResult(fakeDiagnosis, Arrays.asList(
                "Exercice 1 : Lecture lente",
                "Exercice 2 : Respiration rythmée"
        ));
    }

    public String transcribeAudio(java.io.File audioFile) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource resource = new FileSystemResource(audioFile);
        org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(WHISPER_URL, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("transcription")) {
                return responseBody.get("transcription").toString();
            } else if (responseBody != null && responseBody.containsKey("error")) {
                return "Erreur Whisper : " + responseBody.get("error");
            } else {
                return "Erreur inconnue lors de la transcription";
            }
        } catch (Exception e) {
            System.err.println("Error calling Whisper server: " + e.getMessage());
            e.printStackTrace();
            return "Erreur de communication avec le serveur Whisper : " + e.getMessage();
        }
    }

    public Map<String, String> transcribeAndAnalyzeAudio(java.io.File audioFile) {
        Map<String, String> resultMap = new HashMap<>();

        SpeechSession session = new SpeechSession();
        session.setStatus(SessionStatus.QUEUED);
        session = sessionRepository.save(session);
        UUID sessionId = session.getId();
        resultMap.put("sessionId", sessionId.toString());

        try {
            String transcription = transcribeAudio(audioFile);
            resultMap.put("transcription", transcription);

            if (transcription.startsWith("Erreur") || transcription.trim().isEmpty() || "Erreur inconnue lors de la transcription".equals(transcription)) {
                resultMap.put("analysis", "Analysis skipped due to transcription error or empty transcript.");
                session.setStatus(SessionStatus.ERROR);
            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("transcript", transcription);

                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

                try {
                    ResponseEntity<Map> response = restTemplate.postForEntity(LLM_ANALYSIS_URL, requestEntity, Map.class);
                    Map<String, Object> responseBody = response.getBody();

                    if (responseBody != null && responseBody.containsKey("analysis")) {
                        String analysis = responseBody.get("analysis").toString();
                        resultMap.put("analysis", analysis);
                        Map<String, Object> rawData = new HashMap<>();
                        rawData.put("transcription", transcription);
                        rawData.put("analysis", analysis);
                        // Save exercises if present
                        if (responseBody.containsKey("exercises")) {
                            Object exercisesObj = responseBody.get("exercises");
                            if (exercisesObj instanceof java.util.List) {
                                java.util.List<?> exercisesList = (java.util.List<?>) exercisesObj;
                                for (Object exObj : exercisesList) {
                                    if (exObj instanceof Map) {
                                        Map<?,?> exMap = (Map<?,?>) exObj;
                                        String title = exMap.get("title") != null ? exMap.get("title").toString() : "";
                                        String description = exMap.get("description") != null ? exMap.get("description").toString() : "";
                                        String difficulty = exMap.get("difficulty") != null ? exMap.get("difficulty").toString() : "";
                                        Exercise exercise = new Exercise(title, description, difficulty);
                                        exercise = exerciseRepository.save(exercise);
                                        SessionExercise sessionExercise = new SessionExercise(session, exercise);
                                        sessionExerciseRepository.save(sessionExercise);
                                    }
                                }
                            }
                        }
                        session.setRawResult(objectMapper.writeValueAsString(rawData));
                        session.setStatus(SessionStatus.DONE);
                    } else if (responseBody != null && responseBody.containsKey("error")) {
                        resultMap.put("analysis", "Erreur LLM : " + responseBody.get("error"));
                        session.setRawResult("{\"error\": \"" + String.valueOf(responseBody.get("error")).replace("\"", "\\\"") + "\"}");
                        session.setStatus(SessionStatus.ERROR);
                    } else {
                        resultMap.put("analysis", "Erreur inconnue lors de l'analyse LLM");
                        session.setRawResult("{\"error\": \"Unknown LLM error\"}");
                        session.setStatus(SessionStatus.ERROR);
                    }
                } catch (Exception e) {
                    System.err.println("Error calling LLM analysis server: " + e.getMessage());
                    e.printStackTrace();
                    resultMap.put("analysis", "Erreur de communication avec le serveur d'analyse LLM : " + e.getMessage());
                    session.setRawResult("{\"error\": \"" + String.valueOf(e.getMessage()).replace("\"", "\\\"") + "\"}");
                    session.setStatus(SessionStatus.ERROR);
                }
            }
        } catch (Exception e) {
            System.err.println("Error during transcription/analysis process: " + e.getMessage());
            e.printStackTrace();
            resultMap.put("error", "Erreur lors du traitement de l'audio : " + e.getMessage());
            session.setStatus(SessionStatus.ERROR);
        } finally {
            sessionRepository.save(session);
        }

        return resultMap;
    }

    public Map<String, String> transcribeAndDiagnose(java.io.File audioFile) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5005/transcribe";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource resource = new FileSystemResource(audioFile);
        org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("file", resource);
        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        Map<String, Object> responseBody = response.getBody();
        String transcription = "";
        if (responseBody != null && responseBody.containsKey("transcription")) {
            transcription = responseBody.get("transcription").toString();
        } else if (responseBody != null && responseBody.containsKey("error")) {
            transcription = "Erreur Whisper : " + responseBody.get("error");
        } else {
            transcription = "Erreur inconnue lors de la transcription";
        }
        String diagnosis = diagnose(transcription);
        Map<String, String> result = new HashMap<>();
        result.put("transcription", transcription);
        result.put("diagnosis", diagnosis);
        return result;
    }

    private String diagnose(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Aucune voix détectée.";
        }
        String[] words = text.toLowerCase().split("\s+");
        int repetitions = 0;
        for (int i = 1; i < words.length; i++) {
            if (words[i].equals(words[i-1])) {
                repetitions++;
            }
        }
        if (repetitions > 1) {
            return "Répétitions détectées (bégaiement possible).";
        }
        if (text.length() < 10) {
            return "Voix trop courte pour analyse.";
        }
        return "Aucune anomalie détectée.";
    }
}
