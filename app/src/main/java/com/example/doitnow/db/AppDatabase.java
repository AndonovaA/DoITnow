package com.example.doitnow.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.doitnow.App;
import com.example.doitnow.dao.TodoDao;
import com.example.doitnow.models.TodoItem;


@Database(entities = {TodoItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TodoDao todoDao();
    private static AppDatabase INSTANCE; //instanca od bazata

    public static AppDatabase getAppDatabase() {
        if (INSTANCE == null) {
            //kreirame instanca od bazata
            INSTANCE = Room.databaseBuilder(App.mContext, AppDatabase.class, "doITnow")
                    .allowMainThreadQueries()
                    .build();
        } //bazata ke se vika "doITnow"

        return  INSTANCE;
    }

    public static void destroyDB(){
        INSTANCE = null;
    } //unistuvanje na instancata od bazata

}
