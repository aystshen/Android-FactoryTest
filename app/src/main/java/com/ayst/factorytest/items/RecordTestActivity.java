package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ayst.dbv.DashboardView;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.blankj.utilcode.util.VolumeUtils;

import butterknife.BindView;

public class RecordTestActivity extends ChildTestActivity {
    private static final String TAG = "RecordTestActivity";

    private static final int SAMPLE_RATE_DEFAULT = 16000; // 16KHz
    private static final int PCM_CHANNELS = 1; // 1通道
    private static final int PCM_ENCODING_FORMAT = 16; // 16bit

    @BindView(R.id.dashboard)
    DashboardView mDashboardView;

    private int mRecordBufferSize;
    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建AudioRecord
        mRecordBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_DEFAULT,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_DEFAULT,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, mRecordBufferSize);

        // 创建AudioTrack
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_DEFAULT,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_DEFAULT,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize, AudioTrack.MODE_STREAM);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_mic_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        record();
    }

    @Override
    protected void onStop() {
        stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stop();
        mAudioRecord.release();
        mAudioTrack.release();
        super.onDestroy();
    }

    private void record() {
        Log.i(TAG, "record");

        // 音量调取最大
        VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,
                VolumeUtils.getMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_SHOW_UI);

        // 开始录音
        mAudioRecord.startRecording();

        // 开始播放
        mAudioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[mRecordBufferSize];

                // 循环读取语音流（PCM数据包）
                while (mAudioRecord.read(buffer, 0, mRecordBufferSize) > 0) {
                    Log.d(TAG, "AudioRecord read: " + buffer.length);

                    // PCM数据写入AudioTrack中播放
                    mAudioTrack.write(buffer.clone(), 0, buffer.length);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDashboardView.setValue((int)(calculateVol(0, buffer)-100));
                        }
                    });
                }
            }
        }).start();
    }

    private void stop() {
        mAudioTrack.stop();
        mAudioRecord.stop();
    }

    private double calculateVol(int ch, byte[] buffer) {
        long v = 0;
        int value;
        int frame = buffer.length / (PCM_CHANNELS * PCM_ENCODING_FORMAT / 8);
        for (int i = ch * (PCM_ENCODING_FORMAT / 8); i < frame; i += (PCM_CHANNELS * PCM_ENCODING_FORMAT / 8)) {
            value = buffer[i];
            value = (value << 8) | buffer[i + 1];
            v += value * value;
        }
        double mean = v / (double) frame;
        double volume = 10 * Math.log10(mean);

//        Log.i(TAG, "calculateVol, ch: " + ch + ", volume:" + volume);

        return volume;
    }
}