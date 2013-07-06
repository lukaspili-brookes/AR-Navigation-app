package com.siu.android.arapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siu.android.arapp.R;

/**
 * Created by lukas on 7/6/13.
 */
public class NavigationView extends RelativeLayout {

    private TextView mOriginTextView;
    private TextView mDestinationTextView;

    public NavigationView(Context context) {
        super(context);
        initView(context);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.navigation_view, this);

        mOriginTextView = (TextView) findViewById(R.id.navigation_view_origin_text);
        mDestinationTextView = (TextView) findViewById(R.id.navigation_view_destination_text);
    }

    public void updateNavigation(String origin, String destination) {
        mOriginTextView.setText("You are in : " + origin);
        mDestinationTextView.setText("Going to : " + destination);

        invalidate();
        requestLayout();
    }
}
