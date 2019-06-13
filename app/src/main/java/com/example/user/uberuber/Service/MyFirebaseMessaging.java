package com.example.user.uberuber.Service;

import android.content.Intent;
import android.util.Log;

import com.example.user.uberuber.CustommerCall;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by User on 3/3/2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("EDMTDEV" ,remoteMessage.getNotification().getBody());

    //The LatLong cordinates will then come from the rider app
    LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);

    Intent intent = new Intent (getBaseContext() , CustommerCall.class);//getBaseContext()
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// DELETE THIS PERHAPS WHEN YOUR PHONE RUNS ANDROID 7 ...LOL
    intent.putExtra("lat" , customer_location.latitude);
    intent.putExtra("lng" , customer_location.longitude );
    intent.putExtra("customer" , remoteMessage.getNotification().getTitle());

    getBaseContext().startActivity(intent);




    }
}
