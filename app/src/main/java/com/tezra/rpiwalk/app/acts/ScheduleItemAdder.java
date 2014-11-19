package com.tezra.rpiwalk.app.acts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.tasks.LocationValidatorTask;
import com.tezra.rpiwalk.app.utils.Event;


public class ScheduleItemAdder extends ActionBarActivity {

    private Event e;
    private boolean isEditing;

    private void setFromEvent(Event e){
        EditText event_name = (EditText) findViewById(R.id.event_name);
        event_name.setText(e.name);
        AutoCompleteTextView location = (AutoCompleteTextView) findViewById(R.id.location);
        location.setText(e.location);
        TimePicker time = (TimePicker) findViewById(R.id.timePicker);
        time.setCurrentHour(e.hour);
        time.setCurrentMinute(e.minute);

        ((CheckBox)findViewById(R.id.m)).setChecked(e.days[0]);
        ((CheckBox)findViewById(R.id.t)).setChecked(e.days[1]);
        ((CheckBox)findViewById(R.id.w)).setChecked(e.days[2]);
        ((CheckBox)findViewById(R.id.th)).setChecked(e.days[3]);
        ((CheckBox)findViewById(R.id.f)).setChecked(e.days[4]);
        ((CheckBox)findViewById(R.id.s)).setChecked(e.days[5]);
        ((CheckBox)findViewById(R.id.su)).setChecked(e.days[6]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_item_adder);

        Intent i = getIntent();

        isEditing = i.getBooleanExtra(MainAct.EXTRA_MSG,false);

        if(isEditing) {
            e = (Event) i.getSerializableExtra(MainAct.EXTRA_MSG_2);
            setFromEvent(e);
        }

        ArrayAdapter adp = new ArrayAdapter(this,android.R.layout.select_dialog_item,LandingFragment.locations);
        AutoCompleteTextView t = (AutoCompleteTextView) findViewById(R.id.location);
        t.setAdapter(adp);

        t.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });

        final Button c = (Button)findViewById(R.id.button);


        c.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    c.setBackground(getResources().getDrawable(R.drawable.finish_pressed));
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    c.setBackground(getResources().getDrawable(R.drawable.finish_unpressed));
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_item_adder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //Function that returns an array of booleans based on the days the user checked off for that event
    boolean [] getDays() {
        return new boolean [] { ((CheckBox)findViewById(R.id.m)).isChecked(), ((CheckBox)findViewById(R.id.t)).isChecked(),
                ((CheckBox)findViewById(R.id.w)).isChecked(), ((CheckBox)findViewById(R.id.th)).isChecked(), ((CheckBox)findViewById(R.id.f)).isChecked(),
                ((CheckBox)findViewById(R.id.s)).isChecked(),((CheckBox)findViewById(R.id.su)).isChecked()};
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
            AutoCompleteTextView location = (AutoCompleteTextView) findViewById(R.id.location);
            boolean [] days = getDays();
            TimePicker time = (TimePicker) findViewById(R.id.timePicker);
            //Validate user input
            if (event_name.getText().length() != 0) {
                if (location.getText().length() != 0) {
                    if (hasADaySelected(days)) {
                        if (new LocationValidatorTask().execute(location.getText().toString(),getApplicationContext()).get()) {
                            //Set the intent to return all the needed data, then finish the activity
                            Intent resultIntent = new Intent();
                            Event e = new Event(event_name.getText().toString(), location.getText().toString(), days, String.valueOf(time.getCurrentHour()), String.valueOf(time.getCurrentMinute()));
                            resultIntent.putExtra(MainAct.EXTRA_MSG,e);
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
