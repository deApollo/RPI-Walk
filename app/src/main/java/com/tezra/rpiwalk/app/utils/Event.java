package com.tezra.rpiwalk.app.utils;

import java.io.Serializable;

public class Event implements Serializable {
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
            ret += "Monday, ";
        if(days[1])
            ret += "Tuesday, ";
        if(days[2])
            ret += "Wednesday, ";
        if(days[3])
            ret += "Thursday, ";
        if(days[4])
            ret += "Friday";
        return ret.subSequence(0,ret.lastIndexOf(",")).toString();
    }

    public int getEventSeconds(){
        return hour * 60 * 60 + minute * 60;
    }

    public String getMainText() {
        return name + " - " + location;
    }

    public String getSubText() {
        return parseDays() + " - " + hour + ":" + minute;
    }
}
