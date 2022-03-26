package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LedItem implements Serializable {
    public static final int STATE_LOW = 0;
    public static final int STATE_HIGH = 1;

    @SerializedName("name")
    private String name;
    @SerializedName("device")
    private String device;
    @SerializedName("brightness")
    private Integer brightness;
    @SerializedName("state")
    private Integer state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "LedItem{" +
                "name='" + name + '\'' +
                ", device=" + device +
                ", brightness=" + brightness +
                ", state=" + state +
                '}';
    }
}
