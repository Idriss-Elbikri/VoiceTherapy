package com.example.voicetherapy.model;

import java.util.List;

public class AnalysisResult {
    private String diagnosis;
    private List<String> recommendedExercises;

    public String getDiagnosis() {
        return diagnosis;
    }

    public List<String> getRecommendedExercises() {
        return recommendedExercises;
    }
}
