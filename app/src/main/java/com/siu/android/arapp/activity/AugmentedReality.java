package com.siu.android.arapp.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.siu.android.arapp.R;
import com.siu.android.arapp.camera.CameraSurfaceView;
import com.siu.android.arapp.data.ARData;
import com.siu.android.arapp.ui.Marker;
import com.siu.android.arapp.view.LogInfoView;
import com.siu.android.arapp.view.RadarView;

public class AugmentedReality extends SensorsActivity implements OnTouchListener {

    public static boolean portrait = false;
    public static boolean useCollisionDetection = false;

    private CameraSurfaceView mCameraSurfaceView;
    private AugmentedView mAugmentedView;
    private RadarView mRadarView;
    private LogInfoView mLogInfoView;

    private boolean mShowLogInfo = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality_activity);

        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.surface_view);
        mAugmentedView = (AugmentedView) findViewById(R.id.ar_view);
        mRadarView = (RadarView) findViewById(R.id.radar_view);
        mLogInfoView = (LogInfoView) findViewById(R.id.log_info_view);

        mAugmentedView.setOnTouchListener(this);

        updateDataOnZoom();
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mAugmentedView.postInvalidate();
            mRadarView.invalidate();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        if (mShowLogInfo) {
            mLogInfoView.updateLocation(location);
        }
    }

    /**
     * Called when the zoom bar has changed.
     */
    protected void updateDataOnZoom() {
//        float zoomLevel = calcZoomLevel();
//        ARData.setRadius(zoomLevel);
//        ARData.setZoomLevel(FORMAT.format(zoomLevel));
//        ARData.setZoomProgress(myZoomBar.getProgress());
    }

    @Override
    public boolean onTouch(View view, MotionEvent me) {
        // See if the motion event is on a Marker
        for (Marker marker : ARData.getMarkers()) {
            if (marker.handleClick(me.getX(), me.getY())) {
                if (me.getAction() == MotionEvent.ACTION_UP) markerTouched(marker);
                return true;
            }
        }

        return super.onTouchEvent(me);
    }

    protected void markerTouched(Marker marker) {

    }
}
