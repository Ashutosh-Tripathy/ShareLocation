package com.example.tripathy.sharelocation.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.RecentContact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by tripathy on 12/18/2015.
 */


/**
 * Created by tripathy on 12/13/2015.
 */
public class RecentContactArrayAdapter
        extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] myContact;
    final int REQUEST_LOCATION = 2;
    ArrayList<RecentContact> recentContacts = new ArrayList<RecentContact>();
    //    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MMM hh:mm:ss a");

//    displayDateFormat.setTimeZone(TimeZone.getDefault());

    public RecentContactArrayAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        ReadRecentContacts(context);
    }

    @Override
    public int getCount() {
        return recentContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return recentContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_recent_contact, null);
        }
        ((TextView) convertView.findViewById(R.id.lblRecentContactName)).setText(String.valueOf(
                recentContacts.get(position).name == null ? "" : recentContacts.get(position).name));
        ((TextView) convertView.findViewById(R.id.lblRecentContactNumber)).setText(String.valueOf(recentContacts.get(position).number));
        String callType = "";
        switch (recentContacts.get(position).type) {
            case "1":
                callType = "Received";
                break;
            case "2":
                callType = "Dialed";
                break;
            case "3":
                callType = "Missed";
                break;
        }
        ((TextView) convertView.findViewById(R.id.lblRecentContactType)).setText(callType);
        ((TextView) convertView.findViewById(R.id.lblRecentContactDate)).setText(String.valueOf(recentContacts.get(position).date));
//        ((TextView) convertView.findViewById(R.id.lblRecentContactDuration)).setText(String.valueOf(recentContacts.get(position).duration));
        return convertView;
    }

    public void ReadRecentContacts(Context context) //This Context parameter is nothing but your Activity class's Context
    {

        if (checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null,
                CallLog.Calls.DATE + " DESC");
        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        if (contactsCount > 0) {
            //limit number of recent contact to 40
            int counter = 0;
            while (cursor.moveToNext()) {
                try {
                    recentContacts.add(new RecentContact(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)),
                            cursor.getLong(cursor.getColumnIndex(CallLog.Calls.NUMBER)),
                            cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)),
                            displayDateFormat.format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))))),
                            cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))));
                    counter++;
                    if (counter >= 50)
                        break;
                } catch (Exception e) {
                    // Log.d("error",e.toString());
                }
            }
        }
        cursor.close();
    }

}


