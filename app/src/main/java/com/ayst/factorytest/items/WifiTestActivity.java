package com.ayst.factorytest.items;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.WifiListAdapter;
import com.ayst.factorytest.base.ChildTestActivity;

import java.util.List;

import butterknife.BindView;

public class WifiTestActivity extends ChildTestActivity {
    private static final String TAG = "WifiTestActivity";

    @BindView(R.id.lv_wifi)
    ListView mWifiLv;

    private Handler mHandler;

    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private WifiReceiver mWifiReceiver;
    private WifiListAdapter mWifiAdapter;
    private NetworkInfo.DetailedState mLastState = null;

    private ConnectivityManager mConnManager;
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiReceiver();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_wifi_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "start...");

        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);

        if (mWifiManager.setWifiEnabled(true)) {
            scan();
            mWifiList = mWifiManager.getScanResults();
            if (!mWifiList.isEmpty()) {
                mWifiLv.setVisibility(View.VISIBLE);
            }
            mWifiInfo = mWifiManager.getConnectionInfo();
        }
        mWifiAdapter = new WifiListAdapter(this, mWifiList, mWifiInfo);
        mWifiLv.setAdapter(mWifiAdapter);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scan();
                mHandler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "stop...");

        unregisterReceiver(mWifiReceiver);

        super.onStop();
    }

    private void scan() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
        }
    }

    private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "handleEvent, action:" + action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
//                WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action) ||
//                WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
            updateAccessPoints();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState state = (SupplicantState) intent.getParcelableExtra(
                    WifiManager.EXTRA_NEW_STATE);
            updateConnectionState(WifiInfo.getDetailedStateOf(state));
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
                    WifiManager.EXTRA_NETWORK_INFO);
            updateConnectionState(info.getDetailedState());
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            updateConnectionState(null);
        }
    }

    private void updateWifiState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                scan();
                break;

            case WifiManager.WIFI_STATE_ENABLING:
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                mWifiAdapter.updateList(null);
                mWifiAdapter.notifyDataSetChanged();
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                break;
        }
    }

    private void updateAccessPoints() {
        mWifiList = mWifiManager.getScanResults();
        if (!mWifiList.isEmpty()) {
            mWifiLv.setVisibility(View.VISIBLE);
        }
        mWifiAdapter.updateList(mWifiList);
        mWifiAdapter.notifyDataSetChanged();
    }

    private void updateConnectionState(NetworkInfo.DetailedState state) {
        if (state != null) {
            Log.d(TAG, "updateConnectionState, state=" + state.toString());
            mLastState = state;
            mWifiInfo = mWifiManager.getConnectionInfo();
            mWifiAdapter.updateStatus(mWifiInfo, mLastState);
            mWifiAdapter.notifyDataSetChanged();

            if (state == NetworkInfo.DetailedState.CONNECTED) {
                mWifiManager.saveConfiguration();
            }
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            handleEvent(context, intent);
        }
    }
}