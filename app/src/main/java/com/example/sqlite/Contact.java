package com.example.sqlite;

import android.graphics.Bitmap;

public class Contact {
    private int id;
    private String name;
    private Bitmap image;

    public Contact(int id, String name, Bitmap image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }
}
