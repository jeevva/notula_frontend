package com.alrosyid.notula.fragments.meetings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.meetings.AddMeetingsActivity;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class DetailMeetingsFragment extends Fragment {

    private TextInputLayout layoutTitle, layoutDate, layoutStartTime, layoutEndTime, layoutAgenda, layoutLocation;
    private TextInputEditText txtTitle, txtDate, txtStartTime, txtEndTime, txtAgenda, txtLocation;
    private int notulaId = 0, position = 0;
    private TextView addCalender, sendEmail;
    private ImageView imgShare;
    private View view;
    private SharedPreferences sharedPreferences;

    public static DetailMeetingsFragment newInstance() {

        Bundle args = new Bundle();

        DetailMeetingsFragment fragment = new DetailMeetingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailMeetingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_meetings, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutTitle = view.findViewById(R.id.tilTitle);
        layoutDate = view.findViewById(R.id.tilDate);
        layoutStartTime = view.findViewById(R.id.tilStartTime);
        layoutEndTime = view.findViewById(R.id.tilEndTime);
        layoutAgenda = view.findViewById(R.id.tilAgenda);
        layoutLocation = view.findViewById(R.id.tilLocation);
        txtTitle = view.findViewById(R.id.tieTitle);
        txtDate = view.findViewById(R.id.tieDate);
        txtStartTime = view.findViewById(R.id.tieStartTime);
        txtEndTime = view.findViewById(R.id.tieEndTime);
        txtAgenda = view.findViewById(R.id.tieAgenda);
        txtLocation = view.findViewById(R.id.tieLocation);


        setHasOptionsMenu(true);

        getDetailMeetings();

        addCalender = (TextView) view.findViewById(R.id.btnCalender);
        addCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddCalenderActivity();

            }

            private void getAddCalenderActivity() {

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(Integer.valueOf(txtDate.getText().toString().substring(0, 4)),
                        Integer.valueOf(txtDate.getText().toString().substring(5, 7))
                        , Integer.valueOf(txtDate.getText().toString().substring(8, 10))
                        , Integer.valueOf(txtStartTime.getText().toString().substring(0, 2))
                        , Integer.valueOf(txtStartTime.getText().toString().substring(3, 5)));
                Calendar endTime = Calendar.getInstance();
                endTime.set(Integer.valueOf(txtDate.getText().toString().substring(0, 4)),
                        Integer.valueOf(txtDate.getText().toString().substring(5, 7))
                        , Integer.valueOf(txtDate.getText().toString().substring(8, 10))
                        , Integer.valueOf(txtEndTime.getText().toString().substring(0, 2))
                        , Integer.valueOf(txtEndTime.getText().toString().substring(3, 5)));


                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, txtTitle.getText().toString());
                intent.putExtra(CalendarContract.Events.EXDATE, txtDate.getText().toString());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, txtLocation.getText().toString());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, txtAgenda.getText().toString());

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
//
//                );
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                startActivity(intent);
            }
        });

        sendEmail = (TextView) view.findViewById(R.id.btnEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSendEmail();

            }

            private void getSendEmail() {
                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                emailSelectorIntent.setData(Uri.parse("mailto:"));

                final Intent email = new Intent(Intent.ACTION_SEND);
//                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, (getString(R.string.Invitation) + txtTitle.getText().toString()));
                email.putExtra(Intent.EXTRA_TEXT, (
//                        "Dear Sir/Madam, " +
                        getString(R.string.we_are_inviting)
                                + getString(R.string.body_title) + txtTitle.getText().toString()
                                + getString(R.string.body_date) + txtDate.getText().toString()
                                + getString(R.string.body_start) + txtStartTime.getText().toString()
                                + getString(R.string.body_end) + txtEndTime.getText().toString()
                                + getString(R.string.body_agenda) + txtAgenda.getText().toString() + "\n\n"
                                + getString(R.string.body_location) + txtLocation.getText().toString()));


//need this to prompt`enter code here`s email client only
//                email.setType("message/rfc822");
                email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                email.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                email.setSelector(emailSelectorIntent);

                startActivity(email);
            }

        });


        imgShare = (ImageView) view.findViewById(R.id.btnShare);
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShare();

            }

            private void getShare() {
//                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
//                emailSelectorIntent.setData(Uri.parse("mailto:"));
                Intent share = new Intent(Intent.ACTION_SEND);
//                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
//                email.putExtra(Intent.EXTRA_SUBJECT, ("Invitation / Schedule of Meeting : "+txtTitle.getText().toString()));
                share.putExtra(Intent.EXTRA_TEXT, ("Invitation / Schedule of Meeting : " + txtTitle.getText().toString()
//                        +"\n\nDear Sir/Madam, "
                        + "\n\nWe are inviting you a scheduled meeting"
                        + "\n\nTitle : " + txtTitle.getText().toString()
                        + "\nDate : " + txtDate.getText().toString()
                        + "\nStart Time : " + txtStartTime.getText().toString()
                        + "\nEnd Time : " + txtEndTime.getText().toString()
                        + "\n\nAgenda : \n" + txtAgenda.getText().toString() + "\n\n"
                        + "Location : \n" + txtLocation.getText().toString()));
                share.setType("text/plain");


//need this to prompt`enter code here`s email client only
//                share.setType("text/plain");
//                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                email.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                email.setSelector( emailSelectorIntent );

                startActivity(share);
            }

        });

    }


    private void getDetailMeetings() {
        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_MEETING + (id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("meetings"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject meetings = array.getJSONObject(i);
                        txtTitle.setText(meetings.getString("title"));
                        txtDate.setText(meetings.getString("date"));
//                        String source = meetings.getString("date");
//                        String[] sourceSplit= source.split("-");
//                        int anno= Integer.parseInt(sourceSplit[0]);
//                        int mese= Integer.parseInt(sourceSplit[1]);
//                        int giorno= Integer.parseInt(sourceSplit[2]);
//                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.set(anno,mese-1,giorno);
//                        Date data1= calendar.getTime();
//                        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy MM dd");
//                        String   dayFormatted= myFormat.format(data1);
//                        txtDate.setText(dayFormatted);
                        txtTitle.setText(meetings.getString("title"));

                        String startTime = meetings.getString("start_time");

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

                        String endTime = meetings.getString("end_time");

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
                        txtAgenda.setText(meetings.getString("agenda"));
                        txtLocation.setText(meetings.getString("location"));


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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}