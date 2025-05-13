package com.orion.pos_crushty_android.ui.pembayaran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.utility.CustomKeyboardDialog;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

public class PembayaranTunaiActivity extends AppCompatActivity {
    //var componen/view
    private Button btnLanjut, btnUangPas;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvTotal;
    private TextInputEditText txtUangDiterima, txtUangKembalian;
    private TextInputLayout layoutUangDiterima;

    //
    private double totalTagihan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_tunai);
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
        btnUangPas = findViewById(R.id.btnUangPas);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvTotal = findViewById(R.id.tvTotal);
        txtUangDiterima = findViewById(R.id.txtUangDiterima);
        txtUangKembalian = findViewById(R.id.txtUangKembalian);
        layoutUangDiterima = findViewById(R.id.layoutUangDiterima);
    }

    private void InitClass() {
        Global.setEnabledTextInputEditText(txtUangKembalian, false);
        Bundle extra = this.getIntent().getExtras();
        totalTagihan = extra.getDouble("total");
        tvTotal.setText(Global.FloatToStrFmt(totalTagihan,true));
        txtUangDiterima.setShowSoftInputOnFocus(false);

    }

    private void EventClass() {
        txtUangDiterima.setShowSoftInputOnFocus(false); //biar keyboard bawaan gak muncul
        txtUangDiterima.setOnClickListener(v -> {
            if (CustomKeyboardDialog.getInstance(txtUangDiterima).isAdded()) return;
            CustomKeyboardDialog.getInstance(txtUangDiterima).show(getSupportFragmentManager(), "custom_keyboard");
        });

        txtUangDiterima.addTextChangedListener(TextWatcherUtils.formatAmountTextWatcher(txtUangDiterima, new TextWatcherUtils.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable editable) {
                // Implementasi khusus di sini
                double uangDiterima = Global.StrFmtToFloat(editable.toString());
                uangDiterima = uangDiterima - totalTagihan;
                txtUangKembalian.setText(Global.FloatToStrFmt(uangDiterima));
            }
        }));

        btnUangPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUangDiterima.setText(Global.FloatToStrFmt(totalTagihan,false));
                txtUangKembalian.setText(Global.FloatToStrFmt(0,false));
            }
        });

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Double.parseDouble(txtUangDiterima.getText().toString()) == 0){
                    layoutUangDiterima.setError("Uang Diterima belum diisi.");
                    return;
                }else{
                    layoutUangDiterima.setError(null);
                }
                if (Double.parseDouble(txtUangKembalian.getText().toString()) < 0){
                    layoutUangDiterima.setError("Uang Diterima kurang dari total tagihan.");
                    return;
                }else{
                    layoutUangDiterima.setError(null);
                }
                //save ke tabel penjualan
                String penjualanMstUid = GlobalTable.savePenjualan(getApplicationContext(), JConst.TIPE_BAYAR_TUNAI, totalTagihan, Global.StrFmtToFloat(txtUangDiterima.getText().toString()), Global.StrFmtToFloat(txtUangKembalian.getText().toString()), "");
                //munculkan success
                Intent intent = new Intent(PembayaranTunaiActivity.this, PembayaranSuccessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("tipe_bayar", JConst.TIPE_BAYAR_TUNAI);
                intent.putExtra("penjualan_mst_uid", penjualanMstUid);
                startActivity(intent);
            }
        });

    }
/*

    private void setupKeyboardButtons() {
        int[] buttonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_00, R.id.btn_000, R.id.btn_dot, R.id.btn_comma
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(v -> {
                String currentText = txtUangDiterima.getText().toString();
                String buttonText = button.getText().toString();
                txtUangDiterima.setText(currentText + buttonText);
                txtUangDiterima.setSelection(txtUangDiterima.getText().length()); // Pindah cursor ke akhir
            });
        }

        Button btnBackspace = findViewById(R.id.btn_backspace);
        btnBackspace.setOnClickListener(v -> {
            String currentText = txtUangDiterima.getText().toString();
            if (!currentText.isEmpty()) {
                txtUangDiterima.setText(currentText.substring(0, currentText.length() - 1));
                txtUangDiterima.setSelection(txtUangDiterima.getText().length());
            }
        });

        Button btnOK = findViewById(R.id.btn_done);
        btnOK.setOnClickListener(v -> {
            txtUangDiterima.clearFocus();
            customKeyboard.setVisibility(View.GONE);
        });
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

}