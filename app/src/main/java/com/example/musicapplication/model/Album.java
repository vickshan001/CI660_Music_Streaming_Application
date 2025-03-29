package com.example.musicapplication.model;

public class Album {
    private String albumname;
    private String albumart;
    private int albumId;
    private String artist;

    public String getAlbumName() {
        return albumname;
    }

    public void setAlbumName(String albumName) {
        this.albumname = albumName;
    }

    public String getAlbumart() {
        return albumart;
    }

    public void setAlbumart(String albumart) {
        this.albumart = albumart;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
