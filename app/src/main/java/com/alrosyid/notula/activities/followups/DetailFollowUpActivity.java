package com.alrosyid.notula.activities.followups;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DetailFollowUpActivity extends AppCompatActivity {

    private TextInputLayout lytTitle, lytPic, lytDuedate, lytDetail;
    private TextInputEditText txtTitle, txtPic, txtDuedate, txtDetail;
    private ProgressDialog dialog;
    private int notulasId = 0, followUpId = 0;
    private SharedPreferences sharedPreferences;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_follow_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.detail_follow_up
        );
        init();

    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        lytTitle = findViewById(R.id.tilTitle);
        lytPic = findViewById(R.id.tilPic);
        lytDuedate = findViewById(R.id.tilDueDate);
        lytDetail = findViewById(R.id.tilDetail);
        txtTitle = findViewById(R.id.tieTitle);
        txtPic = findViewById(R.id.tiePic);
        txtDuedate = findViewById(R.id.tieDueDate);
        txtDetail = findViewById(R.id.tieDetail);
        notulasId = getIntent().getIntExtra("notulasId", 0);
        followUpId = getIntent().getIntExtra("followUpId", 0);


        getFollowUp();


    }


    private void getFollowUp() {

        Integer id_followUp = getIntent().getIntExtra("followUpId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_FOLLOW_UP + (id_followUp), res -> {

            try {
                JSONObject object = new JSONObject(res);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("followup"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject followUp = array.getJSONObject(i);

                        txtTitle.setText(followUp.getString("title"));
                        txtPic.setText(followUp.getString("pic"));
                        txtDuedate.setText(followUp.getString("due_date"));
                        txtDetail.setText(followUp.getString("detail"));


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

        RequestQueue queue = Volley.newRequestQueue(DetailFollowUpActivity.this);
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