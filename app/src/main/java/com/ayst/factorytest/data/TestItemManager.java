package com.ayst.factorytest.data;

import android.text.TextUtils;
import android.util.Log;

import com.ayst.factorytest.model.TestItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class TestItemManager {
    private static final String TAG = "TestItemManager";

    private static final String CONFIG_PATH = "/vendor/factory_test_config.json";
    private static final String[] ITEMS_DEFAULT = {
            "信息",
            "WiFi",
            "蓝牙",
            "以太网",
            "移动网络",
            "显示",
            "触摸",
            "喇叭",
            "麦克风",
            "按键",
            "摄像头",
            "人体感应",
            "背光",
            "电池",
            "光感",
            "温湿度",
            "G-Sensor",
            "麦克风阵列",
            "GPS",
            "USB",
            "sdcard",
            "串口",
            "定时开关机",
            "看门狗",
            "GPIO",
            "韦根"
    };
    private static TestItemManager sInstance = null;
    private ArrayList<TestItem> mTestItems = new ArrayList<>();

    private TestItemManager() {
        loadConfig();
        if (mTestItems.isEmpty()) {
            Log.i(TAG, "config does not exist, use default");
            for (String name : ITEMS_DEFAULT) {
                mTestItems.add(new TestItem(name, "", TestItem.STATE_UNKNOWN));
            }
        }
    }

    public static TestItemManager getInstance() {
        if (sInstance == null) {
            sInstance = new TestItemManager();
        }
        return sInstance;
    }

    public ArrayList<TestItem> getTestItems() {
        return mTestItems;
    }

    private void loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            Log.w(TAG, "loadConfig, file does not exist");
            return;
        }

        Log.i(TAG, "loadConfig, file: " + CONFIG_PATH);

        try {
            FileInputStream is = new FileInputStream(configFile);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String configStr = new String(buffer);

            Log.i(TAG, "loadConfig, config: " + configStr);

            parse(configStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("name");
                    String param = obj.getString("param");
                    mTestItems.add(new TestItem(name, param, TestItem.STATE_UNKNOWN));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
