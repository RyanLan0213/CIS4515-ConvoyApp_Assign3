package edu.temple.convoy;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class fcm extends FirebaseMessagingService implements GoogleMapActivity.datareturn {

    static List<Vehicle> VehicleList = new ArrayList<>();
    Queue<Record> queue =  new ArrayDeque<Record>();


    public fcm() {

    }

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("I have been created","THE FCM");

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        String rawPayload = remoteMessage.getData().get("payload");
        if (rawPayload == null || rawPayload.equals("")) {
            Log.e("Received FCM MESSAGE WO Payload", "Received new FCM message but payload was empty.");
            return;
        }

        try {
            Log.i("Receive payload and meesage", "Received new FCM message with payload: " + rawPayload);
            JSONObject parsedPayload = new JSONObject(rawPayload);
            if(parsedPayload.getString("action").equals("UPDATE")) {

                JSONArray dataArray = parsedPayload.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    if (!dataObject.getString("username")
                            .equals(getusername())) {
                        // only forward locations for those travelers that AREN'T me
                        Vehicle Vehicle = new Vehicle(
                                dataObject.getString("username"),
                                dataObject.getString("firstname"),
                                dataObject.getString("lastname"),
                                dataObject.getDouble("latitude"),
                                dataObject.getDouble("longitude")
                        );
                        Log.d("Line:",Vehicle.toString());
                        VehicleList.add(Vehicle);

                    }
                }

                Intent intent = new Intent();
                intent.setAction("edu.temple.convoy.broadcast.location_update");
                intent.putExtra("convoy_locations", (Serializable) VehicleList);
                sendBroadcast(intent);

            }
            else if(parsedPayload.getString("action").equals("MESSAGE")) {
                    Log.d("The message_file URL is: ", parsedPayload.getString("message_file"));
                    if(!parsedPayload.getString("username").equals(getusername())) {
                        Record record = new Record(parsedPayload.getString("username"), parsedPayload.getString("message_file"), LocalDateTime.now());
                        Log.d("The record is: ", record.toString());
                        queue.add(record);
                    }

                Intent messageintent = new Intent();
                messageintent.setAction("edu.temple.convoy.broadcast.message_update");
                messageintent.putExtra("message", (Serializable) queue);
                sendBroadcast(messageintent);

            }

            // data = JSON array with format:
            // {"username":"sarah5",
            //      "firstname":"Sarah",
            //      "lastname":"Lehman",
            //      "latitude":"40.036",
            //      "longitude":"-75.2203"}

            // parse out list of locations for fellow convoy travelers

        } catch (JSONException e) {
            Log.e("Error" ,"Something went wrong while parsing the JSON payload: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("Token", s);
        myEdit.commit();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://kamorris.com/lab/convoy/account.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    if (jObject.getString("status").equals("SUCCESS")) {
                        Log.d("NEWTOKEN","SUCESS");

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error:", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> convoydata = new HashMap<>();
                convoydata.put("action", "UPDATE");
                convoydata.put("username", getusername());
                convoydata.put("session_key", getsessionkey());
                convoydata.put("fcm_token", s);


                return convoydata;
            }
        };
        returnqueue().add(stringRequest);


    }

    public interface getList{
        default List getlist() {
            return VehicleList;
        }
    }

}

