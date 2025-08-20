package com.example.travelplanner;

public class FavoritePhoto {
    private String tripName;
    private String photoUri;

    public FavoritePhoto() {} // برای Gson
    public FavoritePhoto(String tripName, String photoUri) {
        this.tripName = tripName;
        this.photoUri = photoUri;
    }
    public String getTripName() { return tripName; }
    public String getPhotoUri() { return photoUri; }
}
