package com.orion.pos_crushty_android.ui.penjualan_keranjang;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailModel;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailTable;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterModel;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.globals.Global;

import java.util.ArrayList;
import java.util.List;

public class CustomPesananActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Button btnSimpan;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    //var for adapter/list
    private CustomPesananAdapter mAdapter;
    public List<KeranjangDetailModel> ListItems = new ArrayList<>();

    //var
    private int seqKeranjang;
    private KeranjangDetailTable keranjangDetailTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_pesanan);
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
        btnSimpan = findViewById(R.id.btnSimpan);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        keranjangDetailTable = new KeranjangDetailTable(this);
    }

    private void InitClass() {
        SetJenisTampilan();
        Bundle extra = this.getIntent().getExtras();
        seqKeranjang = extra.getInt("seq");
        LoadData();
    }

    private void EventClass() {
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveForm()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void SetJenisTampilan() {
        // Use LinearLayoutManager with horizontal orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        // Set the adapter and other configurations
        mAdapter = new CustomPesananAdapter(this, ListItems, R.layout.list_item_custom_pesanan);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        mAdapter.removeAllModel();
        mAdapter.addModels(keranjangDetailTable.getRecordsBySeq(seqKeranjang));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private boolean saveForm() {
        for (int i = 0; i < ListItems.size(); i++) {
            keranjangDetailTable.update(ListItems.get(i));
        }
        return true;
    }

}