package com.tezra.rpiwalk.app.tasks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.tezra.rpiwalk.app.utils.InfoPacket;
import com.tezra.rpiwalk.app.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class MapInitializerTask extends AsyncTask<Object, Void, InfoPacket>{

    private String getParsedRouteTimeString(int seconds){
        int minutes = seconds/60;
        seconds -= minutes*60;
        int hours = minutes/60;
        minutes -= hours*60;
        if(hours != 0)
            return  String.format("%dh %d m %s s", hours,minutes,seconds);
        else if(minutes != 0)
            return  String.format("%d m %s s", minutes,seconds);
        else
            return String.format("%s s", seconds);
    }

    private void populateNodes(Road r, MapView m){
        Drawable nodeIcon = m.getContext().getResources().getDrawable(R.drawable.way_point_icon);
        for(int i = 0; i < r.mNodes.size(); i++){
            RoadNode node = r.mNodes.get(i);
            Marker nodeM = new Marker(m);
            nodeM.setPosition(node.mLocation);
            if(i != 0 && i != r.mNodes.size()-1) {
                nodeM.setIcon(nodeIcon);
                nodeM.setTitle("Step " + i);
            } else if( i == 0) {
                nodeM.setIcon(m.getContext().getResources().getDrawable(R.drawable.start_icon));
                nodeM.setTitle("Start");
                nodeM.setSubDescription("Route Time: " + getParsedRouteTimeString((int)r.mDuration));
            }
            else {
                nodeM.setTitle("Finish");
                nodeM.setIcon(m.getContext().getResources().getDrawable(R.drawable.end_icon));
            }
            nodeM.setSnippet(node.mInstructions);
            m.getOverlays().add(nodeM);
        }
    }

    @Override
    public InfoPacket doInBackground(Object... params){
        try{
            GeoPoint start = (GeoPoint) params[0];
            GeoPoint finish = (GeoPoint) params[1];

            ArrayList<GeoPoint> wp = new ArrayList<GeoPoint>();
            wp.add(start);
            wp.add(finish);

            RoadManager rm = new MapQuestRoadManager("Fmjtd%7Cluur2g6rn1%2C8w%3Do5-lra5q");
            rm.addRequestOption("routeType=pedestrian");

            Road r = rm.getRoad(wp);
            Polyline rOverlay = rm.buildRoadOverlay(r, (Context) params[3]);

            return new InfoPacket((MapView)params[2],rOverlay,start,r);
        } catch (Exception e){
            return null;
        }
    }

    public void onPostExecute(InfoPacket result){
        if(result != null) {
            MapView map = result.m;

            IMapController mCon = map.getController();
            mCon.setCenter(result.p);
            map.getOverlays().add(result.l);
            populateNodes(result.r,map);
            map.invalidate();
        }
    }
}
