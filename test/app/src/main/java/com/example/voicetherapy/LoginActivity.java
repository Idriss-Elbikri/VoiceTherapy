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

public class LoginActivity extends AppCompatActivity {

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpText = findViewById(R.id.signUpText);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

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

        // Login button - navigate to MainActivity
        loginButton.setOnClickListener(v -> {
            // TODO: Add authentication logic
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Navigate to SignupActivity
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Forgot password (placeholder)
        forgotPassword.setOnClickListener(v -> {
            // TODO: Implement password recovery
        });
    }
}