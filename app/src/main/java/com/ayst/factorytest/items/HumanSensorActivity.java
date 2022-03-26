package com.ayst.factorytest.items;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.ResultEvent;
import com.ayst.factorytest.model.TestItem;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class HumanSensorActivity extends ChildTestActivity {
    private static final String TAG = "HumanSensorActivity";

    private static final float STATE_NEAR = 1.0f;
    private static final float STATE_FAR = 2.0f;

    @BindView(R.id.tv_tips)
    TextView mTipsTv;
    @BindView(R.id.v_state)
    View mStateView;

    private float mPreState = 0;
    private SensorManager mSensorManager;
    private SensorEventListener mHumanSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_human_sensor;
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

    private void start() {
        if (null != mSensorManager) {
            // Human Sensor
            // 27: Sensor.TYPE_HUMAN for Android 5.1
            // 36: Sensor.TYPE_HUMAN for Android 8.1
            // 37: Sensor.TYPE_HUMAN for Android 11.0
            int humanSensorType = 36;
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) { // Android 5.1
                humanSensorType = 27;
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) { // Android 8.1
                humanSensorType = 36;
            } else if (Build.VERSION.SDK_INT == 30) { // Android 11
                humanSensorType = 37;
            }
            Sensor humanSensor = mSensorManager.getDefaultSensor(humanSensorType);
            if (humanSensor != null) {
                Log.i(TAG, "humanSensor>>>>");
                mHumanSensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float state = event.values[0];
                        if (state == STATE_NEAR) {
                            mStateView.setSelected(true);
                            mTipsTv.setText(R.string.human_test_prompt_far);
                        } else if (state == STATE_FAR) {
                            mStateView.setSelected(false);
                            mTipsTv.setText(R.string.human_test_prompt_near);
                        }
                        // 一次状态翻转，视为测试通过。
                        if (mPreState != 0 && mPreState != state) {
                            finish(TestItem.STATE_SUCCESS);
                        } else {
                            mPreState = state;
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                mSensorManager.registerListener(mHumanSensorEventListener, humanSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.e(TAG, "humanSensor is null");
            }
        }
    }

    private void stop() {
        if (null != mSensorManager) {
            if (null != mHumanSensorEventListener) {
                mSensorManager.unregisterListener(mHumanSensorEventListener);
            }
        }
    }
}