package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ToggleButton;

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

import butterknife.BindView;

@SuppressLint("LongLogTag")
public class WiFiTransferTestActivity extends ChildTestActivity {
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
    @BindView(R.id.edt_pc_ip)
    EditText mPcIpEdt;
    @BindView(R.id.tv_info)
    TextView mInfoTv;

    private String mLocalIp;
    private WifiManager mWiFiManager;
    private IperfThread mIperfThread;
    private InfoHandler mInfoHandler = new InfoHandler(Looper.getMainLooper());
    private Gson mGson = new Gson();
    private WiFiTransferParam mWiFiTransferParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (AppUtils.isWifiConnected(this)) {
            mLocalIp = Formatter.formatIpAddress(mWiFiManager.getDhcpInfo().ipAddress);
            if (!TextUtils.isEmpty(mLocalIp) && !TextUtils.isEmpty(mWiFiTransferParam.getServerip())) {
                mStartBtn.setEnabled(true);
            } else {
                mStartBtn.setEnabled(false);
            }
        } else {
            mStartBtn.setEnabled(false);
        }

        mLocalIpTv.setText(mLocalIp);
        mPcIpEdt.setText(mWiFiTransferParam.getServerip());

        mStartBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (mDownRdoBtn.isChecked()) {
                        start("/data/iperf -s\n");
                        AlertDialog dialog = new AlertDialog.Builder(WiFiTransferTestActivity.this)
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.wifi_transfer_test_msg_downlink))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                    } else if (mUpRdoBtn.isChecked()) {
                        AlertDialog dialog = new AlertDialog.Builder(WiFiTransferTestActivity.this)
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.wifi_transfer_test_msg_uplink))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        start("/data/iperf -c " + mWiFiTransferParam.getServerip() + " -i 1 -t 60 -w 1m\n");
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
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
}
