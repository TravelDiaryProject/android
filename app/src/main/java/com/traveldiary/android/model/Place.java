package com.traveldiary.android.model;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Place extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;

    private String title;
    private String photo;
    private String thumbnail;
    private String latitude;
    private String longitude;
    private String shootedAt;
    private int cityId;
    private String cityName;
    private int countryId;
    private String countryName;
    private int tripId;
    private int likes;
    private int isLiked;
    private int isInFutureTrips;
    private int isMine;

    public Place() {
    }

    public Place(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getShootedAt() {
        return shootedAt;
    }

    public void setShootedAt(String shootedAt) {
        this.shootedAt = shootedAt;
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

    public int getIsMine() {
        return isMine;
    }

    public void setIsMine(int isMine) {
        this.isMine = isMine;
    }


    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Place)){
            return false;
        }
        Place place = (Place) obj;
        return place.id == id && place.title.equals(title)
                && place.tripId == tripId
                && place.isLiked == isLiked
                && place.isInFutureTrips == isInFutureTrips
                && place.isMine == isMine;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", photo='" + photo + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", shootedAt='" + shootedAt + '\'' +
                ", cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", countryId=" + countryId +
                ", countryName='" + countryName + '\'' +
                ", tripId=" + tripId +
                ", likes=" + likes +
                ", isLiked=" + isLiked +
                ", isInFutureTrips=" + isInFutureTrips +
                ", isMine=" + isMine +
                '}';
    }
}
