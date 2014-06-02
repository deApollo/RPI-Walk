package com.tezra.rpiwalk.app;

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

    String [] locations = {"loc1","loc2","loc3"};

    public void generateToast(CharSequence text, int duration){
        Toast toast = Toast.makeText(getApplicationContext(),text,duration);
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

        final AutoCompleteTextView start = (AutoCompleteTextView) findViewById(R.id.start);
        final AutoCompleteTextView finish = (AutoCompleteTextView) findViewById(R.id.finish);
        final ArrayAdapter adp = new ArrayAdapter(this,android.R.layout.select_dialog_item,locations);

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
            generateToast("Please enter a valid location!",Toast.LENGTH_LONG);
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
