package com.example.euc;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Esp8266Client {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser user = mAuth.getCurrentUser();
    private static final String TAG = "Esp8266Client";
    private static int Pin;


    public static void turnOnGpioPin(String ipAddress, String userPath, int pin) {
        Pin = pin;
        String url = "http://" + ipAddress + "/gpio_on?userPath=" + userPath + "&pin=" + Pin;
        new HttpRequestTask().execute(url);
    }

    public static void turnOffGpioPin(String ipAddress, String userPath, int pin) {
        Pin = pin;
        String url = "http://" + ipAddress + "/gpio_off?userPath=" + userPath + "&pin=" + Pin;
        new HttpRequestTask().execute(url);
    }

    private static class HttpRequestTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String requestBody = "pin=" + Pin;
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "HTTP request successful: " + url);
                } else {
                    Log.d(TAG, "HTTP request failed: " + url);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException while sending HTTP request: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return null;
        }
    }
}
