package com.alrosyid.notula.activities.records;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.fragments.record.RecordFragment;
import com.alrosyid.notula.models.Photos;
import com.alrosyid.notula.models.Records;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddRecordsActivity extends AppCompatActivity {

    private Button btnUpload;
//    private ImageView imgPhotos;
    private TextInputLayout lytTitle, lytRecords;
    private TextInputEditText txtTitle, txtRecords;
    private Bitmap bitmap = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int meetingsId = 0;
    private ProgressDialog dialog;
    String selectedPath = "";
    private SharedPreferences preferences;
    public static final int ACTIVITY_RECORD_SOUND=1;
//    private ArrayList<Photos> listAll;

    Uri audioFileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_records);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_records);
        init();

    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnUpload = findViewById(R.id.btnUpload);
//        imgPhotos = findViewById(R.id.imgAddPhotos);
        lytTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        lytRecords = findViewById(R.id.tilRecordsName);
        txtRecords = findViewById(R.id.tieRecordsName);
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
        if (txtRecords.getText().toString().isEmpty()) {
            lytRecords.setErrorEnabled(true);
            lytRecords.setError(getString(R.string.required));
            return false;
        }
        return true;

    }

    private void create() {
        dialog.setMessage(getString(R.string.uploading));
        dialog.show();
        String titleText = txtTitle.getText().toString();
        String recordText = txtRecords.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_RECORDS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject postObject = object.getJSONObject("records");

                    Records records = new Records();
                    records.setId(postObject.getInt("id"));
                    records.setRecord(postObject.getString("record"));
                    records.setTitle(postObject.getString("title"));


                    RecordFragment.arrayList.add(0, records);
                    RecordFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    RecordFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                    RecordFragment.arrayList.addAll(0, (Collection<? extends Records>) postObject);
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
//                Records records = new Records();
//                RecordFragment.arrayList.add(0, records);
//                RecordFragment.recyclerView.getAdapter().notifyItemInserted(0);
//                RecordFragment.recyclerView.getAdapter().notifyDataSetChanged();
//                Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show();
////                RecordFragment.arrayList.addAll(0, (Collection<? extends Records>) records);
//                finish();



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
                map.put("record", bitmapToString(bitmap));
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(AddRecordsActivity.this);
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

    public void recording(View view) {
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.setType("image/*");
//        startActivityForResult(i, PICK_IMAGE_REQUEST);
//        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
////        intent.setType("audio/*");
//        startActivityForResult(intent);


            Intent intent = new Intent(
                    MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
//        } else if (view == playRecording) {
//
//            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioFileUri);
//            mediaPlayer.setOnCompletionListener(this);
//            mediaPlayer.start();
//            playRecording.setEnabled(false);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            String path=data.getDataString();
            String filePathColumn = MediaStore.Audio.Media.DATA;

            try {
                String filename=path.substring(path.lastIndexOf("/")+1);

                txtRecords.setText(filename);
//                bitmap = MediaStore.Audio.Media.getContentUri(path,);
                Log.d("debug" , "Record Path:" + imgUri);

                Toast.makeText(AddRecordsActivity.this,
                        "Saved: " + filename,
                        Toast.LENGTH_LONG).show();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
//                Uri uri = data.getData();
//                SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
//                draweeView.setImageURI(uri);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//   @Override
//   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == ACTIVITY_RECORD_SOUND) {
//            audioFileUri = data.getData();
//            mAudioURI = Uri.parse(String.valueOf(audioFileUri));
////            playRecording.setEnabled(true);
//        }
//    }

//    public void onCompletion(MediaPlayer mp) {
//        playRecording.setEnabled(true);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//
//            if (requestCode == ACTIVITY_RECORD_SOUND)
//            {
//                System.out.println("SELECT_AUDIO");
//                Uri selectedImageUri = data.getData();
//                selectedPath = getPath(selectedImageUri);
//                System.out.println("SELECT_AUDIO Path : " + selectedPath);
//
////                prgDialog.setMessage("Calling Upload");
////                prgDialog.show();
//            }
//
//        }
//    }




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
