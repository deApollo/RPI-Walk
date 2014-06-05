package com.tezra.rpiwalk.app;

import android.content.Context;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

/**
 * Created by dowde on 6/5/2014.
 */
public class InfoPacket {
    public MapView m;
    public Polyline l;
    public GeoPoint p;

    public InfoPacket(MapView m, Polyline l,GeoPoint s){
        this.m = m;
        this.l = l;
        this.p = s;
    }
}
