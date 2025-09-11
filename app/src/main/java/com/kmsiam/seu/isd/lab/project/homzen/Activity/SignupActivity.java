package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.MainActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.util.Patterns;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText signupName, signupEmail, signup_confirm_password, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    ProgressBar signup_progress;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signup_confirm_password = findViewById(R.id.signup_confirm_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signup_progress = findViewById(R.id.signup_progress);

        signupButton.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();

            String name = Objects.requireNonNull(signupName.getText()).toString().trim();
            String email = Objects.requireNonNull(signupEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(signupPassword.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(signup_confirm_password.getText()).toString().trim();

            if (TextUtils.isEmpty(name)) {
                signupName.setError("Enter your name");
                return;
            } else signupName.setError(null);

            if (TextUtils.isEmpty(email)) {
                signupEmail.setError("Enter an email address");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signupEmail.setError("Enter a valid email address");
                return;
            } else signupEmail.setError(null);

            if (TextUtils.isEmpty(password)) {
                signupPassword.setError("Enter a password");
                return;
            }
            if (password.length() < 6) {
                signupPassword.setError("Password must be at least 6 characters");
                return;
            } else signupPassword.setError(null);

            if (TextUtils.isEmpty(confirmPassword)) {
                signup_confirm_password.setError("Re-enter Password");
                return;
            }
            if (!password.equals(confirmPassword)) {
                signup_confirm_password.setError("Passwords do not match");
            } else {
                signup_confirm_password.setError(null);

                // Show loading
                signupButton.setEnabled(false);
                signup_progress.setVisibility(View.VISIBLE);

                // Create user with Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(createUserTask -> {
                            if (createUserTask.isSuccessful()) {
                                // User created successfully, now save additional user data
                                assert mAuth.getCurrentUser() != null;
                                String uid = mAuth.getCurrentUser().getUid();
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);

                                firestore.collection("users").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(unused -> {
                                            signup_progress.setVisibility(View.GONE);
                                            Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            finishAffinity();
                                        })
                                        .addOnFailureListener(e -> {
                                            signupButton.setEnabled(true);
                                            signup_progress.setVisibility(View.GONE);
                                            Toast.makeText(SignupActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                signupButton.setEnabled(true);
                                signup_progress.setVisibility(View.GONE);
                                String error = createUserTask.getException() != null ?
                                        createUserTask.getException().getMessage() : "Registration failed";
                                Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}