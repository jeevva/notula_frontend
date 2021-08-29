package com.alrosyid.notula.activities.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.points.EditPointsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notes.NotesFragments;
import com.alrosyid.notula.fragments.points.PointFragment;
import com.alrosyid.notula.models.Notes;
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

import java.util.HashMap;
import java.util.Map;

public class EditNotesActivity extends AppCompatActivity {
    private TextInputLayout lytTitle, lytNote;
    private TextInputEditText txtTitle, txtNote;
    private SharedPreferences sharedPreferences;
    private Button btnSave;
    private int noteId = 0, position = 0;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_note);

        init();
    }

    private void init() {

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        lytTitle = findViewById(R.id.tilTitle);
        lytNote = findViewById(R.id.tilNote);
        txtTitle = findViewById(R.id.tieTitle);
        txtNote = findViewById(R.id.tieNote);
        btnSave = findViewById(R.id.btnSave);
        noteId = getIntent().getIntExtra("noteId", 0);
        position = getIntent().getIntExtra("position", 0);

        getDetailNotes();

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });

    }

    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            lytTitle.setErrorEnabled(true);
            lytTitle.setError(getString(R.string.required));
            return false;
        }
        if (txtNote.getText().toString().isEmpty()) {
            lytNote.setErrorEnabled(true);
            lytNote.setError(getString(R.string.required));
            return false;
        }
        return true;
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_NOTES, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Notes notes = NotesFragments.arrayList.get(position);


                    notes.setTitle(txtTitle.getText().toString());
                    notes.setNote(txtNote.getText().toString());

                    NotesFragments.arrayList.set(position, notes);
                    NotesFragments.recyclerView.getAdapter().notifyItemChanged(position);
                    NotesFragments.recyclerView.getAdapter().notifyDataSetChanged();
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
                map.put("id", noteId + "");
//                map.put("notulas_id",notulasId+"");
                map.put("title", txtTitle.getText().toString());
                map.put("note", txtNote.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditNotesActivity.this);
        queue.add(request);
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

        RequestQueue queue = Volley.newRequestQueue(EditNotesActivity.this);
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