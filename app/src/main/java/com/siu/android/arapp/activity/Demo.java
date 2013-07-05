package com.siu.android.arapp.activity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.siu.android.arapp.data.ARData;
import com.siu.android.arapp.data.TestSource;
import com.siu.android.arapp.ui.Marker;

import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class extends the AugmentedReality and is designed to be an example on
 * how to extends the AugmentedReality class to show multiple data sources.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Demo extends AugmentedReality {

    private static final String TAG = "Demo";
    private static final String locale = Locale.getDefault().getLanguage();
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
//    private static final Map<String, NetworkDataSource> sources = new ConcurrentHashMap<String, NetworkDataSource>();

    private static Toast myToast = null;
//    private static VerticalTextView text = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();

        // Create toast
//        myToast = new Toast(getApplicationContext());
//        myToast.setGravity(Gravity.CENTER, 0, 0);
//        // Creating our custom text view, and setting text/rotation
//        text = new VerticalTextView(getApplicationContext());
//        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        text.setLayoutParams(params);
//        text.setBackgroundResource(android.R.drawable.toast_frame);
//        text.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
//        text.setShadowLayer(2.75f, 0f, 0f, Color.parseColor("#BB000000"));
//        myToast.setView(text);
//        // Setting duration and displaying the toast
//        myToast.setDuration(Toast.LENGTH_SHORT);


        // Local
//        LocalDataSource localData = new LocalDataSource(this.getResources());

        TestSource testSource = new TestSource();
        ARData.addMarkers(testSource.getMarkers());

        // Network
//        NetworkDataSource twitter = new TwitterDataSource(this.getResources());
//        sources.put("twitter", twitter);
//        NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources());
//        sources.put("wiki", wikipedia);
//        NetworkDataSource googlePlaces = new GooglePlacesDataSource(this.getResources());
//        sources.put("googlePlaces", googlePlaces);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.v(TAG, "onOptionsItemSelected() item=" + item);
//        switch (item.getItemId()) {
//            case R.id.showRadar:
//                showRadar = !showRadar;
//                item.setTitle(((showRadar) ? "Hide" : "Show") + " Radar");
//                break;
//            case R.id.showZoomBar:
//                showZoomBar = !showZoomBar;
//                item.setTitle(((showZoomBar) ? "Hide" : "Show") + " Zoom Bar");
//                zoomLayout.setVisibility((showZoomBar) ? LinearLayout.VISIBLE : LinearLayout.GONE);
//                break;
//            case R.id.exit:
//                finish();
//                break;
//        }
//        return true;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        updateData(location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void markerTouched(Marker marker) {
        Toast.makeText(this, marker.getName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateDataOnZoom() {
        super.updateDataOnZoom();
        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
    }

    private void updateData(final double lat, final double lon, final double alt) {
//        try {
//            exeService.execute(new Runnable() {
//
//                @Override
//                public void run() {
//                    for (NetworkDataSource source : sources.values())
//                        download(source, lat, lon, alt);
//                }
//            });
//        } catch (RejectedExecutionException rej) {
//            Log.w(TAG, "Not running new download Runnable, queue is full.");
//        } catch (Exception e) {
//            Log.e(TAG, "Exception running download Runnable.", e);
//        }
    }

//    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
//        if (source == null) return false;
//
//        String url = null;
//        try {
//            url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);
//        } catch (NullPointerException e) {
//            return false;
//        }
//
//        List<Marker> markers = null;
//        try {
//            markers = source.parse(url);
//        } catch (NullPointerException e) {
//            return false;
//        }
//
//        ARData.addMarkers(markers);
//        return true;
//    }
}
