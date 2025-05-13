package com.orion.pos_crushty_android.ui.pembayaran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;

public class PembayaranDebitActivity extends AppCompatActivity {

    //var componen/view
    private Button btnLanjut;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvTotal;
    private TextInputEditText txtKeterangan;

    //
    private double totalTagihan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_debit);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CreateView() {
        btnLanjut = findViewById(R.id.btnLanjut);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvTotal = findViewById(R.id.tvTotal);
        txtKeterangan = findViewById(R.id.txtKeterangan);
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        totalTagihan = extra.getDouble("total");
        tvTotal.setText(Global.FloatToStrFmt(totalTagihan,true));

    }

    private void EventClass() {
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save ke tabel penjualan
                String penjualanMstUid = GlobalTable.savePenjualan(getApplicationContext(), JConst.TIPE_BAYAR_DEBIT, totalTagihan, totalTagihan,0, "");

                Intent intent = new Intent(PembayaranDebitActivity.this, PembayaranSuccessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("tipe_bayar", JConst.TIPE_BAYAR_DEBIT);
                intent.putExtra("penjualan_mst_uid", penjualanMstUid);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}