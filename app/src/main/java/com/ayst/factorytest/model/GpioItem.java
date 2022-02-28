package com.ayst.factorytest.model;

import java.io.Serializable;

public class GpioItem implements Serializable {
    public static final int STATE_LOW = 0;
    public static final int STATE_HIGH = 1;

    private String name;
    private int code;
    private int state = STATE_LOW;

    public GpioItem(String name, int code, int state) {
        this.name = name;
        this.code = code;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "GpioItem{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", state=" + state +
                '}';
    }
}
