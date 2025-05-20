package com.example.voicetherapy.model;

public class VoiceInput {
    private String audioBase64;

    public VoiceInput(String audioBase64) {
        this.audioBase64 = audioBase64;
    }

    public String getAudioBase64() {
        return audioBase64;
    }
}