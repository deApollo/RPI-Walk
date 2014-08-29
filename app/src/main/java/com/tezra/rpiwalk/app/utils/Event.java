package com.tezra.rpiwalk.app.utils;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Event implements Serializable {
    private double lat;
    private double lon;

    public String name;
    public String location;
    public boolean [] days;
    public int hour;
    public int minute;


    public Event(String name, String location, boolean [] days, String hour, String minute, double lat, double lon) {
        this.name = name;
        this.location = location;
        this.days = days;
        this.hour = Integer.parseInt(hour);
        this.minute = Integer.parseInt(minute);
        this.lat = lat;
        this.lon = lon;
    }

    private String parseDays(){
        String ret = "";
        if(days[0])
            ret += "Monday, ";
        if(days[1])
            ret += "Tuesday, ";
        if(days[2])
            ret += "Wednesday, ";
        if(days[3])
            ret += "Thursday, ";
        if(days[4])
            ret += "Friday";
        if(ret.contains(","))
            return ret.subSequence(0,ret.lastIndexOf(",")).toString();
        else
            return ret;
    }

    private String parseInt(int i){
        if(i < 10)
            return "0"+String.valueOf(i);
        return String.valueOf(i);
    }

    public int getEventSeconds(){
        return hour * 60 * 60 + minute * 60;
    }

    public String getMainText() {
        return name + " - " + location;
    }

    public String getSubText() {
        return parseDays() + " - " + parseInt(hour) + ":" + parseInt(minute);
    }

    public LatLng getLocation() {
        return new LatLng(lat,lon);
    }
}
