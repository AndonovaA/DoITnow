package com.example.doitnow.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "todoItem")
public class TodoItem {

    @PrimaryKey(autoGenerate = true)
    private int ID;
    @ColumnInfo(name = "todo_title")
    private String title;
    @ColumnInfo(name = "todo_body")
    private String description;
    @ColumnInfo(name = "todo_latitude")
    private String locationLat;
    @ColumnInfo(name = "todo_longitude")
    private String locationLng;

    public TodoItem(String title, String description, String locationLat, String locationLng) {
        this.title = title;
        this.description = description;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
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

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(String locationLng) {
        this.locationLng = locationLng;
    }
}