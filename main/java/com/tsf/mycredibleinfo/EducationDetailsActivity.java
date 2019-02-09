package com.tsf.mycredibleinfo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EducationDetailsActivity extends AppCompatActivity {

    TextView nameText,locationText,degreeText,startText,endText;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    public void getPhoto(){

        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,2);

    }

    public void updateData(View view){
        Intent intent = new Intent(EducationDetailsActivity.this,UpdateEducationActivity.class);
        startActivity(intent);
        finish();
    }

    private void SendImage( final String image) {
        int uid = this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        String url = "http://139.59.65.145:9090/user/educationdetail/certificate/post";
        String UID = String.valueOf(uid);
        Map<String,String> params = new HashMap<String,String>();
        params.put("Content-Type", "application/json");
        params.put("photo",image);
        params.put("uid",UID);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(EducationDetailsActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(EducationDetailsActivity.this, "Done", Toast.LENGTH_SHORT).show();
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

    private void getData(){

        String url = "http://139.59.65.145:9090/user/educationdetail/"
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
                        locationText.setText(jsonObject.get("location").toString());
                        degreeText.setText(jsonObject.get("degree").toString());
                        startText.setText(jsonObject.get("start_year").toString());
                        endText.setText(jsonObject.get("end_year").toString());
                        sharedPreferences.edit().putBoolean("postedEducation",true).apply();
                        sharedPreferences.edit().putInt("educationId",Integer.parseInt(jsonObject.get("id").toString())).apply();
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
        String url = "http://139.59.65.145:9090/user/educationdetail/"
                + this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("educationId",0);

        RequestQueue requestQueue = Volley.newRequestQueue(EducationDetailsActivity.this);
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
        setContentView(R.layout.activity_education_details);

        nameText = findViewById(R.id.nameTextView);
        locationText = findViewById(R.id.locationTextView);
        degreeText = findViewById(R.id.degreeTextView);
        endText = findViewById(R.id.endTextView);
        startText = findViewById(R.id.startTextView);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this,new String[]  {Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            this.getSharedPreferences("com.tsf.mycredibleinfo",Context.MODE_PRIVATE).edit().putBoolean("loggedIn",false).apply();
            Intent intent = new Intent(EducationDetailsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId() == R.id.delete){
            deleteData();
            Intent intent = new Intent(EducationDetailsActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId() == R.id.certificate){
            getPhoto();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.menueducation,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EducationDetailsActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode ==1){

            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode ==2 && resultCode == RESULT_OK && data!=null){

            try {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    Bitmap lastBitmap = null;
                    lastBitmap = bitmap;
                    //encoding image to string
                    String image = getStringImage(lastBitmap);
                    Log.d("image",image);
                    //passing the image to volley
                    SendImage(image);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
}
