package com.tezra.rpiwalk.app.utils;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Event implements Serializable {
    /*
    Serializable event class that stores all the data about a user entered event.
     */

    public String name;
    public String location;
    public boolean [] days;
    public int hour;
    public int minute;


    public Event(String name, String location, boolean [] days, String hour, String minute) {
        this.name = name;
        this.location = location;
        this.days = days;
        this.hour = Integer.parseInt(hour);
        this.minute = Integer.parseInt(minute);
    }

    private String parseDays(){
        String ret = "";
        if(days[0])
            ret += "M ";
        if(days[1])
            ret += "T ";
        if(days[2])
            ret += "W ";
        if(days[3])
            ret += "TH ";
        if(days[4])
            ret += "F ";
        if(days[5])
            ret += "S ";
        if(days[6])
            ret += "SU ";
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
}
