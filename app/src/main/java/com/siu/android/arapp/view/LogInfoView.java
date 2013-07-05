package com.siu.android.arapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.siu.android.arapp.R;

/**
 * Created by lukas on 7/5/13.
 */
public class LogInfoView extends RelativeLayout {

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
    }
}
