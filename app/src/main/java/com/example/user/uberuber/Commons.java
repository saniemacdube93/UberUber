package com.example.user.uberuber;

import com.example.user.uberuber.Remotes.IGoogleAPIService;
import com.example.user.uberuber.Remotes.RetrofitClient;

/**
 * Created by User on 7/4/2018.
 */

public class Commons {

    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPISerice()
    {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);

    }


}
