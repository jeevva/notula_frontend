package com.alrosyid.notula.fragments.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.meetings.AddMeetingsHomeActivity;
import com.alrosyid.notula.activities.notulas.AddNotulasHomeActivity;
import com.alrosyid.notula.activities.notulas.ReportActivity;
import com.alrosyid.notula.adapters.HomeMeetingsAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notulas.ReportFragment;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private TextView dateTimeDisplay;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Meetings> arrayList;
    public static ArrayList<Notula> list;
    private SwipeRefreshLayout refreshLayout;
    private HomeMeetingsAdapter homeMeetingsAdapter;
    private SharedPreferences sharedPreferences;
    private Button addMeetings, addNotulas, allReport;
    private TextView dataEmpty;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        calendar = Calendar.getInstance();
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerMeetings);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeHome);


        setHasOptionsMenu(true);
        getMeets();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMeets();
            }
        });

        allReport = (Button) view.findViewById(R.id.allReport);
        allReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReportList();

            }

            private void getReportList() {

                Intent i = new Intent(getActivity(), ReportActivity.class);

                startActivity(i);

            }
        });

        addMeetings = (Button) view.findViewById(R.id.addMeetings);
        addMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddMeetingsActivity();

            }

            private void getAddMeetingsActivity() {

                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddMeetingsHomeActivity.class);
                i.putExtra("meetingsId", (id_meetings));
                startActivity(i);
            }
        });

        addNotulas = (Button) view.findViewById(R.id.addNotula);
        addNotulas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddMeetingsActivity();

            }

            private void getAddMeetingsActivity() {

//                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddNotulasHomeActivity.class);
//                i.putExtra("meetingsId", (id_meetings));
                startActivity(i);
            }
        });


    }


    private void getMeets() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_MEETING_TODAY, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("meetings"));
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject meetObject = array.getJSONObject(i);
                            Meetings meetings = new Meetings();
                            meetings.setId(meetObject.getInt("id"));
                            meetings.setTitle(meetObject.getString("title"));
                            //covert string to date
                            meetings.setDate(meetObject.getString("date"));
//                        String source = meetObject.getString("date");
//                        String[] sourceSplit= source.split("-");
//                        int anno= Integer.parseInt(sourceSplit[0]);
//                        int mese= Integer.parseInt(sourceSplit[1]);
//                        int giorno= Integer.parseInt(sourceSplit[2]);
//                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.set(anno,mese-1,giorno);
//                        Date data1= calendar.getTime();
//                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");
//
//                        String   dayFormatted= myFormat.format(data1);
//                        meetings.setDate(dayFormatted);


                            arrayList.add(meetings);
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        dataEmpty.setVisibility(View.VISIBLE);
                    }

                    homeMeetingsAdapter = new HomeMeetingsAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(homeMeetingsAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }) {

            // provide token in header

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