
package com.pigmal.android.map;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.pigmal.android.accessory.AccessoryBaseMapActivity;
import com.pigmal.android.ex.accessory.ADKCommandReceiver;
import com.pigmal.android.ex.accessory.DirectionDescriptor.OnDirectionChangeListener;
import com.pigmal.android.ex.accessory.InputController;
import com.pigmal.android.ex.accessory.OutputController;
import com.pigmal.android.ex.accessory.R;

public class MapMainActivity extends AccessoryBaseMapActivity implements LocationListener,
        OnDirectionChangeListener {

    // ADK

    private ADKCommandReceiver mReceiver;

    @SuppressWarnings("unused")
    private OutputController mOutputController;

    private InputController mInputController;

    // ADK
    private static final String TAG = "MapMainActivity";

    private Context mContext;

    Location mLocation;

    MapView mMapView;

    MapController mMapController;

    LocationManager mLocationManager;

    LocationProvider mLocationProvider;

    private ProgressDialog mLoadingDialog;

    private boolean isLoadLocation;

    private static final int MAP_ZOOM = 19;

    private Handler mHandler = new Handler();

    private GpsStopRunnable mGpsStopRunnable = new GpsStopRunnable();

    private static final int GPS_STOP_DELAY_TIME = 60 * 1000;

    private static final int GPS_ACTUARY = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        mContext = getApplicationContext();

        mReceiver = new ADKCommandReceiver();
        mReceiver.setOnDirectionChangeListener(this);
        mOpenAccessory.setListener(mReceiver);

        if (mOpenAccessory.isConnected()) {
        }
        initMapView();
        setNowLocationPin();
        // startGps();
    }

    private void initMapView() {

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(true);

        mMapController = mMapView.getController();
        mMapController.setZoom(MAP_ZOOM);

        int lat = (int) (35.661969 * 1e6);
        int lot = (int) (139.69985 * 1e6);
        GeoPoint geoPoint = new GeoPoint(lat, lot);
        mMapController.animateTo(geoPoint);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    }

    private void getLocation() {
        mLoadingDialog = new ProgressDialog(MapMainActivity.this);
        mLoadingDialog.setMessage(getString(R.string.get_location));
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mLocationManager.removeUpdates(MapMainActivity.this);
                isLoadLocation = false;
                mHandler.removeCallbacks(mGpsStopRunnable);
                finish();
                dialog.dismiss();
            }
        });
        mLoadingDialog.show();

        isLoadLocation = false;
        mLocationManager.removeUpdates(this);
        final Criteria criteria = new Criteria();
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);

        String providerStr = mLocationManager.getBestProvider(criteria, true);
        if (providerStr != null) {
            isLoadLocation = true;
            mLocationManager.requestLocationUpdates(providerStr, 500, 0, this);
        } else {
            mHandler.removeCallbacks(mGpsStopRunnable);
            mLoadingDialog.dismiss();
            Toast.makeText(mContext, "GPSつかえない", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (isLoadLocation && location != null) {

            mLocation = location;

            if (!isEnableActualy(mLocation)) {
                return;
            }

            stopGps();
            mHandler.removeCallbacks(mGpsStopRunnable);
            if (mMapController != null) {
                mMapController.setZoom(MAP_ZOOM);
            }
            mLoadingDialog.setMessage(getString(R.string.get_location));

            setNowLocationPin();

            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }

        }

    }

    private boolean isEnableActualy(Location location) {

        if (location.getAccuracy() <= GPS_ACTUARY) {
            return true;
        } else {
            return false;
        }
    }

    private void startGps() {
        mHandler.postDelayed(mGpsStopRunnable, GPS_STOP_DELAY_TIME);
        getLocation();
    }

    private void stopGps() {
        isLoadLocation = false;
        mLocationManager.removeUpdates(this);
    }

    private void clearOverlay() {
        List<Overlay> overlays = mMapView.getOverlays();
        overlays.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this);
    }

    void update() {
        startGps();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private class GpsStopRunnable implements Runnable {
        public void run() {
            stopGps();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            Toast.makeText(mContext, getString(R.string.err_get_location), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setNowLocationPin() {

        // int latitude = (int) (mLocation.getLatitude() * 1e6);
        // int longitude = (int) (mLocation.getLongitude() * 1e6);

        int latitude = (int) (35.661969 * 1e6);
        int longitude = (int) (139.69985 * 1e6);

        GeoPoint gp = new GeoPoint(latitude, longitude);
        mMapController.animateTo(gp);

        List<Overlay> overlays = mMapView.getOverlays();
        Drawable pin = getResources().getDrawable(R.drawable.ic_launcher_demokit);
        LocationOverlay nowLocationOverlay = new LocationOverlay(pin, (Context) this);
        overlays.add(nowLocationOverlay);
        nowLocationOverlay.addPoint(gp);
    }

    @Override
    protected void onUsbAtached() {
        Log.v(TAG, "onUsbAtached");
    }

    @Override
    protected void onUsbDetached() {
        Log.v(TAG, "onUsbDetached");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Simulate")) {
        } else if (item.getTitle().equals("Quit")) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Simulate");
        menu.add("Quit");
        return true;
    }

    @Override
    public void move(int direction, int accell) {
        // Log.e("TEST", "direction: " + direction);

        GeoPoint mapCenter = mMapView.getMapCenter();
        int latitudeE6 = mapCenter.getLatitudeE6();
        int longitudeE6 = mapCenter.getLongitudeE6();

        GeoPoint geoPoint = null;

        switch (direction) {
            case KeyEvent.KEYCODE_DPAD_UP:
                geoPoint = new GeoPoint(latitudeE6 - 1000, longitudeE6);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                geoPoint = new GeoPoint(latitudeE6 + 1000, longitudeE6);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                geoPoint = new GeoPoint(latitudeE6, longitudeE6 - 1000);
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                geoPoint = new GeoPoint(latitudeE6, longitudeE6 + 1000);
                break;
            default:
                break;
        }

        if (geoPoint != null) {
            mMapController.animateTo(geoPoint);
        }

    }

}
