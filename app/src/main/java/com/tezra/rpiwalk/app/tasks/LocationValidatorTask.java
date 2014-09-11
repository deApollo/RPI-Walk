package com.tezra.rpiwalk.app.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LocationValidatorTask extends AsyncTask<Object, Void, Boolean> {

    /*
    This task validates whether a user entered location is actually a valid location.
    It uses the Google Geocoding API.
     */

    //Function to read a Google Gson JsonElement from a Https web connection
    private JsonElement readJSONFromURL(String url) {
        try {
            URL u = new URL(url);
            HttpsURLConnection req = (HttpsURLConnection) u.openConnection();
            req.connect();
            JsonParser jp = new JsonParser();
            return jp.parse(new InputStreamReader((InputStream) req.getContent()));
        } catch(Exception e){
            return null;
        }
    }

    //Function to assemble the request string for the Google Geocoding API
    private String locationToUrlStr(String loc){
        loc = loc.replaceAll(" ","+");
        loc = "https://maps.googleapis.com/maps/api/geocode/json?address=" + loc + ",troy,ny&key=AIzaSyBLKaT1Lc3zGQ_ORvqWPtIxlWl06a0je6Q";
        return loc;
    }

    //Function that is run when the task is started
    public Boolean doInBackground(Object... args){
        String str = (String)args[0];
        Context c = (Context) args[1];

        //If the location entered exists in the database, just return true
        try {
            if(new DatabaseQuery().doQuery(str,c) != null)
                return true;
        } catch (Exception e) {
            Log.e("ERROR", "There was an error executing a database query task");
        }

        //If the location was not found in the database, attempt to Geocode it
        JsonObject raw = readJSONFromURL(locationToUrlStr(str)).getAsJsonObject();
        if(raw.get("results").getAsJsonArray().size() > 0)
            return true;
        else
            return null;
    }
}
