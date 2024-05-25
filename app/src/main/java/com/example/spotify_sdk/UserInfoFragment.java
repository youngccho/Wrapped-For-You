package com.example.spotify_sdk;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;


public class UserInfoFragment extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView userText, spotifyDisplayNameTextView;
    private TextView nameText;
    private OkHttpClient mOkHttpClient;

    public static final String CLIENT_ID = "93b7629fcdea4f939c768db42d1f99d5";
    public static final String REDIRECT_URI = "spotify-sdk://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_userinfo);
        mOkHttpClient = new OkHttpClient();
        spotifyDisplayNameTextView = findViewById(R.id.spotifyNameView);
        String spotifyDisplayName = getIntent().getStringExtra("spotify_user_name");
        spotifyDisplayNameTextView.setText(spotifyDisplayName);

        // Retrieve profile image URL from Intent
        String profileImageUrl = getIntent().getStringExtra("profile_image_url");
        profileImageView = findViewById(R.id.profile_image);
        nameText = findViewById(R.id.nameView);

        // Load profile image using AsyncTask
        new LoadProfileImageTask().execute(profileImageUrl);

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });
//        FirebaseInter.shared.listWrapped(FirebaseAuth.getInstance().getCurrentUser().getUid(), new IListUpdateWrapped() {
//            @Override
//            public void updateUI(List<Wrapped> wrappedList) {
//                System.out.println(wrappedList.size());
//            }
//        });
        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoFragment.this, HistoryActivity.class);
            startActivity(intent);
        });

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> showEditPopup());

        FirebaseAuth auth = FirebaseAuth.getInstance();

        Button signOutButton = findViewById(R.id.signOut);
        signOutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(UserInfoFragment.this, MainActivity.class));
        });


        userText = findViewById(R.id.usernameTextView);

        userText.setText(auth.getCurrentUser().getEmail());

        String displayName = auth.getCurrentUser().getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            nameText.setText(displayName);
        } else {
            nameText.setText("Name");
        }

        Button deleteBtn = findViewById(R.id.deleteButton);

        deleteBtn.setOnClickListener(v -> {
            auth.getCurrentUser().delete();
            FirebaseInter.shared.deleteUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
            auth.signOut();
            startActivity(new Intent(UserInfoFragment.this, MainActivity.class));
        });

        //String accessToken = getIntent().getStringExtra("access_token");
        //fetchSpotifyUsername(accessToken);
//        getSpotifyToken();
    }

//    private Uri getRedirectUri() {
//        return Uri.parse(REDIRECT_URI);
//    }
//
//    public void getSpotifyToken() {
//        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
//        AuthorizationClient.openLoginActivity(UserInfoFragment.this, AUTH_TOKEN_REQUEST_CODE, request);
//    }
//
//    // Method to create authorization request
//    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
//        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
//                .setShowDialog(false)
//                .setScopes(new String[]{"user-top-read"})
//                .setCampaign("your-campaign-token")
//                .build();
//    }
//
//    // Handle the response from Spotify authentication activity
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
//
//        if (AUTH_TOKEN_REQUEST_CODE == requestCode && response.getType() == AuthorizationResponse.Type.TOKEN) {
//            // Access token retrieved successfully
//            String accessToken = response.getAccessToken();
//            // Use the access token as needed
//            fetchSpotifyUsername(accessToken);
//        }
//    }
//
//    private void fetchSpotifyUsername(String accessToken) {
//        final Request request = new Request.Builder()
//                .url("https://api.spotify.com/v1/me")
//                .addHeader("Authorization", "Bearer " + accessToken)
//                .build();
//
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("HTTP", "Failed to fetch data: " + e);
//                Toast.makeText(UserInfoFragment.this, "Failed to fetch data, watch Logcat for more details",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String responseBody = response.body().string();
//                    Log.d("JSON", "Response body: " + responseBody);
//
//                    final JSONObject jsonObject = new JSONObject(responseBody);
//
////                    final String spotifyDisplayName = jsonObject.optString("display_name", "");
////                    String spotifyDisplayName = getIntent().getStringExtra("spotify_user_name");
////
////                    runOnUiThread(() -> {
////                        spotifyDisplayNameTextView.setText(spotifyDisplayName);
////                    });
//
//                } catch (JSONException e) {
//                    Log.d("JSON", "Failed to parse data: " + e);
//                    runOnUiThread(() -> Toast.makeText(UserInfoFragment.this, "Failed to parse data", Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }

    private class LoadProfileImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageUrl = strings[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap);
            } else {
                // Handle error loading image
                //Toast.makeText(UserInfoFragment.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                profileImageView.setImageResource(R.drawable.defaultpfp);
            }
        }
    }

    private void showEditPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_userinfo, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button saveEditButton = dialogView.findViewById(R.id.saveEditButton);
        EditText editName = dialogView.findViewById(R.id.editName);

        saveEditButton.setOnClickListener(v -> {
//            EditText email = dialogView.findViewById(R.id.editUsername);
            EditText pw = dialogView.findViewById(R.id.editPassword);
            String newName = editName.getText().toString();

            if (pw == null) {
                System.out.println("uh oh");
            }
            if (pw.length() >= 6) {
                FirebaseInter.shared.editPassword(FirebaseAuth.getInstance().getCurrentUser().getUid(), pw.getText().toString());
                Toast.makeText(this, "Password successfully updated!", Toast.LENGTH_SHORT).show();
            } else if (pw.length() > 0){
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }

            if (!newName.isEmpty()) {
                // Update name TextView
                nameText.setText(newName);
                // Update user's display name in Firebase authentication
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();
                    currentUser.updateProfile(profileUpdates);
                    Toast.makeText(this, "Name successfully updated!", Toast.LENGTH_SHORT).show();

                }
            }
            dialog.dismiss();

        });
        dialog.show();
    }


}