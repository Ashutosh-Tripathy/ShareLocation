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
import com.example.tripathy.sharelocation.lib.Dal.SavedContact;
import com.example.tripathy.sharelocation.lib.LocationHelper;
import com.example.tripathy.sharelocation.Adapter.SavedContactArrayAdapter;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by tripathy on 12/8/2015.
 */
public class SavedContactFragment extends CustomFragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InitializeManageAppData();
        LocationHelper.selectedNumber = 0;
        View view = inflater.inflate(R.layout.fragment_saved_contact, container, false);
        final ListView listSavedContact = (ListView) view.findViewById(R.id.listSavedContact);
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
            SavedContactArrayAdapter contactAdapter = new SavedContactArrayAdapter(getContext());
            listSavedContact.setAdapter(contactAdapter);
            listSavedContact.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listSavedContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocationHelper.selectedNumber = ((SavedContact) listSavedContact.getItemAtPosition(position)).number;
                    Log.e("Recent Contact:", "" + LocationHelper.selectedNumber);
                }
            });
        } else
            Toast.makeText(getContext(), "Plese provice read contact permission.", Toast.LENGTH_LONG).show();
        return view;
    }
}