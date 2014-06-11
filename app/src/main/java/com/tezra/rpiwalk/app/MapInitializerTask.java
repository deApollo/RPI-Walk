package com.tezra.rpiwalk.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
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

    private GeoPoint getGeoPointFromLocation(String str, Context c) {
        if(!str.equals("My Location")) {
            JsonArray raw = readJSONFromURL(locationToUrlStr(str));
            JsonObject data = raw.get(0).getAsJsonObject();
            return new GeoPoint(data.get("lat").getAsDouble(), data.get("lon").getAsDouble());
        } else {
            LocationManager m = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE); crit.setAltitudeRequired(false); crit.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            Location l = m.getLastKnownLocation(m.getBestProvider(crit,true));
            return new GeoPoint(l.getLatitude(),l.getLongitude());
        }
    }

    private String getParsedRouteTimeString(int seconds){
        int minutes = seconds/60;
        seconds -= minutes*60;
        int hours = minutes/60;
        minutes -= hours*60;
        if(hours != 0)
            return  String.format("%dh %d m %s s", hours,minutes,seconds);
        else if(minutes != 0)
            return  String.format("%d m %s s", minutes,seconds);
        else
            return String.format("%s s", seconds);
    }

    private void populateNodes(Road r, MapView m){
        Drawable nodeIcon = m.getContext().getResources().getDrawable(R.drawable.marker_node);
        for(int i = 0; i < r.mNodes.size(); i++){
            RoadNode node = r.mNodes.get(i);
            Marker nodeM = new Marker(m);
            nodeM.setPosition(node.mLocation);
            if(i != 0 && i != r.mNodes.size()-1) {
                nodeM.setIcon(nodeIcon);
                nodeM.setTitle("Step " + i);
            } else if( i == 0) {
                nodeM.setTitle("Start");
                nodeM.setSubDescription("Route Time: " + getParsedRouteTimeString((int)r.mDuration));
            }
            else
                nodeM.setTitle("Finish");
            nodeM.setSnippet(node.mInstructions);
            m.getOverlays().add(nodeM);
        }
    }

    @Override
    public InfoPacket doInBackground(Object... params){
        try{
            GeoPoint start = getGeoPointFromLocation((String) params[0],(Context) params[3]);
            GeoPoint finish = getGeoPointFromLocation((String) params[1],(Context) params[3]);

            ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
            wp.add(start);
            wp.add(finish);

            RoadManager rm = new MapQuestRoadManager("Fmjtd%7Cluur2g6rn1%2C8w%3Do5-lra5q");
            rm.addRequestOption("routeType=pedestrian");

            Road r = rm.getRoad(wp);
            Polyline rOverlay = rm.buildRoadOverlay(r, (Context) params[3]);

            return new InfoPacket((MapView)params[2],rOverlay,start,r);
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
            populateNodes(result.r,map);
            map.invalidate();
        }
    }
}
