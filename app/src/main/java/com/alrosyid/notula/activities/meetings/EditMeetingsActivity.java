package com.alrosyid.notula.activities.meetings;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.meetings.MeetingsFragment;
import com.alrosyid.notula.models.Meetings;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditMeetingsActivity extends AppCompatActivity {

    private Button btnSave;
    private TextInputLayout lytTitle, lytAgenda, lytStartTime, lytEndTime, lytDate, lytLocation;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private TextInputEditText txtTitle, txtAgenda, txtStartTime, txtEndTime, txtDate, txtLocation;
    Calendar myCalendar;
    private int meetingsId = 0, position = 0;
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meetings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_meetings);


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
                new DatePickerDialog(EditMeetingsActivity.this, date,
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
                mTimePicker = new TimePickerDialog(EditMeetingsActivity.this, new TimePickerDialog.OnTimeSetListener() {

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
                mTimePicker = new TimePickerDialog(EditMeetingsActivity.this, new TimePickerDialog.OnTimeSetListener() {

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
        dialog = new ProgressDialog(this);
//        dialog.setMessage(getString(R.string.update));
        dialog.setMessage("Update..");

        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);

        position = getIntent().getIntExtra("position", 0);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);

        lytTitle = findViewById(R.id.tilTitle);
        lytAgenda = findViewById(R.id.tilAgenda);
        lytDate = findViewById(R.id.tilDate);
        lytStartTime = findViewById(R.id.tilStartTime);
        lytEndTime = findViewById(R.id.tilEndTime);
        lytLocation = findViewById(R.id.tilLocation);


        txtTitle = findViewById(R.id.tieTitle);
        txtAgenda = findViewById(R.id.tieAgenda);
        txtDate = findViewById(R.id.tieDate);
        txtLocation = findViewById(R.id.tieLocation);
        txtStartTime = findViewById(R.id.tieStartTime);
        txtEndTime = findViewById(R.id.tieEndTime);


        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });
        getMeetings();


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
        if (txtAgenda.getText().toString().trim().length() > 200) {
            lytAgenda.setErrorEnabled(true);
            lytAgenda.setError(getString(R.string.maximum_character));
            return false;
        }
        if (txtLocation.getText().toString().isEmpty()) {
            lytLocation.setErrorEnabled(true);
            lytLocation.setError(getString(R.string.required));
            return false;
        }
        if (txtLocation.getText().toString().trim().length() > 200) {
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

    private void getMeetings() {

        Integer id_meetings = getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_MEETING + (id_meetings), res -> {

            try {
                JSONObject object = new JSONObject(res);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("meetings"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject meeting = array.getJSONObject(i);

                        txtTitle.setText(meeting.getString("title"));
                        txtAgenda.setText(meeting.getString("agenda"));
                        txtDate.setText(meeting.getString("date"));
                        txtLocation.setText(meeting.getString("location"));

                        String startTime = meeting.getString("start_time");

                        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                        //Desired format: 24 hour format: Change the pattern as per the need
                        DateFormat outputformat = new SimpleDateFormat("HH:mm");
                        Date stInput = null;
                        String stOutput = null;
                        try {
                            //Converting the input String to time
                            stInput = df.parse(startTime);
                            //Changing the format of date and storing it in String
                            stOutput = outputformat.format(stInput);
                            //Displaying the time
                            txtStartTime.setText(stOutput);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                        }
                        String endTime = meeting.getString("end_time");
                        DateFormat endDf = new SimpleDateFormat("HH:mm:ss");
                        //Desired format: 24 hour format: Change the pattern as per the need
                        DateFormat endOutputformat = new SimpleDateFormat("HH:mm");
                        Date enInput = null;
                        String enOutput = null;
                        try {
                            //Converting the input String to time
                            enInput = endDf.parse(endTime);
                            //Changing the format of date and storing it in String
                            enOutput = endOutputformat.format(enInput);
                            //Displaying the time
                            txtEndTime.setText(enOutput);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                        }

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

        RequestQueue queue = Volley.newRequestQueue(EditMeetingsActivity.this);
        queue.add(request);
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_MEETINGS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Meetings meetings = MeetingsFragment.arrayList.get(position);

                    meetings.setTitle(txtTitle.getText().toString());
                    meetings.setAgenda(txtAgenda.getText().toString());
                    meetings.setLocation(txtLocation.getText().toString());
                    meetings.setDate(txtDate.getText().toString());
                    meetings.setStart_time(txtStartTime.getText().toString());
                    meetings.setEnd_time(txtEndTime.getText().toString());

                    MeetingsFragment.arrayList.set(position, meetings);
                    MeetingsFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    MeetingsFragment.recyclerView.getAdapter().notifyDataSetChanged();
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
                map.put("id", meetingsId + "");
                map.put("title", txtTitle.getText().toString());
                map.put("agenda", txtAgenda.getText().toString());
                map.put("location", txtLocation.getText().toString());
                map.put("date", txtDate.getText().toString());
                map.put("start_time", txtStartTime.getText().toString());
                map.put("end_time", txtEndTime.getText().toString());


                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditMeetingsActivity.this);
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