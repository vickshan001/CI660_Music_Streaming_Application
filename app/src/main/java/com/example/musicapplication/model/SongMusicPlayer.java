package com.example.musicapplication.model;

public class SongMusicPlayer {
    private String album;
    private int albumId;
    private String albumart;
    private String artist;
    private String name;
    private String url;

    public SongMusicPlayer(String album, int albumId, String albumart, String artist, String name, String url) {
        this.album = album;
        this.albumId = albumId;
        this.albumart = albumart;
        this.artist = artist;
        this.name = name;
        this.url = url;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumart() {
        return albumart;
    }

    public void setAlbumart(String albumart) {
        this.albumart = albumart;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}