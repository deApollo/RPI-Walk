package com.tezra.rpiwalk.app.acts;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tezra.rpiwalk.app.tasks.RouteRetrieverTask;
import com.tezra.rpiwalk.app.R;


public class LandingFragment extends Fragment {

    /*
    This fragment is shown to the user when the app starts up.
    It handles user input relating to direction queries.
     */

    //Array of strings used in the AutoCompleteTextViews to aid in typing long campus location names
    String [] locations = {"87 Gymnasium", "Academy Hall", "Admissions", "Alumni House", "Alumni Sports & Recreation Center", "Amos Eaton Hall", "Barton Hall", "Beman Park Firehouse",
    "Blaw-Knox 1 & 2", "Blitman Residence Commons", "Bray Hall", "Bryckwyck", "Burdett Avenue Residence Hall", "Carnegie Building","Cary Hall",
    "Center for Biotechnology and Interdisciplinary Studies", "Low Center for Industrial Innovation", "Chapel + Cultural Center", "Cogswell Laboratory", "Colonie Apartments",
    "Commons Dining Hall", "Crockett Hall", "Darrin Communications Center", "Davison Hall", "E Complex", "East Campus Athletic Village Arena", "East Campus Athletic Village Stadium",
    "Empire State Hall", "Experimental Media and Performing Arts Center", "Folsom Library", "Graduate Education", "Greene Building", "H Building", "Hall Hall", "Houston Field House",
    "J Building", "J. Erik Jonsson Engineering Center", "Moes Southwest Grill", "Jonsson-Rowland Science Center", "Lally Hall", "LINAC Facility", "Louis Rubin Memorial Approach",
    "Materials Research Center", "Mueller Center", "Nason Hall", "North Hall", "Nugent Hall", "Parking Garage", "Patroon Manor", "Pittsburgh Building", "Playhouse", "Polytechnic Residence Commons",
    "Public Safety", "Quadrangle Complex", "Rensselaer Union", "Ricketts Building", "Robison Swimming Pool", "RPI Ambulance", "Russel Sage Dining Hall", "Russel Sage Laboratory",
    "Sharp Hall", "Troy Building", "Voorhees Computing Center", "Walker Laboratory", "Warren Hall", "West Hall", "Winslow Building"};


    //Function to make sure the two supplied strings are not empty
    private boolean validateText(String i, String j){
        return !i.isEmpty() && !j.isEmpty();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_landing,container,false);

        //Find the AutoCompleteTextViews and set up the adapter controlling them
        AutoCompleteTextView start = (AutoCompleteTextView) v.findViewById(R.id.start);
        AutoCompleteTextView finish = (AutoCompleteTextView) v.findViewById(R.id.finish);
        ArrayAdapter adp = new ArrayAdapter(getActivity(),android.R.layout.select_dialog_item,locations);

        start.setThreshold(0);
        finish.setThreshold(0);
        start.setAdapter(adp);
        finish.setAdapter(adp);


        //Set up the art on the "my location" button to switch when clicked on
        final Button b = (Button)v.findViewById(R.id.my_loc);

        /*
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
        */

        return v;
    }

    //Function called when the "Find Route" button is pressed
    public void findRoute(View view){
        try {
            //Get the from and to parts of user input
            String from = ((AutoCompleteTextView) getView().findViewById(R.id.start)).getText().toString();
            String to = ((AutoCompleteTextView) getView().findViewById(R.id.finish)).getText().toString();
            if (validateText(from, to)) { //If they aren't empty
                //Retrieve the JsonObject containing the route data
                JsonObject route = new RouteRetrieverTask().execute(from,to,getActivity().getApplicationContext()).get();
                //route.get("info").getAsJsonObject().get("statuscode").getAsString().equals("0")
                if (route != null) { //If the locations entered are valid and the route exists
                    //Start the map activity by passing in the route JsonObject
                    Intent dirInt = new Intent(getActivity(), DirectionsAct.class);
                    dirInt.putExtra(MainAct.EXTRA_MSG, route.toString());
                    startActivity(dirInt);
                } else
                    MainAct.generateToast(getActivity(), "Please make sure your entered locations are valid!", Toast.LENGTH_LONG);
            } else
                MainAct.generateToast(getActivity(), "Please enter a location!", Toast.LENGTH_LONG);
        } catch (Exception e){
            e.printStackTrace();
            Log.i("Error:","There was an issue validating route locations",e.getCause());
        }
    }


    //Function to handle the user clicking on the my location button
    public void myLoc(View view){
        ((AutoCompleteTextView)getView().findViewById(R.id.start)).setText("My Location");
    }

}
