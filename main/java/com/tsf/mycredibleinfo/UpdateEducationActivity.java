package com.tsf.mycredibleinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateEducationActivity extends AppCompatActivity {

    EditText nameText,locationText,degreeText,endText,startText;
    Boolean posted;
    ProgressDialog progressDialog;

    public void updateProfile(View view){
        if(posted){
            updateData();
        }else{
            postData();
        }
    }

    private void postData(){

        String url = "http://139.59.65.145:9090/user/educationdetail/"
                + this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        if(nameText.getText().toString().isEmpty() || locationText.getText().toString().isEmpty()
                || degreeText.getText().toString().isEmpty() || endText.getText().toString().isEmpty()
                || startText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
        }
        else {
            params.put("organisation", nameText.getText().toString());
            params.put("location", locationText.getText().toString());
            params.put("start_year",startText.getText().toString());
            params.put("degree",degreeText.getText().toString());
            params.put("end_year",endText.getText().toString());

            progressDialog.setCancelable(false);
            progressDialog.setMessage("Updating");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(UpdateEducationActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    url,
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(UpdateEducationActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(UpdateEducationActivity.this,EducationDetailsActivity.class);
                            startActivity(intent);
                            finish();
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

    private void updateData(){

        Map<String, String> params = new HashMap<String, String>();

        String url = "http://139.59.65.145:9090/user/educationdetail/"
                + this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating");
        progressDialog.show();

        if(nameText.getText().toString().isEmpty() || locationText.getText().toString().isEmpty()
                || degreeText.getText().toString().isEmpty() || endText.getText().toString().isEmpty()
                || startText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
        }
        else {
            params.put("organisation", nameText.getText().toString());
            params.put("location", locationText.getText().toString());
            params.put("start_year",startText.getText().toString());
            params.put("degree",degreeText.getText().toString());
            params.put("end_year",endText.getText().toString());


            RequestQueue requestQueue = Volley.newRequestQueue(UpdateEducationActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(UpdateEducationActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(UpdateEducationActivity.this, EducationDetailsActivity.class);
                            startActivity(intent);
                            finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_education);

        nameText = findViewById(R.id.nameTextView);
        locationText = findViewById(R.id.locationTextView);
        degreeText = findViewById(R.id.degreeTextView);
        startText = findViewById(R.id.startTextView);
        endText = findViewById(R.id.endTextView);
        progressDialog = new ProgressDialog(this);

        posted = this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE).getBoolean("postedEducation",false);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UpdateEducationActivity.this,EducationDetailsActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
