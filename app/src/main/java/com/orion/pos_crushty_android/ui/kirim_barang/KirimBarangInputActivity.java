package com.orion.pos_crushty_android.ui.kirim_barang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.SyncData;
import com.orion.pos_crushty_android.databases.kirim_barang.KirimBarangDetModel;
import com.orion.pos_crushty_android.databases.master_outlet.MasterOutletModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangDetModel;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.ListValue;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangInputAdapter;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.ui.shift_kasir.ShiftSaldoAwalInputActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class KirimBarangInputActivity extends AppCompatActivity {
    private RecyclerView rcvLoad;
    private View v;
    private Activity ThisActivity;
    public KirimBarangInputAdapter mAdapter;
    private TextView tvNomor, toolbarTitle;
    private Button btnSave;
    private AutoCompleteTextView spnOutlet;
    private List<String> listOutletUID;
    private List<String> listOutletString;
    private ArrayAdapter<String> AdapterOutlet;
    public List<KirimBarangDetModel> ListItems = new ArrayList<>();
    private Toolbar toolbar;
    private String mode = "";
    private String uuid;
    final int REQUEST_FILTER_BARANG = 1;
    public int adapterPos;
    private String masterUid, nomor, outletUid, userId, karyawanUid, apiKey, keteranganMst, kodeOutlet ;
    private EditText txtKeterangan;
    private String namaBrg, namaSatuan;
    private String uidBrg, uidSatuan;
    private double konversi;
    ImageButton btnAdd;
    private String outletTujuanUID;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_barang_input);
        JApplication.currentActivity = this;
        Intent intent = getIntent();
        masterUid = intent.getStringExtra("master_uid");
        nomor = intent.getStringExtra("nomor");
        keteranganMst = intent.getStringExtra("keterangan");
        mode = JConst.MODE_ADD;
        if ((masterUid != null) && (!masterUid.equals(""))) {
            mode = JConst.MODE_DETAIL;
        }
        CreateView();
        InitClass();
        EventClass();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("Kirim Barang");
    }
    private void InitClass() {
        this.setTitle("Kirim Barang");
        ThisActivity = this;
        outletUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        userId = SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id");
        kodeOutlet = GlobalTable.getKodeOutlet(getApplicationContext());
        karyawanUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        apiKey = JConst.SKIP_CHECK_API_KEY;// SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key");
        Global.setEnabledClickAutoCompleteText(spnOutlet, false);
        if (mode != JConst.MODE_ADD){
            btnSave.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
            txtKeterangan.setEnabled(false);
        }

        if ((keteranganMst != null) && (!keteranganMst.equals(""))) {
            txtKeterangan.setText(keteranganMst);
        }
        cekSync();//IsiDataOutlet();
        LoadData();
//        nomor = "KRM/1024/001";
        tvNomor.setText(nomor);
        rcvLoad.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        tvNomor = findViewById(R.id.tvNomor);
        toolbar = findViewById(R.id.toolbarinput);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnSave = findViewById(R.id.btnSimpanKrm);
        txtKeterangan = findViewById(R.id.txtKeteranganKrm);
        spnOutlet = findViewById(R.id.spnOutlet);
        btnAdd = findViewById(R.id.btnAddItemKrm);
        this.mAdapter = new KirimBarangInputAdapter(this, ListItems, R.layout.list_item_kirim_barang_input);
        rcvLoad.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        loading = Global.createProgresSpinner(this, getString(R.string.loading));
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
                    IsiDataOutlet();
                }
            });
        } else {
            // Jika tidak ada data yang perlu disinkronkan, lanjutkan langsung
            IsiDataOutlet();
        }
    }

    public void IsiDataOutlet() {
        // Tampilkan dialog loading
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("status_aktif", "T");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL API
        String url = Routes.url_master_outlet() + "/get";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
            response -> {
                ArrayList<MasterOutletModel> itemDataModels = new ArrayList<>();
                JSONArray jsonArray = response.optJSONArray("data");

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if (obj.getInt("seq") > 0) {
                                MasterOutletModel data = new MasterOutletModel(
                                        obj.getString("uid"),
                                        obj.getInt("seq"),
                                        Global.getMillisDateFmt(obj.getString("last_update"), "dd-MM-yyyy hh:mm:ss"),
                                        Global.getMillisDateFmt(obj.getString("disable_date"), "dd-MM-yyyy hh:mm:ss"),
                                        obj.getString("user_id"),
                                        Global.getMillisDateFmt(obj.getString("tgl_input"), "dd-MM-yyyy hh:mm:ss"),
                                        obj.getString("kode"),
                                        obj.getString("nama"),
                                        obj.getString("alamat"),
                                        obj.getString("penanggung_jawab"),
                                        obj.getString("kota"),
                                        obj.getString("keterangan"),
                                        obj.getString("telepon"),
                                        obj.getString("bank_uid"),
                                        obj.getString("kas_uid"),
                                        obj.optInt("versi", 0)
                                );
                                itemDataModels.add(data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Terjadi kesalahan saat parsing data", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }
                }
                listOutletString = ListValue.list_outlet(itemDataModels);
                listOutletUID = ListValue.list_outlet_uid(itemDataModels);
                String[] mStringArrayOutlet = new String[listOutletString.size()];
                mStringArrayOutlet = listOutletString.toArray(mStringArrayOutlet);
                AdapterOutlet = new ArrayAdapter<String>(KirimBarangInputActivity.this, android.R.layout.simple_dropdown_item_1line, mStringArrayOutlet);
                spnOutlet.setAdapter(AdapterOutlet);

                //sementara outlet tujuannya pusat
                if (listOutletUID != null) {
                    for (int i = 0; i < listOutletUID.size(); i++) {
                        if (listOutletUID.get(i).equals(JConst.CG_UID_PUSAT)) {
                            spnOutlet.setSelection(i);
                            spnOutlet.setHint("");
                            spnOutlet.setText(listOutletString.get(i));
                            outletTujuanUID = listOutletUID.get(i);
                            spnOutlet.setEnabled(false);
                            break;
                        }
                    }
                }

                loading.dismiss();
            },
            error -> {
                ApiHelper.handleError(KirimBarangInputActivity.this, error, () -> IsiDataOutlet());
                loading.dismiss();
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", apiKey);
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }
    private void LoadData() {
        if (!Objects.equals(mode, JConst.MODE_ADD)) {
            JSONObject params = new JSONObject();
            try {
                params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
                params.put("master_uid", masterUid);
//                params.put("outlet_asal_uid", outletUid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Routes.url_transaksi() + "kirim_barang_det/get";//" Filter;
            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        List<KirimBarangDetModel> itemDataModels = new ArrayList<>();
                        itemDataModels.clear();
                        JSONArray jsonArray = response.optJSONArray("data");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    if (obj.optInt("seq",0) > 0) {
                                        KirimBarangDetModel Data = new KirimBarangDetModel(
                                                obj.getString("uid"),
                                                obj.getInt("seq"),
                                                obj.getString("master_uid"),
                                                obj.getString("barang_uid"),
                                                obj.getString("satuan_uid"),
                                                obj.getString("keterangan"),
                                                obj.getDouble("qty"),
                                                obj.getDouble("konversi"),
                                                obj.getDouble("qty_primer"),
                                                obj.getString("kode_barang"),
                                                obj.getString("nama_barang"),
                                                obj.getString("satuan_primer_uid"),
                                                obj.getString("satuan_sekunder_uid"),
                                                obj.getString("nama_satuan"),
                                                obj.getString("nama_sat_primer"),
                                                obj.getString("nama_sat_sekunder"),
                                                obj.getDouble("konversi_primer"),
                                                obj.getDouble("konversi_sekunder"),
                                                obj.getString("is_barang_stok")
                                        );
                                        itemDataModels.add(Data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                    loading.dismiss();
                                }
                            }
                        }
                        ListItems = itemDataModels;
                        mAdapter.removeAllModel();
                        mAdapter.addModels(ListItems);
                        loading.dismiss();
                    },
                    error -> {
                        ApiHelper.handleError(KirimBarangInputActivity.this, error, () -> LoadData());
                        loading.dismiss();
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Api-Key", apiKey);
                    return headers;
                }
            };
            JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
        }else {
            get_nomor();
        }
        KirimBarangDetModel data = new KirimBarangDetModel();
        mAdapter.addModel(data);
    }

    public void get_nomor() {
        JSONObject params = new JSONObject();
        try {
            String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
            params.put("tanggal", tanggal);
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("tipe_trans", JConst.TRANS_KIRIM_BARANG);
            params.put("field_outlet", "outlet_asal_uid");
            params.put("outlet_uid", outletUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Routes.URL_API_AWAL + "global/next_nomor/get";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                int no_urut = obj.optInt("nomor", 0)  + 1; // Get the nomor from the JSON

                                // Check if nomor is not empty
                                if (no_urut != 0) {
                                    // Get the current month and year
                                    Calendar calendar = Calendar.getInstance();
                                    int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed
                                    int year = calendar.get(Calendar.YEAR) % 100; // Get last 2 digits of the year

                                    // Format month and year
                                    String formattedMonth = String.format("%02d", month); // Format month to 2 digits
                                    String formattedYear = String.format("%02d", year); // Format year to 2 digits

                                    // Format the nomor to 3 digits
                                    String formattedNomor = String.format("%03d", no_urut);

                                    // Create the final vNomor string
                                    nomor = JConst.TRANS_KIRIM_BARANG+"/" + formattedMonth + formattedYear + "/" + formattedNomor+"-"+kodeOutlet;

                                    tvNomor.setText(nomor);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    }
                    loading.dismiss();
                },
                error -> {
                    ApiHelper.handleError(KirimBarangInputActivity.this, error, () -> get_nomor());
                    loading.dismiss();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key"));
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    // Handle result if needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
        if (requestCode == REQUEST_FILTER_BARANG && resultCode == RESULT_OK) {
            // Handle the result from LovBarangActivity
            if (data!=null) {
                Bundle extra = data.getExtras();
                namaBrg = extra.getString("barang_nama");
                uidBrg = extra.getString("barang_uid");
                konversi = extra.getDouble("konversi_primer");
                uidSatuan = extra.getString("satuan_primer_uid");
                namaSatuan = extra.getString("nama_satuan_primer");
//
//                double qtytemp = mAdapter.Datas.get(adapterPos).getQty();
//
//                mAdapter.Datas.get(adapterPos).setBarangUid(uidBrg);
//                mAdapter.Datas.get(adapterPos).setSatuanUid(uidSatuan);
//                mAdapter.Datas.get(adapterPos).setNamaBarang(namaBrg);
//                mAdapter.Datas.get(adapterPos).setNamaSatuan(namaSatuan);
//                mAdapter.Datas.get(adapterPos).setKonversi(konversi);
//                mAdapter.Datas.get(adapterPos).setQtyPrimer(konversi*qtytemp);
//                mAdapter.notifyItemChanged(adapterPos);

                // Pastikan Anda memodifikasi hanya item pada posisi adapterPos
                KirimBarangDetModel currentItem = mAdapter.Datas.get(adapterPos);

                double qtytemp = currentItem.getQty();  // Ambil nilai qty dari model

                currentItem.setBarangUid(uidBrg);
                currentItem.setSatuanUid(uidSatuan);
                currentItem.setNamaBarang(namaBrg);
                currentItem.setNamaSatuan(namaSatuan);
                currentItem.setKonversi(konversi);
                currentItem.setQtyPrimer(konversi * qtytemp);

                mAdapter.notifyItemChanged(adapterPos);
            }
        }
    }

    private void EventClass() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave.setEnabled(false);
                if (ListItems.size() > 0) {
                    if (isValid()) {
                        Runnable runPositive = new Runnable() {
                            @Override
                            public void run() {
                                save();
                            }
                        };
                        Runnable runNegative = new Runnable() {
                            @Override
                            public void run() {
                                btnSave.setEnabled(true);
                            }
                        };
                        ShowDialog.confirmDialog(ThisActivity, "Konfirmasi", "Apakah data sudah benar?", "Ya", "Tidak", runPositive, runNegative);
                    }else{
                        btnSave.setEnabled(true);
                    }
                } else {
                    btnSave.setEnabled(true);
                    Snackbar.make(view, "Tidak ada data di input.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KirimBarangDetModel data = new KirimBarangDetModel();
                mAdapter.addModel(data);
                adapterPos = mAdapter.getItemCount() - 1;
                mAdapter.notifyItemInserted(adapterPos);
                rcvLoad.scrollToPosition(adapterPos); // Auto-scroll to the last item

            }
        });

        spnOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnOutlet.showDropDown();
            }
        });

        spnOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                outletTujuanUID = listOutletUID.get(position);
            }
        });

    }
    private boolean isValid(){
        int i = 0; // Mulai dari 0
        while (i < ListItems.size()) {
            if (ListItems.get(i).getBarangUid().equals("")) {
                if (ListItems.size() > 1) {
                    mAdapter.removeModel(i);  // Menghapus item
                    // Tidak perlu menambah 'i' di sini karena ukuran list berkurang
                } else {
                    i++;  // Hanya tambahkan 'i' jika tidak ada item yang dihapus
                }
            } else {
                i++;  // Hanya tambahkan 'i' jika tidak ada item yang dihapus
            }
        }
        for (i = 0; i < ListItems.size(); i++) {
            if (ListItems.get(i).getBarangUid().equals("")){
                Snackbar.make(findViewById(android.R.id.content), "Barang pada baris " + (i + 1) + " belum dipilih.", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if (ListItems.get(i).getQty() == 0){
                Snackbar.make(findViewById(android.R.id.content), "Qty pada baris " + (i + 1) + " belum diisi.", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            for (int j = i+1; j < ListItems.size(); j++) {
                if (ListItems.get(i).getBarangUid().equals(ListItems.get(j).getBarangUid())){
                    Snackbar.make(findViewById(android.R.id.content), "Barang pada baris " + (j + 1) + " sama dengan baris " + (i+1) + ".", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }
    private boolean save() {
        String url = Routes.url_transaksi() + "kirim_barang_mst/save";
        JSONObject params = CreateJsonKirimBarang();

        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    // Mengakses objek "data" dari response tanpa try-catch JSONException
                    JSONObject dataObject = response.optJSONObject("data");
                    if (dataObject != null) {
                        // Ambil uid dari objek data
                        uuid = dataObject.optString("uid_kirim_barang_mst", "");
//                        Intent s = getIntent();
//                        s.putExtra("uuid",uuid);
                        setResult(RESULT_OK);
                        finish();  // Close the current activity after successful save
                        btnSave.setEnabled(true);
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                    }
                },
                error -> {
                    ApiHelper.handleError(KirimBarangInputActivity.this, error, () -> save());
                    btnSave.setEnabled(true);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", apiKey);
                return headers;
            }
        };

        // Menambahkan request ke request queue
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
    private JSONObject CreateJsonKirimBarang(){
        try {

            // Membuat objek utama
            JSONObject requestBody = new JSONObject();
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            String tanggalTrans = Global.serverNowFormatedWithTime();

            JSONObject mstObj = new JSONObject();
            get_nomor();
            // Tambahkan field-field lainnya ke mstObj
            mstObj.put("uid", "");
            mstObj.put("tanggal", tanggalTrans);
            mstObj.put("nomor", nomor);
            mstObj.put("outlet_asal_uid", outletUid);
            mstObj.put("outlet_tujuan_uid", outletTujuanUID);
            mstObj.put("karyawan_uid", karyawanUid);
            mstObj.put("keterangan", txtKeterangan.getText().toString().trim());
            mstObj.put("user_id", userId);

            requestBody.put("kirim_barang_mst", mstObj);

            JSONArray detArray = new JSONArray();
            JSONArray penambahPengurangArray = new JSONArray();
            for (KirimBarangDetModel model : ListItems) {
                JSONObject detObj = new JSONObject();

                detObj.put("uid", "");
                detObj.put("seq", 0);
                detObj.put("master_uid", "");
                detObj.put("barang_uid", model.getBarangUid());
                detObj.put("satuan_uid", model.getSatuanUid());
                detObj.put("keterangan", model.getKeterangan().trim());
                detObj.put("qty", model.getQty());
                detObj.put("konversi", model.getKonversi());
                detObj.put("qty_primer", model.getQtyPrimer());

                detArray.put(detObj);
                if (!outletUid.equals(JConst.CG_UID_PUSAT)) {
                    JSONObject penambahPengurangObj = new JSONObject();

                    penambahPengurangObj.put("tanggal", tanggalTrans);
                    penambahPengurangObj.put("nomor", nomor);
                    penambahPengurangObj.put("trans_uid", "");
                    penambahPengurangObj.put("tipe_trans", JConst.TRANS_KIRIM_BARANG);
                    penambahPengurangObj.put("outlet_uid", outletUid);
                    penambahPengurangObj.put("barang_uid", model.getBarangUid());
                    penambahPengurangObj.put("qty_primer", -model.getQtyPrimer());
                    penambahPengurangObj.put("keterangan", model.getKeterangan().trim());
                    penambahPengurangObj.put("user_id", userId);

                    // Menambahkan objek ke array
                    penambahPengurangArray.put(penambahPengurangObj);
                }
            }
            requestBody.put("kirim_barang_det", detArray);
            if (!outletUid.equals(JConst.CG_UID_PUSAT)) {
                requestBody.put("penambah_pengurang_barang", penambahPengurangArray);
            }
            // Loop melalui setiap model dalam listModel
            // Mengembalikan request body
            return requestBody;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
