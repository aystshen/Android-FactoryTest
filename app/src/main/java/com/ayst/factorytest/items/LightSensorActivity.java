package com.ayst.factorytest.items;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.ayst.dbv.DashboardView;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

import butterknife.BindView;

public class LightSensorActivity extends ChildTestActivity {
    private static final String TAG = "LightSensorActivity";

    @BindView(R.id.dashboard)
    DashboardView mLightDashBoard;

    private SensorManager mSensorManager;
    private SensorEventListener mLightSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_light_sensor;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

        start();
    }

    @Override
    protected void onStop() {
        stop();

        super.onStop();
    }

    public void start() {
        if (null != mSensorManager) {
            // Light Sensor
            Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                Log.i(TAG, "lightSensor>>>>");
                mLightSensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float light = event.values[0];
                        mLightDashBoard.setValue((int)light);

                        updateResult(String.format("{'light':%f}", light));
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                mSensorManager.registerListener(mLightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.e(TAG, "lightSensor is null");
            }
        }
    }

    private void stop() {
        if (null != mSensorManager) {
            if (null != mLightSensorEventListener) {
                mSensorManager.unregisterListener(mLightSensorEventListener);
            }
        }
    }
}