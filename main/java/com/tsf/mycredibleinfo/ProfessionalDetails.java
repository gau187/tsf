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

public class ProfessionalDetails extends AppCompatActivity {

    TextView nameText,designationText,startText,endText;
    SharedPreferences sharedPreferences;

    public void updateData(View view){
        Intent intent = new Intent(ProfessionalDetails.this,UpdateProfessionalActivity.class);
        startActivity(intent);
        finish();
    }

    private void getData(){

        String url = "http://139.59.65.145:9090/user/professionaldetail/"
                + this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(!response.getJSONObject("data").get("id").toString().isEmpty()) {
                        JSONObject jsonObject = new JSONObject(response.getJSONObject("data").toString());
                        nameText.setText(jsonObject.get("organisation").toString());
                        designationText.setText(jsonObject.get("designation").toString());
                        startText.setText(jsonObject.get("start_date").toString());
                        endText.setText(jsonObject.get("end_date").toString());
                        sharedPreferences.edit().putBoolean("postedProfessional",true).apply();
                        sharedPreferences.edit().putInt("profId",Integer.parseInt(jsonObject.get("id").toString())).apply();
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
                sharedPreferences.edit().putBoolean("postedEducation",false).apply();
            }
        });
        requestQueue.add(objectRequest);
    }
    private void deleteData(){
        String url = "http://139.59.65.145:9090/user/professionaldetail/"
                + this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("profId",0);

        RequestQueue requestQueue = Volley.newRequestQueue(ProfessionalDetails.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(),response.get("status_message").toString(),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_details);

        nameText = findViewById(R.id.nameTextView);
        designationText = findViewById(R.id.designationTextView);
        endText = findViewById(R.id.endTextView);
        startText = findViewById(R.id.startTextView);
        sharedPreferences = this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE);

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            sharedPreferences.edit().putBoolean("loggedIn",false).apply();
            Intent intent = new Intent(ProfessionalDetails.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId() == R.id.delete){
            deleteData();
            Intent intent = new Intent(ProfessionalDetails.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfessionalDetails.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
