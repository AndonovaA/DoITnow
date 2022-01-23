package com.example.doitnow.adapters;

import android.app.AlertDialog;
import android.content.Context;
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
    private Context activity;

    public TodosRecyclerAdapter(List<TodoItem> todosList, Context activity) {
        this.todosList = todosList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(TodoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.textTitle.setText(todosList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

    public Context getContext(){
        return activity;
    }

    public void updateList(List<TodoItem> todoList) {
        this.todosList = todoList;
        notifyDataSetChanged();
    }

    public TodoItem getTodoItem(int position){
        return todosList.get(position);
    }

    public void deleteItem(int position){
        TodoItem item = todosList.get(position);
        // delete the item from the database
        AppDatabase.getAppDatabase().todoDao().delete(item);
        todosList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TodoItemBinding binding;

        public ViewHolder(TodoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(view -> {
                TodoItem todoItem = todosList.get(getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(todoItem.getTitle());
                builder.setMessage(todoItem.getDescription());
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            });
        }
    }
}
