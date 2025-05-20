package com.example.voicetherapy.network;

import com.example.voicetherapy.model.VoiceInput;
import com.example.voicetherapy.model.AnalysisResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import java.util.Map;

public interface ApiService {
    @POST("/api/voice/analyze")
    Call<AnalysisResult> analyzeVoice(@Body VoiceInput input);

    @Multipart
    @POST("/api/voice/upload")
    Call<Map<String, String>> uploadAudio(@Part MultipartBody.Part file);
}