package com.example.tripathy.sharelocation.lib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.MyLocation;

/**
 * Created by tripathy on 12/1/2015.
 */
public class LocationHelper extends Activity {

    protected static LocationManager locationManager;
    private static final String TAG = "SL: LocationHelper: ";
    //Save location (you can share this location to others)
    public static Location savedLocation;
    public static long selectedNumber;
    public static String locationMessage;
    private static Context mContext;
    //Will store location temporarily when item clicked on Shared/Received fragment,to share.
    public static MyLocation shareSharedReceivedLocation;
    //Flag to disable listing current location when search is clicked.
    public static boolean isSeachClicked=false;

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    public LocationHelper(Context applicationContext) {
        mContext = applicationContext;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    //Check if gps is enabled or not if not prompt user to enable it.
    public boolean IsGpsEnabled() {
        //Request to enable gps if disabled.
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w(TAG, "GPS is not enabled");
            Toast toast = Toast.makeText(mContext, "Please enable location service.", Toast.LENGTH_LONG);
            View view = toast.getView();
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cadetblue));
//            TextView text = (TextView) view.findViewById(android.R.id.message);
//            text.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            toast.show();
            return false;
        }
        return true;
    }

    //    get the last known location from a specific provider (network/gps)
    public Location getBestLocation() {
//        Toast.makeText(mContext, "Calling Best Location.", Toast.LENGTH_SHORT).show();
        Location gpslocation = GetLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =
                GetLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null && networkLocation != null) {
            Log.d(TAG, "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null && gpslocation != null) {
            Log.d(TAG, "No Network Location available.");
            return gpslocation;
        }
        if (networkLocation == null && gpslocation == null) {
//            Toast toast = Toast.makeText(mContext, "Location service is not active.Enable it in setting-> Personal ->Location",
//                    Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
            Location defaultLocation = new Location("");
            defaultLocation.setLatitude(12.9715987);
            defaultLocation.setLongitude(77.5945627);
            defaultLocation.setTime(System.currentTimeMillis());
            return defaultLocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - (60 * 1000);
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location.");
//            Toast.makeText(mContext, "Returning current GPS Location.", Toast.LENGTH_SHORT).show();
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network");
//            Toast.makeText(mContext, "GPS is old, Network is current, returning network.", Toast.LENGTH_SHORT).show();

            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer).");
//            Toast.makeText(mContext, "Both are old, returning gps(newer).", Toast.LENGTH_SHORT).show();
            return gpslocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer)");
//            Toast.makeText(mContext, "Both are old, returning network(newer).", Toast.LENGTH_SHORT).show();
            return networkLocation;
        }
    }

    //Get location for earch provider.
    private Location GetLocationByProvider(String provider) {
        Location location = null;
//        if (!isProviderSupported(provider)) {
//            return null;
//        }
        try {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                Toast.makeText(mContext, "Do not have access of ACCESS_COARSE_LOCATION.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Do not have access of ACCESS_COARSE_LOCATION." + provider);
            }

            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            } else {
//                Toast.makeText(mContext, "Provider is not enabled." + provider, Toast.LENGTH_SHORT).show();
                Log.d(TAG, provider + "is not enabled.");
            }
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Cannot acces Provider." + provider);
            Toast.makeText(mContext, "Cannot acces Provider." + provider, Toast.LENGTH_LONG).show();
        }
        return location;
    }

}
