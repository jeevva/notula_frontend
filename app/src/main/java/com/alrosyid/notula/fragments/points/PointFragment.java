package com.alrosyid.notula.fragments.points;

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
import com.alrosyid.notula.activities.points.AddPointsActivity;
import com.alrosyid.notula.adapters.PointsAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Points;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class PointFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Points> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private PointsAdapter pointsAdapter;
    private ImageButton addPoints;
    private SharedPreferences sharedPreferences;
    private ImageButton addNotulas;
    private TextView dataEmpty, dataBadConnect;

    public PointFragment() {
        // Required empty public constructor
    }

    public static PointFragment newInstance() {

        Bundle args = new Bundle();

        PointFragment fragment = new PointFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_point, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerPoints);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipePoints);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);

        setHasOptionsMenu(true);

        getPoints();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoints();
            }
        });

        addPoints = (ImageButton) view.findViewById(R.id.btnAdd);
        addPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddPointsActivity();

            }

            private void getAddPointsActivity() {

                Integer id_notulas = getActivity().getIntent().getIntExtra("notulasId", 0);
                Intent i = new Intent(getActivity(), AddPointsActivity.class);
                i.putExtra("notulasId", (id_notulas));
                startActivity(i);
            }
        });
    }

    private void getPoints() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_notula = getActivity().getIntent().getIntExtra("notulasId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_POINTS + (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("points"));
//                    if(array.length() >0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject pointsObject = array.getJSONObject(i);
                        Points points = new Points();
                        points.setId(pointsObject.getInt("id"));
                        points.setPoints(pointsObject.getString("points"));

                        arrayList.add(points);


                    }



//                    }else{
//                        recyclerView.setVisibility(View.GONE);
//                        dataEmpty.setVisibility(View.VISIBLE);
//                    }


                    pointsAdapter = new PointsAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(pointsAdapter);

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
                pointsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


}