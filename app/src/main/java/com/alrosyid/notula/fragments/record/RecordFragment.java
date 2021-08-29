package com.alrosyid.notula.fragments.record;

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
import com.alrosyid.notula.activities.records.AddRecordsActivity;
import com.alrosyid.notula.adapters.AttendancesAdapter;
import com.alrosyid.notula.adapters.RecordsListAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.models.Attendances;
import com.alrosyid.notula.models.Records;
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


public class RecordFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Records> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private ImageButton addRecords;
    private RecordsListAdapter recordsListAdapter;
    private SharedPreferences sharedPreferences;

    private TextView dataEmpty, dataBadConnect;

    public RecordFragment() {
        // Required empty public constructor
    }
    public static RecordFragment newInstance() {

        Bundle args = new Bundle();

        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerRecords);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeRecords);
        setHasOptionsMenu(true);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);

        getRecords();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecords();
            }
        });

        addRecords = view.findViewById(R.id.btnAdd);
        addRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddRecordsActivity();

            }

            private void getAddRecordsActivity() {

                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddRecordsActivity.class);
                i.putExtra("meetingsId", (id_meetings));


                startActivity(i);
            }
        });
    }

    private void getRecords() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_RECORDS + (id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray array = new JSONArray(object.getString("records"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendancesObject = array.getJSONObject(i);
                        Records records = new Records();
                        records.setId(attendancesObject.getInt("id"));
                        records.setTitle(attendancesObject.getString("title"));
//                        records.setRecord(attendancesObject.getString("record"));
                        arrayList.add(records);
                    }
                    recordsListAdapter = new RecordsListAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(recordsListAdapter);

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
                recordsListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}