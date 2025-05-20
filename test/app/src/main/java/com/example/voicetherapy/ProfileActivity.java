package com.example.voicetherapy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton homeIcon = findViewById(R.id.homeIcon);
        ImageButton musicIcon = findViewById(R.id.musicIcon);
        ImageButton profileIcon = findViewById(R.id.profileIcon);

        // Bottom navigation
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        musicIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ExercisesActivity.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            // Already on profile
        });
    }
}