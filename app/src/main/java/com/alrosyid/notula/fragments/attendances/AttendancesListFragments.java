package com.alrosyid.notula.fragments.attendances;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.AddAttendancesActivity;
import com.alrosyid.notula.adapters.AttendancesAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Attendances;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendancesListFragments extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Attendances> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private ImageButton addAttendances;
    private AttendancesAdapter attendancesAdapter;
    private SharedPreferences sharedPreferences;

    private TextView dataEmpty, dataBadConnect;

    public AttendancesListFragments() {
        // Required empty public constructor
    }

    public static AttendancesListFragments newInstance() {

        Bundle args = new Bundle();

        AttendancesListFragments fragment = new AttendancesListFragments();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_attendances, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerAttandances);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeAttandances);
        setHasOptionsMenu(true);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);

        getAttendances();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAttendances();
            }
        });

        addAttendances = view.findViewById(R.id.btnAdd);
        addAttendances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddAttendancesActivity();

            }

            private void getAddAttendancesActivity() {
                String title_meetings = getActivity().getIntent().getStringExtra("meetingsTitle");
                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddAttendancesActivity.class);
                i.putExtra("meetingsId", (id_meetings));
                i.putExtra("meetingsTitle", (title_meetings));

                startActivity(i);
            }
        });
    }

    private void getAttendances() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_ATTENDANCES + (id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray array = new JSONArray(object.getString("attendances"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendancesObject = array.getJSONObject(i);
                        Attendances attendances = new Attendances();
                        attendances.setId(attendancesObject.getInt("id"));
                        attendances.setName(attendancesObject.getString("name"));
                        attendances.setPosition(attendancesObject.getString("position"));
                        arrayList.add(attendances);
                    }
                    attendancesAdapter = new AttendancesAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(attendancesAdapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
            dataBadConnect.setVisibility(View.GONE);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                attendancesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}