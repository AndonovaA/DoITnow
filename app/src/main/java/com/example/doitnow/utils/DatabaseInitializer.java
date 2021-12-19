package com.example.doitnow.utils;

import android.os.AsyncTask;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;
import java.util.List;


public class DatabaseInitializer {

    public static void addTodo(final AppDatabase db, List<TodoItem> todosList, TodoItem item) {
        db.todoDao().insert(item);
        todosList.add(db.todoDao().getLastOne());
    }


    public static void populateAsync(AppDatabase db, List<TodoItem> todosList, TodoItem item) {
        new PopulateAsync(db, todosList, item).execute();
    }

    private static class PopulateAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase db;
        List<TodoItem> todosList;
        TodoItem item;
        PopulateAsync(AppDatabase db, List<TodoItem> todosList, TodoItem item) {
            this.db = db;
            this.item = item;
            this.todosList = todosList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            addTodo(db, todosList, item);
            return null;
        }
    }
}
