package com.tezra.rpiwalk.app.acts;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tezra.rpiwalk.app.R;

import java.util.ArrayList;
import java.util.List;


public class DirectionsAct extends ActionBarActivity {

    GoogleMap map;

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        MapsInitializer.initialize(this);

        JsonObject j = (JsonObject) new JsonParser().parse(getIntent().getStringExtra(MainAct.EXTRA_MSG));
        JsonArray routes = j.getAsJsonArray("routes");
        JsonObject route = routes.get(0).getAsJsonObject();
        JsonObject mainLeg = route.get("legs").getAsJsonArray().get(0).getAsJsonObject();
        JsonArray steps = mainLeg.get("steps").getAsJsonArray();

        int stepNum = 1;
        for(JsonElement step : steps) {
            JsonObject jObjStep = step.getAsJsonObject();
            JsonObject point = jObjStep.get("start_location").getAsJsonObject();
            LatLng start = new LatLng(point.get("lat").getAsDouble(),point.get("lng").getAsDouble());
            if(stepNum == 1)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15));
            MarkerOptions m = new MarkerOptions();
            m.position(start); m.title("Step " + stepNum); m.snippet(Html.fromHtml(jObjStep.get("html_instructions").getAsString()).toString());
            map.addMarker(m);
            stepNum++;
        }

        List<LatLng> lis = decodePoly(route.get("overview_polyline").getAsString());
        PolylineOptions p = new PolylineOptions();
        for(LatLng l : lis) {
            p.add(l);
        }

        map.addPolyline(p);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.directions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
