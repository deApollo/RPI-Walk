package com.tezra.rpiwalk.app.acts;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tezra.rpiwalk.app.utils.ParcelableGeoPoint;
import com.tezra.rpiwalk.app.R;
import com.tezra.rpiwalk.app.tasks.MapInitializerTask;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;


public class DirectionsAct extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Intent i = getIntent();
        ArrayList<ParcelableGeoPoint> pList =  i.getParcelableArrayListExtra(MainAct.EXTRA_MSG);

        MapView map = (MapView) findViewById(R.id.dir);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mCon = map.getController();
        mCon.setZoom(18);
        mCon.setCenter(new GeoPoint(42.730234,-73.6766982));

        new MapInitializerTask().execute(pList.get(0).getGeoPoint(),pList.get(1).getGeoPoint(),map,this);

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
