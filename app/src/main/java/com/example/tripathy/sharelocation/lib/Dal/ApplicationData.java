package com.example.tripathy.sharelocation.lib.Dal;

import java.util.LinkedList;

/**
 * Created by tripathy on 11/25/2015.
 */

//This call is responsible to have copy of data in db, we will use this data on shared and received page.
public class ApplicationData {
    LinkedList<MyLocation> sharedLocation;
    LinkedList<MyLocation> receivedLocation;
    private static ApplicationData applicationData;

    private ApplicationData() {
        sharedLocation = new LinkedList<MyLocation>();
        receivedLocation = new LinkedList<MyLocation>();
    }

    static ApplicationData getData() {
        if (applicationData == null) applicationData = new ApplicationData();
        return applicationData;
    }
}
