package com.ex.connectionstatus;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    public static String TAG = "ConnectionStatus";

    private TextView mNetworkType, mNetworkHint, mConnectionType, mConnectionStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (isNetworkAvailable() == true) {
            new SendRequest().execute();
            mNetworkType.setVisibility(View.INVISIBLE);
        } else {
            mNetworkType.setText("Wifi/Mobile Data Not Enabled");
            mNetworkHint.setText("Please enable Wifi/Mobile data and restart the app");
        }

    }



    public void init(){

        mNetworkType = (TextView) findViewById(R.id.text_network_type);
        mNetworkHint = (TextView) findViewById(R.id.text_network_hint);
        mConnectionType = (TextView) findViewById(R.id.text_connection_type);
        mConnectionStatus = (TextView) findViewById(R.id.text_connection_status);

    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                mConnectionType.setText("Connection type: Wifi");
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                mConnectionType.setText("Connected type: Mobile data");
            } else {
                mConnectionType.setText("Connected type: Unknown Network");
            }
            return true;
        } else {
            return false;
        }
    }



    private void notificationManager(int icon, String title, String text, int id) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        builder.setSmallIcon(icon);
        builder.setContentTitle(title);
        builder.setContentText(text);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());

    }



    public class SendRequest extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mConnectionStatus.setText("Checking internet connection, Please wait...");

        }


        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL("https://jsonplaceholder.typicode.com/posts/1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();

                String responseCode = String.valueOf(connection.getResponseCode());

                if (responseCode.equals("200")) {
                    return "HTTP_OK";
                } else return "HTTP_FAILED";

            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (s == "HTTP_OK") {

                notificationManager(R.drawable.notification_connected, "Connected", "You're now connected to internet", 0);

                mConnectionStatus.setText("Connected");

            } else {

                notificationManager(R.drawable.notification_disconnected, "Disconnected", "Internet connection unavailable", 1);

                mConnectionStatus.setText("Disonnected");

            }


        }
    }

}