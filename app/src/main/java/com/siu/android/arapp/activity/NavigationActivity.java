package com.siu.android.arapp.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.siu.android.arapp.Application;
import com.siu.android.arapp.R;
import com.siu.android.arapp.common.Calculator;
import com.siu.android.arapp.data.ARData;
import com.siu.android.arapp.dialog.SavePointDialog;
import com.siu.android.arapp.ui.IconMarker;
import com.siu.android.arapp.ui.Marker;
import com.siu.android.arapp.util.FragmentUtil;
import com.siu.android.arapp.view.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NavigationActivity extends AugmentedRealityActivity2 {

    private static final String START = "start";
    private static final String[] CHECKPOINTS = {"chips", "checkpoint2"};
    private static final String DESTINATION = "stones";

    private Location mInitialLocation;
    private Location mLastPointLocation;

    private Location mNewPointOriginLocation;
    private float mStartCheckpointAzimuth;

    private LinkedList<Location> mPointLocations = new LinkedList<Location>();
    private List<Marker> mMarkers = new LinkedList<Marker>();
    private List<String> mImageTargets = new LinkedList<String>();

    private int mCurrentNavigationPoint;

    private SharedPreferences mSharedPreferences;

    private NavigationView mNavigationView;
    private Button mNavigationNextButton;

    private boolean mLocationFromImageTargets = false;
    private boolean mNavigationMode = false;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ActionBar actionBar = getActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background)));

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mNavigationView = new NavigationView(this);
        mNavigationView.setVisibility(View.GONE);
        mContentView.addView(mNavigationView);

        mNavigationNextButton = (Button) mNavigationView.findViewById(R.id.navigation_next_button);

        mNavigationNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToNextPoint();
            }
        });

        mCurrentLocation = ARData.getCurrentLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        // DO NOTHING
//        if (mLocationFromImageTargets) {
//            Log.d(getClass().getName(), "Ignore location from gps, using location from image targets");
//            return;
//        }
//
//        super.onLocationChanged(location);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_store_initial_location:
//                if (!mFirstLocation) {
//                    Toast.makeText(this, "No location avalaible yet", Toast.LENGTH_LONG).show();
//                    return true;
//                }

                mInitialLocation = mCurrentLocation;
                mLastPointLocation = mInitialLocation;

                addPoint("Initial location", mInitialLocation, START);

                return true;

            case R.id.menu_start_checkpoint:
                mNewPointOriginLocation = mLastPointLocation;
                mStartCheckpointAzimuth = Calculator.getAzimuth();
                return true;

            case R.id.menu_end_checkpoint:
                FragmentUtil.showDialog(getFragmentManager(), new SavePointDialog());
                return true;

            case R.id.menu_start_navigation:
                startNavigation();
                return true;

            case R.id.menu_stop_navigation:
                stopNavigation();
                return true;

            case R.id.menu_save_points:
                savePoints();
                return true;

            case R.id.menu_restore_points:
                restorePoints();
                return true;

            case R.id.menu_reset:
                reset();
                return true;
        }

        return false;
    }

    public void calculateEndCheckpoint(int meters, boolean destination) {
        LatLng checkpointLatLng = LatLngTool.travel(new LatLng(mNewPointOriginLocation.getLatitude(), mNewPointOriginLocation.getLongitude()),
                mStartCheckpointAzimuth, meters, LengthUnit.METER);

        Location location = new Location("point");
        location.setLatitude(checkpointLatLng.getLatitude());
        location.setLongitude(checkpointLatLng.getLongitude());
        location.setAltitude(mNewPointOriginLocation.getAltitude());

        mLastPointLocation = location;

        if (destination) {
            addPoint("Destination", location, DESTINATION);
        } else {
            addPoint("Checkpoint " + mPointLocations.size(), location, CHECKPOINTS[mPointLocations.size() - 1]); // first is start
        }
    }

    private void addPoint(String name, Location location, String imageTarget) {
        Marker marker = createMarker(name, location);

        mPointLocations.add(location);
        mMarkers.add(marker);
        mImageTargets.add(imageTarget);
        ARData.addMarkers(mMarkers);
    }

    private void startNavigation() {
        mNavigationView.setVisibility(View.VISIBLE);

        mCurrentNavigationPoint = -1;
        navigateToNextPoint();

        mNavigationMode = true;
    }

    private void stopNavigation() {
        mNavigationView.setVisibility(View.GONE);

        ARData.clearMarkers();
        ARData.addMarkers(mMarkers);

        mNavigationMode = false;
    }

    private void navigateToNextPoint() {
        if (mCurrentNavigationPoint == mPointLocations.size() - 1) {
            Toast.makeText(this, "You reached the destination, restarting navigation from the beginning", Toast.LENGTH_LONG).show();
            mCurrentNavigationPoint = -1;
        }

        String origin;
        String destination;

        if (mCurrentNavigationPoint == -1) {
            origin = "Navigation start";
            destination = mMarkers.get(mCurrentNavigationPoint + 1).getName();
        } else if (mCurrentNavigationPoint == mPointLocations.size() - 1) {
            origin = mMarkers.get(mCurrentNavigationPoint).getName();
            destination = "Navigation end";
        } else {
            origin = mMarkers.get(mCurrentNavigationPoint).getName();
            destination = mMarkers.get(mCurrentNavigationPoint + 1).getName();
        }

        mNavigationView.updateNavigation(origin, destination);

        ARData.clearMarkers();

        List<Marker> markers = new ArrayList<Marker>();
        markers.add(mMarkers.get(++mCurrentNavigationPoint));

        ARData.addMarkers(markers);
    }

    private void savePoints() {
        Set<String> strings = new HashSet<String>();

        for (int i = 0; i < mPointLocations.size(); ++i) {
            String id = (i < 10) ? "0" + i : Integer.toString(i);
            Location location = mPointLocations.get(i);
            Marker marker = mMarkers.get(i);
            String imageTarget = mImageTargets.get(i);

            strings.add(id + ";" + location.getLatitude() + ";" + location.getLongitude() + ";" + location.getAltitude() + ";" + marker.getName() + ";" + imageTarget);
        }

        mSharedPreferences.edit().putStringSet("points", strings).apply();

        Log.d(getClass().getName(), "Save points");
        for (String s : strings) {
            Log.d(getClass().getName(), "Point : " + s);
        }
    }

    private void restorePoints() {
        stopNavigation();
        reset();

        List<String> strings = new ArrayList<String>(mSharedPreferences.getStringSet("points", new HashSet<String>()));
        Collections.sort(strings);

        Log.d(getClass().getName(), "Restore points");
        for (String string : strings) {
            String[] array = string.split(";");
            Location location = new Location("point");
            location.setLatitude(Double.parseDouble(array[1]));
            location.setLongitude(Double.parseDouble(array[2]));
            location.setAltitude(Double.parseDouble(array[3]));

            mPointLocations.add(location);
            mMarkers.add(createMarker(array[4], location));
            mImageTargets.add(array[5]);

            Log.d(getClass().getName(), "Point : " + string);
        }

        mLastPointLocation = mPointLocations.getLast();

        ARData.addMarkers(mMarkers);
    }

    private Marker createMarker(String name, Location location) {
        Bitmap icon = BitmapFactory.decodeResource(Application.getContext().getResources(), R.drawable.ic_launcher);
        return new IconMarker(name, location.getLatitude(), location.getLongitude(), location.getAltitude(), Color.DKGRAY, icon);
    }

    private void reset() {
        mMarkers.clear();
        mPointLocations.clear();
        mImageTargets.clear();
        mLastPointLocation = mInitialLocation;

        ARData.clearMarkers();
    }

    @Override
    public synchronized void imageRecognized(String name, float distance) {
        if (!mNavigationMode) {
            return;
        }

        if (name.equals(mImageTargets.get(mCurrentNavigationPoint))) {
            Location location = mPointLocations.get(mCurrentNavigationPoint);

            distance = distance * (297 / 247f) / 1000f;

            if (distance <= 1) {
                mCurrentLocation = location;
                ARData.setCurrentLocation(mCurrentLocation);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        navigateToNextPoint();
                    }
                });

                Log.d(getClass().getName(), "POINT " + name + " REACHED VIA IMAGE RECOGNITION");
            } else {
                LatLng res = LatLngTool.travel(new LatLng(location.getLatitude(), location.getLongitude()), ARData.getAzimuth() - 180, (double) distance, LengthUnit.METER);
                mCurrentLocation.setLatitude(res.getLatitude());
                mCurrentLocation.setLongitude(res.getLongitude());
                ARData.setCurrentLocation(mCurrentLocation);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogInfoView.updateLocation(mCurrentLocation);
                }
            });
        }
    }
}
