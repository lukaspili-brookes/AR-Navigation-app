package com.siu.android.arapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.siu.android.arapp.R;

/**
 * Created by lukas on 7/1/13.
 */
public class CameraActivity extends Activity {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean inPreview;

    private View mLogInfoView;
    private TextView mHeadingTextView;
    private TextView mRollTextView;
    private TextView mPitchTextView;
    private TextView mXAxisTextView;
    private TextView mYAxisTextView;
    private TextView mZAxisTextView;

    private SensorManager mSensorManager;
    private float mHeadingAngle;
    private float mPitchAngle;
    private float mRollAngle;

    private float mXAxis;
    private float mYAxis;
    private float mZAxis;

    private boolean mDebugMode = false;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                mHeadingAngle = sensorEvent.values[0];
                mPitchAngle = sensorEvent.values[1];
                mRollAngle = sensorEvent.values[2];

                if (mDebugMode) {
                    mHeadingTextView.setText(getString(R.string.log_info_heading, mHeadingAngle));
                    mPitchTextView.setText(getString(R.string.log_info_pitch, mPitchAngle));
                    mRollTextView.setText(getString(R.string.log_info_roll, mRollAngle));

                    Log.d(getClass().getName(), "Heading: " + String.valueOf(mHeadingAngle));
                    Log.d(getClass().getName(), "Pitch: " + String.valueOf(mPitchAngle));
                    Log.d(getClass().getName(), "Roll: " + String.valueOf(mRollAngle));
                }

            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mXAxis = sensorEvent.values[0];
                mYAxis = sensorEvent.values[1];
                mZAxis = sensorEvent.values[2];

                if (mDebugMode) {
                    mXAxisTextView.setText(getString(R.string.log_info_xaxis, mXAxis));
                    mYAxisTextView.setText(getString(R.string.log_info_yaxis, mYAxis));
                    mZAxisTextView.setText(getString(R.string.log_info_zaxis, mZAxis));

                    Log.d(getClass().getName(), "X Axis: " + String.valueOf(mXAxis));
                    Log.d(getClass().getName(), "Y Axis: " + String.valueOf(mYAxis));
                    Log.d(getClass().getName(), "Z Axis: " + String.valueOf(mZAxis));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mLogInfoView = findViewById(R.id.log_info_view);
        mHeadingTextView = (TextView) findViewById(R.id.log_info_heading_text);
        mPitchTextView = (TextView) findViewById(R.id.log_info_pitch_text);
        mRollTextView = (TextView) findViewById(R.id.log_info_roll_text);
        mXAxisTextView = (TextView) findViewById(R.id.log_info_xaxis_text);
        mYAxisTextView = (TextView) findViewById(R.id.log_info_yaxis_text);
        mZAxisTextView = (TextView) findViewById(R.id.log_info_zaxis_text);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background)));

        initCamera();
        initSensors();
    }


    @Override
    protected void onResume() {
        super.onResume();

        mCamera = Camera.open();

        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        if (inPreview) {
            mCamera.stopPreview();
            inPreview = false;
        }

        mCamera.release();
        mCamera = null;

        mSensorManager.unregisterListener(mSensorEventListener);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_debug:
                mDebugMode = !mDebugMode;
                mLogInfoView.setVisibility(mDebugMode ? View.VISIBLE : View.GONE);
                return true;
        }

        return false;
    }

    private void initCamera() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Camera set preview display error", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
                Camera.Parameters params = mCamera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, params);

                if (size != null) {
                    params.setPreviewSize(size.width, size.height);
                    mCamera.setDisplayOrientation(90);
                    mCamera.setParameters(params);
                    mCamera.startPreview();
                    inPreview = true;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        if (Build.VERSION.SDK_INT < 11) {
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    private void initSensors() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            Log.d(getClass().getName(), String.format("Size %d %d", size.width, size.height));
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
}
