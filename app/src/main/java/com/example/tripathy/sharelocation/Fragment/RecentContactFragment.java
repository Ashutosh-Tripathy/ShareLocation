package com.example.tripathy.sharelocation.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tripathy.sharelocation.BaseClass.CustomFragmentBase;
import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.RecentContact;
import com.example.tripathy.sharelocation.lib.LocationHelper;
import com.example.tripathy.sharelocation.Adapter.RecentContactArrayAdapter;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by tripathy on 12/8/2015.
 */
public class RecentContactFragment extends CustomFragmentBase {

    ListView listRecentContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InitializeManageAppData();
        View view = inflater.inflate(R.layout.fragment_recent_contact, container, false);
        LocationHelper.selectedNumber = 0;
        listRecentContact = (ListView) view.findViewById(R.id.listRecentContact);

//        listRecentContact.OnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick() {
//
//            }
//        });

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
            RecentContactArrayAdapter contactAdapter = new RecentContactArrayAdapter(getContext());
            listRecentContact.setAdapter(contactAdapter);
            listRecentContact.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listRecentContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocationHelper.selectedNumber = ((RecentContact) listRecentContact.getItemAtPosition(position)).number;
                    Log.e("Recent Contact:", "" + LocationHelper.selectedNumber);
                }
            });
        } else
            Toast.makeText(getContext(), "Plese provice read contact permission.", Toast.LENGTH_LONG).show();
        return view;
    }
}