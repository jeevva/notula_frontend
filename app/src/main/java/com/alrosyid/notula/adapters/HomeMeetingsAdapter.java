package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.meetings.DetailMeetingsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Meetings;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HomeMeetingsAdapter extends RecyclerView.Adapter<HomeMeetingsAdapter.MeetsHolder> {

    private Context context;
    private ArrayList<Meetings> list;
    private ArrayList<Meetings> listAll;
    private SharedPreferences preferences;


    public HomeMeetingsAdapter(Context context, ArrayList<Meetings> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public MeetsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meetings, parent, false);
        return new MeetsHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull MeetsHolder holder, int position) {
        Meetings meetings = list.get(position);
        holder.txtTitle.setText(meetings.getTitle());
        holder.txtDate.setText(meetings.getDate());

        holder.detailMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(((Activity) context), DetailMeetingsActivity.class);
                i.putExtra("meetingsId", meetings.getId());
                i.putExtra("meetingsPosition", position);
                context.startActivity(i);
            }
        });

    }

    private void deleteNotula(int meetId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Hapus dari daftar hadir?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_MEETINGS, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                            Toast.makeText(context, "Berhasil menghapus daftar rapat", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = preferences.getString("token", "");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + token);
                        return map;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", meetId + "");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }


    @Override
    public int getItemCount() {

        return list.size();


    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Meetings> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Meetings meetings : listAll) {
                    if (meetings.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || meetings.getDate().toLowerCase().contains(constraint.toString().toLowerCase())
                    ) {
                        filteredList.add(meetings);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Meetings>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class MeetsHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtDate;
        private ImageButton btnPostOption, btnEdit, btnDelete;
        private Button btnAttendances;
        private CardView detailMeetings;

        public MeetsHolder(@NonNull View itemView) {
            super(itemView);
            detailMeetings = itemView.findViewById(R.id.cvMeetings);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }

}
