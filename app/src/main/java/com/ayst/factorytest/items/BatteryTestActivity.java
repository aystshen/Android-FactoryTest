package com.ayst.factorytest.items;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

import butterknife.BindView;

public class BatteryTestActivity extends ChildTestActivity {

    @BindView(R.id.tv_status)
    TextView mStatusTv;
    @BindView(R.id.tv_health)
    TextView mHealthTv;
    @BindView(R.id.tv_level)
    TextView mLevelTv;
    @BindView(R.id.tv_scale)
    TextView mScaleTv;
    @BindView(R.id.tv_voltage)
    TextView mVoltageTv;
    @BindView(R.id.tv_plugged)
    TextView mPluggedTv;
    @BindView(R.id.tv_temperature)
    TextView mTemperatureTv;
    @BindView(R.id.tv_technology)
    TextView mTechnologyTv;

    private BroadcastReceiver mBatteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra("status", 0);
                int health = intent.getIntExtra("health", 0);
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
                int plugged = intent.getIntExtra("plugged", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                String technology = intent.getStringExtra("technology");

                mLevelTv.setText(level + "%");
                mScaleTv.setText(scale + "%");
                mVoltageTv.setText(voltage + "mV");
                mTemperatureTv.setText(((float) temperature / 10) + "℃");
                mTechnologyTv.setText(technology + "");

                switch (status) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        mStatusTv.setText("未知");
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        mStatusTv.setText("充电");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        mStatusTv.setText("放电");
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        mStatusTv.setText("未充电");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        mStatusTv.setText("满电");
                        break;
                }

                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        mHealthTv.setText("未知");
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        mHealthTv.setText("健康");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        mHealthTv.setText("过热");
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        mHealthTv.setText("损坏");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        mHealthTv.setText("过压");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        mHealthTv.setText("未明示故障");
                        break;
                }

                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        mPluggedTv.setText("AC");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        mPluggedTv.setText("USB");
                        break;
                    default:
                        mPluggedTv.setText("");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_battery_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mBatteryBroadcastReceiver);
        super.onStop();
    }
}