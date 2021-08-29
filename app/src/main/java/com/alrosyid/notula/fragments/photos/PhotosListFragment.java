package com.alrosyid.notula.fragments.photos;

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

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.photos.AddPhotosActivity;
import com.alrosyid.notula.adapters.PhotosListAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Photos;
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

public class PhotosListFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Photos> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private PhotosListAdapter photosListAdapter;
    private ImageButton addPhotos;
    private SharedPreferences sharedPreferences;
    private ImageButton addNotulas;
    private TextView dataEmpty, dataBadConnect;

    public PhotosListFragment() {
        // Required empty public constructor
    }

    public static PhotosListFragment newInstance() {

        Bundle args = new Bundle();

        PhotosListFragment fragment = new PhotosListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photos, container, false);
        init();
        return view;

    }

    private void init() {

        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerPhotos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipePhotos);
        dataEmpty = view.findViewById(R.id.dataEmpty);
        dataBadConnect = view.findViewById(R.id.dataBadConnect);
        setHasOptionsMenu(true);

        getPhotos();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPhotos();
            }
        });

        addPhotos = (ImageButton) view.findViewById(R.id.btnAdd);
        addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddPhotosActivity();

            }

            private void getAddPhotosActivity() {

                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddPhotosActivity.class);
                i.putExtra("meetingsId", (id_meetings));


                startActivity(i);
            }
        });
    }

    private void getPhotos() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_PHOTOS + (id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("photos"));
//                    if(array.length() >0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject photosObject = array.getJSONObject(i);

                        Photos photos = new Photos();
                        photos.setId(photosObject.getInt("id"));
                        photos.setPhoto(photosObject.getString("photo"));
                        photos.setTitle(photosObject.getString("title"));
                        photos.setCreated_at(photosObject.getString("created_at"));


                        arrayList.add(photos);
                    }

//                    }else{
//                        recyclerView.setVisibility(View.GONE);
//                        dataEmpty.setVisibility(View.VISIBLE);
//                    }


                    photosListAdapter = new PhotosListAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(photosListAdapter);
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

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                photosListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}