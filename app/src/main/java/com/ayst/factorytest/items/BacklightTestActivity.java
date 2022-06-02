package com.ayst.factorytest.items;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

import butterknife.BindView;

public class BacklightTestActivity extends ChildTestActivity {
    private static final String TAG = "BacklightTestActivity";

    @BindView(R.id.seekbar_backlight)
    SeekBar mBacklightSeekBar;

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

        updateParam(String.format("{'brightness':%d}", getBrightness()));
        mBacklightSeekBar.setProgress(getBrightness());
        mBacklightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                App.getTBManager().setBrightness(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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