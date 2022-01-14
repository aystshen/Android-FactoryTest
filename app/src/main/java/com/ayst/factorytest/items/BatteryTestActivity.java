package com.ayst.factorytest.items;

import android.os.Bundle;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

public class BatteryTestActivity extends ChildTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_battery_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }
}