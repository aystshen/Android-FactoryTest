package com.ayst.factorytest.items;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.xuexiang.xui.widget.picker.XSeekBar;

import butterknife.BindView;

public class BacklightTestActivity extends ChildTestActivity {
    private static final String TAG = "BacklightTestActivity";

    @BindView(R.id.seekbar_backlight)
    XSeekBar mBacklightSeekBar;

    private int mBrightnessDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBrightnessDefault = getBrightness();
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_backlight_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mBacklightSeekBar.setDefaultValue(getBrightness());
        mBacklightSeekBar.setOnSeekBarListener(new XSeekBar.OnSeekBarListener() {
            @Override
            public void onValueChanged(XSeekBar seekBar, int newValue) {
                App.getTBManager().setBrightness(newValue);
            }
        });
    }

    @Override
    protected void onDestroy() {
        App.getTBManager().setBrightness(mBrightnessDefault);

        super.onDestroy();
    }

    public int getBrightness() {
        try {
            return Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            Log.e(TAG, "getBrightness, " + e.getMessage());
        }
        return 0;
    }
}