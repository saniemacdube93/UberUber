package com.example.user.uberuber.Remo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by User on 17/4/2018.
 */
object RetrofitClient {
    private var retrofit: Retrofit?=null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null)
        {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

        }
        return retrofit!!
    }
}