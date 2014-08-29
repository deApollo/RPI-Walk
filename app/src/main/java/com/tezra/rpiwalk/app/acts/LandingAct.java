package com.tezra.rpiwalk.app.acts;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tezra.rpiwalk.app.tasks.RouteRetrieverTask;
import com.tezra.rpiwalk.app.utils.ParcelableGeoPoint;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.tasks.LocationRetrieverTask;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;


public class LandingAct extends Fragment {

    String [] locations = {"87 Gymnasium", "Academy Hall", "Admissions", "Alumni House", "Alumni Sports & Recreation Center", "Amos Eaton Hall", "Barton Hall", "Beman Park Firehouse",
    "Blaw-Knox 1 & 2", "Blitman Residence Commons", "Bray Hall", "Bryckwyck", "Burdett Avenue Residence Hall", "Carnegie Building","Cary Hall",
    "Center for Biotechnology and Interdisciplinary Studies", "Low Center for Industrial Innovation", "Chapel + Cultural Center", "Cogswell Laboratory", "Colonie Apartments",
    "Commons Dining Hall", "Crockett Hall", "Darrin Communications Center", "Davison Hall", "E Complex", "East Campus Athletic Village Arena", "East Campus Athletic Village Stadium",
    "Empire State Hall", "Experimental Media and Performing Arts Center", "Folsom Library", "Graduate Education", "Greene Building", "H Building", "Hall Hall", "Houston Field House",
    "J Building", "J. Erik Jonsson Engineering Center", "Moes Southwest Grill", "Jonsson-Rowland Science Center", "Lally Hall", "LINAC Facility", "Louis Rubin Memorial Approach",
    "Materials Research Center", "Mueller Center", "Nason Hall", "North Hall", "Nugent Hall", "Parking Garage", "Patroon Manor", "Pittsburgh Building", "Playhouse", "Polytechnic Residence Commons",
    "Public Safety", "Quadrangle Complex", "Rensselaer Union", "Ricketts Building", "Robison Swimming Pool", "RPI Ambulance", "Russel Sage Dining Hall", "Russel Sage Laboratory",
    "Sharp Hall", "Troy Building", "Voorhees Computing Center", "Walker Laboratory", "Warren Hall", "West Hall", "Winslow Building"};


    private boolean validateText(String i, String j){
        return !i.isEmpty() && !j.isEmpty();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_landing,container,false);

        AutoCompleteTextView start = (AutoCompleteTextView) v.findViewById(R.id.start);
        AutoCompleteTextView finish = (AutoCompleteTextView) v.findViewById(R.id.finish);
        ArrayAdapter adp = new ArrayAdapter(getActivity(),android.R.layout.select_dialog_item,locations);

        start.setThreshold(0);
        finish.setThreshold(0);
        start.setAdapter(adp);
        finish.setAdapter(adp);

        final Button b = (Button)v.findViewById(R.id.my_loc);

        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    b.setBackground(getResources().getDrawable(R.drawable.location_icon_clicked));
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    b.setBackground(getResources().getDrawable(R.drawable.location_icon));
                return false;
            }
        });

        return v;
    }

    public void findRoute(View view){
        try {
            String from = ((AutoCompleteTextView) getView().findViewById(R.id.start)).getText().toString();
            String to = ((AutoCompleteTextView) getView().findViewById(R.id.finish)).getText().toString();
            if (validateText(from, to)) {
                JsonObject route = new RouteRetrieverTask().execute(from,to).get();
                if (route.get("status").getAsString().equals("OK")) {
                    Intent dirInt = new Intent(getActivity(), DirectionsAct.class);
                    dirInt.putExtra(MainAct.EXTRA_MSG, route.toString());
                    startActivity(dirInt);
                } else
                    MainAct.generateToast(getActivity(), "Please make sure your entered locations are valid!", Toast.LENGTH_LONG);
            } else
                MainAct.generateToast(getActivity(), "Please enter a location!", Toast.LENGTH_LONG);
        } catch (Exception e){
            Log.i("Error:","There was an issue validating route locations",e.getCause());
        }
    }

    public void myLoc(View view){
        ((AutoCompleteTextView)getView().findViewById(R.id.start)).setText("My Location");
    }

}
