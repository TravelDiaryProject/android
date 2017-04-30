package com.traveldiary.android.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Country extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;

    public Country() {
    }

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
