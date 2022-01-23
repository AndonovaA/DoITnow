package com.example.doitnow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doitnow.databinding.ActivityTodoItemDetailsBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;


public class TodoItemDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TodoItemDetailsActivity";

    private ActivityTodoItemDetailsBinding binding;
    private int todoID = -1;
    private String todoTitle = "";
    private String todoDescription = "";
    private String todoGeofenceID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityTodoItemDetailsBinding.inflate(getLayoutInflater());

        Bundle notificationExtras = getIntent().getExtras();
        if (notificationExtras != null) {
            todoID = notificationExtras.getInt("TodoID");
            todoTitle = notificationExtras.getString("TodoTitle");
            todoDescription = notificationExtras.getString("TodoDescription");
            todoGeofenceID = notificationExtras.getString("TodoGeofenceId");
        }

        setComponents();
        setListeners();

        setContentView(binding.getRoot());
    }

    private void setComponents() {
        binding.editTitle.setText(todoTitle);
        binding.editDescription.setText(todoDescription);
    }

    private void setListeners() {
        binding.updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todoID != -1 && binding.editTitle.getText() != null && binding.editDescription.getText() != null) {
                    // updating allowed only for the title and the description
                    todoTitle = binding.editTitle.getText().toString();
                    todoDescription = binding.editDescription.getText().toString();

                    TodoItem todoItem = new TodoItem(todoTitle, todoDescription, todoGeofenceID);
                    todoItem.setID(todoID);
                    AppDatabase.getAppDatabase().todoDao().update(todoItem);
                    Log.d(TAG, "Item successfully updated!");
                }
                else {
                    Log.d(TAG, "Failed to update the item!!!");
                }
                // back to MainActivity
                Intent returnIntent = new Intent(TodoItemDetailsActivity.this, MainActivity.class);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                TodoItemDetailsActivity.this.startActivity(returnIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        Intent setIntent = new Intent();
        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setIntent.setClass(this, MainActivity.class);
        startActivity(setIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        Log.d(TAG, "onDestroy");
    }
}