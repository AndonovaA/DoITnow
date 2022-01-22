package com.example.doitnow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.databinding.ActivityMainBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.helpers.RecyclerItemTouchHelper;
import com.example.doitnow.models.TodoItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    TodosRecyclerAdapter adapter;
    List<TodoItem> todosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setComponents();
        binding.addTodo.setOnClickListener(view -> {
            Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
            MainActivity.this.startActivity(myIntent);
        });
    }

    private void init () {

        // Initializing recycler view adapter
        todosList = new ArrayList<>();

        //read from DB
        todosList.addAll(AppDatabase.getAppDatabase().todoDao().getAll());

        adapter = new TodosRecyclerAdapter(todosList, binding.getRoot().getContext());
        binding.recyclerView.setAdapter(adapter);

        // Swipe left/right for edit/delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        // if there are todos items hide the text
        if(todosList.size() > 0) {
            binding.noTodosText.setVisibility(View.GONE);
        }
    }

    private void setComponents() {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerView.getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation()
        );
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}