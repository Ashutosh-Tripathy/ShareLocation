package com.example.tripathy.sharelocation.lib;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.example.tripathy.sharelocation.R;

/**
 * Created by tripathy on 28-Jan-16.
 */
public class ChangeButtonColor {

    public static void Change(Context context, Button pressedButton, Button[] buttons) {
        for (Button button:
             buttons) {
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.normalButtonBackground));
        }
        pressedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.pressedButtonBackground));
    }
}
