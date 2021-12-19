package com.example.doitnow.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.doitnow.models.TodoItem;
import java.util.List;


//za definiranje na site CRUD operacii vrz tabelata todoItem:
//DAO - Data Access Object
@Dao
public interface TodoDao {

    @Query("SELECT * from todoItem order by id desc")
    List<TodoItem> getAll();

    @Query("SELECT * from todoItem order by id desc limit 1")
    TodoItem getLastOne();

    @Query("select COUNT(*) from todoItem")
    int countNoPersons();

    @Insert
    void insert(TodoItem item);

    @Insert
    void insertAll(TodoItem... item);

    @Update
    void updatePerson(TodoItem item);

    @Delete
    void delete(TodoItem item);

    @Query("delete from todoItem")
    void deleteAll();

}
