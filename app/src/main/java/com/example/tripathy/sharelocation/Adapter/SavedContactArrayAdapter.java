package com.example.tripathy.sharelocation.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.SavedContact;

import java.util.ArrayList;

/**
 * Created by tripathy on 12/13/2015.
 */
public class SavedContactArrayAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] myContact;
    final int REQUEST_LOCATION = 2;
    private static ArrayList<SavedContact> savedContacts = new ArrayList<SavedContact>();

    public SavedContactArrayAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        ReadPhoneContacts(context);
    }

    @Override
    public int getCount() {
        return savedContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return savedContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_saved_contact, null);
        }

        ((TextView) convertView.findViewById(R.id.lblSavedContactName)).setText(String.valueOf(
                savedContacts.get(position).name == null ? "" : savedContacts.get(position).name));
        ((TextView) convertView.findViewById(R.id.lblSavedContactNumber)).setText(String.valueOf(savedContacts.get(position).number));

        return convertView;
    }

    public void ReadPhoneContacts(Context cntx) //This Context parameter is nothing but your Activity class's Context
    {
        if (savedContacts.size() > 0) return;
//        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
//                "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");
//        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
//        if (contactsCount > 0) {
//            while (cursor.moveToNext()) {
//                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    //the below cursor will give you details for multiple contacts
        Cursor pCursor = cntx.getContentResolver().query(Data.CONTENT_URI, null,
                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'",
                null, "UPPER(" + Phone.DISPLAY_NAME + ") ASC");

//                    cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            new String[]{id}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
        while (pCursor.moveToNext()) {
//                        int phoneType = pCursor.getInt(pCursor.getColumnIndex(Phone.TYPE));
            //String isStarred 		= pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
//                        String phoneNo = pCursor.getString(pCursor.getColumnIndex(Phone.NUMBER));
            savedContacts.add(new SavedContact(pCursor.getString(pCursor.getColumnIndex(Phone.
                    DISPLAY_NAME)),
                    Long.parseLong(pCursor.getString(pCursor.getColumnIndex(Phone.NUMBER)).replaceAll("\\D+", ""))));
            //you will get all phone numbers according to it's type as below switch case.
            //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
//                        switch (phoneType) {
//                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                                Log.e(contactName + ": TYPE_MOBILE", " " + phoneNo);
//                                break;
//                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                                Log.e(contactName + ": TYPE_HOME", " " + phoneNo);
//                                break;
//                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                                Log.e(contactName + ": TYPE_WORK", " " + phoneNo);
//                                break;
//                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
//                                Log.e(contactName + ": TYPE_WORK_MOBILE", " " + phoneNo);
//                                break;
//                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//                                Log.e(contactName + ": TYPE_OTHER", " " + phoneNo);
//                                break;
//                            default:
//                                break;
//                            break;
//                        }
        }
        pCursor.close();
    }
//            }
//            cursor.close();
//        }
//    }

}

