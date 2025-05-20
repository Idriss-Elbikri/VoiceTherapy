package com.example.voicetherapy;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        ImageView toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView loginText = findViewById(R.id.loginText);

        // Toggle password visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordInput.setTransformationMethod(null);
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye);
            } else {
                passwordInput.setTransformationMethod(new PasswordTransformationMethod());
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_off);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // Toggle confirm password visibility
        toggleConfirmPasswordVisibility.setOnClickListener(v -> {
            confirmPasswordVisible = !confirmPasswordVisible;
            if (confirmPasswordVisible) {
                confirmPasswordInput.setTransformationMethod(null);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye);
            } else {
                confirmPasswordInput.setTransformationMethod(new PasswordTransformationMethod());
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_off);
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
        });

        // Sign up button - navigate to MainActivity
        signUpButton.setOnClickListener(v -> {
            // TODO: Add registration logic and validation
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Navigate to LoginActivity
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}