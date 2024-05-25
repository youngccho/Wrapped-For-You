package com.example.spotify_sdk;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class FirebaseInter implements Executor {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase db;
    private UIInteraction mUIInteraction;

    public static FirebaseInter shared;

    static {
        try {
            shared = new FirebaseInter(new DummyUserInteraction());
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    public FirebaseInter(boolean signUserIn, UIInteraction uiInteraction) throws LoginException {
        if (signUserIn) {
            this.mAuth = FirebaseAuth.getInstance();
            this.mUser = this.mAuth.getCurrentUser();
            this.mUIInteraction = uiInteraction;

            if (this.mUser == null) {
                throw new LoginException();
            }
        } else {
            this.db = FirebaseDatabase.getInstance();
            this.mAuth = FirebaseAuth.getInstance();
            this.mUIInteraction = uiInteraction;
        }
    }

    public FirebaseInter(UIInteraction uiInteraction) throws LoginException {
        this(false, uiInteraction);
    }

    public void signUp(String email, String password, String token, String authToken, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUser(email, token, authToken, name);
                            mUIInteraction.updateUIFirebase(user, new ArrayList<>());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mUIInteraction.getEmailAcitivityInformation(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mUIInteraction.updateUIFirebase(null, new ArrayList<>());
                        }
                    }
                });
    }

    public void createUser(String email, String token, String authToken, String name) {
        db.getReference().child("users").child(email).child("token").setValue(token);
        db.getReference().child("users").child(email).child("authToken").setValue(authToken);
        db.getReference().child("users").child(email).child("name").setValue(name);
    }

    public void updateTokenWithAuth(String email, String token, String authToken) {
        db.getReference().child("users").child(email).child("token").setValue(token);
        db.getReference().child("users").child(email).child("authToken").setValue(authToken);
    }

    public void updateRefreshToken(String uid, String refreshToken) {
        db.getReference().child("users").child(uid).child("refreshToken").setValue(refreshToken);
    }

    public void getRefreshToken(String uid, IUpdateUI updateUI) {
        db.getReference().child("users").child(uid).child("refreshToken").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    updateUI.updateUI(task.getResult().getValue(String.class));
                } else {
                    updateUI.failureHandler();
                }
            }
        });
    }


    public void updateTokenNoAuth(String email, String token) {
        db.getReference().child("users").child(email).child("token").setValue(token);
    }
    public void updateCode(String email, String code) {
        db.getReference().child("users").child(email).child("authToken").setValue(code);
    }

    public void getCode(String email, IUpdateUI updateUI) {
        db.getReference().child("users").child(email).child("authToken").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    updateUI.updateUI(task.getResult().getValue(String.class));
                } else {
                    updateUI.failureHandler();
                }
            }
        });
    }

    public void getToken(String email, IUpdateUI updateUI) {
        db.getReference().child("users").child(email).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    updateUI.updateUI(task.getResult().getValue(String.class));
                } else {
                    updateUI.failureHandler();
                }
            }
        });
    }

    public void createWrapped(String uid, Wrapped wrapped) {
        String wrappedId = String.format("wrapped_%s", UUID.randomUUID().toString());
        Task<Void> t1 = db.getReference().child("users").child(uid).child(wrappedId).child("date").setValue(wrapped.getmDate());
        Task<Void> t2 = db.getReference().child("users").child(uid).child(wrappedId).child("songs").setValue(wrapped.getmSongs());
        Task<Void> t3 = db.getReference().child("users").child(uid).child(wrappedId).child("artistName").setValue(wrapped.getmArtists());
        Task<Void> t4 = db.getReference().child("users").child(uid).child(wrappedId).child("genres").setValue(wrapped.getmGenres());
        Task<Void> t5 = db.getReference().child("users").child(uid).child(wrappedId).child("genreImage").setValue(wrapped.getGenreImage());
        Task<Void> t6 = db.getReference().child("users").child(uid).child(wrappedId).child("artistImage").setValue(wrapped.getArtistImage());
        db.getReference().child("users").child(uid).child(wrappedId).child("trackImage").setValue(wrapped.getTrackImage());
        db.getReference().child("users").child(uid).child(wrappedId).child("songUrl1").setValue(wrapped.getSong1Url());
        db.getReference().child("users").child(uid).child(wrappedId).child("songUrl2").setValue(wrapped.getSong2Url());
        db.getReference().child("users").child(uid).child(wrappedId).child("songUrl3").setValue(wrapped.getSong3Url());
        System.out.println("tmp");

    }

    public void deleteUser(String email) {
        db.getReference().child("users").child(email).getRef().removeValue();
    }


    public void editPassword(String email, String newPassword) {
        mUser = mAuth.getCurrentUser();
        mUser.updatePassword(newPassword);
//        db.getReference().child("users").child(email).child("password").setValue(newPassword);
    }

    public void listWrapped(String email, IListUpdateWrapped callback) {
        db.getReference().child("users").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Wrapped> out = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().startsWith("wrapped")) {
                        long date = snapshot.child("date").getValue(Long.class);
                        String songsString = snapshot.child("songs").getValue(String.class);
                        String artistNamesString = snapshot.child("artistName").getValue(String.class);
                        String genresString = snapshot.child("genres").getValue(String.class);
                        String genreImage = snapshot.child("genreImage").getValue(String.class);
                        String artistImage = snapshot.child("artistImage").getValue(String.class);
                        String trackImage = snapshot.child("trackImage").getValue(String.class);
                        String song1Url = snapshot.child("songUrl1").getValue(String.class);
                        String song2Url = snapshot.child("songUrl2").getValue(String.class);
                        String song3Url = snapshot.child("songUrl3").getValue(String.class);
                        // Create a new Wrapped object
                        Wrapped wrapped = new Wrapped(date, songsString, artistNamesString, genresString, trackImage, artistImage, genreImage, song1Url, song2Url, song3Url);
                        out.add(wrapped);
                    }
                }
                Collections.sort(out);
                callback.updateUI(out);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }


    public void saveWrappedToFirebase(String uid, Wrapped wrapped) {
        // Assuming FirebaseInter is instantiated and initialized properly

        FirebaseInter.shared.createWrapped(uid, wrapped);
        //System.out.printf("test: %d", FirebaseInter.shared.listWrapped(uid).size());
        // Display a toast message to indicate successful saving
        //Toast.makeText(mUIInteraction.getEmailAcitivityInformation(), "Wrapped saved successfully", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void execute(Runnable command) {
        command.run();
    }

}