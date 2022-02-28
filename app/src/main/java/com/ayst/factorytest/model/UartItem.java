package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UartItem implements Serializable {
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_FAILURE = 1;
    public static final int STATE_SUCCESS = 2;

    @SerializedName("device")
    private String device;
    @SerializedName("baud")
    private Integer baud;
    @SerializedName("send")
    private String send;
    @SerializedName("receive")
    private String receive;
    @SerializedName("state")
    private int state = STATE_UNKNOWN;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Integer getBaud() {
        return baud;
    }

    public void setBaud(Integer baud) {
        this.baud = baud;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "UartItem{" +
                "device='" + device + '\'' +
                ", baud=" + baud +
                ", send='" + send + '\'' +
                ", receive='" + receive + '\'' +
                ", state=" + state +
                '}';
    }
}
