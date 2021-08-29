package com.alrosyid.notula.activities.points;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.photos.EditPhotosActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.fragments.points.PointFragment;
import com.alrosyid.notula.models.Photos;
import com.alrosyid.notula.models.Points;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditPointsActivity extends AppCompatActivity {

    private Button btnSave;
    private SharedPreferences sharedPreferences;
    private TextInputLayout lytPoints;
    private TextInputEditText txtPoints;
    private ProgressDialog dialog;
    public static final Integer RecordAudioRequestCode = 1;
    public static final int RECOGNIZER_RESULT = 1;
    private ImageView speechButton;
    private int pointsId = 0, position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_points);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_point
        );

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytPoints = findViewById(R.id.tilPoints);
        txtPoints = findViewById(R.id.tiePoints);
        speechButton = findViewById(R.id.btnRecord);
        pointsId = getIntent().getIntExtra("pointsId", 0);
        position = getIntent().getIntExtra("position", 0);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        "id-ID");
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to text");
                startActivityForResult(speechIntent,RECOGNIZER_RESULT);
            }
        });
        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });

        getPoints();
    }

    private boolean validate() {
        if (txtPoints.getText().toString().isEmpty()) {
            lytPoints.setErrorEnabled(true);
            lytPoints.setError(getString(R.string.required));
            return false;
        }
        if (txtPoints.getText().toString().trim().length() > 1500) {
            lytPoints.setErrorEnabled(true);
            lytPoints.setError(getString(R.string.max_1500));
            return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtPoints.setText(txtPoints.getText().toString()+ " " + matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getPoints() {

        Integer id_points = getIntent().getIntExtra("pointsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_POINTS + (id_points), res -> {

            try {
                JSONObject object = new JSONObject(res);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("points"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendance = array.getJSONObject(i);

                        txtPoints.setText(attendance.getString("points"));


                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditPointsActivity.this);
        queue.add(request);
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_POINTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Points points = PointFragment.arrayList.get(position);

                    points.setPoints(txtPoints.getText().toString());

                    PointFragment.arrayList.set(position, points);
                    PointFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    PointFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                    finish();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {

            //add token to header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", pointsId + "");
//                map.put("notulas_id",notulasId+"");
                map.put("points", txtPoints.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditPointsActivity.this);
        queue.add(request);
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,R.string.permission_grandated,Toast.LENGTH_SHORT).show();
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