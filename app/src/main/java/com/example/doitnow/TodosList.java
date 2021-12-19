package com.example.doitnow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.databinding.ListTodosBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;
import java.util.ArrayList;
import java.util.List;


public class TodosList extends Fragment{

    private ListTodosBinding binding;
    TodosRecyclerAdapter adapter;
    List<TodoItem> todosList;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = ListTodosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        binding.addTodo.setOnClickListener(view1 -> NavHostFragment.findNavController(TodosList.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
    }

    private void init () {

        // Initializing recycler view adapter
        todosList = new ArrayList<>();

        //read from DB
        todosList.addAll(AppDatabase.getAppDatabase().todoDao().getAll());

        adapter = new TodosRecyclerAdapter(todosList);
        binding.recyclerView.setAdapter(adapter);

        setComponents();
    }

    private void setComponents() {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerView.getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation()
        );
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}