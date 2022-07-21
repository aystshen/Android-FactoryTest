package com.ayst.factorytest.items;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.GpioItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.GpioItem;
import com.xuexiang.xui.utils.WidgetUtils;

import java.util.ArrayList;

import butterknife.BindView;

public class GpioTestActivity extends ChildTestActivity {
    private static final String TAG = "GpioTestActivity";

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private GpioItemAdapter mGpioItemAdapter;
    private ArrayList<GpioItem> mItems = new ArrayList<>();
    private TestThread mTestThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mItems.size() > 0) {
            mTestThread = new TestThread();
            mTestThread.start();
        } else {
            Log.e(TAG, "onCreate, no gpios");
        }
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_gpio_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        for (int i = 0; i < App.getTBManager().getGpioNum(); i++) {
            mItems.add(new GpioItem("GPIO" + i, i, GpioItem.STATE_LOW));
            setGpio(i, GpioItem.STATE_LOW);
        }

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mGpioItemAdapter = new GpioItemAdapter();
        mGpioItemAdapter.setList(mItems);
        mItemsRv.setAdapter(mGpioItemAdapter);

        updateResult(String.format("{'num':%d}", App.getTBManager().getGpioNum()));
    }

    @Override
    protected void onDestroy() {
        if (mTestThread != null) {
            mTestThread.interrupt();
        }

        super.onDestroy();
    }

    private void setGpio(int gpio, int value) {
        App.getTBManager().setGpioDirection(gpio, 1, value);
        mItems.get(gpio).setState(value);
    }

    private void resetGpio() {
        for (int i = 0; i < mItems.size(); i++) {
            setGpio(mItems.get(i).getCode(), GpioItem.STATE_LOW);
        }
    }

    private class TestThread extends Thread {
        @Override
        public void run() {
            super.run();

            int index = mItems.size();
            while (!isInterrupted()) {
                if (index < mItems.size() - 1) {
                    index++;
                } else {
                    index = 0;
                }

                resetGpio();
                setGpio(index, GpioItem.STATE_HIGH);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mGpioItemAdapter.notifyDataSetChanged();
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