package org.example.ai_voice.controller;

import org.example.ai_voice.model.AnalysisResult;
import org.example.ai_voice.model.VoiceInput;
import org.example.ai_voice.service.VoiceAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voice")
public class VoiceAnalysisController {

    @Autowired
    private VoiceAnalysisService service;

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResult> analyzeVoice(@RequestBody VoiceInput input) {
        AnalysisResult result = service.analyze(input);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Aucun fichier reçu");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        try {
            // Sauvegarder le fichier temporairement
            File tempFile = File.createTempFile("audio_", ".3gp");
            Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Appeler le service pour transcrire ET analyser
            Map<String, String> analysisResult = service.transcribeAndAnalyzeAudio(tempFile);

            // Supprimer le fichier temporaire
            tempFile.delete();

            // Retourner la transcription et l'analyse en JSON
            return ResponseEntity.ok(analysisResult);

        } catch (IOException e) {
            System.err.println("Error processing audio file upload: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors du traitement du fichier audio");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
