package com.example.spotify_sdk;

public class Wrapped implements Comparable<Wrapped> {

    private long mDate;

    public Wrapped(long mDate, String mSongs, String mArtists, String mGenres, String trackImage, String artistImage, String genreImage, String song1Url, String song2Url, String song3Url) {
        this.mDate = mDate;
        this.mSongs = mSongs;
        this.mArtists = mArtists;
        this.mGenres = mGenres;
        this.trackImage = trackImage;
        this.artistImage = artistImage;
        this.genreImage = genreImage;
        this.song1Url = song1Url;
        this.song2Url = song2Url;
        this.song3Url = song3Url;
    }

    private String mSongs;
    private String mArtists;
    private String mGenres;

    private String trackImage;

    private String artistImage;

    private String genreImage;

    private String song1Url;
    private String song2Url;
    private String song3Url;



    public Wrapped() {
        this.mDate = System.currentTimeMillis();
        this.mSongs = "";
        this.mArtists = "";
        this.mGenres = "";
        this.trackImage = "";
        this.artistImage = "";
        this.genreImage = "";
        this.song1Url = "";
        this.song2Url = "";
        this.song3Url = "";
    }


    public Wrapped(String mSongs, String mArtists, String genres) {
        this.mDate = System.currentTimeMillis();
        this.mSongs = mSongs;
        this.mArtists = mArtists;
        this.mGenres = genres;
    }

    public Wrapped(long date, String mSongs, String mArtists, String genres) {
        this.mDate = date;
        this.mSongs = mSongs;
        this.mArtists = mArtists;
        this.mGenres = genres;
    }

    public String getSong1Url() {
        return song1Url;
    }

    public void setSong1Url(String song1Url) {
        this.song1Url = song1Url;
    }

    public String getSong2Url() {
        return song2Url;
    }

    public void setSong2Url(String song2Url) {
        this.song2Url = song2Url;
    }

    public String getSong3Url() {
        return song3Url;
    }

    public void setSong3Url(String song3Url) {
        this.song3Url = song3Url;
    }

    public long getmDate() {
        return mDate;
    }

    public String getmSongs() {
        return mSongs;
    }

    public String getmArtists() {
        return mArtists;
    }

    public String getmGenres() {
        return mGenres;
    }

    public String getTrackImage() {
        return trackImage;
    }

    public String getArtistImage() {
        return artistImage;
    }

    public String getGenreImage() {
        return genreImage;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public void setmSongs(String mSongs) {
        this.mSongs = mSongs;
    }

    public void setmArtists(String mArtists) {
        this.mArtists = mArtists;
    }

    public void setmGenres(String mGenres) {
        this.mGenres = mGenres;
    }

    public void setTrackImage(String trackImage) {
        this.trackImage = trackImage;
    }

    public void setArtistImage(String artistImage) {
        this.artistImage = artistImage;
    }

    public void setGenreImage(String genreImage) {
        this.genreImage = genreImage;
    }

    @Override
    public int compareTo(Wrapped o) {
        return - (int)( (this.mDate - o.mDate));
    }
}
