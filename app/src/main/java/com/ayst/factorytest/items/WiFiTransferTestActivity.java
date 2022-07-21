package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.WiFiTransferParam;
import com.ayst.factorytest.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.topband.tbapi.utils.ShellUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

@SuppressLint("LongLogTag")
public class WiFiTransferTestActivity extends ChildTestActivity {
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;
    private static final String TAG = "WiFiTransferTestActivity";
    private static final int COMMAND_UPDATE_INFO = 1;
    @BindView(R.id.tv_prompt)
    TextView mPromptTv;
    @BindView(R.id.btn_start)
    ToggleButton mStartBtn;
    @BindView(R.id.rb_up)
    RadioButton mUpRdoBtn;
    @BindView(R.id.rb_down)
    RadioButton mDownRdoBtn;
    @BindView(R.id.tv_local_ip)
    TextView mLocalIpTv;
    @BindView(R.id.edt_server_ip)
    EditText mServerIpEdt;
    @BindView(R.id.tv_info)
    TextView mInfoTv;

    private int mNetworkId;
    private String mLocalIp;
    private WifiManager mWiFiManager;
    private IperfThread mIperfThread;
    private InfoHandler mInfoHandler = new InfoHandler(Looper.getMainLooper());
    private Gson mGson = new Gson();
    private WiFiTransferParam mWiFiTransferParam;
    private WifiReceiver mWifiReceiver;

    private static boolean isCorrectIp(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    private static String convertToQuotedString(@NonNull String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        int lastPos = string.length() - 1;
        if (lastPos < 0 || (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string;
        }

        return "\"" + string + "\"";
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_wifi_transfer_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (!TextUtils.isEmpty(mTestItem.getParam())) {
            mWiFiTransferParam = parseParam(mTestItem.getParam());
        } else {
            mWiFiTransferParam = new WiFiTransferParam();
        }

        mPromptTv.setText(String.format(getString(R.string.wifi_transfer_test_prompt), mWiFiTransferParam.getSsid()));

        mWiFiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWiFiManager.setWifiEnabled(true)) {
            mWiFiManager.startScan();
            WifiInfo wifiInfo = mWiFiManager.getConnectionInfo();

            List<ScanResult> scanResults = mWiFiManager.getScanResults();
            for (ScanResult result : scanResults) {
                if (TextUtils.equals(result.SSID, mWiFiTransferParam.getSsid())) {
                    boolean connected = false;
                    String ssid = mWiFiTransferParam.getSsid();
                    WifiConfiguration config = checkExist(ssid);

                    if (config != null) {
                        disconnectWifi(wifiInfo.getNetworkId());
                        connected = reconnectWifi(config);
                    } else {
                        disconnectWifi(wifiInfo.getNetworkId());
                        connected = connectWifi(createWifiInfo(ssid,
                                mWiFiTransferParam.getPassword(),
                                getSecurity(result)), ssid);
                    }

                    if (connected) {
                        Toast.makeText(this, String.format(getString(R.string.wifi_transfer_test_connect_success),
                                ssid), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, String.format(getString(R.string.wifi_transfer_test_connect_fail),
                                ssid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        mServerIpEdt.setText(mWiFiTransferParam.getServerip());

        mStartBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (mDownRdoBtn.isChecked()) {
                        start("/data/iperf -s\n");
                        AlertDialog dialog = new AlertDialog.Builder(WiFiTransferTestActivity.this)
                                .setTitle(getString(R.string.note))
                                .setMessage(String.format(getString(R.string.wifi_transfer_test_msg_downlink), mLocalIp))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                    } else if (mUpRdoBtn.isChecked()) {
                        String serverip = mServerIpEdt.getText().toString();
                        if (!TextUtils.isEmpty(serverip) && isCorrectIp(serverip)) {
                            AlertDialog dialog = new AlertDialog.Builder(WiFiTransferTestActivity.this)
                                    .setTitle(getString(R.string.note))
                                    .setMessage(getString(R.string.wifi_transfer_test_msg_uplink))
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            start("/data/iperf -c " + serverip + " -i 1 -t 60 -w 1m\n");
                                            dialogInterface.dismiss();
                                        }
                                    }).create();
                            dialog.show();
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                        } else {
                            Toast.makeText(WiFiTransferTestActivity.this,
                                    getString(R.string.wifi_transfer_test_serverip_invalid),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    stop();
                }
            }
        });
    }



    private WiFiTransferParam parseParam(String param) {
        Type type = new TypeToken<WiFiTransferParam>() {
        }.getType();
        return mGson.fromJson(param, type);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mWifiReceiver);
        stop();
        super.onDestroy();
    }

    private void start(String cmd) {
        if (!binExist("iperf")) {
            if (!copyBin("iperf")) {
                Log.e(TAG, "start, install iperf error");
                return;
            }
        }

        mIperfThread = new IperfThread(cmd);
        mIperfThread.start();
    }

    private void stop() {
        if (mIperfThread != null) {
            mIperfThread.interrupt();
        }
    }

    private boolean binExist(String name) {
        File bin = new File(AppUtils.getExternalRootDir(this)
                + File.separator + name);
        return bin.exists();
    }

    private boolean copyBin(String name) {
        File desFile = new File(AppUtils.getExternalRootDir(this)
                + File.separator + name);
        if (copyAssetFile("bin/" + name, desFile)) {
            ShellUtils.CommandResult result1 = ShellUtils.execCmd(
                    "cp " + desFile.getAbsolutePath() + " /data/" + name,
                    true);
            Log.i(TAG, "copyBin, cp result: " + result1.toString());

            ShellUtils.CommandResult result2 = ShellUtils.execCmd(
                    "chmod 777 /data/" + name,
                    true);
            Log.i(TAG, "copyBin, chmod result: " + result2.toString());

            return result1.result >= 0 && TextUtils.isEmpty(result1.errorMsg)
                    && result2.result >= 0 && TextUtils.isEmpty(result2.errorMsg);
        }
        return false;
    }

    private boolean copyAssetFile(String from, File to) {
        if (!TextUtils.isEmpty(from) && to != null) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = getResources().getAssets().open(from);
                os = new FileOutputStream(to);

                int count;
                byte[] bytes = new byte[1024];
                while ((count = is.read(bytes)) != -1) {
                    os.write(bytes, 0, count);
                }
                return true;
            } catch (IOException e) {
                Log.e(TAG, "copyFile, copy file error: " + e.getMessage());
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "copyFile, close file error: " + e.getMessage());
                }
            }
        }
        return false;
    }

    private WifiConfiguration createWifiInfo(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        switch (type) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                int length = password.length();
                if ((length == 10 || length == 26 || length == 58) &&
                        password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
                break;

            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = '"' + password + '"';
                }
                break;

            case SECURITY_EAP:
                Log.e(TAG, "createWifiInfo, no support SECURITY_EAP");
                break;

            default:
                return null;
        }
        return config;
    }

    private boolean connectWifi(WifiConfiguration config, String ssid) {
        WifiConfiguration tempConfig = checkExist(ssid);
        if (tempConfig != null) {
            mWiFiManager.removeNetwork(tempConfig.networkId);
        }

        mNetworkId = mWiFiManager.addNetwork(config);
        if (mNetworkId != -1) {
            boolean enabled = mWiFiManager.enableNetwork(mNetworkId, true);
            boolean connected = mWiFiManager.reconnect();
            Log.d(TAG, "connectWifi, mNetworkId=" + mNetworkId + ", enabled=" + enabled + ", connected=" + connected);
            return connected;
        }

        return false;
    }

    public boolean reconnectWifi(WifiConfiguration config) {
        mNetworkId = mWiFiManager.updateNetwork(config);
        if (mNetworkId != -1) {
            boolean enabled = mWiFiManager.enableNetwork(mNetworkId, true);
            boolean connected = mWiFiManager.reconnect();
            Log.d(TAG, "reconnectWifi, mNetworkId=" + mNetworkId + ", enabled=" + enabled + ", connected=" + connected);
            return connected;
        }

        return false;
    }

    public void disconnectWifi(int netId) {
        mWiFiManager.disableNetwork(netId);
        mWiFiManager.disconnect();
    }

    private void removeWifi(int netId) {
        disconnectWifi(netId);
        mWiFiManager.removeNetwork(netId);
        mWiFiManager.saveConfiguration();
    }

    @SuppressLint("MissingPermission")
    private WifiConfiguration checkExist(String ssid) {
        List<WifiConfiguration> existingConfigs = mWiFiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private class InfoHandler extends Handler {
        InfoHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COMMAND_UPDATE_INFO:
                    String line = (String) msg.obj;
                    mInfoTv.append(Html.fromHtml(line));
                    mInfoTv.append("\n");
                    int offset = mInfoTv.getLineCount() * mInfoTv.getLineHeight();
                    if (offset > mInfoTv.getHeight()) {
                        mInfoTv.scrollTo(0, offset - mInfoTv.getHeight());
                    }
                    break;
            }
        }
    }

    private class IperfThread extends Thread {
        private String cmd;

        public IperfThread(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            Log.i(TAG, "iperf run...");

            DataOutputStream dos = null;
            try {
                Process process = Runtime.getRuntime().exec("sh");
                dos = new DataOutputStream(process.getOutputStream());

                dos.write(cmd.getBytes(Charset.forName("utf-8")));
                dos.flush();

                String line;
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                while (!this.isInterrupted() && (line = bufferedReader.readLine()) != null) {
                    Log.i(TAG, "iperf: " + line);
                    Message msg = new Message();
                    msg.what = COMMAND_UPDATE_INFO;
                    msg.obj = line;
                    mInfoHandler.sendMessage(msg);
                }

                dos.writeBytes("exit\n");
                dos.flush();
                Log.w(TAG, "iperf exit.");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                try {
                    if (dos != null) {
                        dos.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState state = info.getDetailedState();
                Log.d(TAG, "WifiReceiver, state=" + state.toString());
                if (state == NetworkInfo.DetailedState.CONNECTED) {
                    mLocalIp = Formatter.formatIpAddress(mWiFiManager.getDhcpInfo().ipAddress);
                    if (isCorrectIp(mLocalIp)) {
                        mStartBtn.setEnabled(true);
                        mDownRdoBtn.setChecked(true);
                        mStartBtn.setChecked(true);
                        mLocalIpTv.setText(mLocalIp);
                    }
                }
            }
        }
    }
}
