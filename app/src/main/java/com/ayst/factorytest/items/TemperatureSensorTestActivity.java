package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
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

    private static final int FACTOR_BUFFER_SIZE = 60;

    @BindView(R.id.dashboard_t)
    DashboardView mTemperatureDashBoard;
    @BindView(R.id.dashboard_h)
    DashboardView mHumidityDashBoard;
    @BindView(R.id.tv_t)
    TextView mTemperatureTv;
    @BindView(R.id.tv_h)
    TextView mHumidityTv;

    private int mRateCnt = 0;
    private int mFactorIndex = 0;
    private int[] mScreenFactorArr = new int[FACTOR_BUFFER_SIZE];
    private float[] mCpuFactorArr = new float[FACTOR_BUFFER_SIZE];

    private Handler mHandler;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private SensorEventListener mTemperatureSensorEventListener;

    /**
     * 每分钟执行一次，统计屏幕、CPU对温度传感器的影响
     */
    private Runnable mMinutelyRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFactorIndex < FACTOR_BUFFER_SIZE-1) {
                mFactorIndex++;
            } else {
                mFactorIndex = 0;
            }

            // 屏幕对温度影响系数
            boolean isScreenOn = false;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                isScreenOn = mPowerManager.isScreenOn();
            } else {
                isScreenOn = mPowerManager.isInteractive();
            }
            if (isScreenOn) {
                int brightness = 0;
                try {
                    brightness = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "MinutelyRunnable, brightness=" + brightness);
                mScreenFactorArr[mFactorIndex] = brightness;
            } else {
                mScreenFactorArr[mFactorIndex] = 0;
            }

            // CPU对温度影响系数
            ShellUtils.CommandResult result = ShellUtils.execCmd(
                    "cat /sys/class/thermal/thermal_zone0/temp", false);
            if (!TextUtils.isEmpty(result.successMsg)) {
                float cpuTemp = Float.parseFloat(result.successMsg) / 1000;
                Log.d(TAG, "MinutelyRunnable, cpuTemp=" + String.format("%.1f", cpuTemp));
                mCpuFactorArr[mFactorIndex] = cpuTemp;
            }

            mHandler.postDelayed(this, 60 * 1000); // 每分钟执行一次
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(getMainLooper());
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mHandler.postDelayed(mMinutelyRunnable, 60 * 1000);

        start();
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

//        start();
    }

    @Override
    protected void onStop() {
//        stop();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stop();

        mHandler.removeCallbacks(mMinutelyRunnable);

        super.onDestroy();
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
                            float temperature = t / 100f; //温度
                            float humidity = h / 100f; //湿度
//                            float sensorTemp = t * 175f / 65535 - 45; //温度
//                            float sensorHumidity = h * 100f / 65535; //湿度
//
//                            // 计算屏幕对温度影响系数
//                            float screenFactor = 0;
//                            for (int i = 0; i < mScreenFactorArr.length; i++) {
//                                screenFactor += mScreenFactorArr[i];
//                            }
//                            screenFactor = screenFactor / mScreenFactorArr.length / 510;
//
//                            // 计算CPU对温度影响系数
//                            float cpuFactor = 0;
//                            for (int i = 0; i < mCpuFactorArr.length; i++) {
//                                cpuFactor += mCpuFactorArr[i];
//                            }
//                            cpuFactor = cpuFactor / mCpuFactorArr.length / 240;
//
//                            // 计算开机时间对温度影响系数
//                            float timeFactor = Math.min((float) SystemClock.elapsedRealtime() / 1000 / 60 / 30, 1.0f);
//
//                            // 实际温度 = (sensor温度 - 屏幕系数 - cpu系数 - 开机时间系数)
//                            //float temperature = sensorTemp - screenFactor - cpuFactor - timeFactor;
//                            float temperature = sensorTemp - cpuFactor - timeFactor;
//                            float humidity = sensorHumidity;
//
//                            Log.i(TAG, "onSensorChanged, sensorTemp=" + String.format("%.1f", sensorTemp)
//                                    + ", screenFactor=" + screenFactor
//                                    + ", cpuFactor=" + cpuFactor
//                                    + ", timeFactor=" + timeFactor
//                                    + ", temperature=" + temperature);

                            mTemperatureDashBoard.setValue((int) temperature);
                            mTemperatureTv.setText(String.format("%.1f", temperature));
                            mHumidityDashBoard.setValue((int) humidity);
                            mHumidityTv.setText(String.format("%.1f", humidity));

                            updateResult(String.format("{'temperature':%f, 'humidity':%f}", temperature, humidity));
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