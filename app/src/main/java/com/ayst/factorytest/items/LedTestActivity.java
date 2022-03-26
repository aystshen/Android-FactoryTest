package com.ayst.factorytest.items;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.LedItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.LedItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.topband.tbapi.utils.ShellUtils;
import com.xuexiang.xui.utils.WidgetUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;

public class LedTestActivity extends ChildTestActivity {

    private static final String TAG = "LedTestActivity";

    private static final String PARAM_DEFAULT =
            "[{'name': '红色LED', 'device': 'led_r', 'brightness': 1000, 'state': 0}, " +
                    "{'name': '绿色LED', 'device': 'led_g', 'brightness': 1000, 'state': 0}, " +
                    "{'name': '蓝色LED', 'device': 'led_b', 'brightness': 1000, 'state': 0}]";

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private Gson mGson = new Gson();
    private LedItemAdapter mLedItemAdapter;
    private ArrayList<LedItem> mLedItems = new ArrayList<>();
    private TestThread mTestThread;

    private static boolean setLedBrightness(String devices, int brightness) {
        String cmd = "echo " + brightness + " >  " + "/sys/class/leds/" + devices + "/brightness";

        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        Log.i(TAG, "setLedBrightness, " + result.toString());

        return result.errorMsg.isEmpty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mLedItems.size() > 0) {
            mTestThread = new TestThread();
            mTestThread.start();
        } else {
            Log.e(TAG, "onCreate, no leds");
        }
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_led_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (TextUtils.isEmpty(mTestItem.getParam())) {
            mTestItem.setParam(PARAM_DEFAULT);
        }

        Log.i(TAG, "initViews, param: " + mTestItem.getParam());

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mLedItems = parseParam(mTestItem.getParam());
        mLedItemAdapter = new LedItemAdapter();
        mLedItemAdapter.setList(mLedItems);
        mItemsRv.setAdapter(mLedItemAdapter);
    }

    @Override
    protected void onDestroy() {
        if (mTestThread != null) {
            mTestThread.interrupt();
        }
        resetLed();

        super.onDestroy();
    }

    private ArrayList<LedItem> parseParam(String param) {
        Type collectionType = new TypeToken<Collection<LedItem>>() {
        }.getType();
        return mGson.fromJson(param, collectionType);
    }

    private void setLed(LedItem led, int state) {
        if (state == LedItem.STATE_HIGH) {
            setLedBrightness(led.getDevice(), led.getBrightness());
        } else {
            setLedBrightness(led.getDevice(), 0);
        }
        led.setState(state);
    }

    private void resetLed() {
        for (LedItem led : mLedItems) {
            setLed(led, LedItem.STATE_LOW);
        }
    }

    private class TestThread extends Thread {
        @Override
        public void run() {
            super.run();

            int index = mLedItems.size();
            while (!isInterrupted()) {
                if (index < mLedItems.size() - 1) {
                    index++;
                } else {
                    index = 0;
                }

                resetLed();
                setLed(mLedItems.get(index), LedItem.STATE_HIGH);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLedItemAdapter.notifyDataSetChanged();
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.w(TAG, "TestThread, interrupted");
                    break;
                }
            }
            Log.i(TAG, "TestThread, exit");
        }
    }
}