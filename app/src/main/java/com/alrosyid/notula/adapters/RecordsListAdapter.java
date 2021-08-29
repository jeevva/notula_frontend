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
import com.alrosyid.notula.activities.photos.DetailPhotosActivity;
import com.alrosyid.notula.activities.photos.EditPhotosActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Photos;
import com.alrosyid.notula.models.Records;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.RecordsHolder> {


    private Context context;
    private ArrayList<Records> list;
    private ArrayList<Records> listAll;
    private SharedPreferences preferences;

    public RecordsListAdapter(Context context, ArrayList<Records> list) {
        Fresco.initialize(context);
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecordsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_records, parent, false);
        return new RecordsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordsHolder holder, int position) {
        Records records = list.get(position);
//        String  uri = Constant.URL + "storage/photos/" +  photos.getPhoto();
//        Picasso.get().load(Constant.URL + "storage/photos/" + (photo)).into(photoview2);
//        holder.imgPhotos.setImageURI(uri);
//        Picasso.get().load(Constant.URL + "storage/photos/" + photos.getPhoto()).into(holder.imgPhotos);
        holder.txtTitle.setText(records.getTitle());
//        holder.txtRecords.setText(records.getRecord());
//        holder.txtDate.setText(photos.getCreated_at());

//        if (post.getUser().getId() == preferences.getInt("id", 0)) {
//            holder.btnPostOption.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnPostOption.setVisibility(View.GONE);
//        }

        holder.detailRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetailNotulaActivity();
            }

            private void getDetailNotulaActivity() {

//                Intent i = new Intent(((Activity) context), DetailPhotosActivity.class);
//                i.putExtra("photosId", photos.getId());
//                i.putExtra("photo", photos.getPhoto());
//                i.putExtra("title", photos.getTitle());
//                i.putExtra("position", position);
//                context.startActivity(i);
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(((Activity) context), EditPhotosActivity.class);
//                i.putExtra("photosId", photos.getId());
//                i.putExtra("position", position);
//                context.startActivity(i);


            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(records.getId(), position);

            }
        });


    }

    //     delete post
    private void deleteRecord(int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_RECORDS, response -> {

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
                        map.put("id", postId + "");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

            ArrayList<Records> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Records records : listAll) {
                    if (records.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())

                    ) {
                        filteredList.add(records);
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
            list.addAll((Collection<? extends Records>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class RecordsHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtRecords;
        private ImageButton btnEdit, btnDelete;
        private CardView detailRecords;

        public RecordsHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.tvTitle);
//            txtRecords = itemView.findViewById(R.id.tvRecord);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            detailRecords = itemView.findViewById(R.id.cvRecords);

        }
    }
}

