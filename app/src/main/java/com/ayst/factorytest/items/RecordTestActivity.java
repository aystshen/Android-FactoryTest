package com.ayst.factorytest.items;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.ayst.dbv.DashboardView;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.blankj.utilcode.util.VolumeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;

public class RecordTestActivity extends ChildTestActivity {
    private static final String TAG = "RecordTestActivity";

    private static final int SAMPLE_RATE_DEFAULT = 16000; // 16KHz
    private static final int PCM_CHANNELS = 1; // 1通道
    private static final int PCM_ENCODING_FORMAT = 16; // 16bit

    @BindView(R.id.dashboard)
    DashboardView mDashboardView;
    @BindView(R.id.btn_record_play)
    Button mRecordPlayBtn;

    private boolean isRecording = false;
    private int mRecordBufferSize;
    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;
    private String mRecordFilePath;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建文件路径
        mRecordFilePath = ContextCompat.getExternalFilesDirs(this,
                Environment.DIRECTORY_MUSIC)[0].getAbsolutePath() + File.separator + "record.pcm";

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
    protected void initViews() {
        super.initViews();

        mRecordPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    mRecordPlayBtn.setText(R.string.mic_test_record);
                    play();
                } else {
                    mRecordPlayBtn.setText(R.string.mic_test_play);
                    record();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mAudioTrack.stop();
        mAudioTrack.release();

        mAudioRecord.stop();
        mAudioRecord.release();

        File file = new File(mRecordFilePath);
        if (file.exists()) {
            file.delete();
        }
        super.onDestroy();
    }

    private void record() {
        Log.i(TAG, "record");

        isRecording = true;

        // 停止播放
        mAudioTrack.stop();

        // 开始录音
        mAudioRecord.startRecording();

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(mRecordFilePath);
                if (file.exists()) {
                    file.delete();
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mRecordFilePath);
                    byte[] buffer = new byte[mRecordBufferSize];

                    // 循环读取语音流（PCM数据包）
                    while (mAudioRecord.read(buffer, 0, mRecordBufferSize) > 0) {
                        Log.d(TAG, "AudioRecord read: " + buffer.length);

                        // PCM数据保存到本地文件
                        fos.write(buffer);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mDashboardView.setValue((int) (calculateVol(0, buffer) - 100));
                            }
                        });
                    }
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
        }).start();
    }

    private void play() {
        Log.i(TAG, "play");

        isRecording = false;

        // 停止录音
        mAudioRecord.stop();

        // 音量调取最大
        VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,
                VolumeUtils.getMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_SHOW_UI);

        // 开始播放
        mAudioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(mRecordFilePath);
                if (!file.exists()) {
                    Log.e(TAG, "play, " + mRecordFilePath + " not exist");
                    return;
                }

                long fileSize = file.length();
                if (fileSize > Integer.MAX_VALUE) {
                    Log.w(TAG, "play, file too big");
                    fileSize = Integer.MAX_VALUE;
                }

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mRecordFilePath);

                    int size;
                    byte[] buffer = new byte[mRecordBufferSize];
                    while ((size = fis.read(buffer, 0, mRecordBufferSize)) >= 0) {

                        Log.d(TAG, "AudioTrack write: " + size + "/" + fileSize);

                        // PCM数据写入AudioTrack中播放
                        mAudioTrack.write(buffer.clone(), 0, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
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