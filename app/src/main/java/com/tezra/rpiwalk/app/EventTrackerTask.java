package com.tezra.rpiwalk.app;


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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EventTrackerTask implements LocationListener {
    private Location curLoc;
    private NotificationManager m;
    private int timeDiff = 5;
    private Context c;

    private JsonArray readJSONFromURL(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection req = (HttpURLConnection) u.openConnection();
            req.connect();
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent()));
            return root.getAsJsonArray();
        } catch(Exception e){
            return null;
        }
    }

    private String locationToUrlStr(String loc){
        loc = loc.replaceAll(" ","+");
        loc = "http://nominatim.openstreetmap.org/search?q=" + loc + ",+troy&format=json";
        return loc;
    }

    private GeoPoint getGeoPointFromLocation(String str) {
        JsonArray raw = readJSONFromURL(locationToUrlStr(str));
        JsonObject data = raw.get(0).getAsJsonObject();
        return new GeoPoint(data.get("lat").getAsDouble(), data.get("lon").getAsDouble());
    }

    private double getTimeToEvent(String eLoc){
        ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
        wp.add(new GeoPoint(curLoc));
        wp.add(getGeoPointFromLocation(eLoc));
        RoadManager rm = new MapQuestRoadManager("Fmjtd%7Cluur2g6rn1%2C8w%3Do5-lra5q");
        rm.addRequestOption("routeType=pedestrian");
        return rm.getRoad(wp).mDuration;
    }

    private void buildNotification(Event e){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(e.name)
                        .setContentText("Leave within " + timeDiff + " seconds to arrive on time!");
        Intent resultIntent = new Intent(c,DirectionsAct.class);
        resultIntent.putExtra(MainAct.EXTRA_MSG,new String [] {"My Location", e.location});
        TaskStackBuilder sBuilder = TaskStackBuilder.create(c);
        sBuilder.addParentStack(MainAct.class);
        sBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                sBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        m.notify(e.minute,mBuilder.build());
    }

    private void checkEvents(){
        for(Event e : MainAct.eventList) {
            Time t = new Time();
            t.setToNow();
            if(getTimeToEvent(e.location) + timeDiff > Math.abs((t.hour * 60 * 60 + t.minute * 60 + t.second) - e.getEventSeconds())) {
                buildNotification(e);
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
