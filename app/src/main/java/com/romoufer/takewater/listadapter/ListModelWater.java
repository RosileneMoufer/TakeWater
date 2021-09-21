package com.romoufer.takewater.listadapter;

public class ListModelWater {

    int id;
    String date;
    String hour;
    int ml;

    public ListModelWater(int id, String date, String hour, int ml) {
        this.id = id;
        this.date = date;
        this.hour = hour;
        this.ml = ml;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getMl() {
        return ml;
    }

    public void setMl(int ml) {
        this.ml = ml;
    }
}
