package com.orion.pos_crushty_android.ui.shift_kasir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ShiftPagerAdapter extends FragmentStateAdapter {

    public ShiftPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new RiwayatShiftFragment();
            default:
                return new BerlangsungShiftFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs: Berlangsung and Riwayat
    }
}

