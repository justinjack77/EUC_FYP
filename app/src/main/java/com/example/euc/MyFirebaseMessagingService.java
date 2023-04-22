package com.example.euc;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Context context;
    final private String SERVER_KEY="AAAAnea8dls:APA91bElszOsfGQ5QvxLK8VAnu7rhJ41mOfqgv_AJjJ5cZkpFFVdrJLoKXUrtKnX83mjQPuXQYSDVdu7ff3t1nko9sAqV546WJapg5tRKVC231i87LUSiVkX00etSJywLnqWnbP1Uh7U";
    public void sendNotification(String deviceToken, String message) {
        try {
            // Create the JSON payload for the notification
            JSONObject notification = new JSONObject();
            notification.put("title", "My App");
            notification.put("body", message);

            // Create the JSON payload for the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("to", deviceToken);
            requestBody.put("notification", notification);

            // Create the request
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Log.i("NotificationSender", "Notification sent successfully");
            }, error -> {
                Log.e("NotificationSender", "Error sending notification: " + error.toString());
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    // Set the authorization header with your FCM server key
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", SERVER_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    // Return the request body as bytes
                    return requestBody.toString().getBytes();
                }
            };

            // Add the request to the request queue
            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e("NotificationSender", "Error creating notification", e);
        }
    }


}
