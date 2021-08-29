package com.alrosyid.notula.fragments.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alrosyid.notula.R;

import java.util.Calendar;


public class AboutFragment extends Fragment {
private Button btnReport, btnHelp;
   View view;
    public AboutFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_about, container, false);
        init();
        return view;
    }


    private void init() {

        setHasOptionsMenu(true);


        btnReport = (Button) view.findViewById(R.id.btnReport);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSendEmail();

            }

            private void getSendEmail() {
                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                emailSelectorIntent.setData(Uri.parse("mailto:"));

                final Intent email = new Intent(Intent.ACTION_SEND);
//                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_EMAIL,  new String[]{getString(R.string.email_developer)});
                email.putExtra(Intent.EXTRA_SUBJECT, (getString(R.string.subject_report_bugs)));
                email.putExtra(Intent.EXTRA_TEXT, (
//                        "Dear Sir/Madam, " +
                        getString(R.string.body_email_report)));


//need this to prompt`enter code here`s email client only
//                email.setType("message/rfc822");
                email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                email.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                email.setSelector(emailSelectorIntent);

                startActivity(email);
            }

        });

        btnHelp = (Button) view.findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSendEmail();

            }

            private void getSendEmail() {
                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                emailSelectorIntent.setData(Uri.parse("mailto:"));

                final Intent email = new Intent(Intent.ACTION_SEND);
//                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_EMAIL,  new String[]{getString(R.string.email_developer)});
                email.putExtra(Intent.EXTRA_SUBJECT, (getString(R.string.Notula_help)));
                email.putExtra(Intent.EXTRA_TEXT, (
//                        "Dear Sir/Madam, " +
                        getString(R.string.do_you_need_help) +
                                getString(R.string.please_explain)));


//need this to prompt`enter code here`s email client only
//                email.setType("message/rfc822");
                email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                email.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                email.setSelector(emailSelectorIntent);

                startActivity(email);
            }

        });


    }


}
