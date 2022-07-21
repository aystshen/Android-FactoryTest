package com.ayst.factorytest.items;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

public class CameraTestActivity extends ChildTestActivity {
    private static final String TAG = "CameraTestActivity";

    private static final int SUPPORT_CAMERA_MAX = 4;

    @BindView(R.id.layout_camera)
    FlexboxLayout mCameraLayout;

    private List<CameraItem> mCameras = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_camera_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mContainerLayout.setPadding(1, 1, 1, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stop();
    }

    private void start() {
        Log.i(TAG, "start...");

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int cameraNumber = Math.min(Camera.getNumberOfCameras(), SUPPORT_CAMERA_MAX);
                Log.i(TAG, "Camera num: " + cameraNumber);
                int width = mCameraLayout.getWidth() / (cameraNumber > 1 ? 2 : 1);
                int height = mCameraLayout.getHeight() / (cameraNumber > 2 ? 2 : 1);
                for (int i = mCameras.size(); i < cameraNumber; i++) {
                    SurfaceView surface = new SurfaceView(CameraTestActivity.this);
                    if (openCamera(i, surface)) {
                        final int cameraId = i;
                        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                                preview(cameraId);
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                            }
                        });
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                        mCameraLayout.addView(surface, layoutParams);
                    }
                }

                updateResult(String.format("{'num':%d}", cameraNumber));
            }
        }, 1000);
    }

    private void stop() {
        Log.i(TAG, "stop...");

        for (CameraItem cameraItem : mCameras) {
            cameraItem.release();
        }
        mCameras.clear();
    }

    private boolean openCamera(int id, SurfaceView surface) {
        try {
            Log.i(TAG, "openCamera, id:" + id);
            Camera camera = Camera.open(id);

            CameraItem cameraItem = new CameraItem();
            cameraItem.id = id;
            cameraItem.camera = camera;
            cameraItem.surface = surface;
            mCameras.add(cameraItem);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Open camera:" + id + "failed: " + e.getMessage());
        }

        return false;
    }

    private void preview(int id) {
        for (CameraItem cameraItem : mCameras) {
            if (cameraItem.id == id) {
                try {
                    Camera.Parameters parameters = cameraItem.camera.getParameters();
                    List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                    if (sizes != null && !sizes.isEmpty()) {
                        Collections.sort(sizes, new SizeComparator());
                        Camera.Size previewSize = sizes.get(sizes.size() - 1);
                        parameters.setPreviewSize(previewSize.width, previewSize.height);
                        cameraItem.camera.setParameters(parameters);
                        Log.i(TAG, "preview, preview size: " + previewSize.width
                                + "x" + previewSize.height);
                    }
                    cameraItem.camera.setPreviewDisplay(cameraItem.surface.getHolder());
                    cameraItem.camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CameraItem {
        private int id;
        private Camera camera;
        private SurfaceView surface;

        private void release() {
            if (null != camera) {
                Log.i(TAG, "Release camera, id:" + id);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }

    private class SizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            return (size1.width - size2.width) == 0 ? (size1.height - size2.height) : (size1.width - size2.width);
        }
    }
}