package com.example.voicetherapy;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import com.example.voicetherapy.network.ApiService;
import com.example.voicetherapy.network.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Map;
import com.example.voicetherapy.models.Exercise;

public class DetailActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button playButton = findViewById(R.id.listenNowButton);
        TextView titleText = findViewById(R.id.mainTitle);
        TextView descriptionText = findViewById(R.id.mainDescription);

        // Get data from intent
        Intent intent = getIntent();
        if (intent.hasExtra("exerciseId")) {
            int exerciseId = intent.getIntExtra("exerciseId", 1);
            titleText.setText("Exercise " + exerciseId);
            descriptionText.setText("Description for exercise " + exerciseId);
        } else if (intent.hasExtra("audioFilePath")) {
            audioFilePath = intent.getStringExtra("audioFilePath");
            titleText.setText("Your Recording");
            descriptionText.setText("Uploading and analyzing recording...");

            // Envoi du fichier audio au backend
            uploadAudioToBackend(audioFilePath);
        }

        playButton.setOnClickListener(v -> {
            if (audioFilePath != null) {
                if (!isPlaying) {
                    playAudio(audioFilePath);
                    playButton.setText("Stop Playing");
                    isPlaying = true;
                } else {
                    stopAudio();
                    playButton.setText("Listen Now");
                    isPlaying = false;
                }
            } else {
                // For exercises, you would play predefined audio
                Toast.makeText(this, "Playing exercise audio", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Implement back button functionality if needed
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // This will navigate back to the previous activity
        });
    }

    private void playAudio(String filePath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                Button playButton = findViewById(R.id.listenNowButton);
                playButton.setText("Listen Now");
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to play recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAudio();
    }

    private void uploadAudioToBackend(String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "Fichier audio introuvable", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/3gp"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Use your correct IP address here. Assuming 192.168.1.174
        ApiService apiService = RetrofitClient.getClient("http://192.168.1.174:8080/").create(ApiService.class);

        // The response type is now Map<String, String> or a custom object
        // Using Map<String, String> for flexibility to get both fields
        Call<Map<String, String>> call = apiService.uploadAudio(body);

        call.enqueue(new Callback<Map<String, String>>() { // Change the expected response type
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) { // Change the response type
                TextView descriptionText = findViewById(R.id.mainDescription);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> responseBody = response.body();
                    String transcription = responseBody.get("transcription");
                    String analysis = responseBody.get("analysis");

                    // Display the analysis in a Toast
                    if (analysis != null && !analysis.isEmpty()) {
                         Toast.makeText(DetailActivity.this, "Analysis: " + analysis, Toast.LENGTH_LONG).show();
                     } else {
                         Toast.makeText(DetailActivity.this, "Analysis received is empty.", Toast.LENGTH_SHORT).show();
                     }


                    // Display the transcription in the TextView
                    if (transcription != null && !transcription.isEmpty()) {
                        descriptionText.setText("Transcription :\n" + transcription);
                    } else {
                         descriptionText.setText("Transcription : (No transcription received)");
                        Toast.makeText(DetailActivity.this, "No transcription received.", Toast.LENGTH_SHORT).show();
                    }

                    // Create a sample exercise (replace with actual data from backend later)
                    Exercise sampleExercise = new Exercise("Practice Pronunciation", "Repeat the following sentence clearly: 'The quick brown fox jumps over the lazy dog.'");

                    // Redirect to ExercisesActivity after successful analysis
                    Intent intent = new Intent(DetailActivity.this, ExercisesActivity.class);
                    // Pass the sample exercise data to the ExercisesActivity
                    intent.putExtra("exercise", sampleExercise);
                    startActivity(intent);
                    finish(); // Optional: Close DetailActivity so user cannot navigate back to it


                } else {
                    String errorBody = "Error receiving response body.";
                    try {
                        if (response.errorBody() != null) {
                           errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    descriptionText.setText("Backend Error: " + response.code());
                    Toast.makeText(DetailActivity.this, "Backend Error (" + response.code() + "): " + errorBody, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) { // Change the call type
                 TextView descriptionText = findViewById(R.id.mainDescription);
                 descriptionText.setText("Upload Failed: " + t.getMessage());
                Toast.makeText(DetailActivity.this, "Upload Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // Print stack trace for debugging
            }
        });
    }
}