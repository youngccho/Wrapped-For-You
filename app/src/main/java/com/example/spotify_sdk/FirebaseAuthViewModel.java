package com.example.spotify_sdk;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FirebaseAuthViewModel extends ViewModel {

    private MutableLiveData<String> mAuthToken;
    private MutableLiveData<String> mCode;

    public FirebaseAuthViewModel() {
        mAuthToken = new MutableLiveData<>();
        mCode = new MutableLiveData<>();
    }

    public LiveData<String> getAuthToken() {
        return mAuthToken;
    }

    public LiveData<String> getCode() {
        return mCode;
    }

    public void setAuthToken(String authToken) {
        mAuthToken.setValue(authToken);
    }

    public void setCode(String code) {
        mCode.setValue(code);
    }



}
