package com.alrosyid.notula.activities.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

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

import java.util.HashMap;
import java.util.Map;

public class DetailNotesActivity extends AppCompatActivity {
    private TextInputLayout lytTitle, lytNote;
    private TextInputEditText txtTitle, txtNote;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.detail_note);

        init();
    }

    private void init() {

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        lytTitle = findViewById(R.id.tilTitle);
        lytNote = findViewById(R.id.tilNote);
        txtTitle = findViewById(R.id.tieTitle);
        txtNote = findViewById(R.id.tieNote);

        getDetailNotes();

    }


    private void getDetailNotes() {
        Integer id_notula = getIntent().getIntExtra("noteId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_NOTES + (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("notes"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notula = array.getJSONObject(i);

                        txtTitle.setText(notula.getString("title"));
                        txtNote.setText(notula.getString("note"));

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

        RequestQueue queue = Volley.newRequestQueue(DetailNotesActivity.this);
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