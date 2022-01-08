package com.example.doitnow;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.databinding.ListTodosBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;
import java.util.ArrayList;
import java.util.List;

/**
 * This class refers to the FirstFragment.
 */
public class TodosList extends Fragment{

    private ListTodosBinding binding;
    private MainActivity mainActivity;
    TodosRecyclerAdapter adapter;
    List<TodoItem> todosList;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        //TODO: remove the action bar from this fragment:
        //create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat_DayNight_NoActionBar);
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        // inflate the layout using the cloned inflater, not default inflater
        binding = ListTodosBinding.inflate(localInflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setComponents();
        binding.addTodo.setOnClickListener(view1 -> NavHostFragment.findNavController(TodosList.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
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

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }
}