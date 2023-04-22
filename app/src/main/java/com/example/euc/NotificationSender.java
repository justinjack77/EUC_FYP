package com.example.euc;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSender {
    private Context context;
    public void sendNotification(String deviceToken, String message) {
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", "My App");
            notificationBody.put("message", message);
            notification.put("to", deviceToken);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e("TAG", "Error creating notification", e);
        }
        String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("TAG", "Notification sent successfully");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Notification send failed", error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=YOUR_FCM_SERVER_KEY");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
