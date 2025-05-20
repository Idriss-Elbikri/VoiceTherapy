package com.example.voicetherapy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import com.example.voicetherapy.models.Exercise;

public class ExercisesActivity extends AppCompatActivity {

    private TextView recordNowText;
    private Button recordNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        GridLayout exerciseGrid = findViewById(R.id.exerciseGrid);
        ImageButton homeIcon = findViewById(R.id.homeIcon);
        ImageButton musicIcon = findViewById(R.id.musicIcon);
        ImageButton profileIcon = findViewById(R.id.profileIcon);

        // Find views
        recordNowText = findViewById(R.id.recordNowText);
        recordNowButton = findViewById(R.id.recordNowButton);

        // Get Exercise data from Intent using the type-safe method
        Exercise exercise = getIntent().getParcelableExtra("exercise", Exercise.class);

        if (exercise != null) {
            // Display exercise details in the TextView
            recordNowText.setText(exercise.getTitle() + "\n" + exercise.getDescription());

            // Set up the record button click listener
            recordNowButton.setOnClickListener(v -> {
                // TODO: Implement recording logic here
                Toast.makeText(this, "Record button clicked for: " + exercise.getTitle(), Toast.LENGTH_SHORT).show();
                // Start audio recording
                // Once recording is done, send audio to backend for re-analysis and scoring
            });
        } else {
            // Handle case where no exercise data is received
            recordNowText.setText("No exercise found.");
            recordNowButton.setEnabled(false); // Disable button if no exercise
            Toast.makeText(this, "Error: No exercise data received.", Toast.LENGTH_LONG).show();
        }

        // Set click listeners for each exercise (simplified example)
        for (int i = 0; i < exerciseGrid.getChildCount(); i++) {
            final int exerciseNumber = i + 1;
            exerciseGrid.getChildAt(i).setOnClickListener(v -> {
                Intent intent = new Intent(ExercisesActivity.this, DetailActivity.class);
                intent.putExtra("exerciseId", exerciseNumber);
                startActivity(intent);
            });
        }

        // Bottom navigation
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ExercisesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        musicIcon.setOnClickListener(v -> {
            // Already on exercises
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ExercisesActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}