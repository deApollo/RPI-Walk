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
import android.widget.Toast;

import com.tezra.rpiwalk.app.acts.DirectionsAct;
import com.tezra.rpiwalk.app.utils.Event;
import com.tezra.rpiwalk.app.acts.MainAct;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.utils.ParcelableGeoPoint;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EventLocationListener implements LocationListener {
    private Location curLoc;
    private NotificationManager m;
    private int timeDiffSeconds = 5;
    private HashMap<String,Double> tMap = new HashMap<String, Double>();
    private Context c;

    private double getTimeToEvent(Event event){
        if(tMap.containsKey(event.name))
            return tMap.get(event.name);
        else {
            try {
                ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
                wp.add(new GeoPoint(curLoc));
                wp.add(event.getLocation());
                double d = new RoadRetrieverTask().execute(wp).get().mDuration;
                tMap.put(event.name,d);
                return  d;
            } catch (Exception e) {
                return -1;
            }
        }
    }

    private String parseSeconds(int seconds){
        if(seconds > 60)
            return String.valueOf(seconds%60) + "m " + String.valueOf(seconds/60 - seconds%60*60) + "s";
        else
            return String.valueOf(seconds) + "s";
    }

    private void buildNotification(Event e,int leaveTime){
        try {

            ArrayList<ParcelableGeoPoint> pList = new ArrayList<ParcelableGeoPoint>();
            pList.add(new ParcelableGeoPoint(new LocationRetrieverTask().execute("My Location", c).get()));
            pList.add(new ParcelableGeoPoint(e.getLocation()));

            Intent i = new Intent(c,DirectionsAct.class);
            i.putExtra(MainAct.EXTRA_MSG,pList);
            PendingIntent pI = PendingIntent.getActivity(c,e.minute,i,PendingIntent.FLAG_UPDATE_CURRENT);

            Notification n = new Notification.Builder(c)
                    .setContentTitle(e.name)
                    .setContentText("Leave within " + parseSeconds(leaveTime) + " to arrive on time!")
                    .setContentIntent(pI)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setAutoCancel(true)
                    .build();

            m.notify(e.minute,n);

        } catch (Exception ex) {
            Log.i("Error:", "There was an error building the notification!");
        }
    }

    public void checkEvents(){
        for(Event e : MainAct.eventList) {
            Time t = new Time();
            t.setToNow();
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
            if(day > 0 && day <= 4 && e.days[day]) {
                double timeToEvent = getTimeToEvent(e);
                if(timeToEvent > -1) {
                    if ((e.getEventSeconds() - timeToEvent - timeDiffSeconds) <= (t.hour * 60 * 60 + t.minute * 60 + t.second) ) {
                        MainAct.generateToast(c,String.valueOf(timeToEvent), Toast.LENGTH_LONG);
                        int dif = (int) ((t.hour * 60 * 60 + t.minute * 60 + t.second) - (e.getEventSeconds() - timeToEvent - timeDiffSeconds));
                        buildNotification(e,dif);
                    }
                }
            }
        }
    }

    public EventLocationListener(Location curLocation, NotificationManager manager, Context con){
        curLoc = curLocation;
        m = manager;
        c = con;
        checkEvents();
    }

    @Override
    public void onLocationChanged(Location loc) {
        curLoc = loc;
        tMap.clear();
        checkEvents();
    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

    }
}
