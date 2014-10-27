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

    /*
    This task returns a Google Gson JsonObject containing the data for the route with the start and endpoints specified.
    The route is calculated by the MapQuest Open Directions API.
     */

    private Context c;
    private boolean userLocationSupplied;

    //Function that reads Json from a Https web connection
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

    //Function that uses the LocationManager to retrieve the user's location
    private Location retrieveUserLocation(){
        LocationManager m = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        return m.getLastKnownLocation(m.getBestProvider(criteria,true));
    }

    //Function that uses the Google Maps Geocoding API to turn a string into a LatLng pair
    private String geocodeLocation(String loc) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + loc + ",troy,ny&key=AIzaSyBLKaT1Lc3zGQ_ORvqWPtIxlWl06a0je6Q";
        JsonElement j = readJSONFromURL(url);
        JsonObject locObj = j.getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
        return  locObj.get("lat").getAsString()+","+locObj.get("lng").getAsString();
    }

    //Function to handle the input location strings
    private String handleInput(String s, Context c){
        if(s.equals("My Location")) { //If the user is using their location, retrieve it
            Location userLoc = retrieveUserLocation();
            return userLoc.getLatitude() + "," + userLoc.getLongitude();
        } else {
            if(userLocationSupplied) {
                userLocationSupplied = false;
                return s;
            }
            else {
                try {
                    //Otherwise, first attempt to look the location up in the database
                    //If the lookup fails, just geocode the location
                    String locStr = new DatabaseQuery(c).doQuery(s);
                    if (locStr != null)
                        return locStr;
                    else
                        return geocodeLocation(s.replaceAll(" ", "+"));
                } catch (Exception e) {
                    Log.e("ERROR", "There was an error executing a database query task");
                    return geocodeLocation(s.replaceAll(" ", "+"));
                }
            }
        }
    }

    //Function to build and return the final MapQuest API call string
    private String mQuestLocationsToUrlStr(String from, String to) {
        return "https://open.mapquestapi.com/directions/v2/route?key=Fmjtd%7Cluur2h6rl1%2C7s%3Do5-9w2w9a&from=" + from + "&to=" + to + "&routeType=pedestrian&outFormat=json&ambiguities=ignore&shapeFormat=raw&fullShape=true";
    }

    //Function that runs when the task is initiated
    public JsonObject doInBackground(Object... params) {
        userLocationSupplied = params.length > 3;
        c = (Context) params[2];
        String start = handleInput((String) params[0], c);
        String end = handleInput((String) params[1], c);

        return readJSONFromURL(mQuestLocationsToUrlStr(start,end)).getAsJsonObject();
    }

}
