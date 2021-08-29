package com.alrosyid.notula.activities.followups;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.followup.FollowUpFragment;
import com.alrosyid.notula.fragments.points.PointFragment;
import com.alrosyid.notula.models.FollowUp;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditFollowUpActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout lytTitle, lytPic, lytDuedate, lytDetail;
    private TextInputEditText txtTitle, txtPic, txtDuedate, txtDetail;
    private ProgressDialog dialog;
    private int notulasId = 0,followUpId =0;
    private SharedPreferences sharedPreferences;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_follow_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_follow_up
        );
        init();
        txtDuedate = findViewById(R.id.tieDueDate);
//Date Picker
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TextInputEditText tanggal = findViewById(R.id.tieDueDate);
                String myFormat = "yyyy-MM-dd";
//                String myFormat = "dd MMMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tanggal.setText(sdf.format(myCalendar.getTime()));
            }
        };

        txtDuedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditFollowUpActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytTitle = findViewById(R.id.tilTitle);
        lytPic = findViewById(R.id.tilPic);
        lytDuedate = findViewById(R.id.tilDueDate);
        lytDetail= findViewById(R.id.tilDetail);
        txtTitle = findViewById(R.id.tieTitle);
        txtPic = findViewById(R.id.tiePic);
        txtDuedate = findViewById(R.id.tieDueDate);
        txtDetail = findViewById(R.id.tieDetail);
        notulasId = getIntent().getIntExtra("notulasId", 0);
        position = getIntent().getIntExtra("position", 0);
        followUpId = getIntent().getIntExtra("followUpId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });


        getFollowUp();


    }

    //validasi
    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            lytTitle.setErrorEnabled(true);
            lytTitle.setError(getString(R.string.required));
            return false;
        }
        if (txtPic.getText().toString().isEmpty()) {
            lytPic.setErrorEnabled(true);
            lytPic.setError(getString(R.string.required));
            return false;
        }if (txtDuedate.getText().toString().isEmpty()) {
            lytDuedate.setErrorEnabled(true);
            lytDuedate.setError(getString(R.string.required));
            return false;
        }
        if (txtDetail.getText().toString().isEmpty()) {
            lytDetail.setErrorEnabled(true);
            lytDetail.setError(getString(R.string.required));
            return false;
        }
        if (txtDetail.getText().toString().trim().length() > 200) {
            lytDetail.setErrorEnabled(true);
            lytDetail.setError(getString(R.string.maximum_character));
            return false;
        }
        return true;
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

        RequestQueue queue = Volley.newRequestQueue(EditFollowUpActivity.this);
        queue.add(request);
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_FOLLOW_UP, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    FollowUp followUp = FollowUpFragment.arrayList.get(position);

                    followUp.setTitle(txtTitle.getText().toString());
                    followUp.setPic(txtPic.getText().toString());
                    followUp.setDue_date(txtDuedate.getText().toString());
                    followUp.setDetail(txtDetail.getText().toString());
                    FollowUpFragment.arrayList.set(position, followUp);
                    FollowUpFragment.recyclerView.getAdapter().notifyItemChanged(position);
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
                map.put("id", followUpId + "");
//                map.put("notulas_id",notulasId+"");
                map.put("title", txtTitle.getText().toString());
                map.put("pic", txtPic.getText().toString());
                map.put("due_date", txtDuedate.getText().toString());
                map.put("detail", txtDetail.getText().toString());

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditFollowUpActivity.this);
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