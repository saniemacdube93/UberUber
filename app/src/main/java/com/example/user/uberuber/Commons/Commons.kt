package com.example.user.uberuber.Commons

import com.example.user.uberuber.Remo.IGoogleAPIService
import com.example.user.uberuber.Remo.RetrofitClient

/**
 * Created by User on 17/4/2018.
 */
object Commons {
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"

    val googleApiService:IGoogleAPIService
        get()= RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

}