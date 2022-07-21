package com.ayst.factorytest.items;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.ND01Param;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.utils.AppUtils;
import com.ayst.nd01sdk.ND01;
import com.ayst.nd01sdk.ND01Data;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.BindView;

public class ND01TestActivity extends ChildTestActivity implements View.OnClickListener {
    private static final String TAG = "ND01TestActivity";

    private static final String PARAM_DEFAULT = "{'i2c': 1, 'calibrate': true}";

    @BindView(R.id.layout_cal1)
    LinearLayout mCal1Layout;
    @BindView(R.id.layout_cal2)
    LinearLayout mCal2Layout;
    @BindView(R.id.layout_distance)
    RelativeLayout mDistanceLayout;
    @BindView(R.id.btn_cal1)
    Button mCal1Btn;
    @BindView(R.id.btn_cal2)
    Button mCal2Btn;
    @BindView(R.id.btn_step1)
    Button mStep1Btn;
    @BindView(R.id.btn_step2)
    Button mStep2Btn;
    @BindView(R.id.tv_distance)
    TextView mDistanceTv;

    private ND01 mND01;
    private RangingThread mRangingThread;
    private Gson mGson = new Gson();
    private ND01Param mND01Param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mND01 = new ND01();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mND01.init(mND01Param.getI2c(), ND01.ND01_DEVICEMODE_CONTINUOUS_RANGING) == 0) {
                    if (mND01Param.getCalibrate()) { // 需要标定，显示标定按钮
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCal1Btn.setEnabled(true);
                            }
                        });
                    } else { // 不需要标定，直接开始测量
                        mRangingThread = new RangingThread();
                        mRangingThread.start();
                    }
                } else {
                    finish(TestItem.STATE_FAILURE);
                }
            }
        }).start();
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_nd01_test;
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

        Log.i(TAG, "onCreate, param: " + mTestItem.getParam());

        mND01Param = parseParam(mTestItem.getParam());

        if (mND01Param.getCalibrate()) { // 需要标定
            mSuccessBtn.setVisibility(View.GONE);
            mCal1Btn.setOnClickListener(this);
            mCal2Btn.setOnClickListener(this);
            mStep1Btn.setOnClickListener(this);
            mStep2Btn.setOnClickListener(this);
        } else { // 不需要标定
            mCal1Layout.setVisibility(View.INVISIBLE);
            mCal2Layout.setVisibility(View.INVISIBLE);
            mDistanceLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRangingThread != null) {
            mRangingThread.interrupt();
        }
    }

    private ND01Param parseParam(String param) {
        Type type = new TypeToken<ND01Param>() {
        }.getType();
        return mGson.fromJson(param, type);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cal1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mND01.ND01Calibration() == 0) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mCal1Btn.setEnabled(true);
                                    mStep1Btn.setEnabled(true);
                                }
                            });
                        } else {
                            finish(TestItem.STATE_FAILURE);
                        }
                    }
                }).start();
                mCal1Btn.setEnabled(false);
                break;

            case R.id.btn_cal2:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mND01.XTalkCalibration() == 0) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    exportCalibrateData(mND01.getCalibData());
                                    mCal2Btn.setEnabled(true);
                                    mStep2Btn.setEnabled(true);
                                }
                            });
                        } else {
                            finish(TestItem.STATE_FAILURE);
                        }
                    }
                }).start();
                mCal2Btn.setEnabled(false);
                break;

            case R.id.btn_step1:
                mCal1Layout.setVisibility(View.INVISIBLE);
                mCal2Layout.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_step2:
                mCal2Layout.setVisibility(View.INVISIBLE);
                mDistanceLayout.setVisibility(View.VISIBLE);
                mSuccessBtn.setVisibility(View.VISIBLE);
                mRangingThread = new RangingThread();
                mRangingThread.start();
                break;
        }
    }

    /**
     * 导出标定数据到文件
     *
     * @param data 标定数据
     */
    private void exportCalibrateData(byte[] data) {
        File file = new File(AppUtils.getExternalRootDir(this) + "/nd01.txt");
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (IOException e) {
            Log.e(TAG, "exportCalibrateData, " + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "exportCalibrateData, " + e.getMessage());
                }
            }
        }
    }

    private class RangingThread extends Thread {
        @Override
        public void run() {
            super.run();

            mND01.startMeasurement();

            while (!this.isInterrupted()) {
                ND01Data data = mND01.ranging();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDistanceTv.setText(Integer.toString(data.dist) + " mm");

                        mND01Param.setDistance((int) data.dist);
                        updateResult(mGson.toJson(mND01Param));
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.w(TAG, "RangingThread, interrupted");
                }
            }

            mND01.stopMeasurement();
        }
    }
}