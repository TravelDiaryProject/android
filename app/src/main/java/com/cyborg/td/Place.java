package com.cyborg.td;

/**
 * Created by Cyborg on 1/29/2017.
 */

public class Place {
    private String title;
    private String photo;

    public Place(String title, String photo) {
        this.title = title;
        this.photo = photo;
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
        return "Place{" +
                "title='" + title + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}