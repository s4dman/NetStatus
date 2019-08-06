package com.ex.connectionstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "ConnectionStatus";

    private Handler mHandler;
    private TextView mNoNetwork, mNoNetworkText, mConnectionType, mConnectionStatus, mWating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        init();

        if (isNetworkAvailable()) {
            requestData();
        } else {
            mNoNetwork.setVisibility(View.VISIBLE);
            mNoNetworkText.setVisibility(View.VISIBLE);
            mNoNetwork.setText("Wifi/Mobile Data Not Enabled");
            mNoNetworkText.setText("Please enable Wifi/Mobile data and restart the app");
        }

    }

    private void requestData() {

        mWating.setVisibility(View.VISIBLE);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.get("https://jsonplaceholder.typicode.com/posts/1")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                mWating.setVisibility(View.GONE);
                                mConnectionStatus.setVisibility(View.VISIBLE);
                                mConnectionStatus.setText("Connected to Internet");
                            }

                            @Override
                            public void onError(ANError anError) {
                                if (anError.getErrorCode() == 0) {
                                    mWating.setVisibility(View.GONE);
                                    mConnectionStatus.setVisibility(View.VISIBLE);
                                    mConnectionStatus.setText("Connection Error");
                                } else {
                                    mConnectionStatus.setText(anError.getErrorDetail());

                                }
                            }
                        });
            }
        }, 3000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            mConnectionType.setVisibility(View.VISIBLE);

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                mConnectionType.setText("Connection type: Wifi");
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                mConnectionType.setText("Connected type: Mobile data");
            } else {
                mConnectionType.setText("Connected type: Unknown Network");
            }
            return true;
        } else {
            return false;
        }

    }

    private void init() {
        mHandler = new Handler();
        mConnectionType = findViewById(R.id.text_connection_type);
        mConnectionStatus = findViewById(R.id.text_connection_status);
        mNoNetwork = findViewById(R.id.no_network);
        mNoNetworkText = findViewById(R.id.text_no_network);
        mWating = findViewById(R.id.text_connecting);

    }


}