package com.tezra.rpiwalk.app;

import java.io.Serializable;

public class Event implements Serializable {
    public String name;
    public String location;
    public String days;
    public int hour;
    public int minute;

    public Event(String name, String location, String days, String hour, String minute) {
        this.name = name;
        this.location = location;
        this.days = days;
        this.hour = Integer.parseInt(hour);
        this.minute = Integer.parseInt(minute);
    }

    public int getEventSeconds(){
        return hour * 60 * 60 + minute * 60;
    }

    public String getMainText() {
        return name + " - " + location;
    }

    public String getSubText() {
        return days + " - " + hour + ":" + minute;
    }
}
