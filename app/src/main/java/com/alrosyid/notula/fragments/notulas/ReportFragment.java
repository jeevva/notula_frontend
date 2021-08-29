package com.alrosyid.notula.fragments.notulas;

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
import com.alrosyid.notula.activities.notulas.AddNotulasActivity;
import com.alrosyid.notula.adapters.NotulasListAdapter;
import com.alrosyid.notula.adapters.ReportsAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Notula;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Notula> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private ReportsAdapter reportsAdapter;
    private SharedPreferences sharedPreferences;

    private TextView dataEmpty, dataBadConnect;

    public ReportFragment() {
    }

    public static ReportFragment newInstance() {

        Bundle args = new Bundle();

        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(args);
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.report);
        view = inflater.inflate(R.layout.fragment_report, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerNotulas);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeNotulas);
//        dataEmpty=view.findViewById(R.id.tvEmpty);


        setHasOptionsMenu(true);

        getNotula();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotula();
            }
        });


    }


    private void getNotula() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.NOTULA , response -> {


            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    String data = object.getString("notulas");
//                    if(array.length() >0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notulaObject = array.getJSONObject(i);
                        Notula notula = new Notula();
                        notula.setId(notulaObject.getInt("id"));
                        notula.setTitle(notulaObject.getString("title"));
                        notula.setDate(notulaObject.getString("date"));
                        notula.setMeetings_title(notulaObject.getString("meetings_title"));
                        notula.setSummary(notulaObject.getString("summary"));

                        arrayList.add(notula);
                    }
                    reportsAdapter = new ReportsAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(reportsAdapter);
//                    }else{
//                        recyclerView.setVisibility(View.VISIBLE);
//                        dataEmpty.setVisibility(View.VISIBLE);
//                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            dataBadConnect.setVisibility(View.VISIBLE);
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
                reportsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}