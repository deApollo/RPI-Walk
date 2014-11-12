package com.tezra.rpiwalk.app.acts;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.utils.MapSnippetAdapter;


public class DirectionsAct extends ActionBarActivity {

    /*
    This Activity handles initializing the map and map UI elements.
    It is either accessed through the "find route" button from the LandingFragment fragment or started by the EventHandler
     */

    GoogleMap map;

    private String parseTime(String formattedTime) {
        String [] components = formattedTime.split(":");
        if(components[0].equals("00"))
            if(components[1].equals("00"))
                return components[2] + "s";
            else
                return components [1] + "m" + components[2] + "s";
        else
            return components[0] + "h" + components[1] + "m" + components[2] + "s";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        //Get the GoogleMap object from the map fragment
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setIndoorEnabled(true);
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        map.setInfoWindowAdapter(new MapSnippetAdapter(getLayoutInflater()));

        //Get the Json data for the route from the supplied intent
        JsonObject j = (JsonObject) new JsonParser().parse(getIntent().getStringExtra(MainAct.EXTRA_MSG));
        JsonObject route = j.getAsJsonObject("route");
        JsonArray legs = route.getAsJsonArray("legs");
        JsonArray maneuvers = legs.get(0).getAsJsonObject().get("maneuvers").getAsJsonArray();

        //Set up the PolylineOptions object from the points supplied in the Jsondata
        PolylineOptions p = new PolylineOptions();
        p.color(Color.BLUE);
        JsonObject shape = route.getAsJsonObject("shape");
        JsonArray shapePoints = shape.getAsJsonArray("shapePoints");
        for(int i = 0; i < shapePoints.size()-1; i+=2)
            p.add(new LatLng(shapePoints.get(i).getAsDouble(),shapePoints.get(i+1).getAsDouble()));

        //Set up the map markers and marker text from the Json data
        int stepNum = 1;
        for(JsonElement step : maneuvers) {
            //Parse the json data
            JsonObject currentStep = step.getAsJsonObject();
            String narrative = currentStep.get("narrative").getAsString();
            narrative = narrative.replaceAll("\\(See map for details\\)","");
            JsonObject point = currentStep.getAsJsonObject("startPoint");
            LatLng startPoint = new LatLng(point.get("lat").getAsDouble(),point.get("lng").getAsDouble());

            String titleStr = "Step " + stepNum;

            //Set up a MarkerOptions object to represent the various points on the route
            MarkerOptions pointOptions = new MarkerOptions();
            if(stepNum == 1) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 18)); //Set the camera to view the start point
                pointOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_icon));
                String time = parseTime(route.get("formattedTime").getAsString());
                narrative += "splithere909"+time;
            }  else if(stepNum == maneuvers.size()) {
                pointOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_icon));
                narrative = "Arrive at destination!";
                titleStr = "Final Step";
            }
            else
                pointOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.way_point_icon));

            pointOptions.position(startPoint);
            pointOptions.title(titleStr);
            pointOptions.snippet(narrative);

            map.addMarker(pointOptions); //Once the mark is finished, add it to the map

            stepNum++;
        }

        map.addPolyline(p); //Add the polyline to the map
    }


    //Handle the options menu list since this is its own activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.directions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
