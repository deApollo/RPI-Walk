package com.tezra.rpiwalk.app.tasks;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.JsonObject;
import com.tezra.rpiwalk.app.acts.DirectionsAct;
import com.tezra.rpiwalk.app.acts.MainAct;
import com.tezra.rpiwalk.app.utils.Event;
import com.tezra.rpiwalk.app.R;
import java.util.Calendar;
import java.util.HashMap;

public class EventHandler {

    /*
    This class is initialized in the EventTrackerService class.
    The EventTrackerService class sets up a loop that calls checkEvents().
     */

    private Location curLoc; //The current location of the user
    private NotificationManager m; //A reference to the notification manager used to build notifications
    private int timeDiffSeconds; //How early in seconds the user wants to be to an event

    //A hashmap with the event name as the key and the time it takes to get to the event in seconds
    //this hashmap is flushed whenever the device detects a location change
    private HashMap<String,Double> timeToEventMap = new HashMap<String, Double>();

    //A hashmap with the event name as the key and boolean indicating whether or not a notification has been
    //shown to the user within the window where a notification should be shown.
    //This was implemented to stop the notification from being spammed during the window where it should be shown
    private HashMap<String,Boolean> notificationDisplayedMap = new HashMap<String, Boolean>();

    //A reference to the applications main context
    private Context c;

    //Function that returns the string to be used as the users location in the RouteRetrieverTask
    private String getLocString(){
        return curLoc.getLatitude() + "," + curLoc.getLongitude();
    }

    //Function that returns the time it takes to get to a specified event
    private double getTimeToEvent(Event event){
        //If the time to the event has already been found, return it from the time table
        if(timeToEventMap.containsKey(event.name))
            return timeToEventMap.get(event.name);
        else { //Look up the time to the event from the route
            try {
                //Retrieve the route data from the RouteRetrieverTask
                JsonObject data = new RouteRetrieverTask().execute(getLocString(),event.location,c,true).get();
                //Get the time taken during the route
                double d = data.getAsJsonObject("route").get("legs").getAsJsonArray().get(0).getAsJsonObject().get("time").getAsDouble();
                timeToEventMap.put(event.name,d); //Add the data to the time map
                return  d; //return the data
            } catch (Exception e) { //Something blew up :O
                return -1;
            }
        }
    }

    //A function that parses seconds into a more human readable form
    //This function is used when building the notification the user sees
    private String parseSeconds(int total_seconds){
        int minutes = total_seconds / 60;
        int seconds = total_seconds - minutes*60;
        if(seconds > 60)
            return minutes + "m " + seconds + "s";
        else
            return seconds + "s";
    }

    //Function to build the notification the user sees
    private void buildNotification(Event e,int leaveTime){
        try {
            //Retrieve the route Json from the RouteRetrieverTask in order to pass it to the map activity
            String s = new RouteRetrieverTask().execute(getLocString(),e.location,c).get().toString();

            //Set up the intent and pending intent so that the map activity is started when the notification is selected
            Intent i = new Intent(c,DirectionsAct.class);
            i.putExtra(MainAct.EXTRA_MSG,s);
            PendingIntent pI = PendingIntent.getActivity(c,e.minute,i,PendingIntent.FLAG_UPDATE_CURRENT);

            //Build the actual notification
            Notification n = new Notification.Builder(c)
                    .setContentTitle(e.name)
                    .setContentText("Leave within " + parseSeconds(leaveTime) + " to arrive on time!")
                    .setContentIntent(pI)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setAutoCancel(true)
                    .build();

            //Notify the user
            m.notify(e.minute,n);

        } catch (Exception ex) { //Something blew up :O
            Log.i("Error:", "There was an error building the notification!");
        }
    }

    //Function to get the number of seconds that have occurred in the specified day
    private int getDaySeconds(Time t) {
        return t.hour * 60 * 60 + t.minute * 60 + t.second;
    }

    //Function called to check whether to display the notification for each event
    public void checkEvents(){
        for(Event e : MainAct.eventList) {
            if(!notificationDisplayedMap.containsKey(e.name)) //If the event hasn't been added to the displayMap, add it
                notificationDisplayedMap.put(e.name,false);

            //Get the current time
            Time t = new Time();
            t.setToNow();

            //Get the current day of the week
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
            if(e.days[day]) {
                double timeToEvent = getTimeToEvent(e);
                double daySeconds = getDaySeconds(t);
                if(timeToEvent > -1) { //If the time to the event could be calculated and matches the set of criteria laid out below
                    if (daySeconds >= (e.getEventSeconds() - timeToEvent - timeDiffSeconds) & daySeconds < e.getEventSeconds() & !notificationDisplayedMap.get(e.name)) {
                        //build the notification and set it as displayed while its display window is open
                        int dif = (int) (daySeconds - Math.abs(e.getEventSeconds() - timeToEvent - timeDiffSeconds));
                        buildNotification(e,dif);
                        notificationDisplayedMap.put(e.name,true);
                    } else
                        notificationDisplayedMap.put(e.name,false);
                }
            }
        }
    }

    //When this is set up, initialize the variables
    public EventHandler(Location curLocation, NotificationManager manager, Context con, int early_seconds){
        curLoc = curLocation;
        m = manager;
        c = con;
        timeDiffSeconds = early_seconds;
        checkEvents();
    }

    //When the location gets changed, update the stored location and wipe the timeToEventMap map, then check all the events.
    public void checkEventsHandler(Location loc) {
        if(curLoc != loc) {
            curLoc = loc;
            timeToEventMap.clear();
        }
        checkEvents();
    }
}
