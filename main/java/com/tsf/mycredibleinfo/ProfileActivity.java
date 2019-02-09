package com.tsf.mycredibleinfo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button personal,proffesional,educational;
    ImageView profileImage;
    ProgressDialog progressDialog;

    public void getPhoto(){

        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,2);

    }

    public void imageUpdate(View view){
        getPhoto();
    }

    private void getImage(){

        String url = "http://139.59.65.145:9090/user/personaldetail/profilepic/" +
                this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        Glide.with(profileImage.getContext())
                .load(url)
                .apply(new RequestOptions().override(200, 200))
                .into(profileImage);
    }

    private void SendImage( final String image) {
        int uid = this.getSharedPreferences("com.tsf.mycredibleinfo",MODE_PRIVATE).getInt("id",0);

        String url = "http://139.59.65.145:9090/user/personaldetail/pp/post";
        String UID = String.valueOf(uid);
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        params.put("photo",image);
        params.put("uid",UID);

            progressDialog.setCancelable(false);
            progressDialog.setMessage("Updating");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    url,
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(ProfileActivity.this, "Done", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        personal = findViewById(R.id.personal);
        proffesional = findViewById(R.id.professional);
        educational = findViewById(R.id.educational);
        personal.setOnClickListener(this);
        proffesional.setOnClickListener(this);
        educational.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        profileImage = findViewById(R.id.profleImage);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this,new String[]  {Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }
        getImage();
    }

    @Override
    public void onClick(View v) {
        if(v==personal){
            Intent intent = new Intent(this,PersonalDetailsActivity.class);
            startActivity(intent);
            finish();
        }else if(v==educational){
            Intent intent = new Intent(this,EducationDetailsActivity.class);
            startActivity(intent);
            finish();
        }else if(v==proffesional){
            Intent intent = new Intent(this,ProfessionalDetails.class);
            startActivity(intent);
            finish();
        }
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
