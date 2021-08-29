package com.alrosyid.notula.activities.meetings;

import android.os.Bundle;

import com.alrosyid.notula.R;
import com.alrosyid.notula.fragments.attendances.AttendancesListFragments;
import com.alrosyid.notula.fragments.meetings.DetailMeetingsFragment;
import com.alrosyid.notula.fragments.notulas.NotulasListFragment;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.fragments.record.RecordFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DetailMeetingsActivity extends AppCompatActivity {

    private int meetingsId = 0, position = 0;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_meetings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.meeting_detail);


        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);

    }

    private void setupViewPager(ViewPager viewPager) {
        DetailMeetingsActivity.SectionPagerAdapter adapter = new DetailMeetingsActivity.SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(DetailMeetingsFragment.newInstance(), getString(R.string.meeting));
        adapter.addFragment(NotulasListFragment.newInstance(), getString(R.string.notula));
//        adapter.addFragment(RecordFragment.newInstance(), getString(R.string.Record));
        adapter.addFragment(AttendancesListFragments.newInstance(), getString(R.string.attendances));
        adapter.addFragment(PhotosListFragment.newInstance(), getString(R.string.photos));


        viewPager.setAdapter(adapter);
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SectionPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}