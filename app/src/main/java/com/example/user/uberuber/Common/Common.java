package com.example.user.uberuber.Common;

import android.location.Location;

import com.example.user.uberuber.Model.User;
import com.example.user.uberuber.Remote.FCMClient;
import com.example.user.uberuber.Remote.IFCMService;
import com.example.user.uberuber.Remote.IGoogleAPI;
import com.example.user.uberuber.Remote.RetrofitClient;

/**
 * Created by User on 15/1/2018.
 */

public class Common {



    public static final String driver_tb1 = "Drivers";
    public static final String user_driver_tb1 = "DriversInformation";
    public static final String user_rider_tb1 = "RidersInformation";
    public static final String pickup_request_tb1 = "PickupRequest";
    public static final String token_tb1 = "Tokens";


    public static User currentUser;


    public static Location mLastLocation = null;




    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";


    public static double base_fare = 2.55; // these are the charges being used
    private static double time_rate = 0.35;
    private static double distance_rate = 1.75;

    public static double formulaPrice(double km,double min)
    {
        return base_fare+(distance_rate*km)+(time_rate*min);
    }


    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }


    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }



}
