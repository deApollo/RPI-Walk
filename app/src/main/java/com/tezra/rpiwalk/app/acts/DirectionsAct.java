package com.tezra.rpiwalk.app.acts;

import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        MapsInitializer.initialize(this);

        JsonObject j = (JsonObject) new JsonParser().parse(getIntent().getStringExtra(MainAct.EXTRA_MSG));
        JsonObject route = j.getAsJsonObject("route");
        JsonArray legs = route.getAsJsonArray("legs");
        JsonArray maneuvers = legs.get(0).getAsJsonObject().get("maneuvers").getAsJsonArray();

        PolylineOptions p = new PolylineOptions();
        p.color(Color.BLUE);
        JsonObject shape = route.getAsJsonObject("shape");
        JsonArray shapePoints = shape.getAsJsonArray("shapePoints");
        for(int i = 0; i < shapePoints.size()-1; i+=2)
            p.add(new LatLng(shapePoints.get(i).getAsDouble(),shapePoints.get(i+1).getAsDouble()));

        int stepNum = 1;
        for(JsonElement step : maneuvers) {
            JsonObject currentStep = step.getAsJsonObject();
            String narrative = currentStep.get("narrative").getAsString();
            JsonObject point = currentStep.getAsJsonObject("startPoint");
            LatLng startPoint = new LatLng(point.get("lat").getAsDouble(),point.get("lng").getAsDouble());
            MarkerOptions pointOptions = new MarkerOptions();
            if(stepNum == 1) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 18));
                pointOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_icon));
            }  else if(stepNum == maneuvers.size())
                pointOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_icon));
            else
                pointOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.way_point_icon));
            pointOptions.position(startPoint); pointOptions.title("Step " + stepNum); pointOptions.snippet(narrative);

            map.addMarker(pointOptions);

            stepNum++;
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
