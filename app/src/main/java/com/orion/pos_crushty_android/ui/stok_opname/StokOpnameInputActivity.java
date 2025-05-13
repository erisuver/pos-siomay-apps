package com.orion.pos_crushty_android.ui.stok_opname;

import static android.view.View.GONE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.SyncData;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.databases.stok_opname_det.StokOpnameDetModel;
import com.orion.pos_crushty_android.databases.stok_opname_det.StokOpnameDirtyTable;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.shift_kasir.ShiftPenerimaanAktualInputActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class StokOpnameInputActivity extends AppCompatActivity {
    private RecyclerView rcvLoad;
    private View v;
    private SwipeRefreshLayout swipe;
    private Activity ThisActivity;
    public StokOpnameInputAdapter mAdapter;
    private TextView txtNomor, toolbarTitle;
    private Button btnSave;
    public List<StokOpnameDetModel> ListItems = new ArrayList<>();
    private Toolbar toolbar;
    private String mode = "";
    private String uuid;
    final int REQUEST_FILTER_BARANG = 1;
    public int adapterPos;
    private String master_uid, nomor, outletUid, userId, is_detail, kodeOutlet;
    private EditText txtKeterangan;
    private String namaBrg, satuan;
    private String uidBrg, uidSatuan;
    private double stokProgram;
    ImageButton btnAdd;
    private boolean isDirty;    //flag untuk menunjukan udah ngisi data
    private StokOpnameDirtyTable stokOpnameDirtyTable;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_opname_input);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(" Input Stok Opname"); // Set title text

        btnAdd.setVisibility(GONE);
    }

    private void InitClass() {
        this.setTitle("Stok Opname Input");
        ThisActivity = this;
        outletUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        kodeOutlet = GlobalTable.getKodeOutlet(getApplicationContext());
        userId = SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id");
        nomor = ""; //Sementara dikosongkan
        master_uid = "";
        is_detail = "F";
        txtNomor.setText(nomor);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            master_uid = extra.getString("uid");
            outletUid = extra.getString("outlet_uid");
            is_detail = extra.getString("is_detail");
            nomor = extra.getString("nomor");
            txtNomor.setText(nomor);
            txtKeterangan.setText(extra.getString("keterangan"));
            txtKeterangan.setEnabled(false);
            btnSave.setVisibility(GONE);
            btnAdd.setVisibility(GONE);
        }
        LoadData();
        cekSync();
        isDirty = SharedPrefsUtils.getBooleanPreference(getApplicationContext(), "is_dirty_so", false);

        rcvLoad.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        txtNomor = findViewById(R.id.txtNomorSO);
        swipe = findViewById(R.id.swipe);
        toolbar = findViewById(R.id.toolbarinput);
        toolbarTitle = findViewById(R.id.toolbar_title_input);
        btnSave = findViewById(R.id.btnSimpanSO);
        txtKeterangan = findViewById(R.id.txtKeteranganSO);
        btnAdd = findViewById(R.id.btnAddItemSO);

        this.mAdapter = new StokOpnameInputAdapter(this, ListItems, R.layout.list_item_stok_opname_input);
        rcvLoad.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        stokOpnameDirtyTable = new StokOpnameDirtyTable(this);
        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void LoadAllBarang(){
        if (master_uid.equals("")) {
            if (isDirty){
                Runnable runLoadBarang = () -> LoadDataBarang();
                Runnable runLoadDirty = () -> LoadDirty();

                ShowDialog.confirmDialog(this, "Informasi", "Ada beberapa data yang sudah diinput. Lanjutkan?",
                        "Lanjutkan", "Tidak", runLoadDirty, runLoadBarang);
            }else{
                setDirty(false);
                LoadDataBarang();
            }
        }else{
            loading.dismiss();
        }
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
                    LoadAllBarang();
                }
            });
        } else {
            // Jika tidak ada data yang perlu disinkronkan, lanjutkan langsung
            LoadAllBarang();
        }
    }

    private void LoadData() {
        if (!master_uid.equals("")) {
            JSONObject params = new JSONObject();
            try {
                params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
                params.put("master_uid", master_uid);
                params.put("outlet_uid", outletUid);
                is_detail = "T";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Routes.url_transaksi() + "stok_opname_det/get";//" Filter;
            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        List<StokOpnameDetModel> itemDataModels = new ArrayList<>();
                        itemDataModels.clear();
                        JSONArray jsonArray = response.optJSONArray("data");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    if (obj.getInt("seq") > 0) {
                                        StokOpnameDetModel Data = new StokOpnameDetModel(
                                                obj.getString("uid"),
                                                obj.getString("master_uid"),
                                                obj.getString("barang_uid"),
                                                obj.getString("satuan_uid"),
                                                obj.getString("nama_barang"),
                                                obj.getString("nama_satuan"),
                                                obj.getDouble("qty"),
                                                obj.getDouble("qty_program"),
                                                obj.getDouble("qty_selisih"),
                                                obj.getString("keterangan"));
                                        Data.setIs_detail("T");
                                        itemDataModels.add(Data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                    swipe.setRefreshing(false);
                                    loading.dismiss();
                                }
                            }
                        }
                        ListItems = itemDataModels;
                        mAdapter.removeAllModel();
                        mAdapter.addModels(ListItems);
                        swipe.setRefreshing(false);
                        loading.dismiss();
                    },
                    error -> {
                        ApiHelper.handleError(StokOpnameInputActivity.this, error, () -> LoadData());
                        swipe.setRefreshing(false);
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
        } else {
            get_nomor();
        }
        StokOpnameDetModel data = new StokOpnameDetModel();
        mAdapter.addModel(data);
    }

    private void LoadDataBarang() {
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("outlet_uid", outletUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_global() + "saldo/get_for_stok_opname";//" Filter;
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<StokOpnameDetModel> itemDataModels = new ArrayList<>();
                    itemDataModels.clear();
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
//                                SELECT b.uid AS barang_uid, b.nama AS nama_barang, s.uid AS satuan_uid, s.nama AS nama_satuan, SUM(p.qty_primer) AS qty_program
                                JSONObject obj = jsonArray.getJSONObject(i);
                                    StokOpnameDetModel Data = new StokOpnameDetModel(
                                            "",
                                            "",
                                            obj.getString("barang_uid"),
                                            obj.getString("satuan_uid"),
                                            obj.getString("nama_barang"),
                                            obj.getString("nama_satuan"),
                                            0,
                                            obj.optDouble("qty_program", 0),
                                            0,
                                            ""
                                    );
                                    Data.setIs_detail("F");
                                    itemDataModels.add(Data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                swipe.setRefreshing(false);
                                loading.dismiss();
                            }
                        }
                    }
                    ListItems = itemDataModels;
                    mAdapter.removeAllModel();
                    mAdapter.addModels(ListItems);
                    swipe.setRefreshing(false);
                    loading.dismiss();
                },
                error -> {
                    ApiHelper.handleError(StokOpnameInputActivity.this, error, () -> LoadData());
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

        StokOpnameDetModel data = new StokOpnameDetModel();
        mAdapter.addModel(data);
    }

    public void get_nomor() {
        JSONObject params = new JSONObject();
        try {
            String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
            params.put("tanggal", tanggal);
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("tipe_trans", JConst.TRANS_STOK_OPNAME);
            params.put("outlet_uid", outletUid);
            params.put("field_outlet", "outlet_uid");
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
                                int no_urut = obj.optInt("nomor", 0) + 1; // Get the nomor from the JSON

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
                                    nomor = JConst.TRANS_STOK_OPNAME + "/" + formattedMonth + formattedYear + "/" + formattedNomor + "-" + kodeOutlet;

                                    txtNomor.setText(nomor);
                                }
                                swipe.setRefreshing(false);
                                loading.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                swipe.setRefreshing(false);
                                loading.dismiss();
                            }
                        }
                    }
                },
                error -> {
                    ApiHelper.handleError(StokOpnameInputActivity.this, error, () -> get_nomor());
                    swipe.setRefreshing(false);
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

    public void getstok_program() {
        JSONObject params = new JSONObject();
        try {
            params.put("barang_uid", uidBrg);
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("outlet_uid", outletUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_get_stok();
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                stokProgram = obj.getDouble("saldoakhir");
                                satuan = obj.getString("satuan");
                                mAdapter.Datas.get(adapterPos).setqty_program(stokProgram);
                                mAdapter.Datas.get(adapterPos).setSatuan(satuan);
                                mAdapter.notifyItemChanged(adapterPos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                swipe.setRefreshing(false);
                            }
                        }
                    }

                },
                error -> {
                    ApiHelper.handleError(StokOpnameInputActivity.this, error, () -> getstok_program());
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
            if (data != null) {
                Bundle extra = data.getExtras();
                namaBrg = extra.getString("barang_nama");
                uidBrg = extra.getString("barang_uid");
                uidSatuan = extra.getString("satuan_uid");
                mAdapter.Datas.get(adapterPos).setNama_brg(namaBrg);
                mAdapter.Datas.get(adapterPos).setBarang_uid(uidBrg);
                mAdapter.Datas.get(adapterPos).setSatuan_uid(uidSatuan);
                getstok_program();
                mAdapter.notifyItemChanged(adapterPos);
            }
        } else {
            // Handle the result from LovBarangActivity
            if (data != null) {
                Bundle extra = data.getExtras();
                master_uid = extra.getString("uid");
                outletUid = extra.getString("outlet_uid");
                is_detail = extra.getString("is_detail");
                LoadData();
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
                                btnSave.setEnabled(true);
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
                StokOpnameDetModel data = new StokOpnameDetModel();
                mAdapter.addModel(data);
                adapterPos = mAdapter.getItemCount() - 1;
                mAdapter.notifyItemInserted(adapterPos);
                rcvLoad.scrollToPosition(adapterPos); // Auto-scroll to the last item

            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rcvLoad.scrollToPosition(0);
                if (is_detail.equals("T")) {
                    LoadData();
                }
                swipe.setRefreshing(false);
            }
        });
    }

    private boolean isValid(){
        int i = 0; // Mulai dari 0
        while (i < ListItems.size()) {
            if (ListItems.get(i).getBarang_uid().equals("")) {
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
            if (ListItems.get(i).getBarang_uid().equals("")){
                Snackbar.make(findViewById(android.R.id.content), "Barang pada baris " + (i + 1) + " belum dipilih.", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            for (int j = i+1; j < ListItems.size(); j++) {
                if (ListItems.get(i).getBarang_uid().equals(ListItems.get(j).getBarang_uid())){
                    Snackbar.make(findViewById(android.R.id.content), "Barang pada baris " + (j + 1) + " sama dengan baris " + (i+1) + ".", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean save() {
        loading.show();
        JSONObject paramsMst = new JSONObject();
        String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
        get_nomor();
        try {
            paramsMst.put("outlet_uid", outletUid);
            paramsMst.put("user_id", userId);
            paramsMst.put("nomor", nomor);
            paramsMst.put("tanggal", tanggal);
            paramsMst.put("keterangan", txtKeterangan.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
            loading.dismiss();
        }
        int count = 0;
        JSONArray PenambahPengurangArray = new JSONArray();
        JSONArray stokOpnameDetArray = new JSONArray();
        for (int i = 0; i < ListItems.size(); i++) {
            try {
                JSONObject paramsDet = new JSONObject();
                JSONObject paramsPP = new JSONObject();
                paramsDet.put("master_uid", uuid);
                paramsDet.put("barang_uid", ListItems.get(i).getBarang_uid());
                paramsDet.put("satuan_uid", ListItems.get(i).getSatuan_uid());
                paramsDet.put("qty", ListItems.get(i).getQty());
                paramsDet.put("qty_program", ListItems.get(i).getqty_program());
                paramsDet.put("qty_selisih", ListItems.get(i).getqty_selisih());
                paramsDet.put("keterangan", ListItems.get(i).getKeterangan().trim());
                stokOpnameDetArray.put(paramsDet);

                if (ListItems.get(i).getqty_selisih() != 0) {
                    paramsPP.put("trans_uid", uuid);
                    paramsPP.put("tanggal", tanggal);
                    paramsPP.put("nomor", nomor);
                    paramsPP.put("outlet_uid", outletUid);
                    paramsPP.put("user_id", userId);
                    paramsPP.put("tipe_trans", JConst.TRANS_STOK_OPNAME);
                    paramsPP.put("barang_uid", ListItems.get(i).getBarang_uid());
                    paramsPP.put("qty_primer", ListItems.get(i).getqty_selisih());
                    paramsPP.put("keterangan", ListItems.get(i).getKeterangan().trim());
                    PenambahPengurangArray.put(paramsPP);
                    count++;
                }


            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("stok_opname_mst", paramsMst); // Attach array to the main JSON object
            params.put("stok_opname_det", stokOpnameDetArray); // Attach array to the main JSON object
            params.put("penambah_pengurang_barang", PenambahPengurangArray); // Attach array to the main JSON object
        } catch (JSONException e) {
            e.printStackTrace();
            loading.dismiss();
        }
        String url = Routes.url_transaksi() + "stok_opname_mst/save";


        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    uuid = dataObject.getString("uid_stok_opname_mst");

                    Intent intent = getIntent();
                    intent.putExtra("uuid", uuid);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(this, "Penyimpanan berhasil.", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);

                    setDirty(false);    //set dirty false karena sudah berhasil save > delete dirty
                    loading.dismiss();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                swipe.setRefreshing(false);
                loading.dismiss();
            }
        }, error -> {
            ApiHelper.handleError(StokOpnameInputActivity.this, error, () -> save());
            btnSave.setEnabled(true);
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

    public void setDirty(boolean isDirtyTable){
        if (isDirtyTable){
            //save ke tabel
            stokOpnameDirtyTable.deleteAll();
            for (StokOpnameDetModel stokOpnameDetModel : mAdapter.Datas) {
                stokOpnameDirtyTable.insert(stokOpnameDetModel);
            }
        }else{
            //delete dirty tabel
            stokOpnameDirtyTable.deleteAll();
        }
        isDirty = isDirtyTable;
        SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_dirty_so", isDirtyTable);
    }

    private void LoadDirty(){
        mAdapter.removeAllModel();
        mAdapter.addModels(stokOpnameDirtyTable.getRecords());
        loading.dismiss();
    }

    @Override
    public void onBackPressed() {
        Runnable runClearDirty = new Runnable() {
            @Override
            public void run() {
                setDirty(false);
                onBackPressed();
            }
        };

        if (isDirty && !is_detail.equals("T")) {
            ShowDialog.confirmDialog(this, "Informasi", "Ada beberapa data yang sudah diisi, tetap keluar?", "Ya", "Tidak", runClearDirty, () -> {});
        } else {
            super.onBackPressed(); // Jika tidak ada perubahan, langsung keluar
        }
    }
}
