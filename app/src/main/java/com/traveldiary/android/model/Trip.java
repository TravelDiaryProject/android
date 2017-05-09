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

    public Trip() {
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getIsFuture() {
        return isFuture;
    }

    public void setIsFuture(int isFuture) {
        this.isFuture = isFuture;
    }

    public int getIsMine() {
        return isMine;
    }

    public void setIsMine(int isMine) {
        this.isMine = isMine;
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
                '}';
    }
}