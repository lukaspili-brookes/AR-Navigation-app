package com.siu.android.arapp.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.siu.android.arapp.R;
import com.siu.android.arapp.common.Calculator;
import com.siu.android.arapp.view.LogInfoView;

/**
 * Created by lukas on 7/7/13.
 */
public class AugmentedRealityActivity2 extends SensorsActivity {

    private AugmentedView mAugmentedView;
    private LogInfoView mLogInfoView;
    protected ViewGroup mContentView;

    private boolean mShowLogInfo = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.augmented_reality_activity_two, null, false);
        mMainContentView.addView(view);


        mAugmentedView = (AugmentedView) view.findViewById(R.id.ar_view);
        mLogInfoView = (LogInfoView) view.findViewById(R.id.log_info_view);
        mContentView = (ViewGroup) view.findViewById(R.id.content);
    }

    @Override
    protected void initApplicationAR() {
        super.initApplicationAR();
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mAugmentedView.postInvalidate();

            if (mShowLogInfo) {
                mLogInfoView.updateCompass(Calculator.getAzimuth(), Calculator.getPitch(), Calculator.getRoll());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        if (mShowLogInfo) {
            mLogInfoView.updateLocation(location);
        }
    }
}
