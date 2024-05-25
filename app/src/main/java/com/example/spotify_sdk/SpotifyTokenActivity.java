package com.example.spotify_sdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;


public class SpotifyTokenActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "93b7629fcdea4f939c768db42d1f99d5";
    public static final String REDIRECT_URI = "spotify-sdk://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    private FirebaseAuthViewModel mAuthVm;

    @Override
    protected void onStart() {
        mAuthVm = new FirebaseAuthViewModel();
        super.onStart();

        mAuthVm.setAuthToken("_");
        getToken();

        FirebaseInter.shared.getToken(FirebaseAuth.getInstance().getCurrentUser().getUid(), new IUpdateUI() {
            @Override
            public void updateUI(String name) {
                mAuthVm.setAuthToken(name);
            }

            @Override
            public void failureHandler() {
                System.out.println("failed");
                mAuthVm.setAuthToken("_");
            }
        });


        mAuthVm.getAuthToken().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (!s.equals("_")) {
                    //ref token functionality
                    Intent intent = new Intent(SpotifyTokenActivity.this, WrappedActivity.class);
                    if (mAccessToken != null) {
                        intent.putExtra("access_token", mAccessToken);
                        startActivity(intent);
                    } else {
                        //red token functionality
                        startActivity(intent);
                    }
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
//       System.out.println(request.getState());
        AuthorizationClient.openLoginActivity(SpotifyTokenActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
//        System.out.println(request.getState());

    }

    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(SpotifyTokenActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            FirebaseInter.shared.updateTokenNoAuth(FirebaseAuth.getInstance().getCurrentUser().getUid(), mAccessToken);
            mAuthVm.setAuthToken(mAccessToken);
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            FirebaseInter.shared.updateCode(FirebaseAuth.getInstance().getCurrentUser().getUid(), mAccessCode);
            mAuthVm.setAuthToken(mAccessToken);
        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {

        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-top-read"})
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    public static String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

}