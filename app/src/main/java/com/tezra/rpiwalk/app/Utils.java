package com.tezra.rpiwalk.app;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class Utils {

    private static String readAll(Reader r) throws IOException {
        StringBuilder s = new StringBuilder();
        int cp;
        while((cp = r.read()) != -1)
            s.append((char)cp);
        return s.toString();
    }

    private static JSONObject readJSONFromURL(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jText = readAll(rd);
            JSONObject jOb = new JSONObject(jText);
            return jOb;
        } finally {
            is.close();
        }
    }

    private static String locationToUrlStr(String loc){
        loc = loc.replaceAll(" ","+");
        loc = "nominatim.openstreetmap.org/search?q=" + loc + ",+troy&format=json";
        return loc;
    }

    public static GeoPoint getPointFromLocationString(String str) throws IOException, JSONException {
        JSONObject data = readJSONFromURL(locationToUrlStr(str));
        return new GeoPoint(data.getDouble("lat"),data.getDouble("lon"));
    }
}
