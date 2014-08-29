package com.tezra.rpiwalk.app.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RouteRetrieverTask extends AsyncTask<Object, Void, JsonObject> {

    private JsonElement readJSONFromURL(String url) {
        try {
            URL u = new URL(url);
            HttpsURLConnection req = (HttpsURLConnection) u.openConnection();
            req.connect();
            JsonParser jp = new JsonParser();
            return jp.parse(new InputStreamReader((InputStream) req.getContent()));
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String locationsToUrlStr(String from, String to){
        from = from.replace(" ","+"); to = to.replace(" ","+");
        String f = "https://maps.googleapis.com/maps/api/directions/json?origin=" + from + ",Troy,NY&destination=" + to + ",Troy,NY&mode=walking&key=AIzaSyBLKaT1Lc3zGQ_ORvqWPtIxlWl06a0je6Q";
        return f;
    }

    public JsonObject doInBackground(Object... params) {
        JsonObject e = readJSONFromURL(locationsToUrlStr((String)params[0],(String)params[1])).getAsJsonObject();
        return e;
    }

}
