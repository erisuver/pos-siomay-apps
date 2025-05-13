package com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments.PengeluaranLainKasFragment;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments.PengeluaranLainBarangFragment;

/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment for the given tab position.
        switch (position) {
            case 0:
                return new PengeluaranLainKasFragment();  // First tab for "Kas"
            case 1:
                return new PengeluaranLainBarangFragment();  // Second tab for "Barang"
            default:
                return new PengeluaranLainKasFragment();  // Default case should not happen.
        }
    }

    @Override
    public int getItemCount() {
        // Show 2 total pages.
        return 2;
    }

    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
}
