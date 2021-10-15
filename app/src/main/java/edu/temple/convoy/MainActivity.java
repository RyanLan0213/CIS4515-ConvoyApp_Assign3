package edu.temple.convoy;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public String usernamestring;
    private String passwordstring;
    private EditText username;
    private EditText password;
    private TextView Information;
    private Button loginButton;
    private Button registerButton;
    String key;
    String[] RegisterArray = new String[4];
    static final int check =1;
    static RequestQueue queue;



    public String URL = "https://kamorris.com/lab/convoy/account.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.UsernameTV);
        password = findViewById(R.id.PasswordTV);
        Information = findViewById(R.id.informationTV);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        queue= Volley.newRequestQueue(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("involke","involke");
                login(view,queue);
            }
        });
       registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivityForResult(intent,check);

               // register(view,queue);

            }
        });


        //usernamestring = passwordstring = "";

        Log.d("START","START");







    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == check && data !=null) {
                RegisterArray[0] = data.getStringExtra("username");
                RegisterArray[1] = data.getStringExtra("firstname");
                RegisterArray[2] = data.getStringExtra("lastname");
                RegisterArray[3] = data.getStringExtra("password");
                Log.d("Action done",RegisterArray[0]);

            }
        }

    }

 */



    /*public void register(View view, RequestQueue queue){

            Log.d("Register()","Invoked");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("String Response:", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error:", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> logindata = new HashMap<>();
                    logindata.put("action", "REGISTER");
                    logindata.put("username", RegisterArray[0]);
                    logindata.put("firstname", RegisterArray[1]);
                    logindata.put("lastname", RegisterArray[2]);
                    logindata.put("password", RegisterArray[3]);
                   // logindata.put("firstname",)

                    return logindata;
                }
            };
            queue.add(stringRequest);
        }

     */




    public void login(View view, RequestQueue queue){
        usernamestring = username.getText().toString().trim();
        passwordstring = password.getText().toString().trim();
        Log.d("username",usernamestring.toString());
        Log.d("password",passwordstring.toString());
        String result;

        if(!usernamestring.equals("")&&!passwordstring.equals("")){
            //Log.d("this part","part invokled");


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("String Response:", response);
                    try {
                        JSONObject jObject = new JSONObject(response);
                       // Log.d("json created","!");
                        //Log.d("Status is ",jObject.getString("status"));
                        //Log.d("session key is ",jObject.getString("session_key"));
                        if(jObject.getString("status").equals("SUCCESS")){
                            Log.d("status verified","!");
                            key = jObject.getString("session_key");
                            Log.d("Session key is: ",key);
                            Intent mapactivity = new Intent(MainActivity.this,GoogleMapActivity.class);
                            Bundle bundle = new Bundle();
                            //bundle.putString("lastname",jObject.getString("lastname"));
                            bundle.putString("sessionkey",key);
                            bundle.putString("username",usernamestring);
                            mapactivity.putExtras(bundle);

                            startActivity(mapactivity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error:",error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> logindata = new HashMap<>();
                    logindata.put("action","LOGIN");
                    logindata.put("username",usernamestring.toString());
                    logindata.put("password",passwordstring.toString());

                    return logindata;
                }


            };









/*

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("response: ", response.toString());


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error:",error.toString());

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> logindata = new HashMap<>();
                    logindata.put("action","REGISTER");
                    logindata.put("username","usernamestring");
                    logindata.put("password","passwordstring");

                    return logindata;



                }
            };

*/
            queue.add(stringRequest);





    }
}
    public String returnkey(){
        return key;
    }


}
