package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.EditAttendancesActivity;
import com.alrosyid.notula.activities.points.EditPointsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Attendances;
import com.alrosyid.notula.models.Notula;
import com.alrosyid.notula.models.Points;
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

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.PointsHolder> {


    private Context context;
    private ArrayList<Points> list;
    private ArrayList<Points> listAll;
    private SharedPreferences preferences;
    private ProgressDialog dialog;

    public PointsAdapter(Context context, ArrayList<Points> list) {
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public PointsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_points, parent, false);
        return new PointsHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull PointsHolder holder, int position) {

        Points points = list.get(position);
        holder.txtPoints.setText(points.getPoints());
        holder.txtNumber.setText(String.valueOf(position + 1 + ". "));

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(((Activity) context), EditPointsActivity.class);
                i.putExtra("pointsId", points.getId());
                i.putExtra("notulasId", points.getNotulas_id());
                i.putExtra("position", position);
                context.startActivity(i);

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAttendances(points.getId(), position);

            }
        });

    }

    private void deleteAttendances(int pointsId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_POINTS, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                            Toast.makeText(context, R.string.delete_successfully, Toast.LENGTH_SHORT).show();
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
                        map.put("id", pointsId + "");
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

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Points> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Points points : listAll) {
                    if (points.getPoints().toLowerCase().contains(constraint.toString().toLowerCase())

                    ) {
                        filteredList.add(points);
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
            list.addAll((Collection<? extends Points>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class PointsHolder extends RecyclerView.ViewHolder {
        private TextView txtNumber, txtPoints;
        private ImageButton btnEdit, btnDelete;

        public PointsHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = itemView.findViewById(R.id.tvNumber);
            txtPoints = itemView.findViewById(R.id.tvPoints);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
