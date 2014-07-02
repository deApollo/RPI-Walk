package com.tezra.rpiwalk.app.utils;

import android.content.Context;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class InfoPacket {
    public MapView m;
    public Polyline l;
    public GeoPoint p;
    public Road r;

    public InfoPacket(MapView m, Polyline l,GeoPoint s, Road r){
        this.m = m;
        this.l = l;
        this.p = s;
        this.r = r;
    }
}
