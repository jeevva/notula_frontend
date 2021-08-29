package com.alrosyid.notula.activities.accounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.api.Constant;
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

public class ChangePassAccountActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout lytPassword, lytConfirmPass;
    private TextInputEditText txtPassword, txtConfirmPass;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.change_password);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytPassword = findViewById(R.id.tilPassword);
        lytConfirmPass = findViewById(R.id.tilConfirmPass);
        txtPassword = findViewById(R.id.tiePass);
        txtConfirmPass = findViewById(R.id.tieConfirmPass);
        btnSave.setOnClickListener(v -> {
            if (validate()) {
                update();
            }
        });


    }

    private boolean validate() {
        if (txtPassword.getText().toString().isEmpty()) {
            lytPassword.setErrorEnabled(true);
            lytPassword.setError(getString(R.string.required));
            return false;
        }
        if (txtConfirmPass.getText().toString().isEmpty()) {
            lytConfirmPass.setErrorEnabled(true);
            lytConfirmPass.setError(getString(R.string.required));
            return false;
        }

        if (txtPassword.getText().toString().length() < 8) {
            lytPassword.setErrorEnabled(true);
            lytPassword.setError(getString(R.string.required_password_characters));
            return false;
        }
        if (!txtConfirmPass.getText().toString().equals(txtPassword.getText().toString())) {
            lytConfirmPass.setErrorEnabled(true);
            lytConfirmPass.setError(getString(R.string.password_does_not_match));
            return false;
        }


        return true;
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CHANGE_PASSWORD, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", txtPassword.getText().toString().trim());

                    editor.apply();
                    Intent restarter = new Intent(ChangePassAccountActivity.this, MainActivity.class);
                    startActivity(restarter);
                    Toast.makeText(this, R.string.change_successfully, Toast.LENGTH_SHORT).show();
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
                map.put("password", txtPassword.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ChangePassAccountActivity.this);
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