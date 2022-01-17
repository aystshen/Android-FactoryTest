package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
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

@SuppressLint("LongLogTag")
public class TemperatureSensorTestActivity extends ChildTestActivity {
    private static final String TAG = "TemperatureSensorTestActivity";

    @BindView(R.id.dashboard_t)
    DashboardView mTemperatureDashBoard;
    @BindView(R.id.dashboard_h)
    DashboardView mHumidityDashBoard;

    private SensorManager mSensorManager;
    private SensorEventListener mTemperatureSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_temperature_sensor_test;
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
            // Temperature Sensor
            Sensor TemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            if (TemperatureSensor != null) {
                Log.i(TAG, "TemperatureSensor>>>>");
                mTemperatureSensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        Log.i(TAG, "onSensorChanged, value: " + event.values[0]);
                        int t = (int) event.values[0] >> 16 & 0xffff;
                        int h = (int) event.values[0] & 0xffff;
                        float temperature = t * 175f / 65535 - 45; //温度
                        float humidity = h * 100f / 65535; //湿度
                        mTemperatureDashBoard.setValue((int)temperature);
                        mHumidityDashBoard.setValue((int)humidity);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                mSensorManager.registerListener(mTemperatureSensorEventListener, TemperatureSensor, SensorManager.SENSOR_DELAY_GAME);
            } else {
                Log.e(TAG, "TemperatureSensor is null");
            }
        }
    }

    private void stop() {
        if (null != mSensorManager) {
            if (null != mTemperatureSensorEventListener) {
                mSensorManager.unregisterListener(mTemperatureSensorEventListener);
            }
        }
    }
}