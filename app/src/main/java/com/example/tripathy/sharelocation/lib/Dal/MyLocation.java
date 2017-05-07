
package com.example.tripathy.sharelocation.lib.Dal;

/**
 * Created by tripathy on 11/24/2015.
 */
public class MyLocation {
    public Integer location_id;
    public Integer sender_id;
    public Integer receiver_id;
    public String message;
    public String created_time;
    public double latitude;
    public double longitude;
    public int status;
    public long mobile_number;

    public MyLocation(Integer location_id, Integer sender_id, Integer receiver_id, String message, String created_time,
                      double latitude, double longitude,  int status,long mobile_number) {
        this.location_id = location_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
        this.created_time = created_time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.mobile_number = mobile_number;
    }
}
