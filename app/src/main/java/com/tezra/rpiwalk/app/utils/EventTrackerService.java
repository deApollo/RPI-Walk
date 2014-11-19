package com.tezra.rpiwalk.app.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

public class EventTrackerService extends IntentService {

    public static final String BROADCAST_ACTION = "RPI Walk - Event Service"; //"Title" of the service
    public final static String EXTRA_MSG = "com.tezra.rpiwalk.MSG"; //Message for recovering data from starting intent
    public int REFRESH_INTERVAL; //Number if milliseconds between runs of mTask
    private final Handler mHandler = new Handler(); //Handler which handles running mTask
    private final Criteria c = new Criteria();
    private Runnable mTask = new Runnable() {
        @Override
        public void run() { //The runnable that calls checkEvents()
            LocationManager m = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
            lis.checkEventsHandler();
            mHandler.postDelayed(mTask,REFRESH_INTERVAL);
        }
    };
    public EventHandler lis;
    Intent i;

    @Override
    public void onCreate() {
        super.onCreate();
        i = new Intent(BROADCAST_ACTION);
    }

    public EventTrackerService() {
        super(BROADCAST_ACTION);
    }

    @Override
    public void onHandleIntent(Intent workIntent) {

    }

    //When this service starts, set up the EventHandler and start the loop to call CheckEvents()
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {

        int [] data = i.getIntArrayExtra(EXTRA_MSG);

        REFRESH_INTERVAL = data[0];
        int seconds_early = data[1];

        LocationManager m = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));

        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        c.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        lis = new EventHandler(m.getLastKnownLocation(m.getBestProvider(c,false)),(NotificationManager)getSystemService(NOTIFICATION_SERVICE),this,seconds_early);
        m.requestLocationUpdates(120000,10,c,lis,null);

        mHandler.postDelayed(mTask,REFRESH_INTERVAL);

        return START_STICKY;
    }

    //When this service stops, stop getting location updates and stop the repeating task.
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTask);
        ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).removeUpdates(lis);
        Log.v("STOP_SERVICE","DONE");
    }
}
