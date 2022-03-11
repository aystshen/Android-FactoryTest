package com.ayst.factorytest.items;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.PwmItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.PwmItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.topband.tbapi.utils.ShellUtils;
import com.xuexiang.xui.utils.WidgetUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;

public class PwmTestActivity extends ChildTestActivity {

    private static final String TAG = "PwmTestActivity";

    private static final String PARAM_DEFAULT =
            "[{'name': '左侧蓝色LED', 'pwm': 0, 'period': 10000, 'duty': 1000, 'state': 0}, " +
                    "{'name': '左侧红色LED', 'pwm': 1, 'period': 10000, 'duty': 1000, 'state': 0}, " +
                    "{'name': '左侧绿色LED', 'pwm': 2, 'period': 10000, 'duty': 1000, 'state': 0}, " +
                    "{'name': '右侧蓝色LED', 'pwm': 4, 'period': 10000, 'duty': 1000, 'state': 0}, " +
                    "{'name': '右侧红色LED', 'pwm': 5, 'period': 10000, 'duty': 1000, 'state': 0}, " +
                    "{'name': '右侧绿色LED', 'pwm': 6, 'period': 10000, 'duty': 1000, 'state': 0}]";

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private Gson mGson = new Gson();
    private PwmItemAdapter mPwmItemAdapter;
    private ArrayList<PwmItem> mPwmItems = new ArrayList<>();
    private TestThread mTestThread;

    private static boolean enablePwm(int pwm, boolean enable) {
        String cmd = "echo " + (enable ? 1 : 0) + " >  /sys/class/pwm/pwmchip" + pwm + "/pwm0/enable";

        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        Log.i(TAG, "enablePwm, " + result.toString());

        return result.errorMsg.isEmpty();
    }

    private static boolean setPwmDuty(int pwm_number, int level) {
        level = level * 10;
        String cmd = "echo " + level + " >  /sys/class/pwm/pwmchip" + pwm_number + "/pwm0/duty_cycle";

        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        Log.i(TAG, "setPwmDuty, " + result.toString());

        return result.errorMsg.isEmpty();
    }

    private static boolean openPwm(int pwm) {
        String cmd = "echo " + 0 + " >  /sys/class/pwm/pwmchip" + pwm + "/export";

        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        Log.i(TAG, "openPwm, " + result.toString());

        return result.errorMsg.isEmpty();
    }

    private static boolean setPwmPeriod(int pwm, int hz) {
        String cmd = "echo " + hz + " >  /sys/class/pwm/pwmchip" + pwm + "/pwm0/period";

        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        Log.i(TAG, "setPwmPeriod, " + result.toString());

        return result.errorMsg.isEmpty();
    }

    private static boolean setPwmPolarity(int pwm, String state) {
        String cmd = "echo " + state + " >  /sys/class/pwm/pwmchip" + pwm + "/pwm0/polarity";

        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        Log.i(TAG, "setPwmPolarity, " + result.toString());

        return result.errorMsg.isEmpty();
    }

    private static void initPwm(int pwm, int period, int duty, boolean enable) {
        ShellUtils.CommandResult result = ShellUtils.execCmd(
                "chmod 777 /sys/class/pwm/pwmchip" + pwm + "/export", true);
        Log.i(TAG, "initPwm, " + result.toString());
        if (result.errorMsg.isEmpty()) {
            openPwm(pwm);

            List<String> cmds = new ArrayList<>();
            cmds.add("chmod 777 /sys/class/pwm/pwmchip" + pwm + "/pwm0/duty_cycle");
            cmds.add("chmod 777 /sys/class/pwm/pwmchip" + pwm + "/pwm0/enable");
            cmds.add("chmod 777 /sys/class/pwm/pwmchip" + pwm + "/pwm0/period");
            cmds.add("chmod 777 /sys/class/pwm/pwmchip" + pwm + "/pwm0/polarity");
            for (String cmd : cmds) {
                ShellUtils.execCmd(cmd, true);
                Log.i(TAG, "initPwm, " + result.toString());
            }

            setPwmPeriod(pwm, period);              // 设置分母
            setPwmDuty(pwm, duty);                  // 设置分子
            setPwmPolarity(pwm, "normal");    // 固定用法不变
            enablePwm(pwm, enable);                 // 使能
        } else {
            Log.e(TAG, "initPwm, error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPwmItems.size() > 0) {
            mTestThread = new TestThread();
            mTestThread.start();
        } else {
            Log.e(TAG, "onCreate, no pwms");
        }
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_pwm_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (TextUtils.isEmpty(mTestItem.getParam())) {
            mTestItem.setParam(PARAM_DEFAULT);
        }

        Log.i(TAG, "initViews, param: " + mTestItem.getParam());

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mPwmItems = parseParam(mTestItem.getParam());
        mPwmItemAdapter = new PwmItemAdapter();
        mPwmItemAdapter.setList(mPwmItems);
        mItemsRv.setAdapter(mPwmItemAdapter);
    }

    @Override
    protected void onDestroy() {
        if (mTestThread != null) {
            mTestThread.interrupt();
        }
        resetPwm();

        super.onDestroy();
    }

    private ArrayList<PwmItem> parseParam(String param) {
        Type collectionType = new TypeToken<Collection<PwmItem>>() {
        }.getType();
        return mGson.fromJson(param, collectionType);
    }

    private void setPwm(PwmItem pwm, int state) {
        if (state == PwmItem.STATE_HIGH) {
            setPwmDuty(pwm.getPwm(), pwm.getDuty());
            enablePwm(pwm.getPwm(), true);
        } else {
            setPwmDuty(pwm.getPwm(), 0);
            enablePwm(pwm.getPwm(), false);
        }
        pwm.setState(state);
    }

    private void resetPwm() {
        for (PwmItem pwm : mPwmItems) {
            setPwm(pwm, PwmItem.STATE_LOW);
        }
    }

    private class TestThread extends Thread {
        @Override
        public void run() {
            super.run();

            for (PwmItem pwm : mPwmItems) {
                initPwm(pwm.getPwm(), pwm.getPeriod(), 0, false);
            }

            int index = mPwmItems.size();
            while (!isInterrupted()) {
                if (index < mPwmItems.size() - 1) {
                    index++;
                } else {
                    index = 0;
                }

                resetPwm();
                setPwm(mPwmItems.get(index), PwmItem.STATE_HIGH);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPwmItemAdapter.notifyDataSetChanged();
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.w(TAG, "TestThread, interrupted");
                    break;
                }
            }
            Log.i(TAG, "TestThread, exit");
        }
    }
}