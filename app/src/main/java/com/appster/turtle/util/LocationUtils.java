/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.appster.turtle.ui.locationlog.LocationReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 *
 */
@SuppressWarnings("ALL")
public class LocationUtils extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {

    public static final String TAG = LocationUtils.class.getSimpleName();
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int REQUEST_LOCATION_PERMISSION = 1000;
    public static final float MINIMUM_DISPLACEMENT = 100;
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected LocationSettingsRequest locationSettingsRequest;
    protected Location currentLocation;
    protected Boolean requestingLocationUpdates;
    private FileHandler fileHandler;
    private boolean isBackground;

    public static void addFragment(AppCompatActivity activity) {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(LocationUtils.newInstance(), LocationUtils.TAG);
        fragmentTransaction.commit();
    }

    public static LocationUtils newInstance() {
        return new LocationUtils();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestingLocationUpdates = false;
        fileHandler = new FileHandler("LocationLogs.csv");
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        startUpdates();
    }

    public void setCustomLocationRequest(int timeInterval, int minDistance, int priority) {
        requestingLocationUpdates = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, LocationUtils.this);
        locationRequest.setInterval(timeInterval);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        locationRequest.setFastestInterval(timeInterval / 2);
        locationRequest.setPriority(priority);
        locationRequest.setSmallestDisplacement(minDistance);
        buildLocationSettingsRequest();
        startUpdates();

    }

    protected synchronized void buildGoogleApiClient() {
        LogUtils.LOGI(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setSmallestDisplacement(MINIMUM_DISPLACEMENT);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient,
                        locationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                LogUtils.LOGI(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                LogUtils.LOGI(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    LogUtils.LOGI(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                LogUtils.LOGI(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        LogUtils.LOGI(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        LogUtils.LOGI(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    public void startUpdates() {
        checkLocationSettings();
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (isBackground) {
                Intent intent = new Intent(getActivity(), LocationReceiver.class);
                PendingIntent pintent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                        pintent).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        requestingLocationUpdates = true;
                    }
                });
            } else {

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                        this).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        requestingLocationUpdates = true;
                    }
                });
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            LogUtils.LOGV(TAG1, "Permission: " + permissions[0] + "was " + grantResults[0]);
            startLocationUpdates();
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                requestingLocationUpdates = false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (googleApiClient.isConnected() && requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
//        if (googleApiClient.isConnected()) {
//            stopLocationUpdates();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        googleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LogUtils.LOGI(TAG, "Connected to GoogleApiClient");
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        //PreferenceUtils.setCurrentLocation(location);


        LogUtils.LOGD("Location", "Lat : " + location.getLatitude() + ", " + "Long : " + location.getLongitude());
        LogUtils.LOGD("Accuracy", "Accuracy level : " + location.getAccuracy());
        LogUtils.LOGD("Battery", "Battery level : " + getBatteryLevel() + "%");
        Toast.makeText(getActivity(), "Location changed" + location.getLatitude() + ", " + "Long : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        fileHandler.appendLog(getActivity(), location, getBatteryLevel(), "");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        LogUtils.LOGI(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        LogUtils.LOGI(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public float getBatteryLevel() {
        Intent batteryIntent = getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    /**
     * set true to run location service in backround even when app is killed
     *
     * @param background flag
     */
    public void setBackground(boolean background) {
        isBackground = background;
    }
}
