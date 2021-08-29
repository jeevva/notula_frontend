package com.alrosyid.notula.activities.points;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.points.PointFragment;
import com.alrosyid.notula.models.Points;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPointsActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout lytPoints;
    private TextInputEditText txtPoints;
    private ProgressDialog dialog;
    public static ArrayList<Points> arrayList;
    private int notulasId = 0;
    public static final Integer RecordAudioRequestCode = 1;
    public static final int RECOGNIZER_RESULT = 1;
    private ImageView speechButton;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_point);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
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
        notulasId = getIntent().getIntExtra("notulasId", 0);
        speechButton = findViewById(R.id.btnRecord);
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
                create();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txtPoints.setText(txtPoints.getText().toString()+ " " + matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //validasi
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

    private void create() {
        dialog.setMessage(getString(R.string.save));
        dialog.show();
        arrayList = new ArrayList<>();
        Point strArray = new Point();
        List<Point> strList = Arrays.asList(strArray);
        String pointsText = txtPoints.getText().toString();
        Integer id_notulas = getIntent().getIntExtra("notulasId", 0);
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_POINTS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {


                    JSONObject pointsObject = object.getJSONObject("points");
                    Points points = new Points();
                    points.setId(pointsObject.getInt("id"));
                    points.setPoints(pointsObject.getString("points"));
//                    PointFragment.arrayList.addAll(0, (Collection<? extends Points>) pointsObject);
//                    PointFragment.arrayList.clear();
                    PointFragment.arrayList.add(0,points);

                    PointFragment.recyclerView.getAdapter().notifyItemInserted(0);
//
                    PointFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Toast.makeText(this, R.string.added_successfully, Toast.LENGTH_SHORT).show();
//                    strList.addAll(0, (Collection) pointsObject);



                    finish();



                }




            } catch (JSONException e) {
                e.printStackTrace();

            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {

            // add token to header


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");

                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);

                return map;
            }

            // add params
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("notulas_id", id_notulas + "");
                map.put("points", pointsText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddPointsActivity.this);
        queue.add(request);

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