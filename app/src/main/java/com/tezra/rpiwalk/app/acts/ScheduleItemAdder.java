package com.tezra.rpiwalk.app.acts;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.tasks.LocationValidatorTask;


public class ScheduleItemAdder extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_item_adder);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_item_adder, menu);
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

    //Function that returns an array of booleans based on the days the user checked off for that event
    boolean [] getDays() {
        return new boolean [] { ((CheckBox)findViewById(R.id.m)).isChecked(), ((CheckBox)findViewById(R.id.t)).isChecked(),
                ((CheckBox)findViewById(R.id.w)).isChecked(), ((CheckBox)findViewById(R.id.th)).isChecked(), ((CheckBox)findViewById(R.id.f)).isChecked()};
    }

    //Check if the user selected a day in the provided array
    boolean hasADaySelected(boolean [] b) {
        for(boolean bb : b) {
            if(bb)
                return true;
        }
        return false;
    }

    //Function to handle the button click
    public void addItem(View view){
        try {
            EditText event_name = (EditText) findViewById(R.id.event_name);
            EditText location = (EditText) findViewById(R.id.location);
            boolean [] days = getDays();
            TimePicker time = (TimePicker) findViewById(R.id.timePicker);
            //Validate user input
            if (event_name.getText().length() != 0) {
                if (location.getText().length() != 0) {
                    if (hasADaySelected(days)) {
                        if (new LocationValidatorTask().execute(location.getText().toString(),getApplicationContext()).get()) {
                            //Set the intent to return all the needed data, then finish the activity
                            Intent resultIntent = new Intent();
                            String[] data = {event_name.getText().toString(), location.getText().toString(), String.valueOf(time.getCurrentHour()), String.valueOf(time.getCurrentMinute())};
                            resultIntent.putExtra(MainAct.EXTRA_MSG, data);
                            resultIntent.putExtra(MainAct.EXTRA_MSG_2,days);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                        else {
                            MainAct.generateToast(this, "Please enter a valid location!", Toast.LENGTH_LONG);
                        }
                    } else {
                        MainAct.generateToast(this, "Please select a day(s)!", Toast.LENGTH_LONG);
                    }
                } else {
                    MainAct.generateToast(this, "Please enter a location!", Toast.LENGTH_LONG);
                }
            } else {
                MainAct.generateToast(this, "Please enter an event name!", Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
