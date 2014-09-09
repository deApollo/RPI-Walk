package com.tezra.rpiwalk.app.tasks;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RouteRetrieverTask extends AsyncTask<Object, Void, JsonObject> {

    private Context c;

    private JsonElement readJSONFromURL(String url) {
        try {
            URL u = new URL(url);
            HttpsURLConnection req = (HttpsURLConnection) u.openConnection();
            req.connect();
            JsonParser jp = new JsonParser();
            return jp.parse(new InputStreamReader((InputStream) req.getContent()));
        } catch (Exception e){
            return null;
        }
    }

    private Location retrieveUserLocation(){
        LocationManager m = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); criteria.setAltitudeRequired(false); criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        return m.getLastKnownLocation(m.getBestProvider(criteria,true));
    }

    private String geocodeLocation(String loc) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + loc + ",troy,ny&key=AIzaSyBLKaT1Lc3zGQ_ORvqWPtIxlWl06a0je6Q";
        JsonElement j = readJSONFromURL(url);
        JsonObject locObj = j.getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
        return  locObj.get("lat").getAsString()+","+locObj.get("lng").getAsString();
    }

    private String parseUserInput(String s,Context c){
        if(s.equals("My Location")) {
            Location l = retrieveUserLocation();
            return String.valueOf(l.getLatitude()) + ","+ String.valueOf(l.getLongitude());
        } else {
            try {
                String locStr = new DatabaseQueryTask().execute(s, c).get();
                if (locStr != null)
                    return locStr;
                else
                    return geocodeLocation(s.replaceAll(" ", "+"));
            } catch (Exception e) {
                Log.e("ERROR", "There was an error executing a database query task");
                return geocodeLocation(s.replaceAll(" ","+"));
            }
        }
    }

    private String mQuestLocationsToUrlStr(String from, String to) {
        return "https://open.mapquestapi.com/directions/v2/route?key=Fmjtd%7Cluur2h6rl1%2C7s%3Do5-9w2w9a&from=" + from + "&to=" + to + "&routeType=pedestrian&outFormat=json&ambiguities=ignore&shapeFormat=raw&fullShape=true";
    }

    public JsonObject doInBackground(Object... params) {
        c = (Context) params[2];
        String start = parseUserInput((String) params[0],c);
        String end = parseUserInput((String) params[1],c);

        return readJSONFromURL(mQuestLocationsToUrlStr(start,end)).getAsJsonObject();
    }

}
