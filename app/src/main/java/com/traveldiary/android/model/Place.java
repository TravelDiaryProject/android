package com.traveldiary.android.model;


public class Place {
    private int id;
    private String photo;
    private String latitude;
    private String longitude;
    private int tripId;
    private int likes;

    public boolean isLike = false;// state buttonLike in recyclerView
    public boolean isFuture = false;// state buttonAddTrip in recyclerView

    public Place(int id, String photo, String latitude, String longitude, int tripId, int likes) {
        this.id = id;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tripId = tripId;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", photo='" + photo + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", tripId=" + tripId +
                ", likes=" + likes +
                '}';
    }
}
