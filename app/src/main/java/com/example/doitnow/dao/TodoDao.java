package com.example.doitnow.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.doitnow.models.TodoItem;

import java.util.List;


/***
 * DAO - Data Access Object
 * For defining CRUD operations on the table todoItem:
 */

@Dao
public interface TodoDao {

    @Query("SELECT * from todoItem order by id desc")
    List<TodoItem> getAll();

    @Query("SELECT * from todoItem order by id desc limit 1")
    TodoItem getLastOne();

    @Insert
    void insert(TodoItem item);

    @Insert
    void insertAll(TodoItem... item);

    @Delete
    void delete(TodoItem item);

    @Query("delete from todoItem")
    void deleteAll();
}
