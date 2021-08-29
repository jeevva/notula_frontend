package com.alrosyid.notula.fragments.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.AuthActivity;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.api.Constant;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignInFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private Button btnSignIn;
    private TextView txtSignUp, txtForgot;
    private ProgressDialog dialog;


    public SignInFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        init();
        return view;
    }

    private void init() {
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignIn);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);
        txtEmail = view.findViewById(R.id.txtEmailSignIn);
        txtSignUp = view.findViewById(R.id.txtSignUp);
        txtForgot = view.findViewById(R.id.tvForgot);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtSignUp.setOnClickListener(v -> {
            //change fragments
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignUpFragment()).commit();
        });
        btnSignIn.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                login();
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

        txtForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://web.jeevva.my.id/password/reset"));
                startActivity(intent);
            }
        });
    }


    private boolean validate() {
        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError(getString(R.string.required));
            return false;
        }
        if (txtPassword.getText().toString().length() < 8) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError(getString(R.string.required_password_characters));
            return false;
        }
        return true;
    }


    private void login() {
        dialog.setMessage(getString(R.string.sign_in_load));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.LOGIN, response -> {
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
                    editor.putString("name", user.getString("name"));
                    editor.putString("email", user.getString("email"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    //if success
                    startActivity(new Intent(((AuthActivity) getContext()), MainActivity.class));
                    ((AuthActivity) getContext()).finish();
                    Toast.makeText(getContext(), R.string.sign_in_successfully, Toast.LENGTH_SHORT).show();
                }
                if (!success) {
                    layoutPassword.setError(getString(R.string.incorrect));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
//            Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();

        }, error -> {
            // error if connection not success
            error.printStackTrace();
            dialog.dismiss();
            Toast.makeText(getContext(), R.string.bad_connection, Toast.LENGTH_SHORT).show();

        }) {

            // add parameters


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txtEmail.getText().toString().trim());
                map.put("password", txtPassword.getText().toString());
                return map;
            }
        };

        //add this request to requestqueue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}