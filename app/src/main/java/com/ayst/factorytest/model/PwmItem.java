package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PwmItem implements Serializable {
    public static final int STATE_LOW = 0;
    public static final int STATE_HIGH = 1;

    @SerializedName("name")
    private String name;
    @SerializedName("pwm")
    private Integer pwm;
    @SerializedName("period")
    private Integer period;
    @SerializedName("duty")
    private Integer duty;
    @SerializedName("state")
    private Integer state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPwm() {
        return pwm;
    }

    public void setPwm(Integer pwm) {
        this.pwm = pwm;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getDuty() {
        return duty;
    }

    public void setDuty(Integer duty) {
        this.duty = duty;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "PwmItem{" +
                "name='" + name + '\'' +
                ", pwm=" + pwm +
                ", period=" + period +
                ", duty=" + duty +
                ", state=" + state +
                '}';
    }
}
