package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.utils.AppUtils;

import butterknife.BindView;

public class MobileNetTestActivity extends ChildTestActivity {
    private static final String TAG = "MobileNetTestActivity";

    private static final String NET_DISCONNECT = "Disconnect";
    private static final String NET_WIFI = "WiFi";
    private static final String NET_ETHERNET = "Ethernet";
    private static final String NET_2G = "2G";
    private static final String NET_3G = "3G";
    private static final String NET_4G = "4G";
    private static final String NET_5G = "5G";
    private static final String NET_UNKNOWN = "Unknown";

    @BindView(R.id.tv_type)
    TextView mTypeTv;
    @BindView(R.id.tv_signal)
    TextView mSignalTv;
    @BindView(R.id.tv_imei)
    TextView mImeiTv;

    private Handler mHandler;

    private ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            Log.d(TAG, "ConnectivityManager->onCapabilitiesChanged");
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) { //WIFI
                updateState(true, networkCapabilities);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) { //移动数据
                updateState(true, networkCapabilities);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) { //以太网
                updateState(true, networkCapabilities);
            } else {
                updateState(false, null);
            }
        }

        public void onLost(Network network) {
            Log.d(TAG, "ConnectivityManager->onLost");
            updateState(false, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(getMainLooper());
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_modem_test;
    }

    @Override
    public void initViews() {
        super.initViews();

//        mSignalTv.setText();
        mImeiTv.setText(AppUtils.getIMEI(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerNetworkCallback(this);
    }

    @Override
    protected void onStop() {
        unregisterNetworkCallback(this);

        super.onStop();
    }

    private void registerNetworkCallback(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
            builder.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);
            cm.registerNetworkCallback(builder.build(), mNetworkCallback);
        }
    }

    private void unregisterNetworkCallback(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            cm.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    private void updateState(boolean isConnected, NetworkCapabilities capabilities) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mTypeTv.setText(getNetworkType(MobileNetTestActivity.this, capabilities));
                    } else {
                        mTypeTv.setText(getNetworkType(MobileNetTestActivity.this));
                    }
                } else {
                    mTypeTv.setText(NET_DISCONNECT);
                }
            }
        });
    }

    private String getNetworkType(Context context) {
        String state = NET_UNKNOWN;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    state = NET_WIFI;
                    break;

                case ConnectivityManager.TYPE_ETHERNET:
                    state = NET_ETHERNET;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            state = NET_2G;
                            break;

                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            state = NET_3G;
                            break;

                        case TelephonyManager.NETWORK_TYPE_LTE:
                            state = NET_4G;
                            break;

                        case TelephonyManager.NETWORK_TYPE_NR:
                            state = NET_5G;
                            break;

                        default:
                            Log.e(TAG, "getNetworkType, TelephonyManager type error");
                            state = NET_UNKNOWN;
                    }
                    break;
                default:
                    Log.e(TAG, "getNetworkType, ConnectivityManager type error");
                    state = NET_UNKNOWN;
            }
        }

        return state;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getNetworkType(Context context, NetworkCapabilities capabilities) {
        String state = NET_UNKNOWN;

        if (capabilities == null) {
            Log.w(TAG, "getNetworkType, NetworkCapabilities is null");
            return NET_DISCONNECT;
        }

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            state = NET_WIFI;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            state = NET_ETHERNET;
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            @SuppressLint("MissingPermission") int type = tm.getDataNetworkType();
            if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                return NET_UNKNOWN;
            }

            Log.i(TAG, "getNetworkType, type: " + type);

            switch (type) {
                case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    state = NET_2G;
                    break;

                case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    state = NET_3G;
                    break;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    state = NET_4G;
                    break;

                case TelephonyManager.NETWORK_TYPE_NR:
                    state = NET_5G;
                    break;

                default:
                    Log.e(TAG, "getNetworkType, type error");
                    state = NET_UNKNOWN;
                    break;
            }
        } else {
            Log.e(TAG, "getNetworkType, NetworkCapabilities type err");
            state = NET_DISCONNECT;
        }

        return state;
    }
}