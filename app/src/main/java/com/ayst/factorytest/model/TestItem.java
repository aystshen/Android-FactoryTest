package com.ayst.factorytest.model;

import android.app.Activity;
import android.content.Context;

import com.ayst.factorytest.R;

import org.jetbrains.annotations.TestOnly;

import java.io.Serializable;

public class TestItem implements Serializable {
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_FAILURE = 1;
    public static final int STATE_SUCCESS = 2;

    private String key;
    private String name;
    private String param;
    private String result;
    private transient Class<? extends Activity> activity;
    private int state = STATE_UNKNOWN;

    public TestItem(String key, String name, String param, String result, Class<? extends Activity> activity, int state) {
        this.key = key;
        this.name = name;
        this.param = param;
        this.result = result;
        this.activity = activity;
        this.state = state;
    }

    public TestItem(String name, Class<? extends Activity> activity) {
        this.key = "";
        this.name = name;
        this.param = "";
        this.result = "";
        this.activity = activity;
        this.state = STATE_UNKNOWN;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", param='" + param + '\'' +
                ", result='" + result + '\'' +
                ", activity=" + activity +
                ", state=" + state +
                '}';
    }
}
