package com.tezra.rpiwalk.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


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

    public void addItem(View view){
        EditText event_name = (EditText) findViewById(R.id.event_name);
        EditText location = (EditText) findViewById(R.id.location);
        EditText days = (EditText) findViewById(R.id.days);
        TimePicker time = (TimePicker) findViewById(R.id.timePicker);
        if(event_name.getText().length() != 0) {
            if(location.getText().length() != 0) {
                if(days.getText().length() != 0) {
                    Intent resultIntent = new Intent();
                    String [] data = {event_name.getText().toString(), location.getText().toString(),days.getText().toString(),String.valueOf(time.getCurrentHour()),String.valueOf(time.getCurrentMinute())};
                    resultIntent.putExtra(MainAct.EXTRA_MSG,data);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    MainAct.generateToast(this,"Please enter a day(s)!",Toast.LENGTH_LONG);
                }
            } else {
                MainAct.generateToast(this,"Please enter a location!",Toast.LENGTH_LONG);
            }
        } else {
            MainAct.generateToast(this,"Please enter an event name!", Toast.LENGTH_LONG);
        }
    }
}
