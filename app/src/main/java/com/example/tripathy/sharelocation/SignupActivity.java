package com.example.tripathy.sharelocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tripathy.sharelocation.BaseClass.BaseFragmentActivity;
import com.example.tripathy.sharelocation.Enum.Enum;
import com.example.tripathy.sharelocation.lib.Authentication.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Random;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;

/**
 * Created by tripathy on 1/4/2016.
 */
public class SignupActivity extends BaseFragmentActivity {

    private static final String TAG = "SL: SignupAct";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket.on("signUpResponse", onSignUpResponse);
        setContentView(R.layout.activity_signup);
//        apiBaseURL = getString(R.string.api_Url);
        final EditText txtMobileNumber = (EditText) findViewById(R.id.txtMobileNumber);
        txtMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 10) {
                    if (!(s.toString().compareTo("7") > 0)) {
                        Toast toast = Toast.makeText(getBaseContext(),
                                "Please provide a valid mobile number (Do not add 0 in prefix).", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
//                    EditText txtMobileNumber = (EditText) findViewById(R.id.txtMobileNumber);
                    userMobileNumber = s.toString();
                    LogicToGenerateAndVerifyOTP();
//                    Toast toast = Toast.makeText(getBaseContext(),
//                            "Logic to get Otp.", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                }
            }
        });
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_SMS},
                    REQUEST_LOCATION);
            return;
        }
//        if (ContextCompat.checkSelfPermission(getBaseContext(),
//                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    this, new String[]{Manifest.permission.SEND_SMS},
//                    REQUEST_LOCATION);
//            return;
//        }
    }

    BroadcastReceiver receiver;
    private int code;
    private boolean isUserVerified = false;
    private static String userMobileNumber = "";

    private void LogicToGenerateAndVerifyOTP() {
        Random rNo = new Random();
        code = rNo.nextInt((99999 - 10000) + 1) + 10000;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Phone Number");
        builder.setMessage("Verification sms will be sent to the number " + userMobileNumber + ". You will be charged as per your sms plan.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //code to send sms here with the code value
                        sendSMS(getBaseContext(), userMobileNumber,
                                "Your verification code for HappyShare is: " + String.valueOf(code) + ".");
                        final ProgressDialog progressdialog = ProgressDialog.show(SignupActivity.this, "Waiting for SMS", "Please hold on");

                        final CountDownTimer timer = new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.v(TAG, "Ticking " + millisUntilFinished / 1000);
                                progressdialog.setMessage("Waiting for the message " + millisUntilFinished / 1000);
                            }

                            @Override
                            public void onFinish() {
                                unregisterReceiver(receiver);
                                progressdialog.dismiss();

                            }
                        }.start();

                        receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                Bundle bundle = intent.getExtras();
                                if (bundle != null) {
                                    if (readSMS(intent, code)) {
                                        Log.v(TAG, "SMS read");
                                        timer.cancel();
                                        progressdialog.dismiss();
                                        try {
                                            unregisterReceiver(receiver);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                        };
                        registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                    }
                }

        );
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    static int REQUEST_LOCATION = 2;

    public static void sendSMS(Context context, String incomingNumber, String sms) {
        SmsManager smsManager = SmsManager.getDefault();                                      //send sms
        smsManager.sendTextMessage(incomingNumber, null, sms, null,
                null);
        Log.v(TAG, "Sms to be sent is " + sms);
    }

    boolean readSMS(Intent intent, int code) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        currentMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0];
                    }
                    String msgOriginatingNumber = currentMessage.getDisplayOriginatingAddress();
                    Log.d(TAG, "msgOriginatingNumber: " + msgOriginatingNumber + " userMobileNumber: " + userMobileNumber);
                    String message = currentMessage.getDisplayMessageBody();
                    if (message.contains(String.valueOf(code)) && msgOriginatingNumber.contains(userMobileNumber)) {
                        Toast toast = Toast.makeText(getBaseContext(),
                                "Mobile number verified successfully.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        final EditText txtMobileNumber = (EditText) findViewById(R.id.txtMobileNumber);
                        txtMobileNumber.setEnabled(false);
                        isUserVerified = true;
                        Log.v(TAG, "Mobile number verified successfully!");
                        return true;
                    } else {
                        Toast toast = Toast.makeText(getBaseContext(),
                                "Wrong OTP.Please try again.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        isUserVerified = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.v(TAG, "Exception here " + e.toString());
            return false;
        }
        return false;
    }


    public void onActionClick(View view) {
        if (view == findViewById(R.id.btnVerify)) {
            EditText txtFirstName = (EditText) findViewById(R.id.txtFirstName);
            EditText txtLastName = (EditText) findViewById(R.id.txtLastName);
            final EditText txtMobileNumber = (EditText) findViewById(R.id.txtMobileNumber);
            EditText txtOtp = (EditText) findViewById(R.id.txtOtp);
            String firstName = txtFirstName.getText().toString();
            String lastName = txtLastName.getText().toString();
            String mobileNumber = txtMobileNumber.getText().toString();
            //String otp = txtOtp.getText().toString();
            String otp = "1234";
            JSONObject jsonObject;
            if (firstName.equals("") || lastName.equals("") || mobileNumber.equals("") || otp.equals("")) {
                Toast toast = Toast.makeText(getBaseContext(),
                        "All fields are mandatory.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
//            isUserVerified = true;
            if (!isUserVerified) {
                Toast toast = Toast.makeText(getBaseContext(),
                        "Please verify your mobile number.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            try {
                jsonObject = new JSONObject();
                jsonObject.put("first_name", firstName);
                jsonObject.put("last_name", lastName);
                jsonObject.put("mobile_number", mobileNumber);
                jsonObject.put("otp", otp);
                jsonObject.put("created_time", utcDateFormat.format(new Date(System.currentTimeMillis())));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "An error occureed.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
//                    final boolean[] isShareLocationSuccessfull = {false};
            Log.d(TAG, "Emitting signupUser.");
            showConnectionErrorMessage = true;
            mSocket.emit("signupUser", jsonObject);
        } else if (view == findViewById(R.id.btnCancelVerify)) {
            if (exit) {
                // finish activity
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            } else {
                Toast toast = Toast.makeText(this, "Press Cancel again to exit.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 5 * 1000);
            }
        }
    }

    private boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            // finish activity
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        } else {
            Toast toast = Toast.makeText(this, "Press Back again to exit.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 5 * 1000);

        }
    }

    //This method will be invoked by server on successful singup.
    public Emitter.Listener onSignUpResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            JSONArray jsonArray = (JSONArray) args[0];
            String response = "";
            int user_id = 0;
            String token = "";
            Intent resultIntent = new Intent();
            try {
                response = ((JSONArray) args[0]).getJSONObject(0).getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            Log.v(TAG, "In onSignUpResponse method.");
            Log.d(TAG, "Response code: " + response);
            if (Enum.SignUpResponse.wrongotp.toString().equals(response)) {
                SignupActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "OTP is wrong. Please try again.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            } else if (Enum.SignUpResponse.fail.toString().equals(response)) {
                SignupActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "An error occurred.Please try again.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            } else if (Enum.SignUpResponse.success.toString().equals(response)) {
                Log.v(TAG, "Successfully signed up.");
                try {
                    user_id = ((JSONArray) args[0]).getJSONObject(0).getInt("user_id");
                    token = ((JSONArray) args[0]).getJSONObject(0).getString("token");
                    Log.d(TAG, "Received data on successful signup(user_id:token: " + user_id + " : " + token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SignupActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "Welcome.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
                LoggedInUser.set(user_id, token, true);
//                LoggedInUser.setUserId(user_id);
//                LoggedInUser.isAuthenticated = true;
//                isMSocketInitialized = false;
//                isForceNew = true;
//                mSocket.close();
                try {
                    IO.Options opt = new IO.Options();
                    Log.d(TAG, "LoggedInUser.getUserId() : LoggedInUser.getToken() " +
                            String.valueOf(LoggedInUser.getUserId()) + " : " + LoggedInUser.getToken());
                    opt.query = "user_id=" + String.valueOf(LoggedInUser.getUserId()) +
                            "&hash_client_token=" + LoggedInUser.getToken();
                    opt.timeout = 4000;
                    opt.forceNew = true;
                    mSocket.disconnect();
//                    isForceNew = false;
                    mSocket = IO.socket(apiBaseURL, opt);
                    mSocket.on("checkNewReceivedMessageResponse", onCheckNewReceivedMessageResponse);
                    mSocket.on("connect_error", onConnectionError);
                    mSocket.on("connect_timeout", onConnectionTimeout);
                    mSocket.on("unauthorizedAccess", onUnauthorizedAccess);

                    //Invoke checkNewReceivedMessage for reveiver when sender send location successfully.
                    mSocket.on("invokeCheckNewReceivedMessage", onInvokeCheckNewReceivedMessage);
                    mSocket.connect();
                } catch (URISyntaxException e) {
                    Log.e(TAG, e.toString());
                }
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                SignupActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getBaseContext(), "An error occurred.Please try again.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            }
        }
    };
}

