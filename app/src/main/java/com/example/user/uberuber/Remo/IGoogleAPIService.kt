package com.example.user.uberuber.Remo

import com.example.user.uberuber.Models.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by User on 17/4/2018.
 */
interface IGoogleAPIService {
    @GET
    fun getNearbyPlaces(@Url url:String): Call<MyPlaces>
}