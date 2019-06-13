package com.example.user.uberuber.Service;

import android.util.Log;


import com.example.user.uberuber.Common.Common;
import com.example.user.uberuber.Model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by User on 3/3/2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    //Press Ctrl+O


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("EDMTDEV", refreshedToken);
        updateTokenToServer(refreshedToken);
    }

    private void updateTokenToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tb1);

        Token token = new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) // if already login , must update Token
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .setValue(token);



    }
}
