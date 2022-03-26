package com.ayst.factorytest.model;

import android.app.Activity;
import android.content.Context;

import com.ayst.factorytest.R;

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

    public String getStateStr(Context context) {
        switch (state) {
            case STATE_UNKNOWN:
                return context.getString(R.string.ignore);
            case STATE_SUCCESS:
                return context.getString(R.string.success);
            case STATE_FAILURE:
                return context.getString(R.string.fail);
        }
        return context.getString(R.string.ignore);
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
