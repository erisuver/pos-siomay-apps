package com.orion.pos_crushty_android.ui.terima;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangDetModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TerimaInputActivity extends AppCompatActivity {
    private RecyclerView rcvLoad;
    private View v;
    private Activity ThisActivity;
    public TerimaInputAdapter mAdapter;
    private TextView toolbarTitle;
    private Button btnSave;
    public List<TerimaBarangDetModel> ListItems = new ArrayList<>();
    private Toolbar toolbar;
    private String mode = "";
    private String masterUid, kirimUid;
    private String nomorKirim;
    private String statusTerima;
    final int REQUEST_FILTER_BARANG = 1;
    public int adapterPos;
    private String nomor, outletUid, userId, outletAsalUID, karyawanUid, apiKey, kodeOutlet ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terima_input);
        JApplication.currentActivity = this;
        masterUid = getIntent().getStringExtra("master_uid");
        nomorKirim = getIntent().getStringExtra("nomor_kirim");
        statusTerima = getIntent().getStringExtra("status_terima");
        kirimUid = getIntent().getStringExtra("kirim_uid");
        outletAsalUID = getIntent().getStringExtra("outlet_asal_uid");
        mode = JConst.MODE_ADD;
        if (masterUid != null && !masterUid.equals("") && !masterUid.equals("null")) {
            mode = JConst.MODE_DETAIL;
        }

        CreateView();
        InitClass();
        EventClass();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(nomorKirim); // Set title text
    }
    private void InitClass() {
        ThisActivity = this;
        outletUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        userId = SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id");
        kodeOutlet = GlobalTable.getKodeOutlet(getApplicationContext());
        karyawanUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        apiKey = JConst.SKIP_CHECK_API_KEY;// SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key");
        nomor = ""; //Sementara dikosongkan
        if (mode.equals(JConst.MODE_ADD)){
            get_nomor();
        }
        LoadData();
        rcvLoad.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        toolbar = findViewById(R.id.toolbarinput);
        toolbarTitle = findViewById(R.id.toolbar_title_input);
        btnSave = findViewById(R.id.btnSimpan);
        this.mAdapter = new TerimaInputAdapter(this, ListItems, R.layout.list_item_terima_input);
        rcvLoad.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        if (statusTerima.equals("T")){
            btnSave.setVisibility(View.GONE);
        }
    }

    private void LoadData() {
        JSONObject params = new JSONObject();
        String url;
        String uidMstTemp;
        if (statusTerima.equals(JConst.TRUE_STRING)){
            url = Routes.url_terima_barang_det() + "/get";
            uidMstTemp = masterUid;
        } else {
            url = Routes.url_kirim_barang_det() + "/get";
            uidMstTemp = kirimUid;
        }
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("master_uid", uidMstTemp);
            params.put("outlet_tujuan_uid", outletUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<TerimaBarangDetModel> itemDataModels = new ArrayList<>();
                    itemDataModels.clear();
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        double QtyTemp;
                        String masteruidtemp;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.getInt("seq") > 0) {
                                    QtyTemp = 0.0;
                                    masteruidtemp = "";

                                    if (statusTerima.equals(JConst.TRUE_STRING)) {
                                        QtyTemp = obj.getDouble("qty");
                                        masteruidtemp = obj.getString("master_uid");
                                    }
                                    TerimaBarangDetModel Data = new TerimaBarangDetModel(
                                            obj.getString("uid"),
                                            obj.getInt("seq"),
                                            masteruidtemp,
                                            obj.getString("barang_uid"),
                                            obj.getString("satuan_uid"),
                                            obj.getString("keterangan"),
                                            QtyTemp,
                                            obj.getDouble("konversi"),
                                            obj.getDouble("konversi")*QtyTemp,
                                            obj.getString("kode_barang"),
                                            obj.getString("nama_barang"),
                                            obj.getString("nama_satuan")
                                    );
                                    itemDataModels.add(Data);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ListItems = itemDataModels;
                    mAdapter.removeAllModel();
                    mAdapter.addModels(ListItems);
                },
                error -> {
                    ApiHelper.handleError(TerimaInputActivity.this, error, () -> LoadData());
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
        TerimaBarangDetModel data = new TerimaBarangDetModel();
        mAdapter.addModel(data);
    }

    public void get_nomor() {
        JSONObject params = new JSONObject();
        try {
            String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
            params.put("tanggal", tanggal);
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("tipe_trans", JConst.TRANS_TERIMA_BARANG);
            params.put("field_outlet", "outlet_tujuan_uid");
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
                                    nomor = JConst.TRANS_TERIMA_BARANG+"/" + formattedMonth + formattedYear + "/" + formattedNomor+"-"+kodeOutlet;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                error -> {
                    ApiHelper.handleError(TerimaInputActivity.this, error, () -> get_nomor());
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
                String namaBrg = extra.getString("barang_nama");
                String uidBrg = extra.getString("barang_uid");
//                mAdapter.Datas.get(adapterPos).setNama_brg(namaBrg);
//                mAdapter.Datas.get(adapterPos).setBarang_uid(uidBrg);
//                mAdapter.Datas.get(adapterPos).setqty_program(stokProgram);
                mAdapter.notifyDataSetChanged();
                mAdapter.notifyItemChanged(adapterPos);
            }
        }
    }
    private boolean isValid(){
        boolean isInputExist = false;
        for (int i = 0; i < ListItems.size(); i++) {
            if (ListItems.get(i).getQty() > 0){
                isInputExist = true;
                break;
            }
        }
        if (isInputExist == false){
            Snackbar.make(findViewById(android.R.id.content), "Tida ada data yang diinput.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                    } else {
                        btnSave.setEnabled(true);
                    }
                } else {
                    btnSave.setEnabled(true);
                    Snackbar.make(view, "Tidak ada data di input.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
    private JSONObject CreateJsonTerima(){
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
            mstObj.put("kirim_uid", kirimUid);
            mstObj.put("outlet_tujuan_uid", outletUid);
            mstObj.put("outlet_asal_uid", outletAsalUID);
            mstObj.put("karyawan_uid", karyawanUid);
            mstObj.put("keterangan", "");
            mstObj.put("user_id", userId);

            requestBody.put("terima_barang_mst", mstObj);

            JSONArray detArray = new JSONArray();
            JSONArray penambahPengurangArray = new JSONArray();
            for (TerimaBarangDetModel model : ListItems) {
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

                JSONObject penambahPengurangObj = new JSONObject();

                penambahPengurangObj.put("tanggal", tanggalTrans);
                penambahPengurangObj.put("nomor", nomor);
                penambahPengurangObj.put("trans_uid", "");
                penambahPengurangObj.put("tipe_trans", JConst.TRANS_TERIMA_BARANG);
                penambahPengurangObj.put("outlet_uid", outletUid);
                penambahPengurangObj.put("barang_uid", model.getBarangUid());
                penambahPengurangObj.put("qty_primer", model.getQtyPrimer());
                penambahPengurangObj.put("keterangan", model.getKeterangan().trim());
                penambahPengurangObj.put("user_id", userId);

                // Menambahkan objek ke array
                penambahPengurangArray.put(penambahPengurangObj);
            }
            requestBody.put("terima_barang_det", detArray);
            requestBody.put("penambah_pengurang_barang", penambahPengurangArray);

            // Loop melalui setiap model dalam listModel
            // Mengembalikan request body
            return requestBody;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean save() {
        String url = Routes.url_transaksi() + "terima_barang_mst/save";
        JSONObject params = CreateJsonTerima();
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    // Mengakses objek "data" dari response tanpa try-catch JSONException
                    JSONObject dataObject = response.optJSONObject("data");
                    if (dataObject != null) {
                        // Ambil uid dari objek data
                        masterUid = dataObject.optString("uid_terima_barang_mst", "");
                        Intent s = getIntent();
                        s.putExtra("uuid",masterUid);
                        btnSave.setEnabled(true);
                        setResult(RESULT_OK);
                        finish();  // Close the current activity after successful save
                    } else {
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnSave.setEnabled(true);
                    ApiHelper.handleError(TerimaInputActivity.this, error, () -> save());
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
}
