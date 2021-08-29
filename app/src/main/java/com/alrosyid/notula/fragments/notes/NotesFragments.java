package com.alrosyid.notula.fragments.notes;

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
import com.alrosyid.notula.activities.meetings.AddMeetingsActivity;
import com.alrosyid.notula.activities.notes.AddNotesActivity;
import com.alrosyid.notula.adapters.MeetingsAdapter;
import com.alrosyid.notula.adapters.NotesAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Notes;
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


public class NotesFragments extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Notes> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private NotesAdapter notesAdapter;
    private SharedPreferences sharedPreferences;
    private TextView dataEmpty, dataBadConnect;

    private ImageButton addNotes;

    public NotesFragments() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meetings, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeHome);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);

        setHasOptionsMenu(true);

        getNotes();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotes();
            }
        });


        addNotes = (ImageButton) view.findViewById(R.id.btnAdd);
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddNotesActivity();

            }

            private void getAddNotesActivity() {
                Intent i = new Intent(getActivity(), AddNotesActivity.class);
                startActivity(i);
            }
        });


    }


    private void getNotes() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_NOTES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("notes"));
//                    if(array.length() >0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notesObject = array.getJSONObject(i);
                        Notes notes = new Notes();
                        notes.setId(notesObject.getInt("id"));
                        notes.setTitle(notesObject.getString("title"));
                        notes.setNote(notesObject.getString("note"));
                        notes.setCreated_at(notesObject.getString("created_at"));
                        //covert string to date
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


                        arrayList.add(notes);
                    }

//                }else{
//                    recyclerView.setVisibility(View.GONE);
//                    dataEmpty.setVisibility(View.VISIBLE);
//                }


                    notesAdapter = new NotesAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(notesAdapter);
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
                notesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}