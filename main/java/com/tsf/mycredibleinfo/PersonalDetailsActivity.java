package com.tsf.mycredibleinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PersonalDetailsActivity extends AppCompatActivity {

    TextView nameText,locationText,numberText,skillText,linkText;
    SharedPreferences sharedPreferences;

    public void updateProfile(View view){
        Intent intent = new Intent(PersonalDetailsActivity.this,UpdateProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void getData(){

        String url = "http://139.59.65.145:9090/user/personaldetail/"
                + this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
//                try {
//                    Toast.makeText(getApplicationContext(),response.get("status_message").toString(),Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                try {
                    if(!response.getJSONObject("data").get("id").toString().isEmpty()) {
                        JSONObject jsonObject = new JSONObject(response.getJSONObject("data").toString());
                        nameText.setText(jsonObject.get("name").toString());
                        locationText.setText(jsonObject.get("location").toString());
                        skillText.setText(jsonObject.get("skills").toString());
                        numberText.setText(jsonObject.get("mobile_no").toString());
                        linkText.setText(jsonObject.get("links").toString());
                        sharedPreferences.edit().putBoolean("posted",true).apply();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No data, Please Update Profile",Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putBoolean("posted",false).apply();
            }
        });
        requestQueue.add(objectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        nameText = findViewById(R.id.nameTextView);
        locationText = findViewById(R.id.locationTextView);
        numberText = findViewById(R.id.numberTextView);
        linkText = findViewById(R.id.linkTextView);
        skillText = findViewById(R.id.skillTextView);
        sharedPreferences = this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE);

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE).edit().putBoolean("loggedIn",false).apply();
            Intent intent = new Intent(PersonalDetailsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.menuprofile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PersonalDetailsActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}