package com.tezra.rpiwalk.app.acts;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.tasks.EventTrackerService;
import com.tezra.rpiwalk.app.utils.Event;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainAct extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public final static String EXTRA_MSG = "com.tezra.rpiwalk.MSG";
    public final static String EXTRA_MSG_2 = "com.tezra.rpiwalk.MSG_2";
    public static ArrayList<Event> eventList = new ArrayList<Event>();

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private AboutAct about;
    private LandingAct landing;
    private ScheduleAct scheduling;

    public void myLoc(View v) {
        landing.myLoc(v);
    }

    public void findRoute(View v) {
        landing.findRoute(v);
    }

    public void addItem(View v){
        scheduling.addItem(v);
    }

    public static void generateToast(Context c, CharSequence text, int duration){
        Toast toast = Toast.makeText(c,text,duration);
        toast.show();
    }

    private void loadData(){
        File file = new File(this.getFilesDir(),"data.dat");
        try {
            if (file.exists()) {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                MainAct.eventList = (ArrayList<Event>) objIn.readObject();
            }
        } catch (Exception e){
            Log.i("Error:", "There was an issue loading the data file!", e.getCause());
        }
    }

    private void startEventTracker() {
        Intent mServiceIntent = new Intent(this, EventTrackerService.class);
        startService(mServiceIntent);
    }

    private void initializeFragments() {
        about = new AboutAct();
        landing = new LandingAct();
        scheduling = new ScheduleAct();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFragments();

        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, landing)
                .commit();

        loadData();
        startEventTracker();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private Fragment parsePosition(int pos) {
        switch(pos) {
            case 0:
                return landing;
            case 1:
                return scheduling;
            case 2:
                return about;
            default:
                return null;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, parsePosition(position))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_activity_main);
                break;
            case 2:
                mTitle = getString(R.string.title_activity_schedule);
                break;
            case 3:
                mTitle = getString(R.string.title_activity_about);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
