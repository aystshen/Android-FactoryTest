package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.ayst.dbv.DashboardView;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.topband.tbapi.utils.ShellUtils;

import butterknife.BindView;

@SuppressLint("LongLogTag")
public class TemperatureSensorTestActivity extends ChildTestActivity {
    private static final String TAG = "TemperatureSensorTestActivity";

    @BindView(R.id.dashboard_t)
    DashboardView mTemperatureDashBoard;
    @BindView(R.id.dashboard_h)
    DashboardView mHumidityDashBoard;
    @BindView(R.id.tv_t)
    TextView mTemperatureTv;
    @BindView(R.id.tv_h)
    TextView mHumidityTv;

    private int mRateCnt = 0;
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
                        if (++mRateCnt >= 20) {
                            mRateCnt = 0;
                            //Log.i(TAG, "onSensorChanged, value: " + event.values[0]);
                            int t = (int) event.values[0] >> 16 & 0xffff;
                            int h = (int) event.values[0] & 0xffff;
                            float temperature = t * 175f / 65535 - 45 - 1.2f; //温度
                            float humidity = h * 100f / 65535; //湿度

                            ShellUtils.CommandResult result = ShellUtils.execCmd(
                                    "cat /sys/class/thermal/thermal_zone0/temp", false);
                            if (!TextUtils.isEmpty(result.successMsg)) {
                                float cpuTemp = Float.parseFloat(result.successMsg) / 1000;
                                Log.i(TAG, "onSensorChanged, cpuTemp=" + cpuTemp + " sensorTemp=" + temperature);
                            }

                            mTemperatureDashBoard.setValue((int) temperature);
                            mTemperatureTv.setText(String.format("%.1f", temperature));
                            mHumidityDashBoard.setValue((int) humidity);
                            mHumidityTv.setText(String.format("%.1f", humidity));
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                mSensorManager.registerListener(mTemperatureSensorEventListener, TemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
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