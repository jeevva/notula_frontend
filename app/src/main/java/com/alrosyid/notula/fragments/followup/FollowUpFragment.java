package com.alrosyid.notula.fragments.followup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alrosyid.notula.activities.followups.AddFollowUpActivity;
import com.alrosyid.notula.adapters.FollowUpAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.FollowUp;
import com.alrosyid.notula.R;
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


public class FollowUpFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<FollowUp> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private FollowUpAdapter followUpAdapter;
    private SharedPreferences sharedPreferences;
    private ImageButton addFollowUp;
    private ImageButton addNotulas;
    private TextView dataEmpty, dataBadConnect;

    public FollowUpFragment() {
        // Required empty public constructor
    }

    public static FollowUpFragment newInstance() {

        Bundle args = new Bundle();

        FollowUpFragment fragment = new FollowUpFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_follow_up, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerFollowUp);
        recyclerView.setHasFixedSize(true);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeFollowUp);
        addFollowUp = (ImageButton) view.findViewById(R.id.btnAdd);
        addFollowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddFollowUpActivity();

            }

            private void getAddFollowUpActivity() {

                Integer id_notulas = getActivity().getIntent().getIntExtra("notulasId", 0);
                Intent i = new Intent(getActivity(), AddFollowUpActivity.class);
                i.putExtra("notulasId", (id_notulas));
                startActivity(i);
            }
        });
        setHasOptionsMenu(true);

        getFollowUp();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFollowUp();
            }
        });
    }

    private void getFollowUp() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_notula = getActivity().getIntent().getIntExtra("notulasId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_FOLLOW_UP + (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("followup"));
//                    if(array.length() >0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject followUpObject = array.getJSONObject(i);
                        FollowUp followUp = new FollowUp();
                        followUp.setId(followUpObject.getInt("id"));
                        followUp.setTitle(followUpObject.getString("title"));
                        followUp.setPic(followUpObject.getString("pic"));
                        followUp.setDue_date(followUpObject.getString("due_date"));
//                        String source = followUpObject.getString("due_date");
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
//                        followUp.setDueDate(dayFormatted);

                        arrayList.add(followUp);
                    }


//                    }else{
//                        recyclerView.setVisibility(View.GONE);
//                        dataEmpty.setVisibility(View.VISIBLE);
//                    }

                    followUpAdapter = new FollowUpAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(followUpAdapter);
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
                followUpAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


}