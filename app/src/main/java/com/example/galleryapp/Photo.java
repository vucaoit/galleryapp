package com.example.galleryapp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo {
    String id;
    String path;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    String format = formatter.format(new Date());
    String date=format;
    public Photo() {
        this.id = null;
        this.path = null;
    }
    public Photo(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", date=" + date +
                '}';
    }
}
