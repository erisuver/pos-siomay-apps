package com.orion.pos_crushty_android.ui.lov;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualModel;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualTable;

import java.util.ArrayList;
import java.util.List;

public class LovBarangJualActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Button btnPrint;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SearchView txtSearch;

    //var for adapter/list
    private LovBarangJualAdapter mAdapter;
    public List<MasterBarangJualModel> ListItems = new ArrayList<>();

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

        mAdapter = new LovBarangJualAdapter(this, ListItems, R.layout.list_item_lov_barang);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        mAdapter.removeAllModel();
        MasterBarangJualTable masterBarangJualTable = new MasterBarangJualTable(this);
        masterBarangJualTable.setSearchQuery(SearchQuery);
        mAdapter.addModels(masterBarangJualTable.getRecords());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_reset:
                Intent intent = getIntent();
                intent.putExtra("barang_jual_uid", "");
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