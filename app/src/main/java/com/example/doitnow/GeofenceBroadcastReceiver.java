package com.example.doitnow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.doitnow.db.AppDatabase;
import com.example.doitnow.helpers.NotificationHelper;
import com.example.doitnow.models.TodoItem;
import com.example.doitnow.utils.DatabaseInitializer;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;



public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast:

        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "Receiver: RequestID: " + geofence.getRequestId());
        }

        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "Receiver: GEOFENCE_TRANSITION_ENTER");
                for (Geofence geofence: geofenceList) {
                    String geofenceID = geofence.getRequestId();
                    TodoItem todoItem = DatabaseInitializer.getTodo(AppDatabase.getAppDatabase(), geofenceID);
                    // this todoItem has valid ID
                    if (todoItem != null) {
                        Log.d(TAG, todoItem.getTitle());
                        notificationHelper.sendHighPriorityNotification(todoItem);
                    }
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d(TAG, "Receiver: GEOFENCE_TRANSITION_DWELL");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "Receiver: GEOFENCE_TRANSITION_EXIT");
                break;
        }

    }
}
