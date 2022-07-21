package com.ayst.factorytest.items;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.serialport.SerialPort;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.UartItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.model.UartItem;
import com.ayst.factorytest.utils.DataConversion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.utils.WidgetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;

public class UartTestActivity extends ChildTestActivity {
    private static final String TAG = "UartTestActivity";

    private static final String PARAM_DEFAULT = "[{'device':'/dev/ttyS1', 'baud': 9600, 'send': '7A7A', 'receive': '7A7A', 'state': 0}, " +
            "{'device':'/dev/ttyS3', 'baud': 9600, 'send': '7A7A', 'receive': '7A7A', 'state': 0}, " +
            "{'device':'/dev/ttyS4', 'baud': 9600, 'send': '7A7A', 'receive': '7A7A', 'state': 0}]";

    private static final long UART_RECEIVE_TIMEOUT = 3000;

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private int mNext = 0;
    private UartItemAdapter mUartItemAdapter;
    private ArrayList<UartItem> mUartItems;
    private Gson mGson = new Gson();
    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private ReadThread mReadThread;
    private UartItem mCurUart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(getMainLooper());

        testUart(mNext++);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_uart_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mSuccessBtn.setVisibility(View.GONE);

        if (TextUtils.isEmpty(mTestItem.getParam())) {
            mTestItem.setParam(PARAM_DEFAULT);
        }

        Log.i(TAG, "initViews, param: " + mTestItem.getParam());

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mUartItems = parseParam(mTestItem.getParam());
        for (UartItem uart : mUartItems) {
            uart.setState(UartItem.STATE_UNKNOWN);
        }
        mUartItemAdapter = new UartItemAdapter();
        mUartItemAdapter.setList(mUartItems);
        mItemsRv.setAdapter(mUartItemAdapter);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        close();

        super.onDestroy();
    }

    private ArrayList<UartItem> parseParam(String param) {
        Type collectionType = new TypeToken<Collection<UartItem>>() {
        }.getType();
        return mGson.fromJson(param, collectionType);
    }

    private void testUart(int index) {
        if (index < mUartItems.size()) {
            testUart(mUartItems.get(index));
        } else {
            updateResult(mGson.toJson(mUartItems));
            finishWithCheckResult();
        }
    }

    private void testUart(final UartItem uart) {
        Log.i(TAG, "testUart, " + uart.toString());

        mCurUart = uart;
        close();
        try {
            mSerialPort = SerialPort
                    .newBuilder(uart.getDevice(),   // 串口地址
                            uart.getBaud())     // 波特率
                    .parity(0)        // 奇偶校验位；0: 无校验位(NONE，默认)；1:奇校验位(ODD)；2:偶校验位(EVEN)
                    .dataBits(8)      // 数据位；默认8；可选值为5~8
                    .stopBits(1)      // 停止位；默认1；1: 1位停止位；2: 2位停止位
                    .build();

            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();

            mReadThread = new ReadThread();
            mReadThread.start();

            mOutputStream.write(DataConversion.decodeHexString(uart.getSend()));
        } catch (Exception e) {
            Log.e(TAG, "testUart, error: " + e.getMessage());

            uart.setState(UartItem.STATE_FAILURE);
            updateState(uart);
            testUart(mNext++);
        }
    }

    private void close() {
        Log.i(TAG, "close");
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mSerialPort != null) {
                mSerialPort.close();
            }
            if (mReadThread != null) {
                mReadThread.interrupt();
            }
        } catch (IOException e) {
            Log.e(TAG, "close, " + e.getMessage());
        }
    }

    private void updateState(UartItem uart) {
        for (int i = 0; i < mUartItems.size(); i++) {
            if (TextUtils.equals(mUartItems.get(i).getDevice(), uart.getDevice())) {
                mUartItems.set(i, uart);
            }
        }
        mUartItemAdapter.setList(mUartItems);
        mUartItemAdapter.notifyDataSetChanged();
    }

    private void finishWithCheckResult() {
        for (UartItem uart : mUartItems) {
            if (uart.getState() != UartItem.STATE_SUCCESS) {
                finish(TestItem.STATE_FAILURE);
                return;
            }
        }
        finish(TestItem.STATE_SUCCESS);
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();

            Log.i(TAG, "ReadThread, start thread");

            int size = 0;
            byte[] buffer = new byte[256];
            long startTime = System.currentTimeMillis();
            try {
                // 等待串口数据就绪
                while (mInputStream.available() <= 0
                        && (System.currentTimeMillis() < startTime + UART_RECEIVE_TIMEOUT)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "ReadThread, interrupted");
                        break;
                    }
                }

                if (mInputStream.available() > 0) {
                    // 读取串口数据
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        byte[] data = new byte[size];
                        System.arraycopy(buffer, 0, data, 0, size);
                        String hexStr = DataConversion.encodeHexString(data);
                        Log.i(TAG, "ReadThread, receive: " + hexStr);

                        if (TextUtils.equals(hexStr, mCurUart.getReceive())) {
                            mCurUart.setState(UartItem.STATE_SUCCESS);
                        } else {
                            mCurUart.setState(UartItem.STATE_FAILURE);
                        }
                    } else {
                        mCurUart.setState(UartItem.STATE_FAILURE);
                        Log.e(TAG, "ReadThread, didn't read anything");
                    }
                } else {
                    mCurUart.setState(UartItem.STATE_FAILURE);
                    Log.e(TAG, "ReadThread, data timeout not ready");
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateState(mCurUart);
                        testUart(mNext++);
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "ReadThread, " + e.getMessage());
            }
            Log.i(TAG, "ReadThread, exit");
        }
    }
}