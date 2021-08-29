package com.alrosyid.notula.activities.accounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.AuthActivity;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.attendances.AttendancesListFragments;
import com.alrosyid.notula.models.Attendances;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditAccountsActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout lytName, lytEmail,lytAddressOrg, lytOrg;
    private TextView txtName, txtEmail, txtOrg, txtAddressOrg;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_account);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytName = findViewById(R.id.tilName);
        lytEmail = findViewById(R.id.tilEmail);
        lytOrg = findViewById(R.id.tilOrg);
        lytAddressOrg = findViewById(R.id.tilAddressOrg);
        txtName = findViewById(R.id.tieName);
        txtEmail = findViewById(R.id.tieEmail);
        txtOrg= findViewById(R.id.tieOrg);
        txtAddressOrg = findViewById(R.id.tieAddressOrg);
        btnSave.setOnClickListener(v -> {
            if (validate()) {
                update();
            }
        });


        getUser();


    }

    private boolean validate() {
        if (txtName.getText().toString().isEmpty()) {
            lytName.setErrorEnabled(true);
            lytName.setError(getString(R.string.required));
            return false;
        }
        if (txtEmail.getText().toString().isEmpty()) {
            lytEmail.setErrorEnabled(true);
            lytEmail.setError(getString(R.string.required));
            return false;
        }
        if (txtOrg.getText().toString().isEmpty()) {
            lytOrg.setErrorEnabled(true);
            lytOrg.setError(getString(R.string.required));
            return false;
        }
        if (txtAddressOrg.getText().toString().isEmpty()) {
            lytAddressOrg.setErrorEnabled(true);
            lytAddressOrg.setError(getString(R.string.required));
            return false;
        }
        if (txtAddressOrg.getText().toString().length() > 200) {
            lytAddressOrg.setErrorEnabled(true);
            lytAddressOrg.setError(getString(R.string.maximum_character));
            return false;
        }

        return true;
    }

    private void getUser() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.ACCOUNT, res -> {

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")) {

                    JSONObject user = object.getJSONObject("user");
                    txtName.setText(user.getString("name"));
                    txtEmail.setText(user.getString("email"));
                    txtOrg.setText(user.getString("name_organization"));
                    txtAddressOrg.setText(user.getString("address_organization"));
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

        RequestQueue queue = Volley.newRequestQueue(EditAccountsActivity.this);
        queue.add(request);
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_USER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", txtName.getText().toString().trim());
                    editor.putString("email", txtEmail.getText().toString().trim());
                    editor.putString("name_organization", txtOrg.getText().toString().trim());
                    editor.putString("address_organization", txtAddressOrg.getText().toString().trim());

                    editor.apply();
                    Intent restart = new Intent(EditAccountsActivity.this, MainActivity.class);
                    startActivity(restart);
                    Toast.makeText(this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
                dialog.dismiss();
                lytEmail.setErrorEnabled(true);
                lytEmail.setError(getString(R.string.owned_by_someone));

            } else if (error instanceof AuthFailureError) {
            } else if (error instanceof ParseError) {
            } else if (error instanceof NoConnectionError) {
            } else if (error instanceof TimeoutError) {
                Toast.makeText(getApplication(),
                        R.string.timeout_error,
                        Toast.LENGTH_LONG).show();
            }
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
                map.put("name", txtName.getText().toString());
                map.put("email", txtEmail.getText().toString());
                map.put("name_organization", txtOrg.getText().toString());
                map.put("address_organization", txtAddressOrg.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditAccountsActivity.this);
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