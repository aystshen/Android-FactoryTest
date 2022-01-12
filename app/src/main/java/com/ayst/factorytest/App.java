package com.ayst.factorytest;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.xuexiang.xui.XUI;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        XUI.init(this);
    }
}
