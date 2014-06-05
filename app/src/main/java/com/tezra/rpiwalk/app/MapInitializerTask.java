package com.tezra.rpiwalk.app;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapInitializerTask extends AsyncTask<Object, Void, InfoPacket>{

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
        return new GeoPoint(data.get("lat").getAsDouble(),data.get("lon").getAsDouble());
    }

    @Override
    public InfoPacket doInBackground(Object... params){
        try{
            GeoPoint start = getGeoPointFromLocation((String) params[0]);
            GeoPoint finish = getGeoPointFromLocation((String) params[1]);

            ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
            wp.add(start);
            wp.add(finish);

            RoadManager rm = new MapQuestRoadManager("Fmjtd%7Cluur2g6rn1%2C8w%3Do5-lra5q");
            rm.addRequestOption("routeType=pedestrian");
            Road r = rm.getRoad(wp);
            Polyline rOverlay = rm.buildRoadOverlay(r,(Context)params[3]);

            return new InfoPacket((MapView)params[2],rOverlay,start);
        } catch (Exception e){
            return null;
        }
    }

    public void onPostExecute(InfoPacket result){
        if(result != null) {
            MapView map = result.m;

            IMapController mCon = map.getController();
            mCon.setCenter(result.p);
            map.getOverlays().add(result.l);
            map.invalidate();
        }
    }
}
