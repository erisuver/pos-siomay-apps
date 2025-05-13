package com.orion.pos_crushty_android.ui.penjualan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualModel;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.calculateWidth;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukAdapter;

import java.util.ArrayList;
import java.util.List;

public class PenjualanFragment extends Fragment {
    //var componen/view
    private View v;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private SearchView txtSearch;
    public TextView tvNoUrut;


    //var for adapter/list
    private PenjualanAdapter mAdapter;
    public List<MasterBarangJualModel> ListItems = new ArrayList<>();

    //var global
    private FragmentActivity thisActivity;
    private int NoOfColumns;
    private LinearLayoutManager linearLayoutManager;
    private String SearchQuery = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_penjualan, container, false);
        v = view;
        JApplication.currentActivity = getActivity();
        CreateView();
        InitClass();
        EventClass();
        LoadData();
        return view;
    }

    private void CreateView() {
        rcvLoad = v.findViewById(R.id.rcvLoad);
        swipe = v.findViewById(R.id.swipe);
        txtSearch = v.findViewById(R.id.txtSearch);
        tvNoUrut = v.findViewById(R.id.tvNoUrut);

        thisActivity = getActivity();
    }

    private void InitClass() {
        SetJenisTampilan();
        String outletUid = SharedPrefsUtils.getStringPreference(getContext(), "outlet_uid");
        String userId = SharedPrefsUtils.getStringPreference(getContext(), "user_id");
        tvNoUrut.setText(GlobalTable.getNomorUrut(outletUid, userId));
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rcvLoad.scrollToPosition(0);
                LoadData();
                swipe.setRefreshing(false);
            }
        });

        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                mAdapter.getFilter().filter(newText);
                if (newText.equals("")) {
                    SearchQuery = "";
                    LoadData();
                }else if (newText.length() >= 2){
                    SearchQuery = newText;
                    LoadData();
                }
                return false;
            }
        });
    }

    public void LoadData() {
        mAdapter.removeAllModel();
        MasterBarangJualTable masterBarangJualTable = new MasterBarangJualTable(thisActivity);
        masterBarangJualTable.setSearchQuery(SearchQuery);
        mAdapter.addModels(masterBarangJualTable.getRecords());
    }

    private void SetJenisTampilan() {
//        NoOfColumns = calculateWidth.calculateNoOfColumns(thisActivity);
//        mAdapter = new PenjualanAdapter(thisActivity, ListItems, this, R.layout.list_item_penjualan_list);
//        rcvLoad.setLayoutManager(new GridLayoutManager(thisActivity, NoOfColumns, GridLayoutManager.VERTICAL, false));
//        linearLayoutManager = (LinearLayoutManager) rcvLoad.getLayoutManager();
//        rcvLoad.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thisActivity);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new PenjualanAdapter(thisActivity, ListItems, this, R.layout.list_item_penjualan_list);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    private void ResetData(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = thisActivity;
        LoadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}