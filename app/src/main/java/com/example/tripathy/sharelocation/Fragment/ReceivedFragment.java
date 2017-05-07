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

import com.example.tripathy.sharelocation.BaseClass.CustomFragmentBase;
import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.MyLocation;
import com.example.tripathy.sharelocation.lib.LocationHelper;
import com.example.tripathy.sharelocation.Adapter.MyLocationArrayAdapter;

import java.util.ArrayList;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by tripathy on 12/3/2015.
 */
public class ReceivedFragment extends CustomFragmentBase {

    private View view;
    private static String TAG = "SL: SharedFragment";
    ListView listReceivedLocation;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isShareLocation = false;
        InitializeManageAppData();
        LocationHelper.shareSharedReceivedLocation = null;
        view = inflater.inflate(R.layout.fragment_received, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            return view;
        }
        listReceivedLocation = (ListView) view.findViewById(R.id.listReceivedLocation);
        myReceivedLocationAdapter = new MyLocationArrayAdapter(getContext(),
                new ArrayList<>(manageAppData.getReceivedLocation()));
        listReceivedLocation.setAdapter(myReceivedLocationAdapter);
        listReceivedLocation.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listReceivedLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationHelper.shareSharedReceivedLocation = ((MyLocation) listReceivedLocation.getItemAtPosition(position));
                Log.v(TAG, "shareSharedReceivedLocation: " + LocationHelper.shareSharedReceivedLocation.message);
            }
        });
        return view;
    }
}