package com.example.android.quakereport;

public class EarthQuake {
    private double magnitude;
    private String location;
    private String primarylocation;
    private long milliSeconds;
    private String url;


    public EarthQuake(double magnitude,String Locationoffset,String primaryLocation,long Date,String url) {
        this.magnitude = magnitude;
        this.location=Locationoffset;
        this.primarylocation=primaryLocation;
        this.milliSeconds=Date;
        this.url=url;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public long getTimeInMilliSeconds() {
        return milliSeconds;
    }
    public String getLocation(){
        return location;
    }

    public String getPrimarylocation() {
        return primarylocation;
    }
    public String getUrl(){
        return url;
    }
}
