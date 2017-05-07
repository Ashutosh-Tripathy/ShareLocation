package com.example.tripathy.sharelocation.BaseClass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import com.example.tripathy.sharelocation.Adapter.MyLocationArrayAdapter;
import com.example.tripathy.sharelocation.R;
import com.example.tripathy.sharelocation.lib.Dal.ManageAppData;
import com.example.tripathy.sharelocation.lib.Dal.MyLocation;
import com.example.tripathy.sharelocation.lib.Db.FeedReaderDbHelper;
import com.example.tripathy.sharelocation.lib.Db.MyLocationDbSet;

/**
 * Created by tripathy on 12/3/2015.
 */
public class CustomFragmentBase extends Fragment {

    protected static ManageAppData manageAppData;
    private MyLocationDbSet locationDbSet;
    protected static MyLocationArrayAdapter mySharedLocationAdapter;
    protected static MyLocationArrayAdapter myReceivedLocationAdapter;
    //This flag will help us to know currnet fragment Shared/Received.
    protected static boolean isShareLocation = true;
    private static String TAG = "SL: CustFragBase";

    protected void InitializeManageAppData() {
        if (manageAppData == null) {
            locationDbSet = new MyLocationDbSet(new FeedReaderDbHelper(getContext()));
            manageAppData = new ManageAppData(locationDbSet);
        }
    }

    public void DeleteLocation(final MyLocation myLocation, final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete this location?")
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeleteLocationConfirmed(myLocation);
                        Toast toast = Toast.makeText(context,
                                "Deleted.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void DeleteLocationConfirmed(MyLocation myLocation) {
//        Log.d(TAG, "Deleting location.Id: " + myLocation.location_id + " time: " + myLocation.created_time);
        manageAppData.DeleteLocation(myLocation);
        NotifyChange();
    }

    public static void NotifyChange() {
        if (isShareLocation && mySharedLocationAdapter != null)
            mySharedLocationAdapter.NotifyDataSetChanged(manageAppData.getSharedLocation());
        else if (myReceivedLocationAdapter != null)
            myReceivedLocationAdapter.NotifyDataSetChanged(manageAppData.getReceivedLocation());
    }
}
