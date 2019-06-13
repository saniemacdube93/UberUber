package com.example.user.uberuber;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.uberuber.Common.Common;
import com.example.user.uberuber.Model.FCMResponse;
import com.example.user.uberuber.Model.Notification;
import com.example.user.uberuber.Model.Sender;
import com.example.user.uberuber.Model.Token;
import com.example.user.uberuber.Remote.IFCMService;
import com.example.user.uberuber.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustommerCall extends AppCompatActivity {

    TextView txtTime ,txtAddress , txtDistance;
    Button btnCancel , btnAcept;
    MediaPlayer mediaPlayer;

    IGoogleAPI mService;
    IFCMService mFCMService;




    String customerId;

    double lat , lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);


        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();

        //InitView
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtTime = (TextView) findViewById(R.id.txtTime);

        btnAcept = (Button)findViewById(R.id.btnAccept);
        btnCancel = (Button)findViewById(R.id.btnDecline);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(customerId))
                    cancelBooking(customerId);
            }
        });


        btnAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustommerCall.this,DriverTracking.class);

                //Here we use putExtra to send customer location to the tracking activity affter accepting the request
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("customerId", customerId);



                startActivity(intent); //hence we open the DriverTracking Activity
                finish();//then we close the current Custommer Call Activity because we do not need it
            }
        });




    mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
    mediaPlayer.setLooping(true);
    mediaPlayer.start();

    if (getIntent() != null)
    {
         lat = getIntent().getDoubleExtra("lat" , -1.0 );
         lng = getIntent().getDoubleExtra("lng" , -1.0 );
        customerId = getIntent().getStringExtra("customer");

        getDirection(lat,lng);


    }
    }

    private void cancelBooking(String customerId) {
            Token token = new Token(customerId);

        Notification notification = new Notification("Cancel", "Driver has cancelled request");//instead of Cancel there was Notice
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1)
                        {
                            Toast.makeText(CustommerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });

    }

    private void getDirection(double lat , double lng) {

        String requestApi = null;

        try {

            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+ Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("EDMTDEV" , requestApi);//Print URL FOR DEBUG

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                             try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");


                            //after get routes just get first element of routes
                                JSONObject object = routes.getJSONObject(0);


                              //after getting first element we need an array with the name "legs"
                                JSONArray  legs = object.getJSONArray("legs");


                                //and get first element of legs array
                                JSONObject legsObject = legs.getJSONObject(0);

                                //getting distance
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));


                                //get Time
                                JSONObject time =  legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));


                                //get address
                                String address =  legsObject.getString("end_address");
                                txtAddress.setText(address);














                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustommerCall.this , ""+t.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }


    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }


    @Override
    protected void onResume(){
        super.onResume();
        mediaPlayer.start();
    }

}
