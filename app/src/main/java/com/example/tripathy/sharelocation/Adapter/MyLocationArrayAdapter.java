package com.example.tripathy.sharelocation.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tripathy.sharelocation.BaseClass.CustomFragmentBase;
import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.MyLocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimeZone;

/**
 * Created by tripathy on 12/2/2015.
 */
public class MyLocationArrayAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<MyLocation> lnkLstMyLocation = new ArrayList<MyLocation>();
    private MyLocation myLocation;
    private static String TAG = "SL: MyLAdptr";
    private static HashMap<String, String> hashNumberName = new HashMap<String, String>();
    private static CustomFragmentBase customFragmentBase = new CustomFragmentBase();
    private static Context context;
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MMM hh:mm a");
    private static SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public MyLocationArrayAdapter(Context ctx, ArrayList<MyLocation> objects) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.lnkLstMyLocation.clear();
        this.lnkLstMyLocation.addAll(objects);
        utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public int getCount() {
        return lnkLstMyLocation.size();
    }

    @Override
    public MyLocation getItem(int position) {
        return lnkLstMyLocation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_layout, null);
        }
        myLocation = lnkLstMyLocation.get(position);

        (convertView.findViewById(R.id.btnDelete)).setOnClickListener(
                new View.OnClickListener() {
                    MyLocation temp = new MyLocation(myLocation.location_id,
                            myLocation.sender_id, myLocation.receiver_id, myLocation.message, myLocation.created_time,
                            myLocation.latitude, myLocation.longitude, myLocation.status, myLocation.mobile_number);

                    @Override
                    public void onClick(View v) {
                        customFragmentBase.DeleteLocation(temp, context);
                    }

                }
        );
        ((TextView) convertView.findViewById(R.id.lblLocationId)).setText(String.valueOf(myLocation.location_id));
        ((TextView) convertView.findViewById(R.id.lblSenderId)).setText(String.valueOf(myLocation.sender_id));
        ((TextView) convertView.findViewById(R.id.lblReceiverId)).setText(String.valueOf(myLocation.receiver_id));
        try {
            ((TextView) convertView.findViewById(R.id.lblCreatedTime)).
                    setText(String.valueOf(displayDateFormat.format(utcDateFormat.parse(myLocation.created_time))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ((TextView) convertView.findViewById(R.id.lblMessage)).setText
                (String.valueOf(myLocation.message));
        ((TextView) convertView.findViewById(R.id.lblLatitude)).setText
                (String.valueOf(myLocation.latitude));
        ((TextView) convertView.findViewById(R.id.lblLongitude)).setText(String.valueOf(myLocation.longitude));
        ((TextView) convertView.findViewById(R.id.lblStatus)).setText(String.valueOf(myLocation.status));
        String contactName;
        if (hashNumberName.containsKey(myLocation.mobile_number)) {
            contactName = hashNumberName.get(myLocation.mobile_number);
        } else {
            contactName = getContactName(context, String.valueOf(myLocation.mobile_number));
        }
        if (contactName == null || contactName.equals("Not in contact")) {
            ((TextView) convertView.findViewById(R.id.lblMobileNumber)).setText
                    (String.valueOf(myLocation.mobile_number));
        } else {
            ((TextView) convertView.findViewById(R.id.lblMobileNumber)).setText
                    (contactName);
        }
        if (!hashNumberName.containsKey(myLocation.mobile_number)) {
            hashNumberName.put(String.valueOf(myLocation.mobile_number), contactName == null ? "Not in contact" : contactName);
        }


        return convertView;
    }

    public void NotifyDataSetChanged(LinkedList<MyLocation> objects) {
        this.lnkLstMyLocation.clear();
        this.lnkLstMyLocation.addAll(objects);
        this.notifyDataSetChanged();
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
