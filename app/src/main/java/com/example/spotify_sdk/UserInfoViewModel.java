package com.example.spotify_sdk;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class UserInfoViewModel extends ViewModel  {
    private MutableLiveData<String> email;


    public UserInfoViewModel(String email) {
        this.email = new MutableLiveData<>(email);
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public LiveData<String> getEmail() {
        return this.email;
    }


}
