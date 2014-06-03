package com.tezra.rpiwalk.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;


public class DirectionsAct extends ActionBarActivity {

    private void initializeMap(GeoPoint start, GeoPoint finish){
        MapView map = (MapView) findViewById(R.id.dir);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mCon = map.getController();
        mCon.setZoom(10);
        mCon.setCenter(start);

        RoadManager rm = new MapQuestRoadManager("api");
        rm.addRequestOption("routeType=pedestrian");

        ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
        wp.add(start);
        wp.add(finish);

        Road r = rm.getRoad(wp);
        Polyline rOverlay = rm.buildRoadOverlay(r,this);

        map.getOverlays().add(rOverlay);
        map.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Intent i = getIntent();
        String [] locs = i.getStringArrayExtra(MainAct.EXTRA_MSG);

        GeoPoint start;
        GeoPoint finish;

        try{
            start = Utils.getPointFromLocationString(locs[0]);
            finish = Utils.getPointFromLocationString(locs[1]);
            initializeMap(start,finish);
        } catch(Exception e){
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.directions, menu);
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
