package com.example.voicetherapy;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String currentAudioFilePath;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordNowButton = findViewById(R.id.recordNowButton);
        ImageButton homeIcon = findViewById(R.id.homeIcon);
        ImageButton musicIcon = findViewById(R.id.musicIcon);
        ImageButton profileIcon = findViewById(R.id.profileIcon);

        // Demander les permissions si nécessaire
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            permissionToRecordAccepted = true;
        }

        // Record button functionality
        recordNowButton.setOnClickListener(v -> {
            if (!permissionToRecordAccepted) {
                Toast.makeText(this, "Permission d'enregistrement refusée", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isRecording) {
                startRecording();
                recordNowButton.setText("Stop Recording");
                recordNowButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED));
                isRecording = true;
            } else {
                stopRecording();
                recordNowButton.setText("Record Now");
                recordNowButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#7E9E94")));
                isRecording = false;

                // Navigate to detail activity with the recorded file
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("audioFilePath", currentAudioFilePath);
                startActivity(intent);
            }
        });

        // Bottom navigation
        homeIcon.setOnClickListener(v -> {
            // Already on home
        });

        musicIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExercisesActivity.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Create unique filename with timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        // Utiliser le dossier privé de l'app pour la compatibilité Android 10+
        currentAudioFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                + "/" + timeStamp + "_audio.3gp";

        mediaRecorder.setOutputFile(currentAudioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording failed to start", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error stopping recording", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}