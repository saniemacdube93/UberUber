package com.example.user.uberuber.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by User on 15/1/2018.
 */

public interface IGoogleAPI {

    @GET
    Call<String> getPath(@Url String url);

}
