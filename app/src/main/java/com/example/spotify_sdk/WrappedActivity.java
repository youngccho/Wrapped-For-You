package com.example.spotify_sdk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap; // For HashMap class
import java.util.List; // For List interface
import java.util.Map; // For Map interface
import java.util.Comparator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WrappedActivity extends AppCompatActivity {

    private OkHttpClient mOkHttpClient;
    private TextView topArtistsTextView, topTracksTextView, topGenresTextView;
    private ImageView topArtistImage, topTrackImage, topGenreImage, profileImageView;
    private ImageButton playTrack1Button, playTrack2Button, playTrack3Button;
    private MediaPlayer mediaPlayer;
    private String range, profileImageUrl, spotify_user_name;
    private Button short_term_button, medium_term_button, long_term_button, save_button;

    private FirebaseAuthViewModel mAuthVm;

    private Wrapped wrapped;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        range = "long_term";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_page);

        // Initialize OkHttpClient
        mOkHttpClient = new OkHttpClient();

        mAuthVm = new FirebaseAuthViewModel();


        mAuth = FirebaseAuth.getInstance();

        // Initialize the views
        topArtistsTextView = findViewById(R.id.top_artists);
        topTracksTextView = findViewById(R.id.top_tracks);
        topGenresTextView = findViewById(R.id.top_genres);
        topArtistImage = findViewById(R.id.top_artist_image);
        topTrackImage = findViewById(R.id.top_track_image);
        topGenreImage = findViewById(R.id.top_genre_image);
        playTrack1Button = findViewById(R.id.play_track1_button);
        playTrack2Button = findViewById(R.id.play_track2_button);
        playTrack3Button = findViewById(R.id.play_track3_button);
        short_term_button = findViewById(R.id.short_term);
        medium_term_button = findViewById(R.id.medium_term);
        long_term_button = findViewById(R.id.long_term);
        profileImageView = findViewById(R.id.profile_image_view);
        save_button = findViewById(R.id.save_button);
        wrapped = new Wrapped();


        // Retrieve access token from intent
//        String accessToken = getIntent().getStringExtra("access_token");

        // Fetch user top artists, tracks, and genres
        FirebaseInter.shared.getToken(FirebaseAuth.getInstance().getCurrentUser().getUid(), new IUpdateUI() {
            @Override
            public void updateUI(String name) {
                mAuthVm.setAuthToken(name);
            }

            @Override
            public void failureHandler() {
                System.out.println("failed");
            }
        });

        FirebaseInter.shared.getCode(FirebaseAuth.getInstance().getCurrentUser().getUid(), new IUpdateUI() {
            @Override
            public void updateUI(String name) {
                mAuthVm.setCode(name);
            }

            @Override
            public void failureHandler() {
                System.out.println("failed");
            }
        });

        mAuthVm.getAuthToken().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                fetchAllSpotifyData(s);
            }
        });

        mAuthVm.getCode().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                // Cache data
            }
        });

        topTracksTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(WrappedActivity.this);
                LayoutInflater inflater = WrappedActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.topten, null); // Inflate your modal layout

                // Set the view to the dialog builder
                builder.setView(dialogView);

                TextView dialogTextView = dialogView.findViewById(R.id.top_ten_text);

                // Set the text of the dialog TextView to wrapped.getmTracks()
                dialogTextView.setText(wrapped.getmSongs());


                // Create and show the dialog
                AlertDialog dialog = builder.create();

                dialog.show();

                Button backButton = dialogView.findViewById(R.id.backButton);

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
            }
        });

        topArtistsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(WrappedActivity.this);
                LayoutInflater inflater = WrappedActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.topten, null); // Inflate your modal layout

                // Set the view to the dialog builder
                builder.setView(dialogView);

                TextView dialogTextView = dialogView.findViewById(R.id.top_ten_text);

                // Set the text of the dialog TextView to wrapped.getmTracks()
                dialogTextView.setText(wrapped.getmArtists());

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                Button backButton = dialogView.findViewById(R.id.backButton);

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
            }
        });

        topGenresTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(WrappedActivity.this);
                LayoutInflater inflater = WrappedActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.topten, null); // Inflate your modal layout

                // Set the view to the dialog builder
                builder.setView(dialogView);

                TextView dialogTextView = dialogView.findViewById(R.id.top_ten_text);

                // Set the text of the dialog TextView to wrapped.getmTracks()
                dialogTextView.setText(wrapped.getmGenres());

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                Button backButton = dialogView.findViewById(R.id.backButton);

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
            }
        });

        // Set click listener for profile image view
        profileImageView.setOnClickListener(v -> {
            launchUserInfoFragment();
        });

        short_term_button.setOnClickListener(v -> {
            range = "short_term";
            fetchAllSpotifyData(mAuthVm.getAuthToken().getValue());
            stopMusic();
        });
        medium_term_button.setOnClickListener(v -> {
            range = "medium_term";
            fetchAllSpotifyData(mAuthVm.getAuthToken().getValue());
            stopMusic();
        });
        long_term_button.setOnClickListener(v -> {
            range = "long_term";
            fetchAllSpotifyData(mAuthVm.getAuthToken().getValue());
            stopMusic();
        });

        save_button.setOnClickListener(v -> {
            if (TextUtils.isEmpty(wrapped.getmSongs())
                    || TextUtils.isEmpty(wrapped.getmArtists())
                    || TextUtils.isEmpty(wrapped.getmGenres())) {
                // Display a toast message if any attribute is empty
                Toast.makeText(getApplicationContext(), "Some parts of Wrapped are empty", Toast.LENGTH_SHORT).show();
                return;
            }

            wrapped.setmDate(System.currentTimeMillis());

            if (Objects.requireNonNull(mAuth.getCurrentUser()).getUid() != null) {
                FirebaseInter.shared.saveWrappedToFirebase(mAuth.getCurrentUser().getUid(), wrapped);
                Toast.makeText(getApplicationContext(), "Wrapped saved succcessfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Email is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllSpotifyData(String accessToken) {
        fetchUserArtists(accessToken);
        fetchUserTopTracks(accessToken);
        fetchUserTopGenres(accessToken);
        fetchUserProfile(accessToken);
        getTrackObjectAsync(0);
        getTrackObjectAsync(1);
        getTrackObjectAsync(2);
        // Set click listeners for track buttons
        playTrack1Button.setOnClickListener(v -> playTrackPreview(wrapped.getSong1Url()));
        playTrack2Button.setOnClickListener(v -> playTrackPreview(wrapped.getSong2Url()));
        playTrack3Button.setOnClickListener(v -> playTrackPreview(wrapped.getSong3Url()));

    }

    private void fetchUserArtists(String accessToken) {
        // Create a request to get the user's top artists
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists" + "?time_range=" + range)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(WrappedActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d("JSON", "Response body: " + responseBody);

                    final JSONObject jsonObject = new JSONObject(responseBody);

                    String artistNameList = "";
                    String artistImage = "";
                    for (int i = 0; i < 10; i++) {
                        String artistName = jsonObject.optJSONArray("items").optJSONObject(i).optString("name", "");
                        artistNameList += (i + 1) + ". " + artistName + "\n";
                        if (i == 0) {
                            artistImage = jsonObject.optJSONArray("items").optJSONObject(i).optJSONArray("images").optJSONObject(0).optString("url", "");
                        }
                    }

                    wrapped.setmArtists(artistNameList);

                    // Update UI on the main thread
                    String finalArtistNameList = artistNameList;
                    String finalArtistImage = artistImage;
                    runOnUiThread(() -> {
                        topArtistsTextView.setText(finalArtistNameList.substring(0, finalArtistNameList.indexOf("4.")));
                        // Load profile image using AsyncTask
                        new LoadImageTask(topArtistImage).execute(finalArtistImage);
                        wrapped.setArtistImage(finalArtistImage);
                    });

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show());
                } catch (NullPointerException e) {
                    Log.e("Error", "Null pointer exception: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchUserTopTracks(String accessToken) {
        // Create a request to get the user's top tracks
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks" + "?time_range=" + range)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top tracks: " + e);
                runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to fetch top tracks", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d("JSON", "Top tracks response: " + responseBody);

                    final JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray items = jsonObject.optJSONArray("items");
                    if (items != null && items.length() > 0) {
                        StringBuilder trackListText = new StringBuilder();
                        String trackImageUrl = null;
                        for (int i = 0; i < 10; i++) {
                            JSONObject track = items.optJSONObject(i);
                            if (i == 0) {
                                trackImageUrl = track.optJSONObject("album").optJSONArray("images").optJSONObject(0).optString("url", "");
                            }
                            if (track != null) {
                                String trackName = track.optString("name", "");
                                trackListText.append((i + 1)).append(". ").append(trackName).append("\n");
                            }
                        }

                        wrapped.setmSongs(trackListText.toString());


                        String finalTrackImageUrl = trackImageUrl;
                        runOnUiThread(() -> {
                            topTracksTextView.setText((trackListText.toString()).substring(0, (trackListText.toString()).indexOf("4.")));
                            new LoadImageTask(topTrackImage).execute(finalTrackImageUrl);
                            wrapped.setTrackImage(finalTrackImageUrl);
                        });
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top tracks data: " + e);
                    runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to parse top tracks data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchUserTopGenres(String accessToken) {
        // Create a request to get the user's top artists
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists" + "?time_range=" + range)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch top artists: " + e);
                runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to fetch top artists", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d("JSON", "Top artists response: " + responseBody);

                    final JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray artistsArray = jsonObject.optJSONArray("items");
                    if (artistsArray != null && artistsArray.length() > 1) {
                        JSONObject secondArtist = artistsArray.optJSONObject(1); // Second artist in the list
                        String artistImageUrl = Objects.requireNonNull(secondArtist.optJSONArray("images")).optJSONObject(0).optString("url", "");

                        ArrayList<String> genres = new ArrayList<>();
                        HashMap<String, Integer> genreCount = new HashMap<>(); // Track genre occurrences

// Loop through artists and genres to count occurrences
                        for (int i = 0; i < artistsArray.length(); i++) {
                            JSONArray genreList = artistsArray.optJSONObject(i).optJSONArray("genres");
                            if (genreList != null) {
                                for (int j = 0; j < genreList.length(); j++) {
                                    String genre = genreList.getString(j);
                                    genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1); // Update genre count
                                }
                            }
                        }

// Sort genres by occurrence count in descending order
                        List<Map.Entry<String, Integer>> sortedGenres = new ArrayList<>(genreCount.entrySet());
                        sortedGenres.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

// Add genres in descending order of occurrence to the genres list
                        for (Map.Entry<String, Integer> entry : sortedGenres) {
                            genres.add(entry.getKey());
                            if (genres.size() >= 10) {
                                break;
                            }
                        }

                        StringBuilder genre_text = new StringBuilder();
                        for (int i = 0; i < 10; i++) {
                            String genreName = genres.get(i);
                            genre_text.append((i + 1)).append(". ").append(SpotifyTokenActivity.toTitleCase(genreName)).append("\n");
                        }

                        String finalGenresList = genre_text.toString();
                        wrapped.setmGenres(finalGenresList);

                        runOnUiThread(() -> {
                            topGenresTextView.setText(finalGenresList.substring(0, finalGenresList.indexOf("4.")));
                            new LoadImageTask(topGenreImage).execute(artistImageUrl);
                            wrapped.setGenreImage(artistImageUrl);
                        });
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse top artists data: " + e);
                    runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to parse top artists data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchUserProfile(String accessToken) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(WrappedActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d("JSON", "Response body: " + responseBody);

                    final JSONObject jsonObject = new JSONObject(responseBody);

                    // Extract profile image URL from JSON object
                    spotify_user_name = jsonObject.optString("display_name");
                    profileImageUrl = jsonObject.optJSONArray("images").optJSONObject(0).optString("url", "");
                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        // Load profile image using AsyncTask
                        new LoadProfileImageTask().execute(profileImageUrl);
                    });
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(() -> Toast.makeText(WrappedActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show());
                } catch (NullPointerException e) {
                    Log.e("Error", "Null pointer exception: " + e.getMessage());
                    // Proceed to onPostExecute with default profile picture
                    runOnUiThread(() -> new LoadProfileImageTask().onPostExecute(null));
                }
            }
        });
    }

    private void launchUserInfoFragment() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        Intent intent = new Intent(this, UserInfoFragment.class);
        intent.putExtra("profile_image_url", profileImageUrl);
        intent.putExtra("spotify_user_name", spotify_user_name);
        startActivity(intent);
        stopMusic();
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void playTrackPreview(String songURL) {
            if (!songURL.isEmpty()) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(songURL);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Sorry, you need a premium account to use the playback feature!")
                            .setCancelable(false)
                            .setPositiveButton("Buy Premium", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Open the URL in a browser when "Buy Premium" is clicked
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.spotify.com/us/premium/"));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss(); // Dismiss the dialog when "Cancel" is clicked
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();                }
            } else {
                Toast.makeText(this, "Preview not available for this track", Toast.LENGTH_SHORT).show();
            }
    }

    private void getTrackObjectAsync(int index) {
        new AsyncTask<Integer, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Integer... params) {
                try {
                    String responseBody = Objects.requireNonNull(mOkHttpClient.newCall(new Request.Builder()
                            .url("https://api.spotify.com/v1/me/top/tracks" + "?time_range=" + range)
                            .addHeader("Authorization", "Bearer " + mAuthVm.getAuthToken().getValue())
                            .build()).execute().body()).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray items = jsonObject.optJSONArray("items");
                    if (items != null && index < items.length()) {
                        return items.optJSONObject(index);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                switch (index) {
                    case 0:
                        wrapped.setSong1Url(result.optString("preview_url", ""));
                        break;
                    case 1:
                        wrapped.setSong2Url(result.optString("preview_url", ""));
                        break;
                    case 2:
                        wrapped.setSong3Url(result.optString("preview_url", ""));
                        break;
                }
            }
        }.execute(index);
    }


    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

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
                imageView.setImageBitmap(bitmap);
            } else {
                // Handle error loading image
            }
        }
    }

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
                //Toast.makeText(WrappedActivity.this, "Default profile picture not loaded", Toast.LENGTH_SHORT).show();
                profileImageView.setImageResource(R.drawable.defaultpfp);
            }
        }
    }
}