package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WiFiTransferParam implements Serializable {

    @SerializedName("ssid")
    private String ssid;

    @SerializedName("password")
    private String password;

    @SerializedName("serverip")
    private String serverip;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    @Override
    public String toString() {
        return "WiFiTransferParam{" +
                "ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                ", serverip='" + serverip + '\'' +
                '}';
    }
}
