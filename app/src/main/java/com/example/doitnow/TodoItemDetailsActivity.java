package com.example.doitnow;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doitnow.databinding.ActivityTodoItemDetailsBinding;
import com.example.doitnow.models.TodoItem;


public class TodoItemDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TodoItemDetailsActivity";

    private ActivityTodoItemDetailsBinding binding;
    private String todoTitle = null;
    private String todoDescription = null;
    private String todoGeofenceID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityTodoItemDetailsBinding.inflate(getLayoutInflater());

        Bundle notificationExtras = getIntent().getExtras();
        todoTitle = notificationExtras.getString("TodoTitle");
        todoDescription = notificationExtras.getString("TodoDescription");
        todoGeofenceID = notificationExtras.getString("TodoGeofenceId");

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
                // TODO: change todo details
                // take the values for title and description from the view
                TodoItem todoItem = new TodoItem(todoTitle, todoDescription, todoGeofenceID);
                // update the record in the database with todoItem
            }
        });
    }


    // TODO: on back pressed to go to MainActivity
}