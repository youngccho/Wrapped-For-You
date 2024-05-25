package com.example.spotify_sdk;

import android.app.Activity;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public interface UIInteraction {
    void updateUIFirebase(FirebaseUser user, List<Wrapped> wrapped);
    Activity getEmailAcitivityInformation();
}
