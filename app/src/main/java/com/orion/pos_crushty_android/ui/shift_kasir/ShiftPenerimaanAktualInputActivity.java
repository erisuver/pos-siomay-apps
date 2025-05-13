package com.orion.pos_crushty_android.ui.shift_kasir;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.SyncData;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.databases.shift_master.ShiftMasterModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShiftPenerimaanAktualInputActivity extends AppCompatActivity {
    //var componen/view
    private Button btnSimpan, btnStokOpname;
    private Toolbar toolbar;
    private TextView toolbarTitle, txtStatusSO;
    private TextInputEditText txtSaldoAwal, txtPenerimaanTunai, txtPenerimaanNonTunai, txtPengeluaranLain,
            txtSetorTunai, txtSisaKasTunai, txtPenerimaanOVO;
    private ProgressDialog loading;

    //var
    private ShiftMasterModel dataLoad;
    private double saldoAwal, penerimaanTunai, penerimaanNonTunai, pengeluaranLain, setorTunai, sisaKasTunai, penerimaanOVO;
    private String uidShiftMaster;
    final int REQUEST_STOK_OPNAME = 1;
    boolean isSudahOpname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_penerimaan_aktual_input);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        cekSync();//LoadData();
    }

    private void CreateView() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnSimpan = findViewById(R.id.btnSimpan);
        txtSaldoAwal = findViewById(R.id.txtSaldoAwal);
        txtPenerimaanTunai = findViewById(R.id.txtPenerimaanTunai);
        txtPenerimaanNonTunai = findViewById(R.id.txtPenerimaanNonTunai);
        txtPenerimaanOVO = findViewById(R.id.txtPenerimaanOVO);
        txtPengeluaranLain = findViewById(R.id.txtPengeluaranLain);
        txtSetorTunai = findViewById(R.id.txtSetorTunai);
        txtSisaKasTunai = findViewById(R.id.txtSisaKasTunai);
        txtStatusSO = findViewById(R.id.txtStatusSO);
        btnStokOpname = findViewById(R.id.btnStokOpname);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void InitClass() {
        saldoAwal = 0;
        penerimaanTunai = 0;
        penerimaanNonTunai = 0;
        penerimaanOVO = 0;
        pengeluaranLain = 0;
        setorTunai = 0;
        sisaKasTunai = 0;
        setValueText();
        setEnabledDisabledComponent();
        Bundle extra = this.getIntent().getExtras();
        uidShiftMaster = extra.getString("uid_shift_master");
        isSudahOpname = false;
        setStatusOpname();
    }

    private void EventClass() {
        txtPenerimaanTunai.addTextChangedListener(TextWatcherUtils.formatAmountTextWatcher(txtPenerimaanTunai, new TextWatcherUtils.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable editable) {
                // Implementasi khusus di sini
                penerimaanTunai = Global.StrFmtToFloat(editable.toString());
                sisaKasTunai = saldoAwal + penerimaanTunai - setorTunai - pengeluaranLain;
                txtSisaKasTunai.setText(Global.FloatToStrFmt(sisaKasTunai));
            }
        }));

        txtSetorTunai.addTextChangedListener(TextWatcherUtils.formatAmountTextWatcher(txtSetorTunai, new TextWatcherUtils.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable editable) {
                // Implementasi khusus di sini
                setorTunai = Global.StrFmtToFloat(editable.toString());
                sisaKasTunai = saldoAwal + penerimaanTunai - setorTunai - pengeluaranLain;
                txtSisaKasTunai.setText(Global.FloatToStrFmt(sisaKasTunai));
            }
        }));

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lakukan update
                btnSimpan.setEnabled(false);
                if (isValid()) {
                    updateTransaksi();
                    btnSimpan.setEnabled(true);
                }else{
                    btnSimpan.setEnabled(true);
                }
            }
        });

        btnStokOpname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(ShiftPenerimaanAktualInputActivity.this, StokOpnameInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(s, REQUEST_STOK_OPNAME);  // Use activity context
            }
        });
    }

    private void setStatusOpname() {
        if (isSudahOpname) {
            txtStatusSO.setText("Anda sudah melakukan stok opname.");
            txtStatusSO.setTextColor(getColor(R.color.green));
            btnStokOpname.setEnabled(false);
        } else {
            txtStatusSO.setText("Anda belum melakukan stok opname.");
            txtStatusSO.setTextColor(getColor(R.color.red));
            btnStokOpname.setEnabled(true);
        }
    }

    private void setEnabledDisabledComponent() {
        Global.setEnabledTextInputEditText(txtSaldoAwal, false);
        Global.setEnabledTextInputEditText(txtPenerimaanTunai, true);
        Global.setEnabledTextInputEditText(txtPenerimaanNonTunai, false);
        Global.setEnabledTextInputEditText(txtPenerimaanOVO, false);
        Global.setEnabledTextInputEditText(txtPengeluaranLain, false);
        Global.setEnabledTextInputEditText(txtSetorTunai, true);
        Global.setEnabledTextInputEditText(txtSisaKasTunai, false);
    }

    private void setValueText() {
        txtSaldoAwal.setText(Global.FloatToStrFmt(saldoAwal));
        txtPenerimaanTunai.setText(Global.FloatToStrFmt(penerimaanTunai));
        txtPenerimaanNonTunai.setText(Global.FloatToStrFmt(penerimaanNonTunai));
        txtPenerimaanOVO.setText(Global.FloatToStrFmt(penerimaanOVO));
        txtPengeluaranLain.setText(Global.FloatToStrFmt(pengeluaranLain));
        txtSetorTunai.setText(Global.FloatToStrFmt(setorTunai));
        txtSisaKasTunai.setText(Global.FloatToStrFmt(sisaKasTunai));
    }

    public void cekSync() {
        loading.show();
        int penjualanBelumSync = new PenjualanMstTable(this).getCountPenjualanBelumSync();
        int pengeluaranLainBelumSync = new PengeluaranLainMstTable(this).getCountPengeluaranLainBelumSync();

        if (penjualanBelumSync > 0 || pengeluaranLainBelumSync > 0) {
            new SyncData(this).SyncAll(new SyncData.SyncCallback() {
                @Override
                public void onSyncComplete() {
                    // Lanjutkan memuat data setelah sinkronisasi selesai
                    LoadData();
                }
            });
        } else {
            // Jika tidak ada data yang perlu disinkronkan, lanjutkan langsung
            LoadData();
        }
    }

    public void LoadData() {
        // URL API
        String url = Routes.url_transaksi() + "shift_master/get_end_shift";

        // Membuat request body dalam bentuk JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid"));
            requestBody.put("is_max_tanggal", "T");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat request dengan JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                JSONArray jsonArray = response.optJSONArray("data");
                if (jsonArray != null) {
                    JSONObject obj = jsonArray.optJSONObject(0);
                    dataLoad = new ShiftMasterModel(
                            obj.getString("uid"),
                            obj.getString("tanggal"),
                            obj.getString("outlet_uid"),
                            obj.getString("karyawan_uid"),
                            obj.getString("shift_mulai"),
                            obj.getString("shift_akhir"),
                            obj.getDouble("saldo_awal"),
                            obj.getDouble("tunai_aktual"),
                            obj.getDouble("non_tunai_aktual"),
                            obj.getDouble("pengeluaran_lain"),
                            obj.getDouble("setor_tunai"),
                            obj.getDouble("sisa_kas_tunai"),
                            obj.getDouble("program_tunai"),
                            obj.getDouble("selisih"),
                            obj.getString("keterangan"),
                            obj.getString("user_id"),
                            obj.getString("tgl_input")
                    );
                    dataLoad.setTunaiSistem(obj.getDouble("tunai"));
                    dataLoad.setNonTunaiSistem(obj.getDouble("nontunai"));
                    dataLoad.setPengeluaranLainSistem(obj.getDouble("pengeluaranlain"));
                    dataLoad.setOvoSistem(obj.getDouble("ovo"));
                }
                isiData();
                loading.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            } finally {
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
            ApiHelper.handleError(ShiftPenerimaanAktualInputActivity.this, error, () -> LoadData());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // contoh header
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key")); // skip
                return headers;
            }
        };

        // Tambahkan request ke request queue
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private void isiData() {
        saldoAwal = dataLoad.getSaldoAwal();
        penerimaanTunai = dataLoad.getTunaiSistem();
        penerimaanNonTunai = dataLoad.getNonTunaiSistem();
        penerimaanOVO = dataLoad.getOvoSistem();
        pengeluaranLain = dataLoad.getPengeluaranLainSistem();
        setorTunai = dataLoad.getSetorTunai();
        sisaKasTunai = saldoAwal + penerimaanTunai - setorTunai - pengeluaranLain;

        txtSaldoAwal.setText(Global.FloatToStrFmt(saldoAwal));
        txtPenerimaanTunai.setText(Global.FloatToStrFmt(penerimaanTunai));
        txtPenerimaanNonTunai.setText(Global.FloatToStrFmt(penerimaanNonTunai));
        txtPenerimaanOVO.setText(Global.FloatToStrFmt(penerimaanOVO));
        txtPengeluaranLain.setText(Global.FloatToStrFmt(pengeluaranLain));
        txtSetorTunai.setText(Global.FloatToStrFmt(setorTunai));
        txtSisaKasTunai.setText(Global.FloatToStrFmt(sisaKasTunai));
    }

    private boolean isValid() {
        if (Global.StrFmtToFloat(txtSisaKasTunai.getText().toString()) < 0) {
            Snackbar.make(findViewById(android.R.id.content), "Sisa kas tidak boleh minus.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!isSudahOpname) {
            Snackbar.make(findViewById(android.R.id.content), "Anda belum melakukan stok opname.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void updateTransaksi() {
        loading.show();
        String url = Routes.url_transaksi() + "shift_master/update";
        JSONObject requestBody = createShiftMasterRequestBody();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    int statusCode = response.getInt("statusCode");
                    if (statusCode == 200) {
                        //close
                        loading.dismiss();
                        Intent intent = getIntent();
                        intent.putExtra("uid_shift_master", uidShiftMaster);
//                        setResult(RESULT_OK, intent);
//                        finish();
                        ShowDialog.infoDialog(ShiftPenerimaanAktualInputActivity.this,
                                "Informasi", "Shift sudah berakhir. Anda akan logout otomatis.", ()->{
                                    Global.clearDatabase();
                                    JApplication.isSyncAwal = true;
                                    JApplication.isAutoLogin = false;
                                    SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_logged_in", false);
                                    startActivity(new Intent(ShiftPenerimaanAktualInputActivity.this, LoginActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                    ApiHelper.deleteUserLogon(ShiftPenerimaanAktualInputActivity.this);
                                    SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_sudah_print_z", false);
                                });

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                loading.dismiss();
                btnSimpan.setEnabled(true);
            }
        }, error -> {
            ApiHelper.handleError(ShiftPenerimaanAktualInputActivity.this, error, () -> updateTransaksi());
            loading.dismiss();
            btnSimpan.setEnabled(true);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // contoh header
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key")); // skip
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    public JSONObject createShiftMasterRequestBody() {
        try {
            // Membuat objek utama
            JSONObject requestBody = new JSONObject();
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);

            // Membuat objek MASTER
            JSONObject mstObj = new JSONObject();
            // Mengisi objek dengan data dari model
            mstObj.put("uid", dataLoad.getUid());
            mstObj.put("tanggal", dataLoad.getTanggal()); // Pastikan format tanggal sesuai
            mstObj.put("outlet_uid", dataLoad.getOutletUid());
            mstObj.put("karyawan_uid", dataLoad.getKaryawanUid());
            mstObj.put("shift_mulai", dataLoad.getShiftMulai());
            mstObj.put("shift_akhir", Global.serverNowFormatedWithTime());
            mstObj.put("saldo_awal", saldoAwal);
            mstObj.put("tunai_aktual", penerimaanTunai);
            mstObj.put("non_tunai_aktual", penerimaanNonTunai);
            mstObj.put("pengeluaran_lain", pengeluaranLain);
            mstObj.put("setor_tunai", setorTunai);
            mstObj.put("sisa_kas_tunai", sisaKasTunai);
            mstObj.put("program_tunai", dataLoad.getTunaiSistem());
            mstObj.put("selisih", dataLoad.getTunaiSistem() - penerimaanTunai);
            mstObj.put("keterangan", "");
            mstObj.put("user_id", SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id"));
            mstObj.put("tgl_input", dataLoad.getTglInput());
            mstObj.put("ovo", penerimaanOVO);

            // Menambahkan mst ke objek utama
            requestBody.put("shift_master", mstObj);
            return requestBody;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_STOK_OPNAME) {
                if (data != null) {
                    Bundle extra = data.getExtras();
                    isSudahOpname = !extra.getString("uuid").isEmpty();
                    setStatusOpname();
                }
            }
        }
    }

}