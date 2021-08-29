package com.alrosyid.notula.activities.notulas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.meetings.AddMeetingsHomeActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notulas.NotulasListFragment;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNotulasActivity extends AppCompatActivity {

    private Button btnSave;
    private TextInputLayout lytTitle, lytSummary;
    private TextInputEditText txtTitle, txtSummary;
    private ProgressDialog dialog;
    private int meetingsId = 0;
    public static final Integer RecordAudioRequestCode = 1;
    public static final int RECOGNIZER_RESULT = 1;
    private ImageView speechButton;
    private SharedPreferences sharedPreferences;


    public static ArrayList<Meetings> arrayList;

    //TextViews to display details
    private TextView txtTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notulas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_notula);

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


        //Initializing Spinner
//        spinner = (Spinner) findViewById(R.id.spinner);
        lytTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        lytSummary = findViewById(R.id.tilSummary);
        txtSummary = findViewById(R.id.tieSummary);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                create();
            }
        });

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


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txtSummary.setText(txtSummary.getText().toString()+ " " + matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            lytTitle.setErrorEnabled(true);
            lytTitle.setError(getString(R.string.required));
            return false;
        }
        if (txtSummary.getText().toString().isEmpty()) {
            lytSummary.setErrorEnabled(true);
            lytSummary.setError(getString(R.string.required));
            return false;
        }

        return true;
    }

    private void create() {
        dialog.setMessage(getString(R.string.save_load));
        dialog.show();
        String titleText = txtTitle.getText().toString();
        String summaryText = txtSummary.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_NOTULA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject notulaObject = object.getJSONObject("notulas");

                    Notula notula = new Notula();
                    notula.setId(notulaObject.getInt("id"));
                    notula.setTitle(notulaObject.getString("summary"));
                    notula.setTitle(notulaObject.getString("title"));

                    NotulasListFragment.arrayList.add(0, notula);
                    NotulasListFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    NotulasListFragment.recyclerView.getAdapter().notifyDataSetChanged();
//                    Intent restart = new Intent(AddNotulasActivity.this, MainActivity.class);
//                    startActivity(restart);

                    Toast.makeText(this, R.string.added_successfully, Toast.LENGTH_SHORT).show();
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
                map.put("meetings_id", meetingsId + "");
                map.put("summary", summaryText);
                map.put("title", titleText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddNotulasActivity.this);
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
