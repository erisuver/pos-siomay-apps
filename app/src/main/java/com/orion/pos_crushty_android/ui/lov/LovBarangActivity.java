package com.orion.pos_crushty_android.ui.lov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.master_barang.MasterBarangModel;
import com.orion.pos_crushty_android.databases.master_barang.MasterBarangTable;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukAdapter;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukModel;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LovBarangActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Button btnPrint;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SearchView txtSearch;

    //var for adapter/list
    private LovBarangAdapter mAdapter;
    public List<MasterBarangModel> ListItems = new ArrayList<>();

    //var
    private String SearchQuery = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lov_barang);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        txtSearch = findViewById(R.id.txtSearch);
    }

    private void InitClass() {
        SetJenisTampilan();
        LoadData();
    }

    private void EventClass() {
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
                } else if (newText.length() >= 2) {
                    SearchQuery = newText;
                    LoadData();
                }
                return false;
            }
        });
    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new LovBarangAdapter(this, ListItems, R.layout.list_item_lov_barang);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        boolean isMunculNonStok = true;
        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            isMunculNonStok = extra.getBoolean("isMunculNonStok");
        }
        mAdapter.removeAllModel();
        MasterBarangTable masterBarangTable = new MasterBarangTable(this);
        masterBarangTable.setSearchQuery(SearchQuery);
        masterBarangTable.setMunculNonStok(isMunculNonStok);
        mAdapter.addModels(masterBarangTable.getRecords());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_reset:
                Intent intent = getIntent();
                intent.putExtra("barang_uid", "");
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

}