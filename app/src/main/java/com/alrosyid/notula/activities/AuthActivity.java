package com.alrosyid.notula.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.fragments.auth.SignInFragment;


public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                boolean isLoggedIn = userPref.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
                }
            }
        }, 1500);

//        if(!isNetworkAvailable()==true)
//        {
//            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle(R.string.internet_alert)
//                    .setMessage(R.string.check_connection)
//                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            finish();
//                        }
//                    }).show();
//        }
//        else if(isNetworkAvailable()==true)
//        {
//
//        }
    }

//    public boolean isNetworkAvailable() {
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivityManager != null) {
//
//
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//                if (capabilities != null) {
//                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//
//                        return true;
//                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//
//                        return true;
//                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//
//                        return true;
//                    }
//                }
//            }
//        }
//
//        return false;
//
//    }
}