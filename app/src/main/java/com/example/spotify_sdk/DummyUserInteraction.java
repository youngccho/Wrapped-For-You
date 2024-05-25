package com.example.spotify_sdk;

import android.app.Activity;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DummyUserInteraction implements UIInteraction {
    @Override
    public void updateUIFirebase(FirebaseUser user, List<Wrapped> wrapped) {

    }

    @Override
    public Activity getEmailAcitivityInformation() {
        return null;
    }
}
