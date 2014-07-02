package com.tezra.rpiwalk.app.tasks;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import com.tezra.rpiwalk.app.acts.DirectionsAct;
import com.tezra.rpiwalk.app.utils.Event;
import com.tezra.rpiwalk.app.acts.MainAct;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.utils.ParcelableGeoPoint;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EventTrackerTask implements LocationListener {
    private Location curLoc;
    private NotificationManager m;
    private int timeDiff = 5;
    private Context c;
    private Map<String,GeoPoint> locationCache = new HashMap<String, GeoPoint>();

    private GeoPoint getGeoPoint(String location) {
        if(locationCache.containsKey(location))
            return locationCache.get(location);
        else {
            try {
                GeoPoint p = new LocationRetrieverTask().execute(location, c).get();
                if(p != null) {
                    locationCache.put(location,p);
                }
                return p;
            } catch (Exception e){
                return null;
            }
        }
    }

    private double getTimeToEvent(String eLoc){
        try {
            ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
            wp.add(new GeoPoint(curLoc));
            wp.add(getGeoPoint(eLoc));
            return new RoadRetrieverTask().execute(wp).get().mDuration;
        } catch (Exception e){
            return 0;
        }
    }

    private void buildNotification(Event e){
        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(c)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle(e.name)
                            .setContentText("Leave within " + timeDiff + " seconds to arrive on time!")
                            .setAutoCancel(true);

            ArrayList<ParcelableGeoPoint> pList = new ArrayList<ParcelableGeoPoint>();
            pList.add(new ParcelableGeoPoint(new LocationRetrieverTask().execute("My Location", c).get()));
            pList.add(new ParcelableGeoPoint(locationCache.get(e.location)));

            Intent resultIntent = new Intent(c, DirectionsAct.class);
            resultIntent.putExtra(MainAct.EXTRA_MSG, pList);

            TaskStackBuilder sBuilder = TaskStackBuilder.create(c);
            sBuilder.addParentStack(DirectionsAct.class);
            sBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    sBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            m.notify(e.minute, mBuilder.build());
        } catch (Exception ex) {
            Log.i("Error:", "There was an error building the notification!");
        }
    }

    public void checkEvents(){
        for(Event e : MainAct.eventList) {
            Time t = new Time();
            t.setToNow();
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
            if(day > 0 && day < 4 && e.days[day]) {
                if (getTimeToEvent(e.location) + timeDiff > Math.abs((t.hour * 60 * 60 + t.minute * 60 + t.second) - e.getEventSeconds())) {
                    buildNotification(e);
                }
            }
        }
    }

    public EventTrackerTask(Location curLocation, NotificationManager manager, Context con){
        curLoc = curLocation;
        m = manager;
        c = con;
        checkEvents();
    }

    @Override
    public void onLocationChanged(Location loc) {
        curLoc = loc;
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
