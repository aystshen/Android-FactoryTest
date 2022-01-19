package com.ayst.factorytest.items;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.ResultEvent;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.utils.Mcu;

import org.greenrobot.eventbus.EventBus;

public class RegularBootTestActivity extends ChildTestActivity {
    private static final String TAG = "RegularBootTestActivity";

    private Mcu mMcu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMcu = new Mcu(this);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_regular_boot_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        start();
    }

    @Override
    public void initViews() {
        super.initViews();

        mSuccessBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void start() {
        mMcu.setUptime(10);
        int uptime = mMcu.getUptime();
        Log.i(TAG, "start, uptime: " + uptime);
        if (uptime == 10) {
            mMcu.setUptime(0); // 取消定时开机
            mTestItem.setState(TestItem.STATE_SUCCESS);
            EventBus.getDefault().post(new ResultEvent(mTestItem));
            finish();
        } else {
            mMcu.setUptime(0); // 取消定时开机
            mTestItem.setState(TestItem.STATE_FAILURE);
            EventBus.getDefault().post(new ResultEvent(mTestItem));
            finish();
        }
    }
}