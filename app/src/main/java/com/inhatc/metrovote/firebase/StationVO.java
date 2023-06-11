package com.inhatc.metrovote.firebase;

public class StationVO {
    private String statnID;
    private Double statnLat;
    private Double statnLong;
    private String statnName;
    private int    subwayID;
    private String lineName;

    private double distance;

    public StationVO() {
    }

    public StationVO(StationVO other) {
        this.statnName = other.statnName;
        this.statnLat = other.statnLat;
        this.statnLong = other.statnLong;
        this.statnName = other.statnName;
        this.subwayID = other.subwayID;
        this.lineName = other.lineName;
        this.distance = other.distance;
    }

    public StationVO(String statnID, Double statnLat, Double statnLong, String statnName, int subwayID, String lineName, double distance) {
        this.statnID = statnID;
        this.statnLat = statnLat;
        this.statnLong = statnLong;
        this.statnName = statnName;
        this.subwayID = subwayID;
        this.lineName = lineName;
        this.distance = distance;
    }

    public String getStatnID() {
        return statnID;
    }

    public void setStatnID(String statnID) {
        statnID = statnID.substring(statnID.length() - 3);
        this.statnID = statnID;
    }

    public Double getStatnLat() {
        return statnLat;
    }

    public void setStatnLat(Double statnLat) {
        this.statnLat = statnLat;
    }

    public Double getStatnLong() {
        return statnLong;
    }

    public void setStatnLong(Double statnLong) {
        this.statnLong = statnLong;
    }

    public String getStatnName() {
        return statnName;
    }

    public void setStatnName(String statnName) {
        this.statnName = statnName;
    }

    public int getSubwayID() {
        return subwayID;
    }

    public void setSubwayID(int subwayID) {
        this.subwayID = subwayID;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
