package com.example.user.uberuber

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.user.uberuber.Commons.Commons
import com.example.user.uberuber.Models.MyPlaces
import com.example.user.uberuber.Remo.IGoogleAPIService
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response





class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null

    //Location
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000

    }

    lateinit var mService:IGoogleAPIService

    internal lateinit var currentPlace:MyPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Init Service
        mService = Commons.googleApiService


        //Request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermission()) {
                buildLocationRequest();
                buildLocationCallBack();

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
        else
        {buildLocationRequest();
            buildLocationCallBack();

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        bottom_navigation_view.setOnNavigationItemSelectedListener { item->
            when(item.itemId)
            {
                R.id.action_hospital -> nearByPlace("hospital")
                R.id.action_restaurant -> nearByPlace("restaurant")


            }
            true

        }

    }

    private fun nearByPlace(typePlace: String) {
        //Clear all markers on Map
        mMap.clear()
        //build URL request base on location
        val url = getUrl(latitude,longitude,typePlace)
        mService.getNearbyPlaces(url)
                .enqueue(object : Callback<MyPlaces> {
                    override fun onResponse(call: Call<MyPlaces>?, response: Response<MyPlaces>?) {
                        currentPlace = response!!.body()!!

                        if (response.isSuccessful)
                        {

                            for (i in 0 until response.body()!!.results!!.size)
                            {
                                val markerOptions= MarkerOptions()
                                val googlePlace = response.body()!!.results!![i]
                                val lat = googlePlace.geometry!!.location!!.lat
                                val lng = googlePlace.geometry!!.location!!.lng
                                val placeName = googlePlace.name
                                val latLng = LatLng(lat,lng)

                                markerOptions.position(latLng)
                                markerOptions.title(placeName)

                                if (typePlace.equals("hospitals"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital))
                                else if (typePlace.equals("restaurant"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant))
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital))

                                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))


                                markerOptions.snippet(i.toString())  //Assign Index for Market
                                mMap.addMarker(markerOptions)





                            }

                            //Move Camera
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude,longitude)))
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

                        }
                    }

                    override fun onFailure(call: Call<MyPlaces>?, t: Throwable?) {
                        Toast.makeText(baseContext,""+t!!.message, Toast.LENGTH_SHORT).show()
                    }

                })


    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {
        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=10000") //10kilometres
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&key=AIzaSyChfMci_YZ3JmKn124amPUFZKbUUTBgruE")


        Log.d("URL_DEBUG",googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0.locations.size - 1)   //get last location


                if (mMarker != null) {
                    mMarker!!.remove()
                }


                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude


                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title("Your Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                //!! for mMap
                mMarker = mMap.addMarker(markerOptions)


                //Move Camera
                //!! for mMap
                //!! for mMap
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

            }
        }

    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f

    }


    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            else

                ActivityCompat.requestPermissions(this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            return false

        } else
            return true
    }


    //Overide OnRequestPermissionResult
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission()) {
                            buildLocationRequest();
                            buildLocationCallBack();

                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                            mMap.isMyLocationEnabled = true
                        }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onStop() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //!! for mMap
                mMap.isMyLocationEnabled = true

            }

        }
        else
        //!! for mMap
            mMap.isMyLocationEnabled = true

        //Enable Zoom Control
        mMap.uiSettings.isZoomControlsEnabled=true

    }
}