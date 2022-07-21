package com.ayst.factorytest.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class TestResultExport {
    private static final String TAG = "TestResultExport";

    private Context mContext;
    private Gson mGson = new GsonBuilder().disableHtmlEscaping().create();;
    private ArrayList<TestItem> mItems;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TestResultExport(Context context, ArrayList<TestItem> items) {
        mContext = context;
        mItems = items;
    }

    /**
     * 生成简单二维码
     *
     * @param content              字符串内容
     * @param width                二维码宽度
     * @param height               二维码高度
     * @param characterSet         编码方式（一般使用UTF-8）
     * @param errorCorrectionLevel 容错率 L：7% M：15% Q：25% H：35%
     * @param margin               空白边距（二维码与边框的空白区域）
     * @param colorBlack           黑色色块
     * @param colorWhite           白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String characterSet, String errorCorrectionLevel,
                                            String margin, int colorBlack, int colorWhite) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(characterSet)) {
                hints.put(EncodeHintType.CHARACTER_SET, characterSet);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(errorCorrectionLevel)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = colorBlack;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = colorWhite;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
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
    public void exportFile() {
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
                fos.write((item.getName() + ": " + item.getStateStr(mContext) + " " + item.getParam() + "\n").getBytes());
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

    public Bitmap exportQRCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (TestItem item : mItems) {
            sb.append("{key:" + item.getKey()
                    + ", result:" + item.getResult()
                    + ", state:" + item.getState() + "},");
        }
        sb.append("}");
        String qr = sb.toString();
        qr = qr.replaceAll("\"","");
        qr = qr.replaceAll("\'","");
        Log.i(TAG, "exportQRCode, content: " + qr);
        return createQRCodeBitmap(qr, 1000, 1000,"UTF-8",
                "L", "1", Color.BLACK, Color.WHITE);
    }
}
