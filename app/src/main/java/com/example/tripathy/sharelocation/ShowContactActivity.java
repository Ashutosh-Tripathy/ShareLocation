package com.example.tripathy.sharelocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.tripathy.sharelocation.BaseClass.BaseFragmentActivity;
import com.example.tripathy.sharelocation.BaseClass.CustomFragmentBase;
import com.example.tripathy.sharelocation.Enum.Enum;
import com.example.tripathy.sharelocation.Fragment.HomeFragment;
import com.example.tripathy.sharelocation.Fragment.RecentContactFragment;
import com.example.tripathy.sharelocation.Fragment.SavedContactFragment;
import com.example.tripathy.sharelocation.lib.Authentication.LoggedInUser;
import com.example.tripathy.sharelocation.lib.ChangeButtonColor;
import com.example.tripathy.sharelocation.lib.Dal.MyLocation;
import com.example.tripathy.sharelocation.lib.LocationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.socket.emitter.Emitter;

//import com.github.nkzawa.emitter.Emitter;

/**
 * Created by tripathy on 12/8/2015.
 */
public class ShowContactActivity extends BaseFragmentActivity {


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String TAG = "SL: ShowCntAct: ";
    String apiBaseURL;
    public static RequestQueue queue;
    //Will determine to share searched or sharedreceived location.
    public static Enum.ShareLocationRequestCode requestCode;

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mSocket.off("shareLocationResponse", onShareLocationResponse);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationHelper.selectedNumber = 0;
        setContentView(R.layout.activity_show_contact);
//        Log.e(TAG, "before manage app data create.");
//        Log.e(TAG, "after manage app data create.");
        apiBaseURL = getString(R.string.api_Url);
        FragmentManager fm = getSupportFragmentManager();
        if (queue == null) queue = Volley.newRequestQueue(getBaseContext());
//        FragmentTransaction transaction = fm.beginTransaction();
////        Log.e(TAG, "before RecentContactFragment app data create.");
//        RecentContactFragment recentContactFragment = new RecentContactFragment();
//        transaction.add(R.id.fragment_contact_placeholder, recentContactFragment);
//        transaction.commit();
        onSelectFragment(null);
        //Set home fragment onCreate.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
//        Log.e(TAG, "On create complted.");
        //Close this activity if location is sent successfully.
        mSocket.on("shareLocationResponse", onShareLocationResponse);
    }

    public void onSelectFragment(View view) {

        Fragment fragment;
//        Button btnSearch1 = (Button) findViewById(R.id.btnView);
        Button btnSavedContact = (Button) findViewById(R.id.btnSavedContact);
        Button btnRecentContact = (Button) findViewById(R.id.btnRecentContact);

        if (view == findViewById(R.id.btnSavedContact)) {
            fragment = new SavedContactFragment();
            ChangeButtonColor.Change(getBaseContext(), btnSavedContact,
                    new Button[]{btnSavedContact, btnRecentContact});
        } else {
            fragment = new RecentContactFragment();
            ChangeButtonColor.Change(getBaseContext(), btnRecentContact,
                    new Button[]{btnSavedContact, btnRecentContact});
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_contact_placeholder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    Intent resultIntent = new Intent();

    public void onActionClick(View view) {
        if (view == findViewById(R.id.btnSend)) {
//            LoggedInUser.getLoggedInUser().setUserId(1);
//            mSocket.on("connect_error", onConnectionError);
            String url = apiBaseURL + "postLocation";
            Log.d(TAG, "post url: " + url);
            JSONObject jsonObject;
            if (LocationHelper.selectedNumber == 0) {
                Toast toast = Toast.makeText(getBaseContext(), "Please select any contact.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            MyLocation tempLocation;
            //Request code 1 is for sharing searched location and 2 for shared/received location.
            if (requestCode == Enum.ShareLocationRequestCode.sharedreceived.searched) {
                tempLocation = new MyLocation(null, LoggedInUser.getUserId()
                        , 0, LocationHelper.locationMessage,
                        utcDateFormat.format(new Date(System.currentTimeMillis())),
                        LocationHelper.savedLocation.getLatitude(),
                        LocationHelper.savedLocation.getLongitude(), 1, LocationHelper.selectedNumber);
            } else {
                tempLocation = new MyLocation(null, LoggedInUser.getUserId()
                        , 0, LocationHelper.shareSharedReceivedLocation.message,
                        utcDateFormat.format(new Date(System.currentTimeMillis())),
                        LocationHelper.shareSharedReceivedLocation.latitude,
                        LocationHelper.shareSharedReceivedLocation.longitude, 1,
                        LocationHelper.selectedNumber);
            }


            //Create Json object for sharing.
            try {
                jsonObject = new JSONObject();
                // jsonObject.put("location_id", tempLocation.location_id);
                jsonObject.put("sender_id", tempLocation.sender_id);
                //jsonObject.put("receiver_id", tempLocation.receiver_id);
                jsonObject.put("message", tempLocation.message);
                //Included to check timeout issue.
                jsonObject.put("created_time", tempLocation.created_time);
                jsonObject.put("latitude", tempLocation.latitude);
                jsonObject.put("longitude", tempLocation.longitude);
                // jsonObject.put("status", tempLocation.status);
                jsonObject.put("mobile_number", tempLocation.mobile_number);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "An error occureed.",
                        Toast.LENGTH_SHORT).show();
//                mSocket.off("connect_error", onConnectionError);
                return;
            }
            final boolean[] isShareLocationSuccessfull = {false};
            showConnectionErrorMessage = true;
            mSocket.emit("shareLocation", jsonObject);
//            mSocket.off("connect_error", onConnectionError);

//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        Log.v(TAG, "Start waiting for thread.");
//                        synchronized (this) {
//                            wait(6000);
//                        }
//                    } catch (InterruptedException ex) {
//                    }
//                    // TODO
//                }
//            };
//            thread.start();
//            try {
//                synchronized (this) {
//                    wait(7000);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            final CountDownLatch loginLatch = new CountDownLatch(1);
//            try {
//                if (loginLatch.await(10L, TimeUnit.SECONDS)) {
//
//                } else {
//                    Log.w(TAG, "loginLatch timeout");
//                    Toast toast = Toast.makeText(getBaseContext(), "Timeout.Please try again.", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                }
//            } catch (InterruptedException e) {
//                Log.w(TAG, "InterruptedException");
//                Toast toast = Toast.makeText(getBaseContext(), "An error occoured.Please try again.", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                Log.e(TAG, e.toString());
//            }
//            Log.v(TAG, "isShareLocationSuccessfull: " + isShareLocationSuccessfull[0]);
//            if (isShareLocationSuccessfull[0]) {

//            } else {
//                Toast toast = Toast.makeText(getBaseContext(), "An error occoured.Please try again.", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }
        } else if (view == findViewById(R.id.btnCancel)) {
            LocationHelper.selectedNumber = 0;
//            LocationHelper.shareSharedReceivedLocation = null;
            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        }
    }

    MyLocation tempLocation;
    public Emitter.Listener onShareLocationResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            JSONArray jsonArray = (JSONArray) args[0];
            String response = "";
            JSONObject data = new JSONObject();
            try {
                data = jsonArray.getJSONObject(0);
                response = data.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            Log.v(TAG, "In onShareLocationResponse method.");
            Log.d(TAG, "Response code : Data " + response + " : " + data.toString());

            if (response == "") {
                ShowContactActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "An error occurred.Please try again.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            } else if (response.equals(Enum.ShareLocationResponse.success.toString())) {
                Log.v(TAG, "Got acknowledgement from server.");
                HomeFragment.showCurrentLocation = true;
                LocationHelper.selectedNumber = 0;
                LocationHelper.shareSharedReceivedLocation = null;
//                isShareLocationSuccessfull[0] = true;
//                Log.v(TAG, "isShareLocationSuccessfull: " + isShareLocationSuccessfull[0]);
                MyLocation tempLocation;
                try {
                    tempLocation = new MyLocation(data.getInt("location_id"),
                            LoggedInUser.getUserId(),
                            data.getInt("receiver_id"),
                            data.getString("message"), data.getString("created_time"),
                            data.getDouble("latitude"), data.getDouble("longitude"), data.getInt("status"),
                            data.getLong("mobile_number"));
                    manageAppData.InsertLocation(tempLocation);
                    ShowContactActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            CustomFragmentBase.NotifyChange();
                        }
                    });
                    ShowContactActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(getBaseContext(), "Shared location: " + new Gson().toJson(
//                                            manageAppData.getSharedLocation()),
//                                    Toast.LENGTH_LONG).show();
                            Toast toast = Toast.makeText(getBaseContext(), "Sent successfully!!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            } else if (response.equals(Enum.ShareLocationResponse.fail.toString())) {
                ShowContactActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "An error occurred.Please try again.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            } else if (response.equals(Enum.ShareLocationResponse.notreg.toString())) {

                tempLocation = null;
                try {
                    tempLocation = new MyLocation(data.getInt("location_id"),
                            LoggedInUser.getUserId(),
                            data.getInt("receiver_id"),
                            data.getString("message"), data.getString("created_time"),
                            data.getDouble("latitude"), data.getDouble("longitude"), data.getInt("status"),
                            data.getLong("mobile_number"));
                    ShowContactActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(ShowContactActivity.this)
                                    .setTitle("Confirm")
                                    .setMessage("This user is not registered in our system.Do you want to share this location " +
                                            "through SMS?")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            manageAppData.InsertLocation(tempLocation);
                                            CustomFragmentBase.NotifyChange();
                                            String uri = "smsto:" + tempLocation.mobile_number;
                                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
                                            Uri mapUri = Uri.parse("http://maps.google.com/maps?z=15&q=" + tempLocation.latitude + "," +
                                                    tempLocation.longitude);
                                            intent.putExtra("sms_body",
                                                    "HappyShare(https://goo.gl/hJ7ap4 ) got a new location for you: " + tempLocation.message +
                                                            " (" + mapUri + " )");
                                            intent.putExtra("compose_mode", true);
                                            startActivity(intent);
                                            setResult(Activity.RESULT_OK, resultIntent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    setResult(Activity.RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            }

                                    ).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

            } else {
                Toast.makeText(this, "Until you grant the permission, we can not display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
//        super.onBackPressed();
    }
}
