package com.example.doitnow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.databinding.ListTodosBinding;
import com.example.doitnow.databinding.TodoAddBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;
import com.example.doitnow.utils.DatabaseInitializer;
import java.util.ArrayList;
import java.util.List;



public class AddTodo extends Fragment {

    private TodoAddBinding binding;
    private ListTodosBinding todosBinding;
    TodosRecyclerAdapter adapter;
    List<TodoItem> todosList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = TodoAddBinding.inflate(inflater, container, false);
        todosBinding = ListTodosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setListeners();
    }

    private void init () {
        // Get the recycler view adapter
        todosList = new ArrayList<>();
        adapter = (TodosRecyclerAdapter) todosBinding.recyclerView.getAdapter();
    }

    private void setListeners () {
        binding.submitItem.setOnClickListener(view1 -> {
            List<Boolean> pass = new ArrayList<>();
            // input validation
            if (binding.editTextTitle.getText().toString().trim().length() == 0) {
                binding.editTextTitle.setError("Required");
                pass.add(false);
            }
            if (binding.editTextDescription.getText().toString().trim().length() == 0) {
                binding.editTextDescription.setError("Required");
                pass.add(false);
            }
            if(pass.size() == 0 ){
                // validation pass, create entity record
                String title = binding.editTextTitle.getText().toString().trim();
                String description = binding.editTextDescription.getText().toString().trim();

                //dodavanje na nov podatok za TodoItem i vo listata i vo bazata
                // TODO: change location
                DatabaseInitializer.populateAsync(AppDatabase.getAppDatabase(), todosList, new TodoItem(title, description, ""));

                //nazad kon first fragment
                NavHostFragment.findNavController(AddTodo.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}