package com.alrosyid.notula.activities.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.AddAttendancesActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.attendances.AttendancesListFragments;
import com.alrosyid.notula.fragments.notes.NotesFragments;
import com.alrosyid.notula.models.Attendances;
import com.alrosyid.notula.models.Notes;
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

public class AddNotesActivity extends AppCompatActivity {
    private Button btnSave;
    private ProgressDialog dialog;
    private TextInputLayout lytTitle, lytNote;
    private TextInputEditText txtTitle, txtNote;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_note);

        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytTitle = findViewById(R.id.tilTitle);
        lytNote = findViewById(R.id.tilNote);
        txtTitle = findViewById(R.id.tieTitle);
        txtNote = findViewById(R.id.tieNote);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                create();
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

    private void create() {
        dialog.setMessage(getString(R.string.save));
        dialog.show();
        String titleText = txtTitle.getText().toString();
        String noteText = txtNote.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_NOTES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {


                    JSONObject notesObject = object.getJSONObject("notes");


                    Notes notes = new Notes();
                    notes.setId(notesObject.getInt("id"));
                    notes.setNote(notesObject.getString("note"));
                    notes.setTitle(notesObject.getString("title"));
                    notes.setCreated_at(notesObject.getString("created_at"));


                    NotesFragments.arrayList.add(0, notes);
                    NotesFragments.recyclerView.getAdapter().notifyItemInserted(0);
                    NotesFragments.recyclerView.getAdapter().notifyDataSetChanged();

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
                map.put("title", titleText);
                map.put("note", noteText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddNotesActivity.this);
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