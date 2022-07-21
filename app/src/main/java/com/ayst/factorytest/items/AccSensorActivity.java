package com.ayst.factorytest.items;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;

import butterknife.BindView;

public class AccSensorActivity extends ChildTestActivity {
    private static final String TAG = "AccSensorActivity";

    private static final int THRESHOLD = 7;

    @BindView(R.id.tv_x)
    TextView mXTv;
    @BindView(R.id.tv_y)
    TextView mYTv;
    @BindView(R.id.tv_z)
    TextView mZTv;

    private SensorManager mSensorManager;
    private SensorEventListener mAccSensorEventListener;
    private boolean mX = false, mY = false, mZ = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_acc_sensor;
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

    @Override
    public void initViews() {
        super.initViews();

        mSuccessBtn.setVisibility(View.GONE);
    }

    private void update(float x, float y, float z) {
        mXTv.setText(String.valueOf(x));
        mYTv.setText(String.valueOf(y));
        mZTv.setText(String.valueOf(z));
        if (Math.abs(x - y) > THRESHOLD && Math.abs(x - z) > THRESHOLD) {
            mXTv.setTextColor(getResources().getColor(R.color.green));
            mX = true;
        } else if (Math.abs(y - x) > THRESHOLD && Math.abs(y - z) > THRESHOLD) {
            mYTv.setTextColor(getResources().getColor(R.color.green));
            mY = true;
        } else if (Math.abs(z - x) > THRESHOLD && Math.abs(z - y) > THRESHOLD) {
            mZTv.setTextColor(getResources().getColor(R.color.green));
            mZ = true;
        }
        if (mX && mY && mZ) {
            finish(TestItem.STATE_SUCCESS);
        }
    }

    private void start() {
        if (null != mSensorManager) {
            // Accelerometer Sensor
            Sensor accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accSensor != null) {
                Log.i(TAG, "accSensor>>>>");
                mAccSensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        update(x, y, z);

                        updateResult(String.format("{'x':%f, 'y':%f, 'z':%f}", x, y, z));
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                mSensorManager.registerListener(mAccSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.e(TAG, "accSensor is null");
            }
        }
    }

    private void stop() {
        if (null != mSensorManager) {
            if (null != mAccSensorEventListener) {
                mSensorManager.unregisterListener(mAccSensorEventListener);
            }
        }
    }
}