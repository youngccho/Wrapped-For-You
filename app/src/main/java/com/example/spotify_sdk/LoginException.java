package com.example.spotify_sdk;

public class LoginException extends Exception {
    public LoginException() {
        super("User not signed in");
    }

}
