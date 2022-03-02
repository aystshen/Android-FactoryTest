package com.ayst.factorytest.model;

import android.app.Activity;

import java.io.Serializable;

public class TestItem implements Serializable {
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_FAILURE = 1;
    public static final int STATE_SUCCESS = 2;

    private String name;
    private String param;
    private Class<? extends Activity> activity;
    private int state = STATE_UNKNOWN;

    public TestItem(String name, String param, Class<? extends Activity> activity, int state) {
        this.name = name;
        this.param = param;
        this.activity = activity;
        this.state = state;
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

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public void setActivity(Class<? extends Activity> activity) {
        this.activity = activity;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateStr() {
        switch (state) {
            case STATE_UNKNOWN:
                return "忽略";
            case STATE_SUCCESS:
                return "通过";
            case STATE_FAILURE:
                return "失败";
        }
        return "忽略";
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "name='" + name + '\'' +
                ", param='" + param + '\'' +
                ", activity=" + activity +
                ", state=" + state +
                '}';
    }
}
