package com.example.doitnow.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doitnow.App;
import com.example.doitnow.R;
import com.example.doitnow.TodoItemDetailsActivity;
import com.example.doitnow.adapters.TodosRecyclerAdapter;
import com.example.doitnow.models.TodoItem;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


/**
 * Delete and Edit action by swiping one task item.
 */
public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TodosRecyclerAdapter adapter;
    private GeofencingClient geofencingClient;

    public RecyclerItemTouchHelper(TodosRecyclerAdapter adapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.geofencingClient = LocationServices.getGeofencingClient(App.mContext);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction){
        final int position = viewHolder.getAdapterPosition();

        if(direction == ItemTouchHelper.LEFT){
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure you want to delete this Task?");

            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // remove the geofence first:
                    TodoItem todoItem = adapter.getTodoItem(position);
                    String geofenceId = todoItem.getGeofenceID();
                    ArrayList<String> geofenceIDs = new ArrayList<String>() {{ add(geofenceId); }};

                    Task<Void> removeGeofenceTask = geofencingClient.removeGeofences(geofenceIDs);
                    removeGeofenceTask.addOnSuccessListener(unused ->
                            Log.d("RecyclerItemTouchHelper", "Successfully deleted geofence."));
                    removeGeofenceTask.addOnFailureListener(e ->
                            Log.d("RecyclerItemTouchHelper", "Failed to delete geofence."));

                    // remove the item from the recycler view
                    adapter.deleteItem(position);   // this will also delete the item from the DB
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            // Open TodoItemDetailsActivity for UPDATING/EDITING the todoItem
            TodoItem todoItem = adapter.getTodoItem(position);

            Log.d("RecyclerTouchHelper", String.valueOf(todoItem.getID()));

            Intent detailsTodoActivityIntent = new Intent();
            detailsTodoActivityIntent.putExtra("TodoID", todoItem.getID());
            detailsTodoActivityIntent.putExtra("TodoTitle", todoItem.getTitle());
            detailsTodoActivityIntent.putExtra("TodoDescription", todoItem.getDescription());
            detailsTodoActivityIntent.putExtra("TodoGeofenceId", todoItem.getGeofenceID());
            detailsTodoActivityIntent.setClass(this.adapter.getContext(), TodoItemDetailsActivity.class);
            this.adapter.getContext().startActivity(detailsTodoActivityIntent);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if(dX>0){
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
            background = new ColorDrawable(ContextCompat.getColor((adapter.getContext()), R.color.design_default_color_primary_dark));
        }
        else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
            background = new ColorDrawable(Color.RED);
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if(dX > 0){ // swipe to Right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int)dX) + backgroundCornerOffset,
                    itemView.getBottom());
        }
        else if(dX < 0){ //swipe to left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int)dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        }
        else {
            background.setBounds(0, 0 , 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
