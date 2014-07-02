package com.tezra.rpiwalk.app.tasks;


import android.os.AsyncTask;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class RoadRetrieverTask extends AsyncTask<Object, Void, Road> {

    public Road doInBackground(Object... params) {
        RoadManager rm = new MapQuestRoadManager("Fmjtd%7Cluur2g6rn1%2C8w%3Do5-lra5q");
        rm.addRequestOption("routeType=pedestrian");
        return rm.getRoad((ArrayList<GeoPoint>)params[0]);
    }
}
