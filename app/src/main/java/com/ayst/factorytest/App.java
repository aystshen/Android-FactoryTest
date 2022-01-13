package com.ayst.factorytest;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.topband.tbapi.TBManager;
import com.xuexiang.xui.XUI;

public class App extends Application {
    private static final String TAG = "App";

    private static TBManager sTBManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        XUI.init(this);

        sTBManager = new TBManager(this);
        sTBManager.init();
    }

    @Override
    public void onTerminate() {
        sTBManager.deinit();
        super.onTerminate();
    }

    public static TBManager getTBManager() {
        return sTBManager;
    }
}
