package com.orion.pos_crushty_android.ui.shift_kasir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.Global;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShiftKasirFragment extends Fragment {
    private View v;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TextView tvToday;

    //var global
    private FragmentActivity thisActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shift_kasir, container, false);
        v = view;
        JApplication.currentActivity = getActivity();
        CreateView();
        InitClass();
        EventClass();

        return view;
    }

    private void CreateView() {
        tabLayout = v.findViewById(R.id.tabLayout);
        viewPager2 = v.findViewById(R.id.viewPager2);
        tvToday = v.findViewById(R.id.tvToday);
    }

    private void InitClass() {
        //initate judul
        tabLayout.addTab(tabLayout.newTab().setText("Berlangsung"));
        tabLayout.addTab(tabLayout.newTab().setText("Riwayat"));
        // Set up the ViewPager2 with the SectionsPagerAdapter
        ShiftPagerAdapter adapter = new ShiftPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String formattedDate = sdf.format(new Date());
        tvToday.setText(formattedDate);
    }

    private void EventClass() {
        // Handle tab clicks
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // When a tab is selected, switch to the appropriate page
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Handle page swipes
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // When a page is selected by swiping, select the appropriate tab
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}


