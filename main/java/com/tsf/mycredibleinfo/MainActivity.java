package com.tsf.mycredibleinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String res;
    EditText emailText,passwordText;
    TextView loginSignupSwitch;
    Button signup;
    boolean mode = true;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    private void getData(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://139.59.65.145:9090/user/educationdetail/31",
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(objectRequest);
    }

    private void postData(boolean modeSignup){


//        {
//            "email":"gau187",139.59.65.145:9090/user/login
//                "password":"gau187"
//        }

        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        if(emailText.getText().toString().isEmpty() || passwordText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Both fields are required",Toast.LENGTH_SHORT).show();
        }
        else {
            params.put("email", emailText.getText().toString());
            params.put("password", passwordText.getText().toString());

            if(modeSignup) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Signing Up");
                progressDialog.show();
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        "http://139.59.65.145:9090/user/signup",
                        new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), "Signed Up", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
                requestQueue.add(jsonObjectRequest);

            }else{
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Logging In");
                progressDialog.show();
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        "http://139.59.65.145:9090/user/login",
                        new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(!response.get("status_message").toString().isEmpty()){
                                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if(!response.getJSONObject("data").get("id").toString().isEmpty()) {
                                        JSONObject jsonObject = new JSONObject(response.getJSONObject("data").toString());
//                                        Toast.makeText(getApplicationContext(), jsonObject.get("email").toString(), Toast.LENGTH_SHORT).show();
                                        sharedPreferences.edit().putString("email",jsonObject.get("email").toString()).apply();
                                        sharedPreferences.edit().putBoolean("loggedIn",true).apply();
                                        sharedPreferences.edit().putInt("id",Integer.parseInt(jsonObject.get("id").toString())).apply();
                                        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }

        }

    }
    private void deleteData(){
        String url = "http://139.59.65.145:9090/user/educationdetail/58";

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }
    private void updateData(){
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        params.put("skils","pls");
        params.put("mobile_no","97373");
        params.put("name","Gaurav");
        params.put("links","www.mmmm.com");
        params.put("location","kolkata");
        params.put("email","hiww@die.com");

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                "http://139.59.65.145:9090/user/personaldetail/239",
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                    }
                },new Response.ErrorListener(){ @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
        }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginSignupSwitch = findViewById(R.id.loginSwitch);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.password);
        signup = findViewById(R.id.signUp);
        signup.setOnClickListener(this);
        loginSignupSwitch.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("loggedIn",false)){
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }
//        getData();
//        postData();
//        deleteData();
//        updateData();
    }

    @Override
    public void onClick(View v) {
        if(v==signup){
            if(mode) {
                postData(true);
            }else{
                postData(false);
            }
        }else if(v == loginSignupSwitch){
            if(mode){
                signup.setText("Login");
                loginSignupSwitch.setText("Sign Up");
                mode=false;
            }else{
                signup.setText("Sign Up");
                loginSignupSwitch.setText("Login");
                mode=true;
            }
        }
    }
}