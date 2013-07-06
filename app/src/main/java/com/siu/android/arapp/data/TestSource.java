package com.siu.android.arapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.siu.android.arapp.Application;
import com.siu.android.arapp.R;
import com.siu.android.arapp.ui.IconMarker;
import com.siu.android.arapp.ui.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 7/2/13.
 */
public class TestSource extends DataSource {

    @Override
    public List<Marker> getMarkers() {
        Bitmap icon = BitmapFactory.decodeResource(Application.getContext().getResources(), R.drawable.ic_launcher);
        Marker marker = new IconMarker("BASTARD", 48.553239, 2.133495, 135, Color.DKGRAY, icon);

        List<Marker> markers = new ArrayList<Marker>();
        markers.add(marker);

        return markers;
    }
}
