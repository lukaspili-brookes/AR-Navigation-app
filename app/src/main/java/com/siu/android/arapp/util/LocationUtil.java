package com.siu.android.arapp.util;

import android.location.Location;

/**
 * Created by lukas on 7/6/13.
 */
public class LocationUtil {

    public static boolean equals(Location location1, Location location2) {
        return location1.getLatitude() == location2.getLatitude() && location1.getLongitude() == location2.getLatitude();
    }
}
