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
import com.alrosyid.notula.activities.notes.DetailNotesActivity;
import com.alrosyid.notula.activities.notes.EditNotesActivity;
import com.alrosyid.notula.activities.notulas.DetailNotulaActivity;
import com.alrosyid.notula.activities.notulas.EditNotulaActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Notes;
import com.alrosyid.notula.models.Notula;
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

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    private Context context;
    private ArrayList<Notes> list;
    private ArrayList<Notes> listAll;
    private SharedPreferences preferences;

    public NotesAdapter(Context context, ArrayList<Notes> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_list, parent, false);
        return new NotesHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        Notes notes = list.get(position);
        holder.txtTitle.setText(notes.getTitle());
        holder.txtNote.setText(notes.getNote());
        holder.txtDate.setText(notes.getCreated_at());

        holder.detailNotula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetailNotulaActivity();
            }

            private void getDetailNotulaActivity() {

                Intent i = new Intent(((Activity) context), DetailNotesActivity.class);
                i.putExtra("noteId", notes.getId());
                i.putExtra("position", position);
                i.putExtra("title", notes.getTitle());
                context.startActivity(i);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(((Activity) context), EditNotesActivity.class);
                i.putExtra("noteId", notes.getId());
                i.putExtra("position", position);
                i.putExtra("title", notes.getTitle());
                context.startActivity(i);

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotula(notes.getId(), position);

            }
        });


    }

    private void deleteNotula(int notulaId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_NOTES, response -> {

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
                        map.put("id", notulaId + "");
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

            ArrayList<Notes> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Notes notes : listAll) {
                    if (notes.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || notes.getNote().toLowerCase().contains(constraint.toString().toLowerCase())
                            || notes.getCreated_at().toLowerCase().contains(constraint.toString().toLowerCase())
                    ) {
                        filteredList.add(notes);
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
            list.addAll((Collection<? extends Notes>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class NotesHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtDate, txtNote;
        private ImageButton btnEdit, btnDelete;
        private CardView detailNotula;

        public NotesHolder(@NonNull View itemView) {
            super(itemView);
            detailNotula = itemView.findViewById(R.id.cvMeetings);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtNote = itemView.findViewById(R.id.tvNote);
            txtDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
