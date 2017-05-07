package com.example.tripathy.sharelocation.BaseClass;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.tripathy.sharelocation.MainActivity;
import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Authentication.LoggedInUser;
import com.example.tripathy.sharelocation.lib.Dal.ManageAppData;
import com.example.tripathy.sharelocation.lib.Dal.MyLocation;
import com.example.tripathy.sharelocation.lib.Db.FeedReaderDbHelper;
import com.example.tripathy.sharelocation.lib.Db.MyLocationDbSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;

//import com.google.gson.JsonArray;

/**
 * Created by tripathy on 12/20/2015.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected String apiBaseURL = "";
    //    String apiBaseURL = "http://shl-www-app.azurewebsites.net:443/";
//    protected static com.github.nkzawa.socketio.client.Socket mSocket;
    protected static Socket mSocket;
    private static final String TAG = "SL: BaseFrgmntAct: ";
    protected static ManageAppData manageAppData;
    private MyLocationDbSet locationDbSet = new MyLocationDbSet(new FeedReaderDbHelper(this));
    public static boolean isMSocketInitialized = false;
    //Force socket to change after signup.
    public static boolean isForceNew = false;
    //Flag to show connection error message.
    public static boolean showConnectionErrorMessage = false;
    protected SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoggedInUser.InitializeLoggedInUser(getApplicationContext());
        if (manageAppData == null) manageAppData = new ManageAppData(locationDbSet);
        if (apiBaseURL.equals("")) apiBaseURL = getString(R.string.api_Url);
        Log.v(TAG, "BaseFragmentActivity onCreate.");

// else if (!isMSocketInitialized && !isAuthenticated) {
//            Log.v(TAG, "Initializing mSocket (not isAuthenticated).");
////            mSocket.on("receiveLocation", onCheckNewReceivedMessageResponse);
//            mSocket.on("connect_error", onConnectionError);
//            mSocket.on("connect_timeout", onConnectionTimeout);
//            //Invoke checkNewReceivedMessage for reveiver when sender send location successfully.
////            mSocket.on("invokeCheckNewReceivedMessage", onInvokeCheckNewReceivedMessage);
//            mSocket.connect();
//        }
        utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "BaseFragmentActivity onResume.Value of isMSocketInitialized: " + String.valueOf(isMSocketInitialized));
        if (!isMSocketInitialized) {
            isMSocketInitialized = true;
            try {
                IO.Options opt = new IO.Options();
                Log.d(TAG, "LoggedInUser.getUserId() : LoggedInUser.getToken() " +
                        String.valueOf(LoggedInUser.getUserId()) + " : " + LoggedInUser.getToken());
                opt.query = "user_id=" + String.valueOf(LoggedInUser.getUserId()) +
                        "&hash_client_token=" + LoggedInUser.getToken();
                opt.timeout = 4000;
                mSocket = IO.socket(apiBaseURL, opt);
            } catch (URISyntaxException e) {
                Log.e(TAG, e.toString());
            }
            Log.v(TAG, "Initializing mSocket.");
//            mSocket.emit("checkNewReceivedMessage");
            mSocket.on("checkNewReceivedMessageResponse", onCheckNewReceivedMessageResponse);
            mSocket.on("connect_error", onConnectionError);
            mSocket.on("connect_timeout", onConnectionTimeout);
            mSocket.on("unauthorizedAccess", onUnauthorizedAccess);

            //Invoke checkNewReceivedMessage for reveiver when sender send location successfully.
            mSocket.on("invokeCheckNewReceivedMessage", onInvokeCheckNewReceivedMessage);
            mSocket.connect();
            // apiBaseURL = getString(R.string.api_Url);
        }
//        else if (!mSocket.connected()) {
//            mSocket.connect();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mSocket.emit("");
    }


    public Emitter.Listener onCheckNewReceivedMessageResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            JSONArray response = (JSONArray) args[0];
            Log.v(TAG, "Calling onCheckNewReceivedMessageResponse.");
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject item = response.getJSONObject(i);
                    manageAppData.InsertLocation(new MyLocation(item.getInt("location_id"),
                            item.getInt("sender_id"),
                            LoggedInUser.getUserId(),
                            item.getString("message"), item.getString("created_time"),
                            item.getDouble("latitude"), item.getDouble("longitude"), item.getInt("status"),
                            item.getLong("mobile_number")));
                    BaseFragmentActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            CustomFragmentBase.NotifyChange();
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
//                        e.printStackTrace();
                }
            }
            if (response.length() > 0) {
                NotifyUser();
            }
        }
    };
    NotificationManager mNotificationManager;
    Integer notificationID = 1;
//    private static Integer totalMessages=0;

    private void NotifyUser() {
        Log.d(TAG, "Calling NotifyUser.");
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("HappyShare");
        nBuilder.setContentText("You have received new location.");
        nBuilder.setTicker("New Location.");
        nBuilder.setAutoCancel(true);
        nBuilder.setLights(Color.BLUE, 1000, 4000);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder.setSound(alarmSound);
        nBuilder.setPriority(1);
        nBuilder.setSmallIcon(android.R.drawable.ic_dialog_email);
//        nBuilder.setNumber(++totalMessages);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        nBuilder.setLargeIcon(bm);
        MainActivity.isAppOpeningThroughNotification = true;
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, nBuilder.build());
//        -See more at:http:
//www.theappguruz.com/blog/create-notification-alert-using-notificationmanager-android#sthash.NyTXrPbz.dpuf
    }

    public Emitter.Listener onInvokeCheckNewReceivedMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            Log.v(TAG, "Calling onInvokeCheckNewReceivedMessage");
            mSocket.emit("checkNewReceivedMessage");
        }
    };

    public Emitter.Listener onConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (showConnectionErrorMessage) {
                showConnectionErrorMessage = false;
                BaseFragmentActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(),
                                "An error occurred.Please check your internet connection.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            }
                Log.v(TAG, "Calling onConnectionError.");
        }
    };

    public Emitter.Listener onConnectionTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "Calling onConnectionTimeout.");
            if (showConnectionErrorMessage) {
                showConnectionErrorMessage = false;
                BaseFragmentActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "Please check your internet connection.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            }
        }
    };


    public Emitter.Listener onUnauthorizedAccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "Calling onUnauthorizedAccess.");
            if (showConnectionErrorMessage) {
                showConnectionErrorMessage = false;
                BaseFragmentActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "Unauthorized access.You may be logged in to some other device using same mobile " +
                                "number.Clear app data and log in again.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            }
        }
    };

    private String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return "";
    }
};

