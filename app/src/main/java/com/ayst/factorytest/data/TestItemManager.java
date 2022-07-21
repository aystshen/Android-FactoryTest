package com.ayst.factorytest.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ayst.factorytest.R;
import com.ayst.factorytest.items.AccSensorActivity;
import com.ayst.factorytest.items.BacklightTestActivity;
import com.ayst.factorytest.items.BatteryTestActivity;
import com.ayst.factorytest.items.BluetoothTestActivity;
import com.ayst.factorytest.items.CameraTestActivity;
import com.ayst.factorytest.items.EthernetTestActivity;
import com.ayst.factorytest.items.GpioTestActivity;
import com.ayst.factorytest.items.HumanSensorActivity;
import com.ayst.factorytest.items.InfoTestActivity;
import com.ayst.factorytest.items.KeyTestActivity;
import com.ayst.factorytest.items.LcdTestActivity;
import com.ayst.factorytest.items.LedTestActivity;
import com.ayst.factorytest.items.LightSensorActivity;
import com.ayst.factorytest.items.MobileNetTestActivity;
import com.ayst.factorytest.items.ND01TestActivity;
import com.ayst.factorytest.items.NarTestActivity;
import com.ayst.factorytest.items.PwmTestActivity;
import com.ayst.factorytest.items.RecordTestActivity;
import com.ayst.factorytest.items.RegularBootTestActivity;
import com.ayst.factorytest.items.SdcardTestActivity;
import com.ayst.factorytest.items.SpeakerTestActivity;
import com.ayst.factorytest.items.TemperatureSensorTestActivity;
import com.ayst.factorytest.items.TouchTestActivity;
import com.ayst.factorytest.items.UartTestActivity;
import com.ayst.factorytest.items.UsbTestActivity;
import com.ayst.factorytest.items.WatchdogTestActivity;
import com.ayst.factorytest.items.WiFiTransferTestActivity;
import com.ayst.factorytest.items.WiegandTestActivity;
import com.ayst.factorytest.items.WifiTestActivity;
import com.ayst.factorytest.model.KeyItem;
import com.ayst.factorytest.model.NarParam;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestItemManager {
    private static final String TAG = "TestItemManager";

    private static final String CONFIG_PATH = "/vendor/factory_test_config.json";

    private static TestItemManager sInstance = null;
    private static Context sContext;
    private Gson mGson = new Gson();
    private ArrayList<TestItem> mTestItems = new ArrayList<>();
    private LinkedHashMap<String, TestItem> mItemTargets = new LinkedHashMap<>();

    private TestItemManager(Context context) {
        sContext = context;
        bindTargets();
    }

    public static TestItemManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TestItemManager(context);
        }
        return sInstance;
    }

    public ArrayList<TestItem> loadTestItems() {
        mTestItems.clear();

        loadConfig();

        if (mTestItems.isEmpty()) {
            Log.i(TAG, "config does not exist, use default");
            for (Map.Entry<String, TestItem> entry : mItemTargets.entrySet()) {
                mTestItems.add(entry.getValue());
            }
        }
        return mTestItems;
    }

    private void bindTargets() {
        mItemTargets.put("info", new TestItem(sContext.getString(R.string.test_item_info), InfoTestActivity.class));

        // 全自动测试项（自动测试项放前面）
        mItemTargets.put("wifi", new TestItem(sContext.getString(R.string.test_item_wifi), WifiTestActivity.class));
        mItemTargets.put("bt", new TestItem(sContext.getString(R.string.test_item_bluetooth), BluetoothTestActivity.class));
        mItemTargets.put("eth", new TestItem(sContext.getString(R.string.test_item_ethernet), EthernetTestActivity.class));
        mItemTargets.put("mobile", new TestItem(sContext.getString(R.string.test_item_mobile), MobileNetTestActivity.class));
        mItemTargets.put("timingboot", new TestItem(sContext.getString(R.string.test_item_timingboot), RegularBootTestActivity.class));
        mItemTargets.put("watchdog", new TestItem(sContext.getString(R.string.test_item_watchdog), WatchdogTestActivity.class));
        mItemTargets.put("uart", new TestItem(sContext.getString(R.string.test_item_uart), UartTestActivity.class));

        // 半自动测试项
        mItemTargets.put("human", new TestItem(sContext.getString(R.string.test_item_humansensor), HumanSensorActivity.class));
        mItemTargets.put("acc", new TestItem(sContext.getString(R.string.test_item_accsensor), AccSensorActivity.class));

        // 人工测试项（人工测试项放后面）
        mItemTargets.put("display", new TestItem(sContext.getString(R.string.test_item_display), LcdTestActivity.class));
        mItemTargets.put("touch", new TestItem(sContext.getString(R.string.test_item_touch), TouchTestActivity.class));
        mItemTargets.put("spk", new TestItem(sContext.getString(R.string.test_item_speader), SpeakerTestActivity.class));
        mItemTargets.put("mic", new TestItem(sContext.getString(R.string.test_item_mic), RecordTestActivity.class));
        mItemTargets.put("micarray", new TestItem(sContext.getString(R.string.test_item_micarray), NarTestActivity.class));
        mItemTargets.put("key", new TestItem(sContext.getString(R.string.test_item_key), KeyTestActivity.class));
        mItemTargets.put("camera", new TestItem(sContext.getString(R.string.test_item_camera), CameraTestActivity.class));
        mItemTargets.put("backlight", new TestItem(sContext.getString(R.string.test_item_backlight), BacklightTestActivity.class));
        mItemTargets.put("battery", new TestItem(sContext.getString(R.string.test_item_battery), BatteryTestActivity.class));
        mItemTargets.put("light", new TestItem(sContext.getString(R.string.test_item_light), LightSensorActivity.class));
        mItemTargets.put("temp", new TestItem(sContext.getString(R.string.test_item_tempsensor), TemperatureSensorTestActivity.class));
        mItemTargets.put("usb", new TestItem(sContext.getString(R.string.test_item_usb), UsbTestActivity.class));
        mItemTargets.put("sd", new TestItem(sContext.getString(R.string.test_item_sdcard), SdcardTestActivity.class));
        mItemTargets.put("gpio", new TestItem(sContext.getString(R.string.test_item_gpio), GpioTestActivity.class));
        mItemTargets.put("wiegand", new TestItem(sContext.getString(R.string.test_item_wiegand), WiegandTestActivity.class));
        mItemTargets.put("nd01", new TestItem(sContext.getString(R.string.test_item_nd01), ND01TestActivity.class));
        mItemTargets.put("pwm", new TestItem(sContext.getString(R.string.test_item_pwm), PwmTestActivity.class));
        mItemTargets.put("led", new TestItem(sContext.getString(R.string.test_item_led), LedTestActivity.class));
        mItemTargets.put("wifitransfer", new TestItem(sContext.getString(R.string.test_item_wifi_transfer),
                WiFiTransferTestActivity.class));
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
                Type type = new TypeToken<TestItem>() {
                }.getType();
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String key = obj.getString("key");
                    String param = obj.getString("param");
                    if (mItemTargets.get(key) != null) {
                        String history = SPUtils.get(sContext).getData(key, "");
                        if (!TextUtils.isEmpty(history)) {
                            TestItem item = mGson.fromJson(history, type);
                            mTestItems.add(new TestItem(key, mItemTargets.get(key).getName(), param,
                                    item.getResult(), mItemTargets.get(key).getActivity(), item.getState()));
                        } else {
                            mTestItems.add(new TestItem(key, mItemTargets.get(key).getName(), param,
                                    "", mItemTargets.get(key).getActivity(), TestItem.STATE_UNKNOWN));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
