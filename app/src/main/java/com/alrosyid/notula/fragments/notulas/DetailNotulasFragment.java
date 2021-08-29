package com.alrosyid.notula.fragments.notulas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alrosyid.notula.R;
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

import java.util.HashMap;
import java.util.Map;


public class DetailNotulasFragment extends Fragment {
    private TextInputLayout lytTitle, lytMeetingsTitle, lytDate;
    private TextInputEditText txtTitle, txtMeetingsTitle, txtDate, txtSummary;
    private int notulasId = 0, position = 0;
    private Button btnExport;
    private View view;
    private SharedPreferences sharedPreferences;

    public static DetailNotulasFragment newInstance() {

        Bundle args = new Bundle();

        DetailNotulasFragment fragment = new DetailNotulasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailNotulasFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_notula, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        lytTitle = view.findViewById(R.id.tilTitle);
        lytMeetingsTitle = view.findViewById(R.id.tilMeetingsTitle);
        lytDate = view.findViewById(R.id.tilDate);
        txtTitle = view.findViewById(R.id.tieTitle);
        txtSummary = view.findViewById(R.id.tieSummary);
        txtMeetingsTitle = view.findViewById(R.id.tieMeetingsTitle);
        txtDate = view.findViewById(R.id.tieDate);

        setHasOptionsMenu(true);

        getDetailNotulas();

    }


    private void getDetailNotulas() {
        Integer id_notula = getActivity().getIntent().getIntExtra("notulasId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_NOTULA + (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notula = array.getJSONObject(i);

                        txtTitle.setText(notula.getString("title"));
                        txtMeetingsTitle.setText(notula.getString("meetings_title"));
                        txtDate.setText(notula.getString("date"));
                        txtSummary.setText
                                (notula.getString("summary"));

//                        String source = notula.getString("date");
//                        String[] sourceSplit= source.split("-");
//                        int anno= Integer.parseInt(sourceSplit[0]);
//                        int mese= Integer.parseInt(sourceSplit[1]);
//                        int giorno= Integer.parseInt(sourceSplit[2]);
//                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.set(anno,mese-1,giorno);
//                        Date data1= calendar.getTime();
//                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");
//                        String   dayFormatted= myFormat.format(data1);
//                        txtDate.setText(dayFormatted);


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