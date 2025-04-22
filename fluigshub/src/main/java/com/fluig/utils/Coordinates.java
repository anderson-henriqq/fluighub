package com.fluig.utils;

// Coordenates.java
public class Coordinates {
    private String localName;
    private double latitude;
    private double longitude;

    public Coordinates(String localname, double latitude, double longitude) {
        this.localName = localname;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "{" +
                "localName='" + localName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}