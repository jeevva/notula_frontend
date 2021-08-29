package com.alrosyid.notula.activities.attendances;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.attendances.AttendancesListFragments;
import com.alrosyid.notula.models.Attendances;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAttendancesActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout layoutName, layoutPosition;
    private TextInputEditText txtName, txtPosition;
    private ProgressDialog dialog;
    private int meetingsId = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendances);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_attendances);

        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        layoutName = findViewById(R.id.tilName);
        layoutPosition = findViewById(R.id.tilPosition);
        txtName = findViewById(R.id.tieName);
        txtPosition = findViewById(R.id.tiePosition);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                create();
            }
        });


    }

    //validasi
    private boolean validate() {
        if (txtName.getText().toString().isEmpty()) {
            layoutName.setErrorEnabled(true);
            layoutName.setError(getString(R.string.required));
            return false;
        }
        if (txtPosition.getText().toString().isEmpty()) {
            layoutPosition.setErrorEnabled(true);
            layoutPosition.setError(getString(R.string.required));
            return false;
        }
        return true;
    }

    private void create() {
        dialog.setMessage(getString(R.string.save));
        dialog.show();
        String nameText = txtName.getText().toString();
        String positionText = txtPosition.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_ATTENDANCES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {


                    JSONObject attendancesObject = object.getJSONObject("attendances");


                    Attendances attendances = new Attendances();
                    attendances.setId(attendancesObject.getInt("id"));

                    attendances.setName(attendancesObject.getString("name"));
                    attendances.setPosition(attendancesObject.getString("position"));


                    AttendancesListFragments.arrayList.add(0, attendances);
                    AttendancesListFragments.recyclerView.getAdapter().notifyItemInserted(0);
                    AttendancesListFragments.recyclerView.getAdapter().notifyDataSetChanged();

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
                map.put("name", nameText);
                map.put("position", positionText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddAttendancesActivity.this);
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