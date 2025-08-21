package com.example.travelplanner;

public class FavoritePhoto {
    private String tripName;
    private String photoUri;
    private long id;
    private long timestamp;

    public FavoritePhoto() {} // برای Gson
    public FavoritePhoto(String tripName, String photoUri) {
        this.tripName = tripName;
        this.photoUri = photoUri;
        this.id = System.currentTimeMillis();
        this.timestamp = System.currentTimeMillis();
    }
    public long getId() { return id; }
    public String getTripName() { return tripName; }
    public String getPhotoUri() { return photoUri; }
    public long getTimestamp() { return timestamp; }
}
