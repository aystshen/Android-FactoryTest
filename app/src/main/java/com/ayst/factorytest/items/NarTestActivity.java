package com.ayst.factorytest.items;

import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayst.audio.NativeAudioRecord;
import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.NarItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.NarParam;
import com.blankj.utilcode.util.VolumeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.utils.WidgetUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;

public class NarTestActivity extends ChildTestActivity {
    private static final String TAG = "NarTestActivity";

    private static final String PARAM_DEFAULT = "{'card':0, 'device': 1, 'channels': 4, 'rate':16000, 'bits':16, 'period_size':1024, 'period_cnt':3, 'play_channel':2}";

    private static final int MESSAGE_UPDATE_VOL = 1;

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private NarItemAdapter mNarItemAdapter;
    private NarParam mNarParam;
    private ArrayList<Double> mNarItems = new ArrayList<>();
    private Gson mGson = new Gson();
    private LocalHandler mHandler;
    private NativeAudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new LocalHandler();

        // 创建录音器
        mAudioRecord = new NativeAudioRecord(mNarParam.getCard(), mNarParam.getDevice(),
                mNarParam.getChannels(), mNarParam.getRate(), mNarParam.getBits(),
                mNarParam.getPeriodSize(), mNarParam.getPeriodCnt());

        // 创建AudioTrack
        int bufferSize = AudioTrack.getMinBufferSize(mNarParam.getRate(),
                AudioFormat.CHANNEL_OUT_MONO, getAudioFormat(mNarParam.getBits()));
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mNarParam.getRate(),
                AudioFormat.CHANNEL_OUT_MONO, getAudioFormat(mNarParam.getBits()),
                bufferSize, AudioTrack.MODE_STREAM);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_nar_test;
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

        mNarParam = parseParam(mTestItem.getParam());

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        for (int i = 0; i < mNarParam.getChannels(); i++) {
            mNarItems.add(0.0);
        }
        mNarItemAdapter = new NarItemAdapter();
        mNarItemAdapter.setList(mNarItems);
        mItemsRv.setAdapter(mNarItemAdapter);
    }

    private NarParam parseParam(String param) {
        Type type = new TypeToken<NarParam>() {
        }.getType();
        return mGson.fromJson(param, type);
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

    private void start() {
        Log.i(TAG, "start...");

        // 音量调取最大
        VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,
                VolumeUtils.getMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_SHOW_UI);

        // 开始录音
        mAudioRecord.start();
        // 开始播放
        mAudioTrack.play();
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer;

                // 循环读取语音流（PCM数据包）
                while (mAudioRecord.isCapturing() && ((buffer = mAudioRecord.read()) != null)) {
                    Log.d(TAG, "AudioRecord read: " + buffer.length);

                    // 更新音量
                    HashMap<Integer, Double> map = new HashMap<>();
                    for (int i = 0; i < mNarParam.getChannels(); i++) {
                        map.put(i, calculateVol(i, buffer));
                    }
                    Message msg = new Message();
                    msg.what = MESSAGE_UPDATE_VOL;
                    msg.obj = map;
                    mHandler.sendMessage(msg);

                    // PCM数据写入AudioTrack中播放
                    byte[] tmpBuf = detachChannel(buffer, mNarParam.getPlayChannel(),
                            mNarParam.getChannels(), mNarParam.getBits());
                    mAudioTrack.write(tmpBuf, 0, tmpBuf.length);
                }
            }
        }).start();
    }

    public void stop() {
        Log.i(TAG, "stop...");

        mAudioRecord.stop();
        mAudioTrack.stop();
        mAudioTrack.release();
    }

    private byte[] detachChannel(byte[] src, int channel, int channels, int bits) {
        if (src == null || src.length <= 0 || channel < 0 || channel > channels) {
            return null;
        }

        int frameSize = channels * bits / 8; // 一帧数据大小(byte)
        int channelSize = bits / 8; // 一通道数据大小(byte)

        byte[] localData = Arrays.copyOf(src, src.length);
        byte[] channelData = new byte[localData.length / channels];

        int cnt = 0;
        for (int i = 0; i < localData.length; ) {
            for (int j = 0; j < channelSize; j++) {
                channelData[cnt++] = localData[i + channel * channelSize + j];
            }
            i += frameSize; // 切换到下一帧数据
        }
        return channelData;
    }

    private double calculateVol(int ch, byte[] buffer) {
        long v = 0;
        int value;
        int frame = buffer.length / (mNarParam.getChannels() * mNarParam.getBits() / 8);
        for (int i = ch * (mNarParam.getBits() / 8); i < frame; i += (mNarParam.getChannels() * mNarParam.getBits() / 8)) {
            value = buffer[i];
            value = (value << 8) | buffer[i + 1];
            v += value * value;
        }
        double mean = v / (double) frame;
        double volume = 10 * Math.log10(mean);

//        Log.i(TAG, "calculateVol, ch: " + ch + ", volume:" + volume);

        return volume;
    }

    private int getAudioFormat(int bits) {
        switch (bits) {
            case 8:
                return AudioFormat.ENCODING_PCM_8BIT;
            case 16:
                return AudioFormat.ENCODING_PCM_16BIT;
            case 24:
                return AudioFormat.ENCODING_PCM_24BIT_PACKED;
            case 32:
                return AudioFormat.ENCODING_PCM_32BIT;
        }
        return AudioFormat.ENCODING_PCM_16BIT;
    }

    class LocalHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_VOL:
                    HashMap<Integer, Double> map = (HashMap<Integer, Double>) msg.obj;
                    for (int i = 0; i < map.size(); i++) {
                        mNarItems.set(i, map.get(i));
                        Log.i(TAG, "handleMessage, value=" + mNarItems.get(i));
                    }
                    mNarItemAdapter.setList(mNarItems);
                    mNarItemAdapter.notifyDataSetChanged();
                    break;
            }

            super.handleMessage(msg);
        }
    }
}