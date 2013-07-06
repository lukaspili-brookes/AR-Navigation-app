package com.siu.android.arapp.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.siu.android.arapp.R;
import com.siu.android.arapp.data.ARData;
import com.siu.android.arapp.data.TestSource;
import com.siu.android.arapp.ui.Marker;

public class Demo extends AugmentedReality {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background)));

        TestSource testSource = new TestSource();
        ARData.addMarkers(testSource.getMarkers());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);
        return true;
    }

    @Override
    protected void markerTouched(Marker marker) {
        Toast.makeText(this, marker.getName(), Toast.LENGTH_SHORT).show();
    }
}
