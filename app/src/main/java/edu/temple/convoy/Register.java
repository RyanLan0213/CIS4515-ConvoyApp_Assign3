package edu.temple.convoy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Register extends MainActivity{

    EditText rusername;
    EditText lastname;
    EditText firstname;
    EditText rpassword;
    Button registerbutton;

    public Register(){
        super();
    }
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rusername = findViewById(R.id.Rusername);
        lastname = findViewById(R.id.lastname);
        firstname = findViewById(R.id.firstname);
        rpassword = findViewById(R.id.Rpassword);
        registerbutton = findViewById(R.id.registerb);

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rusername.getText().toString().trim()!=""&&
                        lastname.getText().toString().trim()!=""&&
                        firstname.getText().toString().trim()!=""&&
                        rpassword.getText().toString().trim()!=""){
                    Intent mIntent = new Intent();
                    mIntent.putExtra("username", rusername.getText().toString().trim());
                    mIntent.putExtra("firstname", firstname.getText().toString().trim());
                    mIntent.putExtra("lastname", lastname.getText().toString().trim());
                    mIntent.putExtra("rpassword", rpassword.getText().toString().trim());

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
                            logindata.put("username", rusername.getText().toString().trim());
                            logindata.put("firstname", firstname.getText().toString().trim());
                            logindata.put("lastname", lastname.getText().toString().trim());
                            logindata.put("password", rpassword.getText().toString().trim());
                            // logindata.put("firstname",)

                            return logindata;
                        }
                    };
                    queue.add(stringRequest);
                    setResult(RESULT_OK, mIntent);
                    finish();



                }
                else{
                   // Toast.LENGTH_SHORT(Short,)
                }




            }
        });


    }

}