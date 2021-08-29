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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.followups.DetailFollowUpActivity;
import com.alrosyid.notula.activities.followups.EditFollowUpActivity;
import com.alrosyid.notula.activities.notulas.DetailNotulaActivity;
import com.alrosyid.notula.activities.notulas.EditNotulaActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.FollowUp;
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

public class FollowUpAdapter extends RecyclerView.Adapter<FollowUpAdapter.FollowUpHolder> {

    private Context context;
    private ArrayList<FollowUp> list;
    private ArrayList<FollowUp> listAll;
    private SharedPreferences preferences;

    public FollowUpAdapter(Context context, ArrayList<FollowUp> list) {
        this.context = context;

        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public FollowUpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_follow_up, parent, false);
        return new FollowUpHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull FollowUpHolder holder, int position) {

        FollowUp followUp = list.get(position);
        holder.txtTitle.setText(followUp.getTitle());
        holder.txtPic.setText(followUp.getPic());
        holder.txtDueDate.setText(followUp.getDue_date());
        holder.detailFollowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetailFollowUpActivity();
            }

            private void getDetailFollowUpActivity() {

                Intent i = new Intent(((Activity) context), DetailFollowUpActivity.class);
                i.putExtra("followUpId", followUp.getId());
                i.putExtra("position", position);
                context.startActivity(i);
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(((Activity) context), EditFollowUpActivity.class);
                i.putExtra("followUpId", followUp.getId());
                i.putExtra("position", position);
                context.startActivity(i);

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFollowUp(followUp.getId(), position);

            }
        });


    }

    private void deleteFollowUp(int followUpId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_FOLLOW_UP, response -> {

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
                        map.put("id", followUpId + "");
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

    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<FollowUp> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (FollowUp followUp : listAll) {
                    if (followUp.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || followUp.getPic().toLowerCase().contains(constraint.toString().toLowerCase())
                            || followUp.getDue_date().toLowerCase().contains(constraint.toString().toLowerCase())

                    ) {
                        filteredList.add(followUp);
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
            list.addAll((Collection<? extends FollowUp>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class FollowUpHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtPic, txtDueDate;
        private ImageButton btnEdit, btnDelete;
        private CardView detailFollowUp;

        public FollowUpHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtPic = itemView.findViewById(R.id.tvPic);
            txtDueDate = itemView.findViewById(R.id.tvDueDate);
            detailFollowUp = itemView.findViewById(R.id.cvFollowup);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
