package com.siu.android.arapp.view;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siu.android.arapp.R;

import java.text.DecimalFormat;

/**
 * Created by lukas on 7/5/13.
 */
public class LogInfoView extends RelativeLayout {

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mAltitudeTextView;
    private TextView mHeadingTextView;
    private TextView mRollTextView;
    private TextView mPitchTextView;
    private TextView mXAxisTextView;
    private TextView mYAxisTextView;
    private TextView mZAxisTextView;

    public LogInfoView(Context context) {
        super(context);
        initView(context);
    }

    public LogInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LogInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.log_info_view, this);

        mLatitudeTextView = (TextView) findViewById(R.id.log_info_latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.log_info_longitude_text);
        mAltitudeTextView = (TextView) findViewById(R.id.log_info_altitude_text);
        mHeadingTextView = (TextView) findViewById(R.id.log_info_heading_text);
        mPitchTextView = (TextView) findViewById(R.id.log_info_pitch_text);
        mRollTextView = (TextView) findViewById(R.id.log_info_roll_text);
        mXAxisTextView = (TextView) findViewById(R.id.log_info_xaxis_text);
        mYAxisTextView = (TextView) findViewById(R.id.log_info_yaxis_text);
        mZAxisTextView = (TextView) findViewById(R.id.log_info_zaxis_text);
    }

    public void updateLocation(Location location) {
        mLatitudeTextView.setText(getContext().getString(R.string.log_info_latitude, location.getLatitude()));
        mLongitudeTextView.setText(getContext().getString(R.string.log_info_longitude, location.getLongitude()));
        mAltitudeTextView.setText(getContext().getString(R.string.log_info_altitude, location.getAltitude()));

        invalidate();
        requestLayout();
    }

    public void updateCompass(float azimuth, float pitch, float roll) {
        mHeadingTextView.setText(getContext().getString(R.string.log_info_heading, mDecimalFormat.format(azimuth)));
        mPitchTextView.setText(getContext().getString(R.string.log_info_pitch, mDecimalFormat.format(pitch)));
        mRollTextView.setText(getContext().getString(R.string.log_info_roll, mDecimalFormat.format(roll)));

        invalidate();
        requestLayout();
    }
}
