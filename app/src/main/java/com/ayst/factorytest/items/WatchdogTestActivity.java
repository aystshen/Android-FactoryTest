package com.ayst.factorytest.items;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.utils.Mcu;

public class WatchdogTestActivity extends ChildTestActivity {
    private static final String TAG = "WatchdogTestActivity";

    private Mcu mMcu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMcu = new Mcu(this);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_watchdog_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mSuccessBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int watchdog = mMcu.getWatchdogDuration();
        Log.i(TAG, "onStart, watchdog: " + watchdog);
        if (watchdog > 0) {
            finish(TestItem.STATE_SUCCESS);
        } else {
            finish(TestItem.STATE_FAILURE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}