package com.traveldiary.android.model;


public class Place {
    private int id;
    private String photo;
    private String latitude;
    private String longitude;

    public boolean selectedLike = false;// state buttonLike in recyclerView
    public boolean selectedAddTrip = false;// state buttonAddTrip in recyclerView

    public Place(int id, String photo, String latitude, String longitude) {
        this.id = id;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", photo='" + photo + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
