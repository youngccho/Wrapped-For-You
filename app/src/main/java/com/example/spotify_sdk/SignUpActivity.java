package com.example.spotify_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        Button signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(v -> signUpUser());

        Button loginRedirectButton = findViewById(R.id.loginRedirectText);
        loginRedirectButton.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, MainActivity.class)));
    }

    private void signUpUser() {
        String email = ((EditText) findViewById(R.id.signup_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.signup_password)).getText().toString();
        String name = ((EditText) findViewById(R.id.signup_name)).getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-up user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Update user's profile with the provided name
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates);
                            FirebaseInter.shared.createUser(user.getUid(), "_", "_", name);
                        }

                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                        // You can direct the user to the main activity or any other activity here
                        mAuth.signOut();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign up fails, display a message to the user.
                        if (password.length() < 6) {
                            Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "User with this email already exists.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}