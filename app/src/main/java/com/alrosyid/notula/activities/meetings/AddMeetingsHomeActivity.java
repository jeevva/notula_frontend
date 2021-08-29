package com.alrosyid.notula.activities.meetings;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.accounts.EditAccountsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.home.HomeFragment;
import com.alrosyid.notula.fragments.meetings.MeetingsFragment;
import com.alrosyid.notula.models.Meetings;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddMeetingsHomeActivity extends AppCompatActivity  {
    private Button btnSave;
    private TextInputLayout lytTitle, lytAgenda, lytStartTime, lytEndTime, lytDate,lytLocation;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private TextInputEditText txtTitle, txtAgenda, txtStartTime, txtEndTime, txtDate, txtLocation;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meetings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_meetings);

        txtDate = findViewById(R.id.tieDate);
//Date Picker
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TextInputEditText tanggal = findViewById(R.id.tieDate);
                String myFormat = "yyyy-MM-dd";
//                String myFormat = "dd MMMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tanggal.setText(sdf.format(myCalendar.getTime()));
            }
        };

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddMeetingsHomeActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
//Time Picker
        txtStartTime = findViewById(R.id.tieStartTime);
        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddMeetingsHomeActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtStartTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });
        txtEndTime = findViewById(R.id.tieEndTime);
        txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddMeetingsHomeActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtEndTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });

        init();
    }
    private void init() {
//        dialog.setMessage(getString(R.string.save));
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        lytTitle = findViewById(R.id.tilTitle);
        lytAgenda= findViewById(R.id.tilAgenda);
        lytLocation= findViewById(R.id.tilLocation);
        lytDate= findViewById(R.id.tilDate);
        lytStartTime= findViewById(R.id.tilStartTime);
        lytEndTime= findViewById(R.id.tilEndTime);


        txtTitle= findViewById(R.id.tieTitle);
        txtAgenda= findViewById(R.id.tieAgenda);
        txtLocation= findViewById(R.id.tieLocation);
        txtDate= findViewById(R.id.tieDate);
        txtStartTime= findViewById(R.id.tieStartTime);
        txtEndTime= findViewById(R.id.tieEndTime);

        btnSave = findViewById(R.id.btnSave);
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
        if (txtAgenda.getText().toString().isEmpty()) {
            lytAgenda.setErrorEnabled(true);
            lytAgenda.setError(getString(R.string.required));
            return false;
        }
        if (txtLocation.getText().toString().isEmpty()) {
            lytLocation.setErrorEnabled(true);
            lytLocation.setError(getString(R.string.required));
            return false;
        }
        if (txtAgenda.getText().toString().trim().length() >200) {
            lytAgenda.setErrorEnabled(true);
            lytAgenda.setError(getString(R.string.maximum_character));
            return false;
        }
        if (txtLocation.getText().toString().trim().length() >200) {
            lytLocation.setErrorEnabled(true);
            lytLocation.setError(getString(R.string.maximum_character));
            return false;
        }
        if (txtDate.getText().toString().isEmpty()) {
            lytDate.setErrorEnabled(true);
            lytDate.setError(getString(R.string.required));
            return false;
        }
        if (txtStartTime.getText().toString().isEmpty()) {
            lytStartTime.setErrorEnabled(true);
            lytStartTime.setError(getString(R.string.required));
            return false;
        }
        if (txtEndTime.getText().toString().isEmpty()) {
            lytEndTime.setErrorEnabled(true);
            lytEndTime.setError(getString(R.string.required));
            return false;
        }
        return true;
    }

    private void create() {
        dialog.setMessage(getString(R.string.save_load));
        dialog.show();
        String titleText = txtTitle.getText().toString();
        String agendaText = txtAgenda.getText().toString();
        String locationText = txtLocation.getText().toString();
        String dateText = txtDate.getText().toString();
        String startTimeText = txtStartTime.getText().toString();
        String endTimeText = txtEndTime.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_MEETINGS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject meetingObject = object.getJSONObject("meetings");

                    Meetings meetings = new Meetings();
                    meetings.setId(meetingObject.getInt("id"));
                    meetings.setTitle(meetingObject.getString("title"));
                    meetings.setAgenda(meetingObject.getString("agenda"));
                    meetings.setLocation(meetingObject.getString("location"));
                    meetings.setDate(meetingObject.getString("date"));
                    meetings.setStart_time(meetingObject.getString("start_time"));
                    meetings.setEnd_time(meetingObject.getString("end_time"));

                    HomeFragment.arrayList.add(0, meetings);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(1);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Intent restart = new Intent(AddMeetingsHomeActivity.this, MainActivity.class);
                    startActivity(restart);
                    Toast.makeText(this, getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
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

//                map.put("position",txtName.getText().toString().trim());

                return map;
            }

            // add params
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("title", titleText);
                map.put("agenda", agendaText);
                map.put("location", locationText);
                map.put("date", dateText);
                map.put("start_time", startTimeText);
                map.put("end_time", endTimeText);

                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddMeetingsHomeActivity.this);
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