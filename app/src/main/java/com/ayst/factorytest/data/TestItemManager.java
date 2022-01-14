package com.ayst.factorytest.data;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ayst.factorytest.MainActivity;
import com.ayst.factorytest.items.BluetoothTestActivity;
import com.ayst.factorytest.items.CameraTestActivity;
import com.ayst.factorytest.items.EthernetTestActivity;
import com.ayst.factorytest.items.HumanSensorActivity;
import com.ayst.factorytest.items.InfoTestActivity;
import com.ayst.factorytest.items.KeyTestActivity;
import com.ayst.factorytest.items.LcdTestActivity;
import com.ayst.factorytest.items.MobileNetTestActivity;
import com.ayst.factorytest.items.RecordTestActivity;
import com.ayst.factorytest.items.SpeakerTestActivity;
import com.ayst.factorytest.items.TouchTestActivity;
import com.ayst.factorytest.items.WifiTestActivity;
import com.ayst.factorytest.model.TestItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestItemManager {
    private static final String TAG = "TestItemManager";

    private static final String CONFIG_PATH = "/vendor/factory_test_config.json";

    private static TestItemManager sInstance = null;
    private ArrayList<TestItem> mTestItems = new ArrayList<>();
    private LinkedHashMap<String, Class<? extends Activity>> mItemTargets = new LinkedHashMap<>();

    private TestItemManager() {
        bindTargets();
        loadConfig();
        if (mTestItems.isEmpty()) {
            Log.i(TAG, "config does not exist, use default");
            for (Map.Entry<String, Class<? extends Activity>> entry : mItemTargets.entrySet()) {
                mTestItems.add(new TestItem(entry.getKey(), "",
                        entry.getValue(), TestItem.STATE_UNKNOWN));
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

    private void bindTargets() {
        mItemTargets.put("信息", InfoTestActivity.class);
        mItemTargets.put("WiFi", WifiTestActivity.class);
        mItemTargets.put("蓝牙", BluetoothTestActivity.class);
        mItemTargets.put("以太网", EthernetTestActivity.class);
        mItemTargets.put("移动网络", MobileNetTestActivity.class);
        mItemTargets.put("显示", LcdTestActivity.class);
        mItemTargets.put("触摸", TouchTestActivity.class);
        mItemTargets.put("喇叭", SpeakerTestActivity.class);
        mItemTargets.put("麦克风", RecordTestActivity.class);
        mItemTargets.put("按键", KeyTestActivity.class);
        mItemTargets.put("摄像头", CameraTestActivity.class);
        mItemTargets.put("人体感应", HumanSensorActivity.class);
        mItemTargets.put("背光", MainActivity.class);
        mItemTargets.put("电池", MainActivity.class);
        mItemTargets.put("光感", MainActivity.class);
        mItemTargets.put("温湿度", MainActivity.class);
        mItemTargets.put("G-Sensor", MainActivity.class);
        mItemTargets.put("麦克风阵列", MainActivity.class);
        mItemTargets.put("GPS", MainActivity.class);
        mItemTargets.put("USB", MainActivity.class);
        mItemTargets.put("sdcard", MainActivity.class);
        mItemTargets.put("串口", MainActivity.class);
        mItemTargets.put("定时开关机", MainActivity.class);
        mItemTargets.put("看门狗", MainActivity.class);
        mItemTargets.put("GPIO", MainActivity.class);
        mItemTargets.put("韦根", MainActivity.class);
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
                    if (mItemTargets.get(name) != null) {
                        mTestItems.add(new TestItem(name, param, mItemTargets.get(name),
                                TestItem.STATE_UNKNOWN));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
