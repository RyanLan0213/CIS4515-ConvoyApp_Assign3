package edu.temple.convoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GoogleMapActivity extends MainActivity implements OnMapReadyCallback{
    SupportMapFragment smf;
    FusedLocationProviderClient client;
    Button logoutbutton, createbutton, joinbutton, leavebutton, confirmbutton;
    public String CONVOYURL = "https://kamorris.com/lab/convoy/convoy.php";
    public String accountURL = "https://kamorris.com/lab/convoy/account.php";
    public static String username;
    public static String token;
    static String sessionkey;
    static String convoyid = "";
    String status;
    TextView convoytextview;
    public String logoutURL = "https://kamorris.com/lab/convoy/account.php";
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    private AlertDialog.Builder dialogbuilder;
    private AlertDialog alertDialog;
    private EditText convoyidedit;
    SharedPreferences preferences;
    private BroadcastReceiver locationReceiver;
    private List<Marker> fellowTravelerMarkers;
    Button recordbutton;
    GoogleMap mMap;
    List<Vehicle> Vehicle = new ArrayList<>();

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }




    public interface datareturn{
        default String getconvoyid(){
            return convoyid;
        }
     default String getusername(){
        return username;
    }
     default String getsessionkey(){
         return sessionkey;
     }
     default String getTOKEN(){
         return token;
     }
     default RequestQueue returnqueue(){
         return queue;
     }

}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        convoytextview = findViewById(R.id.convoytextview);


        preferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        String tokenname = preferences.getString("Token","");
        Log.d("The tokenname is ",tokenname);


        Bundle b = new Bundle();
        b = getIntent().getExtras();
        username = b.getString("username");
        sessionkey = b.getString("sessionkey");
        Log.d("thee key is ", sessionkey);
        uploadTokenIfNotAlreadyRegistered();
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        joinbutton = findViewById(R.id.joinbutton);
        leavebutton = findViewById(R.id.leavebutton);
        recordbutton= findViewById(R.id.recordbutton);

        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinconvoy();
                getCurrentlocation();

            }
        });
        leavebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveconvoy();
            }
        });
        recordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordingActivity = new Intent(GoogleMapActivity.this,RecordingAcitivty.class);
               // Bundle bundle = new Bundle();
                //bundle.putString("lastname",jObject.getString("lastname"));
               // bundle.putString("sessionkey",key);
               // bundle.putString("username",usernamestring);
              //  mapactivity.putExtras(bundle);

                startActivity(recordingActivity);
            }
        });
        logoutbutton = findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                finish();
            }
        });
        createbutton = findViewById(R.id.createbutton);
        createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(GoogleMapActivity.this,createbutton.getText(),Toast.LENGTH_SHORT);
                Log.d("123123", createbutton.getText().toString());
                if (createbutton.getText().toString().equals("Create Convoy")) {
                    startconvoy();
                    getCurrentlocation();

                } else {
                    endconvoy();

                }
            }
        });




    }

    private void uploadTokenIfNotAlreadyRegistered() {
         SharedPreferences.Editor myEdit = preferences.edit();
         SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
         if (sh.getString("Token", "") != null) {
             String tokenInSharedPref = sh.getString("Token", "");
            Log.d("Preference got the token and it is: ",tokenInSharedPref);
            Volley.newRequestQueue(GoogleMapActivity.this).add(new StringRequest(Request.Method.POST, accountURL, response -> {
                // If success, use SharedPreferences to guard against multiple attempts to register with the server
            }, Throwable::printStackTrace) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "UPDATE");
                    map.put("username", username);
                    map.put("session_key", sessionkey);
                    map.put("fcm_token", preferences.getString("Token", "")); // s is your token
                    return map;
                }
            });


             } else {
        Log.d("Preference does not get the token","Preference does not get the token");
        FirebaseMessaging.getInstance()
                .getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                 myEdit.putString("Token",s);
                 myEdit.commit();
                Volley.newRequestQueue(GoogleMapActivity.this).add(new StringRequest(Request.Method.POST, accountURL, response -> {
                    // If success, use SharedPreferences to guard against multiple attempts to register with the server
                }, Throwable::printStackTrace) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("action", "UPDATE");
                        map.put("username", username);
                        map.put("session_key", sessionkey);
                        map.put("fcm_token", s); // s is your token
                        return map;
                    }
                });
            }
        });


         }
    }
    private void leaveconvoy(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    Log.d("convoy id for leaving is ",convoyid);
                    // Log.d("Creation Status is ", response.toString());
                    if (jObject.getString("status").equals("SUCCESS")) {


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
                convoydata.put("action", "LEAVE");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                convoydata.put("convoy_id", convoyid);

                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);

    }


    private void joinconvoy(){
        dialogbuilder = new AlertDialog.Builder((this));
        final View popupview = getLayoutInflater().inflate(R.layout.pop,null);
        convoyidedit = (EditText) popupview.findViewById(R.id.popupconvoyu);
        confirmbutton =(Button)popupview.findViewById(R.id.comfirmbutton);
        dialogbuilder.setView(popupview);
        alertDialog = dialogbuilder.create();
        alertDialog.show();
        confirmbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(convoyidedit.getText().toString()!=null){
                    convoyid = convoyidedit.getText().toString();
                    alertDialog.dismiss();
                }
            }
        });





        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
            try {
                JSONObject jObject = new JSONObject(response);
                 Log.d("Successfully join ", response.toString());
                if (jObject.getString("status").equals("SUCCESS")) {
                   // FirebaseMessaging.getInstance().subscribeToTopic(convoyid);


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
            convoydata.put("action", "JOIN");
            convoydata.put("username", username);
            convoydata.put("session_key", sessionkey);
            convoydata.put("convoy_id", convoyid);
            // logindata.put("firstname",)

            return convoydata;
        }
    };
        queue.add(stringRequest);

    }
    private void logout() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, logoutURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GoogleMapActivity.this, response, Toast.LENGTH_SHORT).show();


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
                convoydata.put("action", "LOGOUT");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);

    }

    private void endconvoy() {
        //Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    // Log.d("Creation Status is ", response.toString());
                    if (jObject.getString("status").equals("SUCCESS")) {
                        opendialog("End");


                        createbutton.setText("Create Convoy");
                        convoytextview.setText("The Convoy has been Ended");


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
                convoydata.put("action", "END");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                convoydata.put("convoy_id", convoyid);
                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);

    }

    private void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("edu.temple.convoy.broadcast.location_update");
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("OutsideScopeReceived","Receivedoutsidescope");
                if(intent.hasExtra("convoy_locations")) {
                    Log.d("GetListInvoked","I am invoked");
                    Vehicle = (List) intent.getSerializableExtra("convoy_locations");
                    Log.d("ThesizeoftheVehicle",String.valueOf(Vehicle.size()));
                }for(Vehicle vehicle : Vehicle){
                    Log.d("LoggingforVehicleInformation",vehicle.toString());
                }
            }
        };
        this.registerReceiver(locationReceiver, filter);
    }
    private void startconvoy() {

        //Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    Log.d("Creation Status is ", response.toString());
                    if (jObject.getString("status").equals("SUCCESS")) {
                        convoyid = jObject.getString("convoy_id");
                        createbutton.setText("End Convoy");
                        String message = "The convoy id is: " + convoyid;
                        String title = "Convoy Created";
                        opendialog("Start");
                        convoytextview.setText("The Convoy ID is : " + convoyid);


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
                convoydata.put("action", "CREATE");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);
    }

    public void opendialog(String status) {
        DialogFra dialog = new DialogFra(convoyid,status);
        dialog.show(getSupportFragmentManager(), "Convoy");

    }

    private void getCurrentlocation() {
        if(convoyid!=null){
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Log.d("in","in");
            //Task<Location> task = client.getLastLocation();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(1);
            mLocationRequest.setFastestInterval(1);
            mLocationRequest.setSmallestDisplacement(10);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("latlng", latlng.toString());
                            updatelocation(latlng);


                            MarkerOptions options = new MarkerOptions().position(latlng).title(username);

                            smf.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    googleMap.clear();
                                    for(int i = 0;i<Vehicle.size();i++) {
                                        Log.d("Printing the information", Vehicle.get(i).toString());
                                        MarkerOptions vehiclesoption = new MarkerOptions().position(Vehicle.get(i).getLocation()).title(Vehicle.get(i).getUsername());
                                        googleMap.addMarker(vehiclesoption);
                                      /*  Marker newMarker = googleMap.addMarker(new MarkerOptions()
                                                .position(vehicle.getLocation())
                                                .title(vehicle.getUsername()));
                                        fellowTravelerMarkers.add(newMarker);

                                       */
                                    }
                                    // googleMap.clear();
                                    googleMap.addMarker(options);
                                    updatelocation(latlng);
                                }
                            });
                        }
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Log.d("location",location.toString());
                    if (location != null) {
                        smf.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                Log.d("latlng", latlng.toString());
                                MarkerOptions options = new MarkerOptions().position(latlng).title("I am here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                                Log.d("I", "I am invoked");
                                googleMap.addMarker(options);
                            }

                        });
                    }
                }
            });

        } else {
            ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);

        }




           /* Task<Location> task = client.getLastLocation();
            Log.d("Task",task.toString());
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                   // Log.d("invoooo","ooooo");
                    Log.d("location",location.toString());
                    if(location!=null){
                        smf.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                                Log.d("latlng",latlng.toString());
                                MarkerOptions options = new MarkerOptions().position(latlng).title("I am here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,10));
                                Log.d("I","I am invoked");
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            });

        }
        else{
            ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 44);

        }

            */

    }
    private void updatelocation(LatLng latlng) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    Log.d("Updating", response.toString());
                    if (jObject.getString("status").equals("SUCCESS")) {
                        Log.d("Status of update = ",jObject.getString("status"));


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
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                convoydata.put("convoy_id",convoyid);
                convoydata.put("latitude",String.valueOf(latlng.latitude));
                convoydata.put("longitude",String.valueOf(latlng.longitude));

                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);
    }
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getCurrentlocation();
            }
        }
    }

     */

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();


    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(locationReceiver);
    }
/*
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper());
        }

    }

 */

   /* @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    */


}