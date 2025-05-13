package com.orion.pos_crushty_android.ui.shift_kasir;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.master_outlet.MasterOutletModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameRekapActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShiftSaldoAwalInputActivity extends AppCompatActivity {
    //var componen/view
    private Button btnSimpan, btnStokOpname;
    private Toolbar toolbar;
    private TextView toolbarTitle, txtStatusSO;
    private TextInputEditText txtSaldoAwal;
    private ProgressDialog loading;

    //var
    double saldoAwal;
    final int REQUEST_STOK_OPNAME = 1;
    boolean isSudahOpname, isStokBersama;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_saldo_awal_input);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }

    private void CreateView() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnStokOpname = findViewById(R.id.btnStokOpname);
        txtSaldoAwal = findViewById(R.id.txtSaldoAwal);
        txtStatusSO = findViewById(R.id.txtStatusSO);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void InitClass() {
        saldoAwal = 0;
        txtSaldoAwal.setText(Global.FloatToStrFmt(saldoAwal));
        isSudahOpname = false;
        isStokBersama = GlobalTable.getIsStokBersama(this);
        txtStatusSO.setVisibility(View.GONE);
        btnStokOpname.setVisibility(View.GONE);
        setStatusOpname();
    }

    private void EventClass() {
        txtSaldoAwal.addTextChangedListener(TextWatcherUtils.formatAmountTextWatcher(txtSaldoAwal));

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSimpan.setEnabled(false);
                if (isSudahOpname) {
                    saveTransaksi();
                }else{
                    Snackbar.make(view, "Anda belum melakukan stok opname.", Snackbar.LENGTH_SHORT).show();
                    btnSimpan.setEnabled(true);
                }
            }
        });

        btnStokOpname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(ShiftSaldoAwalInputActivity.this, StokOpnameInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(s, REQUEST_STOK_OPNAME);  // Use activity context
            }
        });
    }

    public void LoadData() {
        loading.show();

        // URL API
        String url = Routes.url_transaksi() + "shift_master/get";

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
                    saldoAwal = obj.optDouble("sisa_kas_tunai", 0);
                    boolean isShiftDua = obj.optString("is_shift_dua").equals(JConst.TRUE_STRING);
                    //jika stok bersama dan mulai shift ke 2 maka tidak perlu stok opname lagi.
                    if (isShiftDua && isStokBersama){
                        isSudahOpname = true;
                        txtStatusSO.setVisibility(View.GONE);
                        btnStokOpname.setVisibility(View.GONE);
                    }else{
                        isSudahOpname = false;
                        txtStatusSO.setVisibility(View.VISIBLE);
                        btnStokOpname.setVisibility(View.VISIBLE);
                    }

                }else {
                    isSudahOpname = false;
                    txtStatusSO.setVisibility(View.VISIBLE);
                    btnStokOpname.setVisibility(View.VISIBLE);
                }
                // Set default saldo
                txtSaldoAwal.setText(Global.FloatToStrFmt(saldoAwal));
                loading.dismiss();

            } finally {
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
            ApiHelper.handleError(ShiftSaldoAwalInputActivity.this, error, () -> LoadData());
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

    public void saveTransaksi() {
        loading.show();
        String url = Routes.url_transaksi() + "shift_master/save";
        JSONObject requestBody = createShiftMasterRequestBody();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    String uidShiftMaster = dataObject.getString("uid_shift_master");

                    //close
                    loading.dismiss();
                    Intent intent = getIntent();
                    intent.putExtra("uid_shift_master", uidShiftMaster);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                loading.dismiss();
                btnSimpan.setEnabled(true);
            }
        }, error -> {
            ApiHelper.handleError(ShiftSaldoAwalInputActivity.this, error, () -> saveTransaksi());
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
            mstObj.put("uid", UUID.randomUUID().toString());
            mstObj.put("tanggal", Global.serverNowFormatedWithTime()); // Pastikan format tanggal sesuai
            mstObj.put("outlet_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid"));
            mstObj.put("karyawan_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid"));
            mstObj.put("shift_mulai", Global.serverNowFormatedWithTime());
            mstObj.put("shift_akhir", JSONObject.NULL);
            mstObj.put("saldo_awal", Global.StrFmtToFloat(txtSaldoAwal.getText().toString()));
            mstObj.put("tunai_aktual", 0);
            mstObj.put("non_tunai_aktual", 0);
            mstObj.put("pengeluaran_lain", 0);
            mstObj.put("setor_tunai", 0);
            mstObj.put("sisa_kas_tunai", Global.StrFmtToFloat(txtSaldoAwal.getText().toString()));
            mstObj.put("program_tunai", 0);
            mstObj.put("selisih", 0);
            mstObj.put("keterangan", "");
            mstObj.put("user_id", SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id"));
            mstObj.put("tgl_input", Global.serverNowFormatedWithTime());

            // Menambahkan mst ke objek utama
            requestBody.put("shift_master", mstObj);
            return requestBody;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setStatusOpname(){
        if (isSudahOpname){
            txtStatusSO.setText("Anda sudah melakukan stok opname.");
            txtStatusSO.setTextColor(getColor(R.color.green));
            btnStokOpname.setEnabled(false);
        }else{
            txtStatusSO.setText("Anda belum melakukan stok opname.");
            txtStatusSO.setTextColor(getColor(R.color.red));
            btnStokOpname.setEnabled(true);
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