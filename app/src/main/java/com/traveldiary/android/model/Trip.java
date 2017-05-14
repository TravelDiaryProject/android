package com.traveldiary.android.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Trip extends RealmObject {

    @PrimaryKey
    private int id;

    private String title;
    private String description;
    private String photo;
    private String thumbnail;
    private int isMine;
    private int isFuture;
    private String startDate;

    public Trip() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getIsMine() {
        return isMine;
    }

    public void setIsMine(int isMine) {
        this.isMine = isMine;
    }

    public int getIsFuture() {
        return isFuture;
    }

    public void setIsFuture(int isFuture) {
        this.isFuture = isFuture;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Trip)){
            return false;
        }
        Trip trip = (Trip) obj;
        return trip.id == id && trip.title.equals(title)
                && trip.description.equals(description);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", photo='" + photo + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", isMine=" + isMine +
                ", isFuture=" + isFuture +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}