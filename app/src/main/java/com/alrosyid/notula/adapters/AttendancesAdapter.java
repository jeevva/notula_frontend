package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.attendances.EditAttendancesActivity;
import com.alrosyid.notula.activities.notulas.EditNotulaActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Attendances;
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

public class AttendancesAdapter extends RecyclerView.Adapter<AttendancesAdapter.AttendancesHolder> {

    private Context context;
    private ArrayList<Attendances> list;
    private ArrayList<Attendances> listAll;
    private SharedPreferences preferences;
    private ProgressDialog dialog;

    public AttendancesAdapter(Context context, ArrayList<Attendances> list) {
        this.context = context;
        this.list = list;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public AttendancesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attendances, parent, false);
        return new AttendancesHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull AttendancesHolder holder, int position) {
        Attendances attendances = list.get(position);
        if (attendances.getId() == preferences.getInt("id", 0)) {
            holder.txtName.setText("Data Kosong");
        } else {
            holder.txtName.setText(attendances.getName());
            holder.txtPosition.setText(attendances.getPosition());

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(((Activity) context), EditAttendancesActivity.class);
                    i.putExtra("attendancesId", attendances.getId());
                    i.putExtra("position", position);
                    i.putExtra("positions", attendances.getPosition());
                    i.putExtra("name", attendances.getName());
                    context.startActivity(i);

                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAttendances(attendances.getId(), position);

                }
            });


        }

    }

    private void deleteAttendances(int attendancesId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_ATTENDANCES, response -> {

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
                        map.put("id", attendancesId + "");
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

            ArrayList<Attendances> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Attendances attendances : listAll) {
                    if (attendances.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || attendances.getPosition().toLowerCase().contains(constraint.toString().toLowerCase())
                    ) {
                        filteredList.add(attendances);
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
            list.addAll((Collection<? extends Attendances>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class AttendancesHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtPosition;
        private ImageButton btnEdit, btnDelete;

        public AttendancesHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tvName);
            txtPosition = itemView.findViewById(R.id.tvPosition);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
