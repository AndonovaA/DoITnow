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
    @ColumnInfo(name = "todo_geofence_id")
    private String geofenceID;

    public TodoItem(String title, String description, String geofenceID) {
        this.title = title;
        this.description = description;
        this.geofenceID = geofenceID;
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

    public String getGeofenceID() {
        return geofenceID;
    }

    public void setGeofenceID(String geofenceID) {
        this.geofenceID = geofenceID;
    }
}