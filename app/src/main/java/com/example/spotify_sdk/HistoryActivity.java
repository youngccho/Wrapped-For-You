package com.example.spotify_sdk;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Wrapped> wrappedList = new ArrayList<>();
    private FirebaseInter firebaseInter;
    private Spinner dateSpinner;
    private TextView textViewTracks, textViewArtists, textViewGenres;

    private ImageView topArtistImage, topTrackImage, topGenreImage;
    private ImageButton playTrack1Button, playTrack2Button, playTrack3Button;

    private MediaPlayer mediaPlayer;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_history_page);

        // Initialize date Spinner
        dateSpinner = findViewById(R.id.date_spinner);

        // Initialize TextViews for tracks, artists, and genres
        textViewTracks = findViewById(R.id.top_tracks);
        textViewArtists = findViewById(R.id.top_artists);
        textViewGenres = findViewById(R.id.top_genres);

        topArtistImage = findViewById(R.id.top_artist_image);
        topTrackImage = findViewById(R.id.top_track_image);
        topGenreImage = findViewById(R.id.top_genre_image);

        playTrack1Button = findViewById(R.id.play_track1_button);
        playTrack2Button = findViewById(R.id.play_track2_button);
        playTrack3Button = findViewById(R.id.play_track3_button);

        // Set up listener for date selection
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle date selection
                updateUIForSelectedDate(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });

        Button backButton = findViewById(R.id.historyBackButton);

        // Set OnClickListener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                finish();
            }
        });



        // Load wrapped data and populate the date Spinner
        initializeFirebaseInter();
        fetchWrappedData();
    }

    private void initializeFirebaseInter() {
        UIInteraction uiInteraction = new UIInteraction() {
            @Override
            public void updateUIFirebase(FirebaseUser user, List<Wrapped> wrapped) {
                // You might update RecyclerView here or handle user-specific UI changes
                wrappedList.clear();
                wrappedList.addAll(wrapped);
            }

            @Override
            public Activity getEmailAcitivityInformation() {
                // This could be used for context or activity-specific information
                return HistoryActivity.this;
            }
        };

        try {
            firebaseInter = new FirebaseInter(false, uiInteraction);
        } catch (LoginException e) {
            Log.e(TAG, "Error initializing Firebase Interface", e);
        }
    }

    private void fetchWrappedData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseInter.listWrapped(userId, new IListUpdateWrapped() {
            @Override
            public void updateUI(List<Wrapped> wrappedList) {
                // Update the UI with the fetched data
                if (wrappedList.isEmpty()) {
                    // Display an AlertDialog
                    showNoWrappedAlertDialog();
                } else {
                    // Update the UI with the fetched data
                    Collections.sort(wrappedList);
                    updateUIWithWrappedList(wrappedList);
                }
            }
        });
    }

    private void populateDateSpinner() {
        List<String> dateStrings = new ArrayList<>();
        int wrappedCount = wrappedList.size();
        for (Wrapped wrapped : wrappedList) {
            String wrappedDate = "Wrapped " + wrappedCount + " - " + dateFormat.format(new Date(wrapped.getmDate()));
            dateStrings.add(wrappedDate);
            wrappedCount--;
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerAdapter);
    }

    private void updateUIForSelectedDate(int position) {
        // Get the Wrapped object for the selected date
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        Wrapped selectedWrapped = wrappedList.get(position);

        playTrack1Button.setOnClickListener(v -> playTrackPreview(selectedWrapped.getSong1Url()));
        playTrack2Button.setOnClickListener(v -> playTrackPreview(selectedWrapped.getSong2Url()));
        playTrack3Button.setOnClickListener(v -> playTrackPreview(selectedWrapped.getSong3Url()));

        // Update the TextViews with tracks/artists/genres from the selectedWrapped
        textViewTracks.setText(selectedWrapped.getmSongs().substring(0, selectedWrapped.getmSongs().indexOf("4.")));
        textViewArtists.setText(selectedWrapped.getmArtists().substring(0, selectedWrapped.getmArtists().indexOf("4.")));
        textViewGenres.setText(selectedWrapped.getmGenres().substring(0, selectedWrapped.getmGenres().indexOf("4.")));

        textViewTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                LayoutInflater inflater = HistoryActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.topten, null); // Inflate your modal layout

                // Set the view to the dialog builder
                builder.setView(dialogView);

                TextView dialogTextView = dialogView.findViewById(R.id.top_ten_text);

                // Set the text of the dialog TextView to wrapped.getmTracks()
                dialogTextView.setText(selectedWrapped.getmSongs());


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

        textViewArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                LayoutInflater inflater = HistoryActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.topten, null); // Inflate your modal layout

                // Set the view to the dialog builder
                builder.setView(dialogView);

                TextView dialogTextView = dialogView.findViewById(R.id.top_ten_text);

                // Set the text of the dialog TextView to wrapped.getmTracks()
                dialogTextView.setText(selectedWrapped.getmArtists());


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

        textViewGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                LayoutInflater inflater = HistoryActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.topten, null); // Inflate your modal layout

                // Set the view to the dialog builder
                builder.setView(dialogView);

                TextView dialogTextView = dialogView.findViewById(R.id.top_ten_text);

                // Set the text of the dialog TextView to wrapped.getmTracks()
                dialogTextView.setText(selectedWrapped.getmGenres());


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

        new HistoryActivity.LoadImageTask(topTrackImage).execute(selectedWrapped.getTrackImage());
        new HistoryActivity.LoadImageTask(topArtistImage).execute(selectedWrapped.getArtistImage());
        new HistoryActivity.LoadImageTask(topGenreImage).execute(selectedWrapped.getGenreImage());
    }

    // Helper method to update UI with Wrapped list
    private void updateUIWithWrappedList(List<Wrapped> wrappedList) {
        this.wrappedList.clear();
        this.wrappedList.addAll(wrappedList);

        // Populate date Spinner after wrappedList is updated
        populateDateSpinner();
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

    private void showNoWrappedAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No saved wrapped data.")
                .setCancelable(false)
                .setPositiveButton("RETURN", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}