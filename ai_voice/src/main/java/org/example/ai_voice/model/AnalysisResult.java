package org.example.ai_voice.model;

import java.util.List;

public class AnalysisResult {
    private String diagnosis;
    private List<String> recommendedExercises;

    public AnalysisResult(String diagnosis, List<String> recommendedExercises) {
        this.diagnosis = diagnosis;
        this.recommendedExercises = recommendedExercises;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public List<String> getRecommendedExercises() {
        return recommendedExercises;
    }
}
