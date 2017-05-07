package com.example.tripathy.sharelocation.Enum;

/**
 * Created by tripathy on 10-Jan-16.
 */
public class Enum {
    public enum SignUpResponse {
        wrongotp,
        fail,
        success
    };

    public enum ShareLocationResponse {
        fail,
        success,
        //When receiver is not registered.
        notreg
    };

    public enum ShareLocationRequestCode {
        searched,
        sharedreceived
    };


    public enum CurrentFragment{
        homefragment,
        sharedfragment,
        receivedfragment;
    }
}
