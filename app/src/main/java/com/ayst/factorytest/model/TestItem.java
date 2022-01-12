package com.ayst.factorytest.model;

public class TestItem {
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_FAIL = 1;
    public static final int STATE_SUCCESS = 2;

    private String name;
    private String param;
    private int state = STATE_UNKNOWN;

    public TestItem(String name, String param, int state) {
        this.name = name;
        this.state = state;
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
