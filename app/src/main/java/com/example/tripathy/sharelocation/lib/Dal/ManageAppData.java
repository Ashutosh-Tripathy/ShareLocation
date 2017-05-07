package com.example.tripathy.sharelocation.lib.Dal;

import android.util.Log;

import com.example.tripathy.sharelocation.lib.Authentication.LoggedInUser;
import com.example.tripathy.sharelocation.lib.Db.MyLocationDbSet;
import com.google.gson.Gson;

import java.util.LinkedList;

/**
 * Created by tripathy on 11/25/2015.
 */

//This class is responsible for initializing Applcation Data  and doing db operation.
public class ManageAppData {
    private ApplicationData applicationData;
    private static final String TAG = "SL: ManageAppData: ";
    public MyLocationDbSet locationDbSet;

    public ManageAppData(MyLocationDbSet locationDbSet) {
        this.locationDbSet = locationDbSet;
        applicationData = ApplicationData.getData();
        if (applicationData.sharedLocation.isEmpty() && applicationData.receivedLocation.isEmpty()) {
            InitializeApplicationData(locationDbSet.Read());
        }
    }

    //It will initialize Application data from database.
    private void InitializeApplicationData(LinkedList<MyLocation> lstMyLocation) {
        for (MyLocation item : lstMyLocation) {
            if (item.sender_id == LoggedInUser.getUserId())
                applicationData.sharedLocation.addLast(item);
            else
                applicationData.receivedLocation.addLast(item);
        }
    }

    public void InsertLocation(MyLocation myLocation) {
        if (myLocation.sender_id == LoggedInUser.getUserId())
            InsertSharedLocation(myLocation);
        else
            InsertReceivedLocation(myLocation);

        try {
            locationDbSet.Insert(myLocation);
            Log.e(TAG, new Gson().toJson(myLocation));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            throw e;
        }
    }

    private void InsertSharedLocation(MyLocation myLocation) {
        applicationData.sharedLocation.addFirst(myLocation);
    }

    private void InsertReceivedLocation(MyLocation myLocation) {
        applicationData.receivedLocation.addFirst(myLocation);
    }

    public int GetLastReceivedMessageId() {
        return applicationData.receivedLocation.getFirst().location_id;
    }

    public void DeleteLocation(MyLocation myLocation) {
        if (myLocation.sender_id == LoggedInUser.getUserId())
            DeleteSharedLocation(myLocation);
        else
            DeleteReceivedLocation(myLocation);
        Log.d(TAG, "Delete location: " + new Gson().toJson(myLocation));
        locationDbSet.Delete(myLocation);
    }

    private void DeleteSharedLocation(MyLocation myLocation) {
        Log.d(TAG, "Deleting location from shared location.");
        if (myLocation.location_id > 0) {
            for (MyLocation item :
                    applicationData.sharedLocation) {
                if (item.location_id == myLocation.location_id) {
                    applicationData.sharedLocation.remove(item);
                    break;
                }
            }
        } else {
            for (MyLocation item :
                    applicationData.sharedLocation) {
                if (item.created_time == myLocation.created_time) {
                    applicationData.sharedLocation.remove(item);
                    break;
                }
            }
        }
    }

    private void DeleteReceivedLocation(MyLocation myLocation) {
        Log.d(TAG, "Deleting location from received location.");
        if (myLocation.location_id > 0) {
            for (MyLocation item :
                    applicationData.receivedLocation) {
                if (item.location_id == myLocation.location_id) {
                    applicationData.receivedLocation.remove(item);
                    break;
                }
            }
        } else {
            for (MyLocation item :
                    applicationData.sharedLocation) {
                if (item.created_time == myLocation.created_time) {
                    applicationData.sharedLocation.remove(item);
                    break;
                }
            }
        }
    }

    public LinkedList<MyLocation> getSharedLocation() {
        return applicationData.sharedLocation;
    }

    public LinkedList<MyLocation> getReceivedLocation() {
        return applicationData.receivedLocation;
    }

}
