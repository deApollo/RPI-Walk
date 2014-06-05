package com.tezra.rpiwalk.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


public class MainAct extends ActionBarActivity {

    public final static String EXTRA_MSG = "com.tezra.rpiwalk.MSG";

    String [] locations = {"87 Gymnasium", "Academy Hall", "Admissions", "Alumni House", "Alumni Sports & Recreation Center", "Amos Eaton Hall", "Barton Hall", "Beman Park Firehouse",
    "Blaw-Knox 1 & 2", "Blitman Residence Commons", "Bray Hall", "Bryckwyck", "Burdett Avenue Residence Hall", "Carnegie Building","Cary Hall",
    "Center for Biotechnology and Interdisciplinary Studies", "Low Center for Industrial Innovation", "Chapel + Cultural Center", "Cogswell Laboratory", "Colonie Apartments",
    "Commons Dining Hall", "Crockett Hall", "Darrin Communications Center", "Davison Hall", "E Complex", "East Campus Athletic Village Arena", "East Campus Athletic Village Stadium",
    "Empire State Hall", "Experimental Media & Performing Arts Center", "Folsom Library", "Graduate Education", "Greene Building", "H Building", "Hall Hall", "Houston Field House",
    "J Building", "J. Erik Jonsson Engineering Center", "Moes Southwest Grill", "Jonsson-Rowland Science Center", "Lally Hall", "LINAC Facility", "Louis Rubin Memorial Approach",
    "Materials Research Center", "Mueller Center", "Nason Hall", "North Hall", "Nugent Hall", "Parking Garage", "Patroon Manor", "Pittsburgh Building", "Playhouse", "Polytechnic Residence Commons",
    "Public Safety", "Quadrangle Complex", "Rensselaer Union", "Ricketts Building", "Robison Swimming Pool", "RPI Ambulance", "Russel Sage Dining Hall", "Russel Sage Laboratory",
    "Sharp Hall", "Troy Building", "Voorhees Computing Center", "Walker Laboratory", "Warren Hall", "West Hall", "Winslow Building"};

    public static void generateToast(Context c, CharSequence text, int duration){
        Toast toast = Toast.makeText(c,text,duration);
        toast.show();
    }

    private boolean validateText(String i, String j){
        boolean f1 = false; boolean f2 = false;
        for(String s : locations){
            if(s.equals(i))
                f1 = true;
            if(s.equals(j))
                f2 = true;
        }
        return f1 && f2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView start = (AutoCompleteTextView) findViewById(R.id.start);
        AutoCompleteTextView finish = (AutoCompleteTextView) findViewById(R.id.finish);
        ArrayAdapter adp = new ArrayAdapter(this,android.R.layout.select_dialog_item,locations);

        start.setThreshold(0);
        finish.setThreshold(0);
        start.setAdapter(adp);
        finish.setAdapter(adp);
    }

    public void findRoute(View view){
        String from = ((AutoCompleteTextView) findViewById(R.id.start)).getText().toString();
        String to = ((AutoCompleteTextView) findViewById(R.id.finish)).getText().toString();
        if(validateText(from, to)){
            Intent dirInt = new Intent(this, DirectionsAct.class);
            String [] locs = {from, to};
            dirInt.putExtra(EXTRA_MSG,locs);
            startActivity(dirInt);
        }
        else
            generateToast(getApplicationContext(),"Please enter a valid location!",Toast.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
