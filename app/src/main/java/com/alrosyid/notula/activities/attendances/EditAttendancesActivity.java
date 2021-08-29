package com.alrosyid.notula.activities.attendances;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.accounts.EditAccountsActivity;
import com.alrosyid.notula.activities.meetings.DetailMeetingsActivity;
import com.alrosyid.notula.adapters.AttendancesAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.attendances.AttendancesListFragments;
import com.alrosyid.notula.fragments.meetings.MeetingsFragment;
import com.alrosyid.notula.models.Attendances;
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

import java.util.HashMap;
import java.util.Map;

public class EditAttendancesActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout layoutName, layoutPosition;
    private TextInputEditText txtName, txtPosition;
    private ProgressDialog dialog;
    private int attendancesId = 0, position = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendances);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_attendances
        );
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
        position = getIntent().getIntExtra("position", 0);
        attendancesId = getIntent().getIntExtra("attendancesId", 0);
        btnSave.setOnClickListener(v -> {
            if (validate()) {
                update();
            }
        });


        getAttendances();


    }

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

    private void getAttendances() {

        Integer id_attendances = getIntent().getIntExtra("attendancesId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_ATTENDANCES + (id_attendances), res -> {

            try {
                JSONObject object = new JSONObject(res);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("attendances"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendance = array.getJSONObject(i);

                        txtName.setText(attendance.getString("name"));
                        txtPosition.setText(attendance.getString("position"));


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

        RequestQueue queue = Volley.newRequestQueue(EditAttendancesActivity.this);
        queue.add(request);
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_ATTENDANCES, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Attendances attendances = AttendancesListFragments.arrayList.get(position);

                    attendances.setName(txtName.getText().toString());
                    attendances.setPosition(txtPosition.getText().toString());
                    AttendancesListFragments.arrayList.set(position, attendances);
                    AttendancesListFragments.recyclerView.getAdapter().notifyItemChanged(position);
                    AttendancesListFragments.recyclerView.getAdapter().notifyDataSetChanged();

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
                map.put("id", attendancesId + "");
                map.put("name", txtName.getText().toString());
                map.put("position", txtPosition.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditAttendancesActivity.this);
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