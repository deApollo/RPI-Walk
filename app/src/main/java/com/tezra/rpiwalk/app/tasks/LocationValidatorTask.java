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

    private String locationToUrlStr(String loc){
        loc = loc.replaceAll(" ","+");
        loc = "https://maps.googleapis.com/maps/api/geocode/json?address=" + loc + ",troy,ny&key=AIzaSyBLKaT1Lc3zGQ_ORvqWPtIxlWl06a0je6Q";
        return loc;
    }

    public Boolean doInBackground(Object... args){
        String str = (String)args[0];
        Context c = (Context) args[1];

        try {
            if(new DatabaseQueryTask().execute(str,c).get() != null)
                return true;
        } catch (Exception e) {
            Log.e("ERROR", "There was an error executing a database query task");
        }

        JsonObject raw = readJSONFromURL(locationToUrlStr(str)).getAsJsonObject();
        if(raw.get("results").getAsJsonArray().size() > 0)
            return true;
        else
            return null;
    }
}
