package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WiFiParam implements Serializable {

    @SerializedName("ssid")
    private String ssid;

    @SerializedName("rssi")
    private Integer rssi;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "WiFiParam{" +
                "ssid='" + ssid + '\'' +
                ", rssi=" + rssi +
                '}';
    }
}
