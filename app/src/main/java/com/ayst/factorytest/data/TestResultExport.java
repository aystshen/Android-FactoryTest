package com.ayst.factorytest.data;

import android.annotation.SuppressLint;
import android.content.Context;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TestResultExport {
    private static final String TAG = "TestResultExport";

    private Context mContext;
    private ArrayList<TestItem> mItems;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TestResultExport(Context context, ArrayList<TestItem> items) {
        mContext = context;
        mItems = items;
    }

    /**
     * 导出测试结果到文件（sdcard/factorytest/result.txt），格式如下：
     * <p>
     * 测试结果（2022-03-02 13:31:43）
     * <p>
     * 信息: 通过
     * WiFi: 通过
     * 蓝牙: 通过
     * 以太网: 通过
     * 移动网络: 失败
     * 定时开关机: 失败
     * 看门狗: 失败
     * 串口: 失败
     * 人体感应: 失败
     * 加速度: 失败
     * 显示: 通过
     * 触摸: 通过
     * 喇叭: 通过
     * 麦克风: 通过
     * 麦克风阵列: 通过
     * 按键: 失败
     * 摄像头: 通过
     * 背光: 通过
     * 电池: 通过
     * 光感: 通过
     * 温湿度: 通过
     * USB: 通过
     * sdcard: 通过
     * GPIO: 通过
     * 韦根: 通过
     * <p>
     * 共计 25 项
     * 通过 18 项
     * 失败 7 项
     * 忽略 0 项
     */
    @SuppressLint("StringFormatMatches")
    public void export() {
        File file = new File(AppUtils.getExternalRootDir(mContext) + "/result.txt");
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            int successCnt = 0;
            int failureCnt = 0;
            int unknownCnt = 0;
            fos = new FileOutputStream(file);

            fos.write(String.format(mContext.getString(R.string.rest_result), mDateFormat.format(new Date())).getBytes());
            for (TestItem item : mItems) {
                fos.write((item.getName() + ": " + item.getStateStr(mContext) + "\n").getBytes());
                switch (item.getState()) {
                    case TestItem.STATE_UNKNOWN:
                        unknownCnt++;
                        break;
                    case TestItem.STATE_SUCCESS:
                        successCnt++;
                        break;
                    case TestItem.STATE_FAILURE:
                        failureCnt++;
                        break;
                }
            }
            fos.write(("\n").getBytes());
            fos.write(String.format(mContext.getString(R.string.rest_result_total), mItems.size()).getBytes());
            fos.write(String.format(mContext.getString(R.string.rest_result_success), successCnt).getBytes());
            fos.write(String.format(mContext.getString(R.string.rest_result_fail), failureCnt).getBytes());
            fos.write(String.format(mContext.getString(R.string.rest_result_ignore), unknownCnt).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
