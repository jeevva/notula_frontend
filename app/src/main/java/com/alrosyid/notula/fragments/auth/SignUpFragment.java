package com.alrosyid.notula.fragments.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.AuthActivity;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.api.Constant;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private View view;
    private TextInputLayout layoutName, layoutEmail,layoutOrg, layoutAddressOrg, layoutPassword, layoutConfirm;
    private TextInputEditText txtName, txtEmail,txtOrg, txttAddressOrg, txtPassword, txtConfirm;
    private TextView txtSignIn;
    private Button btnSignUp;
    private ProgressDialog dialog;

    public SignUpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        init();
        return view;
    }

    private void init() {
        layoutName = view.findViewById(R.id.txtLayoutName);
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignUp);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignUp);
        layoutOrg = view.findViewById(R.id.txtLayoutOrg);
        layoutAddressOrg = view.findViewById(R.id.txtLayoutAddressOrg);
        layoutConfirm = view.findViewById(R.id.txtLayoutConfirmPassword);
        txtName = view.findViewById(R.id.txtName);
        txtOrg = view.findViewById(R.id.txtOrg);
        txttAddressOrg = view.findViewById(R.id.txtAddressOrg);
        txtPassword = view.findViewById(R.id.txtPasswordSignUp);
        txtConfirm = view.findViewById(R.id.txtConfirmPassword);
        txtSignIn = view.findViewById(R.id.txtSignIn);
        txtEmail = view.findViewById(R.id.txtEmailSignUp);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtSignIn.setOnClickListener(v -> {
            //change fragments
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
        });

        btnSignUp.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                register();
            }
        });

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtName.getText().toString().isEmpty()) {
                    layoutName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtPassword.getText().toString().length() > 7) {
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtConfirm.getText().toString().equals(txtPassword.getText().toString())) {
                    layoutConfirm.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

//    private boolean validateEmail() {
//        if (txtName.getText().toString().isEmpty()) {
//            layoutName.setErrorEnabled(true);
//            layoutName.setError(getString(R.string.required));
//            return false;
//        }
//        return true;
//    }


    private boolean validate() {
        if (txtName.getText().toString().isEmpty()) {
            layoutName.setErrorEnabled(true);
            layoutName.setError(getString(R.string.required));
            return false;
        }
        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError(getString(R.string.required));
            return false;
        }
        if (txtOrg.getText().toString().isEmpty()) {
            txtOrg.setText(" ");
            return false;
        }
        if (txttAddressOrg.getText().toString().isEmpty()) {
            txttAddressOrg.setText(" ");
            return false;
        }
        if (txttAddressOrg.getText().toString().length() > 200) {
            layoutAddressOrg.setErrorEnabled(true);
            layoutAddressOrg.setError(getString(R.string.maximum_character));
            return false;
        }
        if (txtPassword.getText().toString().length() < 8) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError(getString(R.string.required_password_characters));
            return false;
        }
        if (!txtConfirm.getText().toString().equals(txtPassword.getText().toString())) {
            layoutConfirm.setErrorEnabled(true);
            layoutConfirm.setError(getString(R.string.password_does_not_match));
            return false;
        }


        return true;
    }


    private void register() {
        dialog.setMessage(getString(R.string.sign_up_load));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {
            //we get response if connection success

            try {
                JSONObject object = new JSONObject(response);
                boolean success = object.getBoolean("success");
                if (success) {
                    JSONObject user = object.getJSONObject("user");
                    //make shared preference user
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putInt("id", user.getInt("id"));
//                    editor.putString("name",user.getString("name"));
                    editor.putBoolean("isLoggedUp", true);
                    editor.apply();
                    //if success
                    startActivity(new Intent(((AuthActivity) getContext()), MainActivity.class));
                    ((AuthActivity) getContext()).finish();
                    Toast.makeText(getContext(), R.string.sign_up_successfully, Toast.LENGTH_SHORT).show();
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new SignUpSucessFragment()).commit();


                } else if (!success) {
                    layoutPassword.setError(getString(R.string.sign_up_failed));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();


        }, error -> {
            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
                dialog.dismiss();
                layoutEmail.setErrorEnabled(true);
                layoutEmail.setError(getString(R.string.owned_by_someone));

            } else if (error instanceof AuthFailureError) {
            } else if (error instanceof ParseError) {
            } else if (error instanceof NoConnectionError) {
            } else if (error instanceof TimeoutError) {
                Toast.makeText(getContext(),
                        R.string.timeout_error,
                        Toast.LENGTH_LONG).show();
            }
        }
//        ,error -> {
//            // error if connection not success
//            error.printStackTrace();
//            dialog.dismiss();
//            Toast.makeText(getContext(), "Pendaftaran Error", Toast.LENGTH_SHORT).show();
//
//        }
        ) {

            // add parameters


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", txtName.getText().toString().trim());
                map.put("email", txtEmail.getText().toString().trim());
                map.put("name_organization", txtOrg.getText().toString().trim());
                map.put("address_organization", txttAddressOrg.getText().toString().trim());
                map.put("password", txtPassword.getText().toString());
                return map;
            }
        };

        //add this request to requestqueue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}