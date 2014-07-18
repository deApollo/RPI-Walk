package com.tezra.rpiwalk.app.tasks;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class EventTrackerService extends IntentService {
    public static final String BROADCAST_ACTION = "RPI Walk - Event Service";
    public static final int REFRESH_INTERVAL = 60000;
    private final Handler mHandler = new Handler();
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            lis.checkEvents();
            mHandler.postDelayed(mTask,REFRESH_INTERVAL);
        }
    };
    public LocationManager m;
    public EventLocationListener lis;
    Intent i;

    @Override
    public void onCreate() {
        super.onCreate();
        i = new Intent(BROADCAST_ACTION);
    }

    public EventTrackerService() {
        super(BROADCAST_ACTION);
    };

    @Override
    public void onHandleIntent(Intent workIntent) {

    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        m = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE); c.setAltitudeRequired(false); c.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        lis = new EventLocationListener(m.getLastKnownLocation(m.getBestProvider(c,true)),(NotificationManager)getSystemService(NOTIFICATION_SERVICE),this);
        m.requestLocationUpdates(m.getBestProvider(c,true),60000,15,lis);

        mHandler.postDelayed(mTask,REFRESH_INTERVAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m.removeUpdates(lis);
        mHandler.removeCallbacks(mTask);
        Log.v("STOP_SERVICE","DONE");
    }
}
