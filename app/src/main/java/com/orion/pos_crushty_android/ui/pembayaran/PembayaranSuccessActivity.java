package com.orion.pos_crushty_android.ui.pembayaran;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukActivity;

public class PembayaranSuccessActivity extends AppCompatActivity {
    //var componen/view
    private Button btnCetak, btnClose;
    private TextView tvTipeBayar;

    //var glob
    private String tipeBayar = "";
    private String penjualanMstUid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_success);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        btnCetak = findViewById(R.id.btnCetak);
        btnClose = findViewById(R.id.btnClose);
        tvTipeBayar = findViewById(R.id.tvTipeBayar);
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        tipeBayar = extra.getString("tipe_bayar");
        penjualanMstUid = extra.getString("penjualan_mst_uid");

        if (tipeBayar.equals(JConst.TIPE_BAYAR_TUNAI)) {
            tvTipeBayar.setText(JConst.TIPE_BAYAR_TUNAI_TEXT);
        }else if (tipeBayar.equals(JConst.TIPE_BAYAR_DEBIT)) {
            tvTipeBayar.setText(JConst.TIPE_BAYAR_DEBIT_TEXT);
        }else if (tipeBayar.equals(JConst.TIPE_BAYAR_QRIS)) {
            tvTipeBayar.setText(JConst.TIPE_BAYAR_QRIS_TEXT);
        }

    }

    private void EventClass() {
        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembayaranSuccessActivity.this, CetakStrukActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("tipe_bayar", tipeBayar);
                intent.putExtra("penjualan_mst_uid", penjualanMstUid);
                startActivityForResult(intent, JConst.REQUEST_CETAK_STRUK);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PembayaranSuccessActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("do_sync", false);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //noting
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
        if (resultCode == RESULT_OK) {
            if (requestCode == JConst.REQUEST_CETAK_STRUK) {
                btnCetak.setVisibility(View.GONE);
            }
        }
    }
}