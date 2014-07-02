package com.tezra.rpiwalk.app.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

public class ParcelableGeoPoint implements Parcelable {

    private GeoPoint p;

    public ParcelableGeoPoint(GeoPoint p){
        this.p = p;
    }

    public GeoPoint getGeoPoint() {
        return p;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(p.getLatitudeE6());
        parcel.writeInt(p.getLongitudeE6());
    }

    public static final Parcelable.Creator<ParcelableGeoPoint> CREATOR
            = new Parcelable.Creator<ParcelableGeoPoint>() {
        public ParcelableGeoPoint createFromParcel(Parcel in) {
            return new ParcelableGeoPoint(in);
        }

        public ParcelableGeoPoint[] newArray(int size){
            return new ParcelableGeoPoint[size];
        }
    };

    private ParcelableGeoPoint(Parcel in){
        int lat = in.readInt();
        int lon = in.readInt();
        p = new GeoPoint(lat,lon);
    }
}
