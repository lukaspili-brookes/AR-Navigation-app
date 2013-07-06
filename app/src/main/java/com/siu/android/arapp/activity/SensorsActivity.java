package com.siu.android.arapp.activity;

import android.app.Activity;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.siu.android.arapp.AppConstants;
import com.siu.android.arapp.common.LowPassFilter;
import com.siu.android.arapp.common.Matrix;
import com.siu.android.arapp.data.ARData;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lukas on 7/2/13.
 */
public class SensorsActivity extends Activity implements SensorEventListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private static final AtomicBoolean computing = new AtomicBoolean(false);

    private static final float temp[] = new float[9];
    private static final float rotation[] = new float[9];
    private static final float grav[] = new float[3];
    private static final float mag[] = new float[3];

    private static final Matrix worldCoord = new Matrix();
    private static final Matrix magneticCompensatedCoord = new Matrix();
    private static final Matrix xAxisRotation = new Matrix();
    private static final Matrix magneticNorthCompensation = new Matrix();

    private static GeomagneticField gmf = null;
    private static float smooth[] = new float[3];
    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    private boolean mPlayServicesInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mPlayServicesInitialized) {
            checkAndInitGooglePlayServices();
        }

        if (mPlayServicesInitialized) {
            mLocationClient.connect();
        }

        double angleX = Math.toRadians(-90);
        double angleY = Math.toRadians(-90);

        xAxisRotation.set(1f,
                0f,
                0f,
                0f,
                (float) Math.cos(angleX),
                (float) -Math.sin(angleX),
                0f,
                (float) Math.sin(angleX),
                (float) Math.cos(angleX));

        try {
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);

            if (sensors.size() > 0) {
                sensorGrav = sensors.get(0);
            }

            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

            if (sensors.size() > 0) {
                sensorMag = sensors.get(0);
            }
            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

            try {
                gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(),
                        (float) ARData.getCurrentLocation().getLongitude(),
                        (float) ARData.getCurrentLocation().getAltitude(),
                        System.currentTimeMillis());
                angleY = Math.toRadians(-gmf.getDeclination());

                synchronized (magneticNorthCompensation) {

                    magneticNorthCompensation.toIdentity();

                    magneticNorthCompensation.set((float) Math.cos(angleY),
                            0f,
                            (float) Math.sin(angleY),
                            0f,
                            1f,
                            0f,
                            (float) -Math.sin(angleY),
                            0f,
                            (float) Math.cos(angleY));

                    magneticNorthCompensation.prod(xAxisRotation);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        if (mPlayServicesInitialized) {
            if (mLocationClient.isConnected()) {
                mLocationClient.removeLocationUpdates(this);
            }
            mLocationClient.disconnect();
        }

        sensorMgr.unregisterListener(this, sensorGrav);
        sensorMgr.unregisterListener(this, sensorMag);
        sensorMgr = null;

        super.onStop();
    }

    private void checkAndInitGooglePlayServices() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Log.w(getClass().getName(), "Google play services not installed");
            Toast.makeText(this, "You need to install Google play services", Toast.LENGTH_LONG).show();
            return;
        }

        mPlayServicesInitialized = true;
        mLocationClient = new LocationClient(this, this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(AppConstants.LOCATION_UPDATE_INTERVAL_MS);
        mLocationRequest.setFastestInterval(AppConstants.LOCATION_UPDATE_FASTEST_INTERVAL_MS);
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (!computing.compareAndSet(false, true)) return;

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            smooth = LowPassFilter.filter(0.5f, 1.0f, evt.values, grav);
            grav[0] = smooth[0];
            grav[1] = smooth[1];
            grav[2] = smooth[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smooth = LowPassFilter.filter(2.0f, 4.0f, evt.values, mag);
            mag[0] = smooth[0];
            mag[1] = smooth[1];
            mag[2] = smooth[2];
        }

        SensorManager.getRotationMatrix(temp, null, grav, mag);

        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotation);

        worldCoord.set(rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]);

        magneticCompensatedCoord.toIdentity();

        synchronized (magneticNorthCompensation) {
            magneticCompensatedCoord.prod(magneticNorthCompensation);
        }

        magneticCompensatedCoord.prod(worldCoord);

        magneticCompensatedCoord.invert();

        ARData.setRotationMatrix(magneticCompensatedCoord);

        computing.set(false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == null) {
            return;
        }

        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w(getClass().getName(), "Compass data unreliable");
        }
    }

    /* Location */

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(getClass().getName(), "Location client connected");
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        location.setAltitude(135);

        mCurrentLocation = location;
        ARData.setCurrentLocation(location);

        gmf = new GeomagneticField((float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                System.currentTimeMillis());

        double angleY = Math.toRadians(-gmf.getDeclination());

        synchronized (magneticNorthCompensation) {
            magneticNorthCompensation.toIdentity();

            magneticNorthCompensation.set((float) Math.cos(angleY),
                    0f,
                    (float) Math.sin(angleY),
                    0f,
                    1f,
                    0f,
                    (float) -Math.sin(angleY),
                    0f,
                    (float) Math.cos(angleY));

            magneticNorthCompensation.prod(xAxisRotation);
        }
    }
}
