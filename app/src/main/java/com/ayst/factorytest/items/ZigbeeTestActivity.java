package com.ayst.factorytest.items;

import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.UartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZigbeeTestActivity extends ChildTestActivity {
    private static final String TAG = "ZigbeeTestActivity";
    private static final String PARAM_DEFAULT = "[{'name': '红色LED', 'device': 'led_r', 'brightness': 1000, 'state': 0}, " +
            "{'name': '绿色LED', 'device': 'led_g', 'brightness': 1000, 'state': 0}, " +
            "{'name': '蓝色LED', 'device': 'led_b', 'brightness': 1000, 'state': 0}]";
    ;


    private String LINE_SEP = System.getProperty("line.separator");
    private Gson mGson = new Gson();
    private ArrayList<UartItem> mUartItems;
    private UartItem mUart = null;
    private ReadInfoThread readInfoThread = null;
    private BufferedReader successResult;
    private BufferedReader errorResult;
    private StringBuilder successMsg = new StringBuilder();
    ;
    private Process process = null;
    private DataOutputStream os = null;
    private DataInputStream is = null;
    private String mMac = null;
    private String mVersion = null;
    private String[] command = {
            "Z3Gateway -f x -b 115200 -p /dev/ttyS3",
            "info",
            "version",
            "net leave",
            "plugin network-steering mask set 1 0x800",
            "plugin network-steering mask set 2 0x800",
            "plugin network-steering start 1",
            "reset"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = findViewById(R.id.info_zigbee);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        process = getShell(true);
        if (process == null)
            return;

        try {
            successResult = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8")
            );
            errorResult = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new ReadInfoThread(successResult).start();
        new ReadInfoThread(errorResult).start();
        new WriteCommandThread().start();

    }

    private ArrayList<UartItem> parseParam(String param) {
        Type collectionType = new TypeToken<Collection<UartItem>>() {
        }.getType();
        return mGson.fromJson(param, collectionType);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_zigbee_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    private class WriteCommandThread extends Thread {
        @Override
        public void run() {
            os = new DataOutputStream(process.getOutputStream());
            try {
                for (String cmd :
                        command) {
                    os.writeBytes(LINE_SEP);
                    os.write(cmd.getBytes());
                    os.writeBytes(LINE_SEP);
                    os.flush();

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class ReadInfoThread extends Thread {
        public Boolean flag = true;

        BufferedReader bfreadResult;
        String tt = "readinfo";

        ReadInfoThread(BufferedReader bufferedReader) {
            bfreadResult = bufferedReader;
            tt = bfreadResult == errorResult ? "error_std" : tt;
        }

        @Override
        public void run() {

            try {
                String line;
                while (flag) {
                    line = bfreadResult.readLine();
                    Log.i(TAG, tt + " +" + line);
                    ZigbeeInfoShow show1 = new ZigbeeInfoShow(line + LINE_SEP);
                    runOnUiThread(show1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private int getTextViewHeight(TextView view) {
        Layout layout = view.getLayout();
        if (layout == null)
            return 0;
        int desired = layout.getLineTop(view.getLineCount());
        int padding = view.getCompoundPaddingTop() + view.getCompoundPaddingBottom();
        return desired + padding;
    }

    class ZigbeeInfoShow implements Runnable {
        String info;

        ZigbeeInfoShow(String line) {
            info = line;
        }

        @Override
        public void run() {
            TextView info_zigbee = findViewById(R.id.info_zigbee);
            TextView zigbee_mac = findViewById(R.id.zigbee_mac);
            TextView zigbee_version = findViewById(R.id.zigbee_version);
            info_zigbee.append(info);
            String regMAC = "\\[\\(>\\).{16}\\]";
            String regVER = "\\[.+ GA build .+\\]";

            int offset = getTextViewHeight(info_zigbee);
            if (offset > info_zigbee.getHeight()) {
                info_zigbee.scrollTo(0, offset - info_zigbee.getHeight());
            }
            Pattern patten = Pattern.compile(regMAC);
            Matcher matcher = patten.matcher(info);
            while (matcher.find()) {
                String tt = matcher.group();
                if (tt.length() == 21) {
                    mMac = tt.substring(4, 20);
                    zigbee_mac.setText("MAC: [" + mMac + "]");
                }
                Log.i(TAG, "zigbee_mac = " + tt);
            }
            patten = Pattern.compile(regVER);
            matcher = patten.matcher(info);
            while (matcher.find()) {
                mVersion = matcher.group();
                zigbee_version.setText("VER: " + mVersion);
                Log.i(TAG, "zigbee_version = " + mVersion);
                String tmp = String.format("{'mac':%s, 'version':%s}", mMac, mVersion);
                Log.i(TAG, "updateResult  tmp = " + tmp);
                updateResult(tmp);
            }
        }
    }

    public Process getShell(final boolean isRooted) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return process;
    }
}
