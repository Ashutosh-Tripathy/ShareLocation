package com.example.tripathy.sharelocation.lib.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by tripathy on 11/26/2015.
 */

//This class store logged in user information.
public class LoggedInUser {
    private static LoggedInUser loggedInUser;

    private static Integer userId;

    private static String token;
    static SharedPreferences preferences;
    private static String TAG = "SL: LoggedInUser";
    private static boolean isAuthenticated = false;

    //    public static final String TOKEN = "TOKEN";
    private LoggedInUser(Context ctx) {
        preferences =
                PreferenceManager.getDefaultSharedPreferences(ctx);
        if (preferences.getString("userId", null) == null) {
            userId = null;
        } else {
            userId = Integer.valueOf(preferences.getString("userId", null));
        }
        if (preferences.getString("is_authenticated", null) == null) {
            isAuthenticated = false;
        } else {
            isAuthenticated = Boolean.valueOf(preferences.getString("is_authenticated", null));
        }
        token = preferences.getString("token", null);
        Log.d(TAG, "Value of userId : Token " + userId + " : " + token);
    }

    public static int getUserId() {
//        if (userId == null)
//            throw new UnsupportedOperationException("userId is null.");
        return userId == null ? 0 : userId;
    }

    public static String getToken() {
//        if (token == null)
//            throw new UnsupportedOperationException("token is null.");
        return token;
    }

//    public static LoggedInUser getLoggedInUser() {
//        return loggedInUser;
//    }


    public static boolean getIsAuthenticated() {
//        if (token == null)
//            throw new UnsupportedOperationException("token is null.");
        return isAuthenticated;
    }

    public static void InitializeLoggedInUser(Context context) {
        new LoggedInUser(context);
    }

    public static void set(int userId, String token, boolean isAuthenticated) {
        loggedInUser.userId = userId;
        loggedInUser.token = token;

        loggedInUser.isAuthenticated = isAuthenticated;
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("userId", String.valueOf(userId));
        edit.putString("token", token);
        edit.putString("is_authenticated", String.valueOf(isAuthenticated));
        edit.commit();
        Log.v(TAG, "Saved userId,token,is_authenticated " +String.valueOf(userId)+
                " : "+token +" : "
                +isAuthenticated);
    }
}
