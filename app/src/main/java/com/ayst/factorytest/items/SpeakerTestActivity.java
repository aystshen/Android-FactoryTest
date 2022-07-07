package com.ayst.factorytest.items;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.blankj.utilcode.util.VolumeUtils;

import java.io.IOException;

public class SpeakerTestActivity extends ChildTestActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_speaker_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        play(R.raw.play);
    }

    @Override
    protected void onStop() {
        stop();
        super.onStop();
    }

    public void play(int resId) {
        stop();
        AssetFileDescriptor file = getResources().openRawResourceFd(resId);
        VolumeUtils.setVolume(AudioManager.STREAM_MUSIC,
                VolumeUtils.getMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_SHOW_UI);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mMediaPlayer.start();
    }
}