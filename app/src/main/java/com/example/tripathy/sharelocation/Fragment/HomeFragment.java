package com.example.tripathy.sharelocation.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripathy.sharelocation.MainActivity;
import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.LocationHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by tripathy on 12/3/2015.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    //    protected GoogleMap mMap;
    private static final String TAG = "SL: HomeFragment: ";
    private SupportMapFragment fragment;
    private static GoogleMap map;
    //Used to check location permission is enabled or not.
    private static boolean isFirstTimeLoading = true;
    protected static LocationHelper locationHelper;
    protected static LocationManager locationManager;
    public static View fragmentHome;
    static boolean hasFineLocationPermission = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Calling onCreateView.");
        locationHelper = new LocationHelper(getContext());
        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        fragmentHome = inflater.inflate(R.layout.fragment_home, container, false);
//        Toast.makeText(getContext(), "createview.", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Requesting ACCESS_FINE_LOCATION permission.");
            hasFineLocationPermission = false;
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            hasFineLocationPermission = true;
        }
        if (!locationHelper.IsGpsEnabled()) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        return fragmentHome;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "Calling onActivityCreated.");
        super.onActivityCreated(savedInstanceState);
        if (hasFineLocationPermission) {
            FragmentManager fm = getChildFragmentManager();
            fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, fragment).commit();
            }
        }

        //Setting search listener
//        EditText txtSearch = (EditText) fragmentHome.findViewById(R.id.txtSearch);
//        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                                                @Override
//                                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                                                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                                                        ((MainActivity) getActivity()).SearchLocation();
//                                                        return true;
//                                                    }
//                                                    return false;
//                                                }
//                                            }
//        );

        EditText textMessage = (EditText) fragmentHome.findViewById(R.id.txtMessage);
//        SearchView txtPlaceDetails = (SearchView) fragmentHome.findViewById(R.id.txtPlaceDetails);
//        txtPlaceDetails.setQueryHint("Search location here");
        textMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                  @Override
                                                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                      if (actionId == EditorInfo.IME_ACTION_SEND) {
                                                          ((MainActivity) getActivity()).BtnShareClickHandler();
                                                          return true;
                                                      }
                                                      return false;
                                                  }
                                              }
        );

    }

    @Override
    public void onResume() {
        Log.d(TAG, "Calling onResume.");
        super.onResume();
        if (hasFineLocationPermission) {
//        if (map == null) {
            map = fragment.getMap();
//        }
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                final int REQUEST_LOCATION = 2;
                if (Build.VERSION.SDK_INT < 23) {
                    ActivityCompat.requestPermissions(
                            getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                    return;
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    } else {
                        ActivityCompat.requestPermissions(
                                getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);
                        return;
                    }

                }
//            Toast.makeText(getBaseContext(), "Do not have access to access location.", Toast.LENGTH_LONG).show();
            }
            if (LocationHelper.isSeachClicked == true) {
                LocationHelper.isSeachClicked = false;
            } else {
                showCurrentLocation = true;
                StartRequestingLocationUpdate();
                EditText editText = (EditText) fragmentHome.findViewById(R.id.txtPlaceDetails);
                if (!editText.getText().equals(""))
                    editText.setText("");
            }
            map.setMyLocationEnabled(true);
//            Toast.makeText(getContext(), "onresume.", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), "showCurrentLocation: ." + showCurrentLocation, Toast.LENGTH_SHORT).show();

            if (showCurrentLocation)
                setMarkerLocation(locationHelper.getBestLocation());
            else {
//                Toast.makeText(getContext(), "locationHelper.savedLocation." + LocationHelper.savedLocation.getLatitude(), Toast.LENGTH_SHORT).show();
                setMarkerLocation(LocationHelper.savedLocation);
            }
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Calling onPause.");
//        Toast.makeText(getContext(), "onPause.", Toast.LENGTH_SHORT).show();
        super.onPause();
        if (hasFineLocationPermission) {
            StopRequestingLocationUpdate();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Calling onDestroy.");
//        Toast.makeText(getContext(), "onDestroy.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
    }

    public static boolean showCurrentLocation = true;
    public static boolean isLocationUpdateAlreadySubsribed = false;
    private static Marker marker;

    protected void setMarkerLocation(Location location) {
        Log.d(TAG, "Calling setMarkerLocation.");
        LocationHelper.savedLocation = location;
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker != null) {
            marker.remove();
            map.clear();
        }
        marker = map.addMarker(new MarkerOptions().position(currentLocation));
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    //Handle location related setting when Search button is clicked in main activity.
    public void ChangeLocationSettingOnSearchClick(double lat, double lang) {
//        StopRequestingLocationUpdate();
        LocationHelper.savedLocation.setLatitude(lat);
        LocationHelper.savedLocation.setLongitude(lang);
        showCurrentLocation = false;
//        setMarkerLocation(LocationHelper.savedLocation);
    }

    //    Subscribe location update.
    public void StartRequestingLocationUpdate() {
        Log.d(TAG, "Calling StartRequestingLocationUpdate.");
        if (showCurrentLocation && !isLocationUpdateAlreadySubsribed) {
            isLocationUpdateAlreadySubsribed = true;
            if (isFirstTimeLoading) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    Toast.makeText(getContext(), "Do not have access of location service.", Toast.LENGTH_LONG).show();
                }
                isFirstTimeLoading = false;
            }
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(bestProvider, 15000, 20, this);
        }
    }

    //    UnSubscribe location update.
    public void StopRequestingLocationUpdate() {
        Log.d(TAG, "Calling StopRequestingLocationUpdate.");
        if (isLocationUpdateAlreadySubsribed) {
            isLocationUpdateAlreadySubsribed = false;
            if (isFirstTimeLoading) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    Toast.makeText(getContext(), "Do not have access to access location.", Toast.LENGTH_LONG).show();
                }
                isFirstTimeLoading = false;
            }
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (showCurrentLocation)
            setMarkerLocation(locationHelper.getBestLocation());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setSearchText(String s) {

    }


}
