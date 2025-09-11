package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.util.Patterns;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText loginEmail, loginPassword, forgetPasswordEmail;
    Button loginButton, forgetPasswordButton;
    TextView signupRedirectText, forgotPasswordButtonText;
    ProgressBar login_progress;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        login_progress = findViewById(R.id.login_progress);
        forgotPasswordButtonText = findViewById(R.id.forgotPasswordButton);

        loginButton.setOnClickListener(v -> {
            if (!loginCredential()) {
                return;
            }
            loginButton.setEnabled(false);
            login_progress.setVisibility(View.VISIBLE);

            String userEmail = Objects.requireNonNull(loginEmail.getText()).toString().trim();
            String userPassword = Objects.requireNonNull(loginPassword.getText()).toString().trim();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Credential Doesn't Match!", Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                        login_progress.setVisibility(View.GONE);
                    }
                });
        });

        forgotPasswordButtonText.setOnClickListener(v -> {
            Dialog forgetPasswordDialog = new Dialog(LoginActivity.this, R.style.TransparentDialog);
            forgetPasswordDialog.setContentView(R.layout.forgate_password_dialog);
            forgetPasswordEmail = forgetPasswordDialog.findViewById(R.id.reset_email);
            forgetPasswordButton = forgetPasswordDialog.findViewById(R.id.btn_reset);
            Button cancelButton = forgetPasswordDialog.findViewById(R.id.btn_cancel);
            
            cancelButton.setOnClickListener(view -> forgetPasswordDialog.dismiss());

            forgetPasswordButton.setOnClickListener(v1 -> {
                String forgetEmail = Objects.requireNonNull(forgetPasswordEmail.getText()).toString().trim();
                if (forgetEmail.isEmpty()) {
                    forgetPasswordEmail.setError("Email cannot be empty");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(forgetEmail).matches()) {
                    forgetPasswordEmail.setError("Enter a valid email address");
                    return;
                }
                
                // Show loading
                forgetPasswordButton.setEnabled(false);
                
                // Check if email exists in Firestore
                firestore.collection("users")
                    .whereEqualTo("email", forgetEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Email exists, send reset email
                                mAuth.sendPasswordResetEmail(forgetEmail)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(LoginActivity.this, "Password reset email sent to " + forgetEmail, Toast.LENGTH_SHORT).show();
                                        forgetPasswordDialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        forgetPasswordButton.setEnabled(true);
                                        Toast.makeText(LoginActivity.this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            } else {
                                forgetPasswordButton.setEnabled(true);
                                forgetPasswordEmail.setError("No account found with this email");
                            }
                        } else {
                            forgetPasswordButton.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Error checking email: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            });
            forgetPasswordDialog.show();
        });

        signupRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    public Boolean loginCredential() {
        String valEmail = Objects.requireNonNull(loginEmail.getText()).toString().trim();
        String valPass = Objects.requireNonNull(loginPassword.getText()).toString().trim();

        if (valEmail.isEmpty()) {
            loginEmail.setError("Email is required");
            return false;
        } else loginEmail.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(valEmail).matches()) {
            loginEmail.setError("Enter a valid email address");
            return false;
        } else loginEmail.setError(null);

        if (valPass.isEmpty()) {
            loginPassword.setError("Password is required");
            return false;
        } else loginPassword.setError(null);

        if (valPass.length() < 6) {
            loginPassword.setError("Password must be at least 6 characters");
            return false;
        } else loginPassword.setError(null);

        return true;
    }

}