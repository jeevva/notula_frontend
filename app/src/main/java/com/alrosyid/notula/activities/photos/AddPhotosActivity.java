package com.alrosyid.notula.activities.photos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.models.Photos;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddPhotosActivity extends AppCompatActivity {

    private Button btnUpload;
//    private ImageView imgPhotos;
    private TextInputLayout lytTitle, lytPhotos;
    private TextInputEditText txtTitle, txtPhotos;
    private Bitmap bitmap = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int meetingsId = 0;
    private ProgressDialog dialog;
    private SharedPreferences preferences;
//    private ArrayList<Photos> listAll;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_add_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_photo);
        init();

    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnUpload = findViewById(R.id.btnUpload);
//        imgPhotos = findViewById(R.id.imgAddPhotos);
        lytTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        lytPhotos = findViewById(R.id.tilPhotosName);
        txtPhotos = findViewById(R.id.tiePhotosName);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);




        btnUpload.setOnClickListener(v -> {
            if (validate()) {
                create();
            }

        });

    }

    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            lytTitle.setErrorEnabled(true);
            lytTitle.setError(getString(R.string.required));
            return false;
        }
        if (txtPhotos.getText().toString().isEmpty()) {
            lytPhotos.setErrorEnabled(true);
            lytPhotos.setError(getString(R.string.required));
            return false;
        }
        return true;

    }

    private void create() {
        dialog.setMessage(getString(R.string.uploading));
        dialog.show();
        String titleText = txtTitle.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_PHOTOS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject postObject = object.getJSONObject("photos");

                    Photos photos = new Photos();
                    photos.setId(postObject.getInt("id"));
                    photos.setPhoto(postObject.getString("photo"));
                    photos.setTitle(postObject.getString("title"));


                    PhotosListFragment.arrayList.add(0, photos);
                    PhotosListFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    PhotosListFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                    PhotosListFragment.arrayList.addAll(0, (Collection<? extends Photos>) postObject);
                    finish();



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();


        }, error -> {
//            error.printStackTrace();
//            dialog.dismiss();
            if (error instanceof ServerError) {
                dialog.dismiss();
//                ArrayList<Photos> items = new ArrayList<>();
                Photos photos = new Photos();
                PhotosListFragment.arrayList.add(0, photos);
                PhotosListFragment.recyclerView.getAdapter().notifyItemInserted(0);
                PhotosListFragment.recyclerView.getAdapter().notifyDataSetChanged();
                Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                PhotosListFragment.arrayList.addAll(0, (Collection<? extends Photos>) photos);
                finish();

            }

        }) {

            // add token to header


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            // add params

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("meetings_id", meetingsId + "");
                map.put("title", titleText);
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(AddPhotosActivity.this);
        queue.add(request);


    }



    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);


        }


        return "";
    }


    public void cancelPost(View view) {
        super.onBackPressed();
    }

    public void changePhoto(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
//        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//        startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            String path=data.getDataString();

            try {
                String filename=path.substring(path.lastIndexOf("%2F")+3);

                txtPhotos.setText(filename);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                Uri uri = data.getData();
                SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
                draweeView.setImageURI(uri);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
