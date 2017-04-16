package com.traveldiary.android.model;


import java.io.Serializable;

public class Place implements Serializable {
    private int id;
    private String photo;
    private String latitude;
    private String longitude;
    private int cityId;
    private int countryId;
    private int tripId;
    private int likes;
    private int isLiked;
    private int isInFutureTrips;

    public Place(int id, String photo, String latitude, String longitude, int cityId, int countryId, int tripId, int likes, int isLiked, int isInFutureTrips) {
        this.id = id;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityId = cityId;
        this.countryId = countryId;
        this.tripId = tripId;
        this.likes = likes;
        this.isLiked = isLiked;
        this.isInFutureTrips = isInFutureTrips;
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

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public int getIsInFutureTrips() {
        return isInFutureTrips;
    }

    public void setIsInFutureTrips(int isInFutureTrips) {
        this.isInFutureTrips = isInFutureTrips;
    }


    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", photo='" + photo + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", cityId=" + cityId +
                ", countryId=" + countryId +
                ", tripId=" + tripId +
                ", likes=" + likes +
                ", isLiked=" + isLiked +
                ", isInFutureTrips=" + isInFutureTrips +
                '}';
    }
}
