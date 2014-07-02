package com.tezra.rpiwalk.app.tasks;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.util.GeoPoint;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationRetrieverTask extends AsyncTask<Object, Void, GeoPoint> {

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

    public GeoPoint doInBackground(Object... args){
        String str = (String)args[0];
        Context c = (Context)args[1];
        if(!str.equals("My Location")) {
            JsonArray raw = readJSONFromURL(locationToUrlStr(str));
            if(raw.size() > 0) {
                JsonObject data = raw.get(0).getAsJsonObject();
                return new GeoPoint(data.get("lat").getAsDouble(), data.get("lon").getAsDouble());
            } else
                return null;
        } else {
            LocationManager m = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE); crit.setAltitudeRequired(false); crit.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            Location l = m.getLastKnownLocation(m.getBestProvider(crit,true));
            if(l != null)
                return new GeoPoint(l.getLatitude(),l.getLongitude());
            else
                return null;
        }
    }
}
