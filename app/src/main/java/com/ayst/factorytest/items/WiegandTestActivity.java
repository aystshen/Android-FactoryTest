package com.ayst.factorytest.items;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.WiegandParam;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.topband.tbapi.TBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.BindView;

public class WiegandTestActivity extends ChildTestActivity {
    private static final String TAG = "WiegandTestActivity";

    private static final String PARAM_DEFAULT = "{'write': {'format': 26, 'value': 7825015}, " +
            "'read': {'format': 26, 'value': 0}}";

    @BindView(R.id.layout_wiegand_read)
    LinearLayout mWgReadLayout;
    @BindView(R.id.layout_wiegand_write)
    LinearLayout mWgWriteLayout;
    @BindView(R.id.tv_wiegand_read)
    TextView mWgReadTv;
    @BindView(R.id.tv_wiegand_write)
    TextView mWgWriteTv;
    @BindView(R.id.view_gap)
    View mGapView;

    private Gson mGson = new Gson();
    private WiegandParam mWgReadParam;
    private WiegandParam mWgWriteParam;
    private Thread mReadThread;
    private Thread mWriteThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_wiegand_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        if (TextUtils.isEmpty(mTestItem.getParam())) {
            mTestItem.setParam(PARAM_DEFAULT);
        }

        Log.i(TAG, "onCreate, param: " + mTestItem.getParam());

        parseParam(mTestItem.getParam());

        if (mWgReadParam != null && mWgReadParam.isSupport()) {
            App.getTBManager().setWiegandReadFormat(
                    mWgReadParam.getFormat() == WiegandParam.FORMAT_26 ?
                            TBManager.WiegandFormat.WIEGAND_FORMAT_26 :
                            TBManager.WiegandFormat.WIEGAND_FORMAT_34);

            if (mReadThread == null) {
                mReadThread = new ReadThread();
                mReadThread.start();
            }
        } else {
            mWgReadLayout.setVisibility(View.GONE);
            mGapView.setVisibility(View.GONE);
        }

        if (mWgWriteParam != null && mWgWriteParam.isSupport()) {
            App.getTBManager().setWiegandWriteFormat(
                    mWgWriteParam.getFormat() == WiegandParam.FORMAT_26 ?
                            TBManager.WiegandFormat.WIEGAND_FORMAT_26 :
                            TBManager.WiegandFormat.WIEGAND_FORMAT_34);

            mWgWriteTv.setText(Integer.toHexString(mWgWriteParam.getValue()));

            if (mWriteThread == null) {
                mWriteThread = new WriteThread();
                mWriteThread.start();
            }
        } else {
            mWgWriteLayout.setVisibility(View.GONE);
            mGapView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mWriteThread != null) {
            mWriteThread.interrupt();
        }

        super.onDestroy();
    }

    private void parseParam(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                Type type = new TypeToken<WiegandParam>() {
                }.getType();

                JSONObject obj = new JSONObject(json);
                if (obj.has("read")) {
                    String read = obj.getString("read");
                    if (!TextUtils.isEmpty(read)) {
                        mWgReadParam = mGson.fromJson(read, type);
                    }
                }

                if (obj.has("write")) {
                    String write = obj.getString("write");
                    if (!TextUtils.isEmpty(write)) {
                        mWgWriteParam = mGson.fromJson(write, type);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                int value = App.getTBManager().wiegandRead();
                Log.i(TAG, "ReadThread, read: " + Integer.toHexString(value));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWgReadTv.setText(Integer.toHexString(value));
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.w(TAG, "ReadThread, interrupted");
                    break;
                }
            }

            Log.i(TAG, "ReadThread, exit");
        }
    }

    private class WriteThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                if (!App.getTBManager().wiegandWrite(mWgWriteParam.getValue())) {
                    Log.e(TAG, "WriteThread, write error");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.w(TAG, "WriteThread, interrupted");
                    break;
                }
            }

            Log.i(TAG, "WriteThread, exit");
        }
    }
}