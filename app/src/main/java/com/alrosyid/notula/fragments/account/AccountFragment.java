package com.alrosyid.notula.fragments.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.AuthActivity;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.accounts.ChangePassAccountActivity;
import com.alrosyid.notula.activities.accounts.EditAccountsActivity;
import com.alrosyid.notula.activities.attendances.EditAttendancesActivity;
import com.alrosyid.notula.activities.meetings.AddMeetingsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.User;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {
    private View view;
    private TextView txtName, txtEmail, txtOrg, txtAddressOrg;
    private SharedPreferences sharedPreferences;
    private Button btnEdit, btnChangePass;


    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnChangePass = view.findViewById(R.id.btnChangePassword);
        txtName = view.findViewById(R.id.tieName);
        txtEmail = view.findViewById(R.id.tieEmail);
        txtOrg= view.findViewById(R.id.tieOrg);
        txtAddressOrg = view.findViewById(R.id.tieAddressOrg);
        setHasOptionsMenu(true);

        getUser();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditAccountActivity();

            }

            private void getEditAccountActivity() {

                Intent i = new Intent(getActivity(), EditAccountsActivity.class);

                startActivity(i);
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChangePassAccountActivity();

            }

            private void getChangePassAccountActivity() {

                Intent i = new Intent(getActivity(), ChangePassAccountActivity.class);

                startActivity(i);
            }
        });


    }

    private void getUser() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.ACCOUNT, res -> {

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")) {

                    JSONObject user = object.getJSONObject("user");

                    txtName.setText(user.getString("name"));
                    txtEmail.setText(user.getString("email"));
                    txtOrg.setText(user.getString("name_organization"));
                    txtAddressOrg.setText(user.getString("address_organization"));


                        if (txtOrg.getText().toString()==null) {
                            txtOrg.setHint(getString(R.string.your_organization));

                        }
                        if (txtAddressOrg.getText().toString()==null) {
                            txtAddressOrg.setHint(getString(R.string.organization_address));
                           
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.want_to_logout);
                builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT, res -> {

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(((MainActivity) getContext()), AuthActivity.class));
                    ((MainActivity) getContext()).finish();
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

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (!hidden) {
            getUser();
        }

        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }
}