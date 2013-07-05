package com.siu.android.arapp.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean mInPreview;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        if (Build.VERSION.SDK_INT < 11) {
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    private void stopCameraIfNeeded() {
        if (mCamera != null) {
            if (mInPreview) {
                mCamera.stopPreview();
                mInPreview = false;
            }

            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        stopCameraIfNeeded();

        mCamera = Camera.open();

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Camera set preview display error", e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCameraIfNeeded();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(width, height, params);

        if (size != null) {
            params.setPreviewSize(size.width, size.height);
            mCamera.setParameters(params);
            mCamera.startPreview();
            mInPreview = true;
        }
    }
}
