package com.example.tripathy.sharelocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.tripathy.sharelocation.BaseClass.BaseFragmentActivity;
import com.example.tripathy.sharelocation.Enum.Enum;
import com.example.tripathy.sharelocation.Fragment.HomeFragment;
import com.example.tripathy.sharelocation.Fragment.ReceivedFragment;
import com.example.tripathy.sharelocation.Fragment.SharedFragment;
import com.example.tripathy.sharelocation.lib.Authentication.LoggedInUser;
import com.example.tripathy.sharelocation.lib.ChangeButtonColor;
import com.example.tripathy.sharelocation.lib.LocationHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Locale;

/**
 * Created by tripathy on 12/3/2015.
 */
public class MainActivity extends BaseFragmentActivity {

    private static final String TAG = "SL: MainActivity: ";
    public static RequestQueue queue;
    private static Enum.CurrentFragment currentFragment;
    public static boolean isAppOpeningThroughNotification = false;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Thread.setDefaultUncaughtExceptionHandler(new ReportHelper(this));
        super.onCreate(savedInstanceState);
        LocationHelper.shareSharedReceivedLocation = null;
        setContentView(R.layout.activity_main);
        //Set home fragment onCreate.
        currentFragment = null;
        setFragment(null);
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        HomeFragment homeFragment = new HomeFragment();
//        transaction.add(R.id.fragment_placeholder, homeFragment);
//        transaction.commit();

        if (queue == null) queue = Volley.newRequestQueue(getBaseContext());
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            final int REQUEST_LOCATION = 2;
            if (Build.VERSION.SDK_INT < 23) {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
                return;
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("You need to allow location access.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_LOCATION);
                                }
                            });
                    return;
                } else {
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                    return;
                }

            }
//            Toast.makeText(getBaseContext(), "Do not have access to access location.", Toast.LENGTH_LONG).show();
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this).setMessage(message)
                .setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show();
    }

    public void onSelectFragment(View view) {

        setFragment(view);
    }

    private void setFragment(View view) {
        Fragment fragment;
//        Button btnView = (Button) findViewById(R.id.btnView);
        LinearLayout layoutSubmit = (LinearLayout) findViewById(R.id.layoutSubmit);
        LinearLayout layoutPlaceholder = (LinearLayout) findViewById(R.id.layoutPlaceholder);
        LinearLayout fragmentPlaceHolder = (LinearLayout) findViewById(R.id.layoutPlaceholder);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        Button btnShared = (Button) findViewById(R.id.btnShared);
        Button btnReceived = (Button) findViewById(R.id.btnReceived);
        btnHome.setActivated(false);
        btnShared.setActivated(false);
        btnReceived.setActivated(false);

        if (view == findViewById(R.id.btnShared)) {
            if (currentFragment == Enum.CurrentFragment.sharedfragment) return;
            layoutSubmit.setVisibility(LinearLayout.VISIBLE);
            currentFragment = Enum.CurrentFragment.sharedfragment;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8.5f);
            params.setMargins(10, 20, 10, 20);
            layoutPlaceholder.setLayoutParams(params);
            fragment = new SharedFragment();
            ChangeButtonColor.Change(getBaseContext(), btnShared,
                    new Button[]{btnHome, btnShared, btnReceived});
        } else if (view == findViewById(R.id.btnReceived) || isAppOpeningThroughNotification) {
            if (currentFragment == Enum.CurrentFragment.receivedfragment) return;
            layoutSubmit.setVisibility(LinearLayout.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8.5f);
            params.setMargins(10, 20, 10, 20);
            layoutPlaceholder.setLayoutParams(params);
            currentFragment = Enum.CurrentFragment.receivedfragment;
            fragment = new ReceivedFragment();
            ChangeButtonColor.Change(getBaseContext(), btnReceived,
                    new Button[]{btnHome, btnShared, btnReceived});
        } else {
            layoutSubmit.setVisibility(LinearLayout.GONE);
            if (currentFragment == Enum.CurrentFragment.homefragment) return;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 9.2f);
            params.setMargins(10, 20, 10, 20);
            layoutPlaceholder.setLayoutParams(params);
            currentFragment = Enum.CurrentFragment.homefragment;
            fragment = new HomeFragment();
            ChangeButtonColor.Change(getBaseContext(), btnHome,
                    new Button[]{btnHome, btnShared, btnReceived});
            //Handling search text search click of android keyboard.
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layoutPlaceholder, fragment, "");
        transaction.addToBackStack(null);
        transaction.commit();
        btnHome.setActivated(true);
        btnShared.setActivated(true);
        btnReceived.setActivated(true);
        if (isAppOpeningThroughNotification) isAppOpeningThroughNotification = false;

    }

    public void onActionClick(View view) {
        if (view == findViewById(R.id.btnView)) {
            btnViewClickHandler();
        } else if (view == findViewById(R.id.btnShare)) {
            BtnShareClickHandler();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!LoggedInUser.getIsAuthenticated()) {
            Intent i = new Intent(getApplicationContext(), SignupActivity.class);
            //Code 3 for starting singup activity.
            startActivityForResult(i, 3);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            Log.v(TAG, "App going in background.");
//            isMSocketInitialized = false;
//            mSocket.disconnect();
//            mSocket.off("checkNewReceivedMessageResponse", onCheckNewReceivedMessageResponse);
//            mSocket.off("connect_error", onConnectionError);
        }
    }

    public void BtnShareClickHandler() {
        Button btnView = (Button) findViewById(R.id.btnView);

        if (currentFragment == Enum.CurrentFragment.homefragment) {
            TextView txtMessage = (TextView) HomeFragment.fragmentHome.findViewById(R.id.txtMessage);
            if (!txtMessage.getText().toString().equals("")) {
                //Request code 2 is for shared/received location (1 is for sharing searched location.
                ShowContactActivity.requestCode = Enum.ShareLocationRequestCode.searched;
                LocationHelper.locationMessage = txtMessage.getText().toString();
                Intent i = new Intent(getApplicationContext(), ShowContactActivity.class);
                startActivityForResult(i, 1);
            } else {
                Toast toast = Toast.makeText(getBaseContext(), "Please enter message text.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                txtMessage.requestFocus();
            }
        } else {
            //Handle case when user is on Shared/Received fragment
            if (LocationHelper.shareSharedReceivedLocation == null) {
                //Code to handle when user did'nt selected any location.
                Toast toast = Toast.makeText(getBaseContext(), "Please select any location to share.",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            //Request code 2 is for shared/received location (1 is for sharing searched location.
            ShowContactActivity.requestCode = Enum.ShareLocationRequestCode.sharedreceived;
            Intent i = new Intent(getApplicationContext(), ShowContactActivity.class);
            startActivityForResult(i, 1);
        }
    }

    TextView txtSearch;

    private void btnViewClickHandler() {
        //This code will execute from shared/received location (On click of view button).
        if (LocationHelper.shareSharedReceivedLocation == null) {
            //Code to handle when user did'nt selected any location.
            Toast toast = Toast.makeText(getBaseContext(), "Please select any location to view.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        double lat = LocationHelper.shareSharedReceivedLocation.latitude;
        double lang = LocationHelper.shareSharedReceivedLocation.longitude;
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=15&q=%f,%f", lat, lang, lat, lang);
        Log.d(TAG, "View uri: " + uri);
        startActivity(
                new Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri)));
    }

//    public void SearchLocation() {
//        txtSearch = (TextView) HomeFragment.fragmentHome.findViewById(R.id.txtPlaceDetails);
//        if (!txtSearch.getText().toString().equals("")) {
//            String url = getString(R.string.google_api_url)
//                    + Uri.encode(txtSearch.getText().toString());
//            Log.d(TAG, "url: " + url);
//            JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                    (Request.Method.GET, url, (JSONObject) null, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                Log.d(TAG, "response: " + response.toString());
//                                JSONArray results = response.getJSONArray("results");
//                                Log.d(TAG, "result: " + results.toString());
//                                if (results.length() == 0) {
//                                    Toast toast = Toast.makeText(getBaseContext(), "No location found.Please modify search text.", Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
//                                } else {
//                                    JSONObject resultobj = results.getJSONObject(0);
//                                    Log.d(TAG, "resultobj: " + resultobj.toString());
//
//                                    JSONObject geometry = resultobj.getJSONObject("geometry");
//                                    Log.d(TAG, "geometry: " + geometry.toString());
//
//                                    JSONObject location = geometry.getJSONObject("location");
//                                    Log.d(TAG, location.toString());
////                                    txtSearch.setText("");
//                                    new HomeFragment().ChangeLocationSettingOnSearchClick(location.getDouble("lat"),
//                                            location.getDouble("lng"));
//                                    //Removing focus to close keypad of android.
//                                    txtSearch.clearFocus();
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                                }
//                            } catch (JSONException e) {
//                                Log.e(TAG, e.toString());
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // TODO Auto-generated method stub
//                            Log.e(TAG, error.toString());
//                            if (error.getClass().equals(TimeoutError.class)) {
//                                // Show timeout error message
//                                Toast toast =
//                                        Toast.makeText(getBaseContext(),
//                                                "Oops! Please check your internet connection.",
//                                                Toast.LENGTH_SHORT);
//                                toast.setGravity(Gravity.CENTER, 0, 0);
//                                toast.show();
//                            } else {
//                                // Show timeout error message
//                                Toast toast =
//                                        Toast.makeText(getBaseContext(),
//                                                "An error occurred.Please ensure you have internet access.",
//                                                Toast.LENGTH_SHORT);
//                                toast.setGravity(Gravity.CENTER, 0, 0);
//                                toast.show();
//                            }
//                        }
//                    });
//            queue.add(jsObjRequest);
//        } else {
//            Toast toast = Toast.makeText(getBaseContext(), "Please enter search text.", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//            txtSearch.requestFocus();
//        }
//    }


    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast toast = Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;

    public void findPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_OK) {
//                    final SearchView txtPlaceDetails = (SearchView) HomeFragment.fragmentHome.findViewById(R.id.txtPlaceDetails);
                    final TextView txtMessage = (TextView) HomeFragment.fragmentHome.findViewById(R.id.txtMessage);
//                    txtPlaceDetails.setQuery("", false);
                    txtMessage.setText("");
                }
                // TODO Update your TextView.
                break;
            }
            case (PLACE_AUTOCOMPLETE_REQUEST_CODE): {
                // A place has been received; use requestCode to track the request.
                EditText txtPlaceDetails = (EditText) HomeFragment.fragmentHome.findViewById(R.id.txtPlaceDetails);
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.d(TAG, "resultCode: " + resultCode);
                    Log.d(TAG, "Place: " + place.getName());
                    Log.d(TAG, "LanLang: " + place.getLatLng());
                    txtPlaceDetails.setText(place.getName());
                    new HomeFragment().ChangeLocationSettingOnSearchClick(place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    LocationHelper.isSeachClicked = true;
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    LocationHelper.isSeachClicked = false;
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());
                    Toast toast = Toast.makeText(getBaseContext(), "An error occurred.Please try again.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                    LocationHelper.isSeachClicked = false;
                    txtPlaceDetails.setText("");
                }
                break;
            }
            case (3): {
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                }
            }
            break;
        }
    }
}
