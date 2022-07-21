package com.ayst.factorytest.items;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.StringItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.google.gson.Gson;
import com.xuexiang.xui.utils.WidgetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;

@RequiresApi(api = Build.VERSION_CODES.M)
public class UsbTestActivity extends ChildTestActivity {
    private static final String TAG = "UsbTestActivity";

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private Gson mGson = new Gson();
    private StringItemAdapter mStringItemAdapter;
    private ArrayList<String> mItems = new ArrayList<>();
    private BroadcastReceiver mUsbStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getUsbDevices();
                }
            }, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_usb_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mStringItemAdapter = new StringItemAdapter();
        mStringItemAdapter.setList(mItems);
        mItemsRv.setAdapter(mStringItemAdapter);

        getUsbDevices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction("android.hardware.usb.action.USB_STATE");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mUsbStateChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mUsbStateChangeReceiver);

        super.onStop();
    }

    private void getUsbDevices() {
        mItems.clear();

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        ArrayList<String> deviceNames = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            mItems.add(device.getDeviceName());
            deviceNames.add(device.getDeviceName());
        }
        updateResult(mGson.toJson(deviceNames));

        mStringItemAdapter.setList(mItems);
        mStringItemAdapter.notifyDataSetChanged();
    }
}