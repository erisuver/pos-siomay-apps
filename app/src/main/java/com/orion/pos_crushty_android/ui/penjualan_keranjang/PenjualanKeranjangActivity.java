package com.orion.pos_crushty_android.ui.penjualan_keranjang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterModel;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.pembayaran.PembayaranDebitActivity;
import com.orion.pos_crushty_android.ui.pembayaran.PembayaranOVOActivity;
import com.orion.pos_crushty_android.ui.pembayaran.PembayaranQRISActivity;
import com.orion.pos_crushty_android.ui.pembayaran.PembayaranTunaiActivity;

import java.util.ArrayList;
import java.util.List;

public class PenjualanKeranjangActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private ConstraintLayout clBayar;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvTotal, tvNomor;

    //var for adapter/list
    private PenjualanKeranjangAdapter mAdapter;
    public List<KeranjangMasterModel> ListItems = new ArrayList<>();
    private double vTotalPesanan;
    private String tipeBayar = JConst.TIPE_BAYAR_TUNAI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_keranjang);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("Pesanan"); // Set title text
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        clBayar = findViewById(R.id.clBayar);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvTotal = findViewById(R.id.tvTotal);
        tvNomor = findViewById(R.id.tvNomor);
    }

    private void InitClass() {
        tvTotal.setText(Global.FloatToStrFmt(0, true));
        SetJenisTampilan();
        LoadData();

        String nomor = GlobalTable.getNomor(this);
        tvNomor.setText(nomor);
    }

    private void EventClass() {
        clBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ListItems.size() > 0) {
                    showBottomDialogBayar();
                }else{
                    Snackbar.make(view, "Tidak ada item di keranjang.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetJenisTampilan() {
        // Use LinearLayoutManager with horizontal orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        // Set the adapter and other configurations
        mAdapter = new PenjualanKeranjangAdapter(this, ListItems, R.layout.list_item_penjualan_keranjang);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        KeranjangMasterTable keranjangMasterTable = new KeranjangMasterTable(this);
        mAdapter.removeAllModel();
        mAdapter.addModels(keranjangMasterTable.getRecords());
        tvTotal.setText(Global.FloatToStrFmt(keranjangMasterTable.getTotalPesanan(), true));
    }

    public void setTotalPesanan(double totalPesanan){
        tvTotal.setText(Global.FloatToStrFmt(totalPesanan,true));
        vTotalPesanan = totalPesanan;
    }

    private void showBottomDialogBayar() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_pembayaran);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // createview
        ConstraintLayout clTunai = dialog.findViewById(R.id.clTunai);
        ConstraintLayout clDebit = dialog.findViewById(R.id.clTerima);
        ConstraintLayout clQris = dialog.findViewById(R.id.clQris);
        ConstraintLayout clOVO = dialog.findViewById(R.id.clOVO);
        ImageButton btnClose = dialog.findViewById(R.id.btnClose);
        Button btnBayar = dialog.findViewById(R.id.btnBayar);
        TextView tvTotal= dialog.findViewById(R.id.tvTotal);

        //Init
        clTunai.setBackgroundResource(R.drawable.style_button_selected);
        tvTotal.setText(Global.FloatToStrFmt(vTotalPesanan,true));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        clTunai.setOnClickListener(view -> {
            tipeBayar = JConst.TIPE_BAYAR_TUNAI;
            clTunai.setBackgroundResource(R.drawable.style_button_selected);
            clDebit.setBackgroundResource(R.drawable.style_button_unselected);
            clQris.setBackgroundResource(R.drawable.style_button_unselected);
            clOVO.setBackgroundResource(R.drawable.style_button_unselected);
        });

        clDebit.setOnClickListener(view -> {
            tipeBayar = JConst.TIPE_BAYAR_DEBIT;
            clTunai.setBackgroundResource(R.drawable.style_button_unselected);
            clDebit.setBackgroundResource(R.drawable.style_button_selected);
            clQris.setBackgroundResource(R.drawable.style_button_unselected);
            clOVO.setBackgroundResource(R.drawable.style_button_unselected);
        });

        clQris.setOnClickListener(view -> {
            tipeBayar = JConst.TIPE_BAYAR_QRIS;
            clTunai.setBackgroundResource(R.drawable.style_button_unselected);
            clDebit.setBackgroundResource(R.drawable.style_button_unselected);
            clQris.setBackgroundResource(R.drawable.style_button_selected);
            clOVO.setBackgroundResource(R.drawable.style_button_unselected);
        });

        clOVO.setOnClickListener(view -> {
            tipeBayar = JConst.TIPE_BAYAR_OVO;
            clTunai.setBackgroundResource(R.drawable.style_button_unselected);
            clDebit.setBackgroundResource(R.drawable.style_button_unselected);
            clQris.setBackgroundResource(R.drawable.style_button_unselected);
            clOVO.setBackgroundResource(R.drawable.style_button_selected);
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (tipeBayar)
                {
                    case JConst.TIPE_BAYAR_TUNAI:
                        intent = new Intent(PenjualanKeranjangActivity.this, PembayaranTunaiActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("total", vTotalPesanan);
                        startActivity(intent);
                        break;
                    case JConst.TIPE_BAYAR_DEBIT:
                        intent = new Intent(PenjualanKeranjangActivity.this, PembayaranDebitActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("total", vTotalPesanan);
                        startActivity(intent);
                        break;
                    case JConst.TIPE_BAYAR_QRIS:
                        intent = new Intent(PenjualanKeranjangActivity.this, PembayaranQRISActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("total", vTotalPesanan);
                        startActivity(intent);
                        break;
                    case JConst.TIPE_BAYAR_OVO:
                        intent = new Intent(PenjualanKeranjangActivity.this, PembayaranOVOActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("total", vTotalPesanan);
                        startActivity(intent);
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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