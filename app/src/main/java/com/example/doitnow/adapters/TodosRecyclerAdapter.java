package com.example.doitnow.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doitnow.databinding.TodoItemBinding;
import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.models.TodoItem;
import java.util.List;


public class TodosRecyclerAdapter extends RecyclerView.Adapter<TodosRecyclerAdapter.ViewHolder>{

    List<TodoItem> todosList;

    public TodosRecyclerAdapter(List<TodoItem> personList) {
        this.todosList = personList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(TodoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.textTitle.setText(todosList.get(position).getTitle());
        holder.binding.textSortDescription.setText(todosList.get(position).getDescription());
        holder.binding.textLocation.setText(todosList.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

    public void updateList(List<TodoItem> todoList) {
        this.todosList = todoList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TodoItemBinding binding;

        public ViewHolder(TodoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.getRoot().setOnClickListener(event -> {
                //OnClick listener za item od recycler view-ot na nivo na adapterot:
                AppDatabase.getAppDatabase().todoDao().delete(todosList.get(getAdapterPosition()));
                todosList.remove(getAdapterPosition());
                notifyDataSetChanged();
            });
        }
    }

}
