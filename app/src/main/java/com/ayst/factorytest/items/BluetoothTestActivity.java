package com.ayst.factorytest.items;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;

import java.util.ArrayList;

import butterknife.BindView;

public class BluetoothTestActivity extends ChildTestActivity {
    private static final String TAG = "BluetoothTestActivity";

    @BindView(R.id.lv_bt)
    ListView mDeviceLv;

    private DeviceListAdapter mDeviceListAdapter = null;
    private ArrayList<ScanResult> mData = new ArrayList<ScanResult>();
    private BluetoothAdapter mBluetoothAdapter;

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    BluetoothDevice device = result.getDevice();
                    String name = device.getName();
                    Log.i(TAG, "onLeScan, found: " + name);

                    for (int i = 0; i < mData.size(); i++) {
                        ScanResult tmp = mData.get(i);
                        if (tmp.getDevice().getAddress().equals(device.getAddress())) {
                            mData.set(i, new ScanResult(tmp.getDevice(), result.getRssi()));
                            mDeviceListAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                    mData.add(new ScanResult(device, result.getRssi()));
                    mDeviceListAdapter.notifyDataSetChanged();
                    mDeviceLv.setVisibility(View.VISIBLE);

                    updateResult(String.format("{'device':'%s', 'rssi':%d}", device.getAddress(), result.getRssi()));
                    finish(TestItem.STATE_SUCCESS);
                }
            });
        }
    };

    private BroadcastReceiver mBTStateChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive, ACTION:" + action);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (btState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        scan(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        scan(false);
                        mData.clear();
                        mDeviceListAdapter.notifyDataSetChanged();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        break;
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                String name = device.getName();
                Log.i(TAG, "scan, found: " + name);

//                if (TextUtils.isEmpty(device.getName())) {
//                    return;
//                }

                for (int i = 0; i < mData.size(); i++) {
                    ScanResult tmp = mData.get(i);
                    if (tmp.getDevice().getAddress().equals(device.getAddress())) {
                        mData.set(i, new ScanResult(tmp.getDevice(), rssi));
                        mDeviceListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
                mData.add(new ScanResult(device, rssi));
                mDeviceListAdapter.notifyDataSetChanged();
                mDeviceLv.setVisibility(View.VISIBLE);

                finish(TestItem.STATE_SUCCESS);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scan(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "not supported");
        }
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_bluetooth_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        mSuccessBtn.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mBluetoothAdapter == null) {
            return;
        }
        registerReceiver(mBTStateChangeReceiver, makeFilter());

        mDeviceListAdapter = new DeviceListAdapter();
        mDeviceLv.setAdapter(mDeviceListAdapter);

        if (mBluetoothAdapter.enable()) {
            scan(true);
        }
    }

    @Override
    public void onStop() {
        if (mBluetoothAdapter == null) {
            return;
        }
        scan(false);
        unregisterReceiver(mBTStateChangeReceiver);

        super.onStop();
    }

    private void scan(final boolean enable) {
        if (mBluetoothAdapter.isEnabled()) {
            //scanClassicalDevice(enable);
            scanLeDevice(enable);
        }
    }

    private void scanClassicalDevice(final boolean enable) {
        if (enable) {
            if (!mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.startDiscovery();
            }
        } else {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }

    private void scanLeDevice(final boolean enable) {
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            scanner.startScan(mLeScanCallback);
        } else {
            scanner.stopScan(mLeScanCallback);
        }
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return filter;
    }

    class ScanResult {
        private BluetoothDevice mDevice = null;
        private int mRssi = 0;

        ScanResult(BluetoothDevice device, int rssi) {
            mDevice = device;
            mRssi = rssi;
        }

        BluetoothDevice getDevice() {
            return mDevice;
        }

        int getRssi() {
            return mRssi;
        }
    }

    private class DeviceListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

        DeviceListAdapter() {
            mInflater = LayoutInflater.from(BluetoothTestActivity.this);
        }

        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (mData != null) {
                return mData.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.device_item, null);
                holder = new ViewHolder();
                holder.mMainTv = (TextView) view.findViewById(R.id.tv_main);
                holder.mSubOneTv = (TextView) view.findViewById(R.id.tv_sub_one);
                holder.mSubTwoTv = (TextView) view.findViewById(R.id.tv_sub_two);
                holder.mIconIv = (ImageView) view.findViewById(R.id.iv_icon);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (!TextUtils.isEmpty(mData.get(i).getDevice().getName())) {
                holder.mMainTv.setText(mData.get(i).getDevice().getName());
                holder.mSubOneTv.setText(mData.get(i).getDevice().getAddress());
                holder.mSubOneTv.setVisibility(View.VISIBLE);
            } else {
                holder.mMainTv.setText(mData.get(i).getDevice().getAddress());
                holder.mSubOneTv.setVisibility(View.GONE);
            }
            holder.mSubTwoTv.setText("rssi: " + mData.get(i).getRssi());
            holder.mIconIv.setImageResource(R.mipmap.ic_device);
            return view;
        }

        final class ViewHolder {
            private TextView mMainTv = null;
            private TextView mSubOneTv = null;
            private TextView mSubTwoTv = null;
            private ImageView mIconIv = null;

        }
    }
}