package com.example.spotify_sdk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseAuthViewModel mAuthVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
//        mAuth.signOut();

        mAuthVm = new FirebaseAuthViewModel();

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> loginUser());

        Button signupRedirectButton = findViewById(R.id.signupRedirectText);
        signupRedirectButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in. Direct them to profile activity.
            startActivity(new Intent(MainActivity.this, SpotifyTokenActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String email = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(MainActivity.this, SpotifyTokenActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
