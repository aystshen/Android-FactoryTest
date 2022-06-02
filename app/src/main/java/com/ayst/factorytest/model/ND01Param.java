package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ND01Param implements Serializable {

    @SerializedName("i2c")
    private Integer i2c;
    @SerializedName("calibrate")
    private Boolean calibrate;
    @SerializedName("distance")
    private Integer distance;

    public Integer getI2c() {
        return i2c;
    }

    public void setI2c(Integer i2c) {
        this.i2c = i2c;
    }

    public Boolean getCalibrate() {
        return calibrate;
    }

    public void setCalibrate(Boolean calibrate) {
        this.calibrate = calibrate;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ND01Param{" +
                "i2c=" + i2c +
                ", calibrate=" + calibrate +
                ", distance=" + distance +
                '}';
    }
}
