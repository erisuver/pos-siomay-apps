package com.orion.pos_crushty_android.ui.pengeluaran_lain;

import static android.view.View.GONE;

import static com.orion.pos_crushty_android.globals.JConst.TIPE_BAYAR_TUNAI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainDetModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.laporan_keuangan.LaporanKeuanganActivity;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.SectionsPagerAdapter;
import com.orion.pos_crushty_android.databinding.ActivityPengeluaranLainInputBinding;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainKasAdapter;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainBarangAdapter;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputAdapter;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PengeluaranLainInputActivity extends AppCompatActivity {

    private ActivityPengeluaranLainInputBinding binding;
    private Toolbar toolbar;
    private TextView txtNomor, toolbarTitle;
    public String master_uid, Nomor, outletUid, userId, karyawanUid, kodeOutlet, TipeInput, kasbankUid, uuid, mode, tipeInputMst ;
    private Activity ThisActivity;
    private View v;
//    private EditText txtKeterangan;
//    private Button btnSaveKas, btnSaveBrg;

    public PengeluaranLainBarangAdapter mAdapterBrg;
    public PengeluaranLainKasAdapter mAdapterKas;
    public List<PengeluaranLainDetModel> ListItems = new ArrayList<>();

    ViewPager2 viewPager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JApplication.currentActivity = this;

        binding = ActivityPengeluaranLainInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, this);
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = binding.tabs;

        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setCustomView(createTabView("Kas"));
                    break;
                case 1:
                    tab.setCustomView(createTabView("Barang"));
                    break;
            }
        }).attach();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateNomorForSelectedTab(position);
            }
        });

        CreateView();
        InitClass();
    }
    private TextView createTabView(String text) {
        TextView textView = new TextView(this);  // Use your activity or fragment context if necessary
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);  // Set bold text
        textView.setGravity(Gravity.CENTER);  // Optional: Center text in the tab
        return textView;
    }
    private void updateNomorForSelectedTab(int position) {
        // Logic to update txtNomor based on the selected tab
        if (position == 0) {
            // Load or set the nomor for the Kas tab
            TipeInput = JConst.TRANS_PENGELUARAN_LAIN_KAS; // You can set it dynamically here
        } else if (position == 1) {
            // Load or set the nomor for the Barang tab
            TipeInput = JConst.TRANS_PENGELUARAN_LAIN_BARANG; // You can set it dynamically here
        }
        // Call getNomor() here if needed to fetch or update the number dynamically
        getNomor(); // Uncomment if you want to fetch new data on tab change
    }

    private void setSelectionTab(int position) {
        // Pastikan posisi yang diberikan valid
        if (position >= 0 && position < binding.viewPager.getAdapter().getItemCount()) {
            // Mengubah tab yang sedang dipilih sesuai posisi
            binding.viewPager.setCurrentItem(position, true);
        }
    }
    private void disableTabChangeIfDetailMode() {
        // Jika mode adalah 'detail', disable swipe dan disable click pada tab
        if (mode.equals("detail")) {
            viewPager.setUserInputEnabled(false);
            // Disable click pada TabLayout
            for (int i = 0; i < tabs.getTabCount(); i++) {
                View tabView = ((ViewGroup) tabs.getChildAt(0)).getChildAt(i);
                tabView.setEnabled(false); // Disable click pada tab
            }
        }
    }

    private void CreateView() {
        toolbar = findViewById(R.id.toolbarinputPL);
        txtNomor = findViewById(R.id.tvNomorPL);
        toolbarTitle = findViewById(R.id.toolbar_title_input);
//        txtKeterangan = findViewById(R.id.txtKeteranganPL);

    }
    private void InitClass() {
        this.setTitle("Pengeluaran Lain Input");
        ThisActivity = this;
        outletUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        userId = SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id");
        kodeOutlet = GlobalTable.getKodeOutlet(getApplicationContext());
        karyawanUid =  SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        kasbankUid = GlobalTable.getKasbankUid(getApplicationContext(),TIPE_BAYAR_TUNAI);
        Nomor = ""; //Sementara dikosongkan
        master_uid = "";
        mode = "add";
        TipeInput = JConst.TRANS_PENGELUARAN_LAIN_KAS;
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            master_uid = extra.getString("uid");
            outletUid = extra.getString("outlet_uid");
            Nomor =  extra.getString("nomor");
            TipeInput = extra.getString("tipe");
            tipeInputMst = extra.getString("tipe");
            txtNomor.setText(Nomor);
            mode = master_uid.equals("") ? "add" : "detail";

//            txtKeterangan.setText(extra.getString("keterangan"));
//            txtKeterangan.setEnabled(false);
//            btnSaveBrg.setVisibility(GONE);
//            btnSaveKas.setVisibility(GONE);
        }
//        LoadData();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(" Input Pengeluaran Lain"); // Set title text
        setSelectionTab(TipeInput.equals(JConst.TRANS_PENGELUARAN_LAIN_KAS) ? 0 : 1);
        disableTabChangeIfDetailMode();
    }
    private void EventClass() {


    }
    /*
    public void LoadData() {
        if (master_uid != "") {
            JSONObject params = new JSONObject();
            try {
                params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
                params.put("master_uid", master_uid);
                params.put("outlet_uid", outletUid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = Routes.url_transaksi() + "pengeluaran_lain_det/get";//" Filter;
            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        List<PengeluaranLainDetModel> itemDataModels = new ArrayList<>();
                        itemDataModels.clear();
                        JSONArray jsonArray = response.optJSONArray("data");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    if (obj.getInt("seq") > 0) {
                                        PengeluaranLainDetModel Data = new PengeluaranLainDetModel(
                                                obj.getString("barang_uid"),
                                                obj.getString("satuan_uid"),
                                                obj.getString("keterangan"),
                                                obj.getString("nama_barang"),
                                                obj.getString("nama_satuan"),
                                                obj.getDouble("qty"),
                                                obj.getDouble("konversi"),
                                                obj.getDouble("qty_primer"),
                                                obj.getString("kas_bank_uid"),
                                                obj.getDouble("jumlah"));
                                        itemDataModels.add(Data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        ListItems = itemDataModels;
                        mAdapterBrg.removeAllModel();
                        mAdapterBrg.addModels(ListItems);
                    },
                    error -> {
                        ApiHelper.handleError(PengeluaranLainInputActivity.this, error, () -> LoadData());
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
            getNomor();
        }
        PengeluaranLainDetModel data = new PengeluaranLainDetModel();
        mAdapterBrg.addModel(data);
    }*/
    /*
    private boolean save() {
        JSONObject paramsMst = new JSONObject();
        String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
        getNomor();
        try {
            paramsMst.put("outlet_uid", outletUid);
            paramsMst.put("user_id", userId);
            paramsMst.put("nomor", Nomor);
            paramsMst.put("tanggal", tanggal);
//            paramsMst.put("keterangan", txtKeterangan.getText().toString().trim());
            paramsMst.put("tipe", TipeInput);
            paramsMst.put("karyawan_uid", karyawanUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int count = 0;
        JSONArray PenambahPengurangArray = new JSONArray();
        JSONArray pengeluaranLainDetArray = new JSONArray();
        for (int i = 0; i < ListItems.size(); i++) {
            try {
                if (TipeInput.equals(JConst.TRANS_PENGELUARAN_LAIN_BARANG)) {
                    JSONObject paramsDet = new JSONObject();
                    JSONObject paramsPP = new JSONObject();
                    paramsDet.put("outlet_uid", outletUid);
                    paramsDet.put("barang_uid", ListItems.get(i).getBarang_uid());
                    paramsDet.put("satuan_uid", ListItems.get(i).getSatuan_uid());
                    paramsDet.put("qty", ListItems.get(i).getQty());
                    paramsDet.put("qty_primer", ListItems.get(i).getQty_primer());
                    paramsDet.put("qty_konversi", ListItems.get(i).getKonversi());
                    paramsDet.put("keterangan", ListItems.get(i).getKeterangan().trim());
                    paramsMst.put("kas_bank_uid", "");
                    paramsMst.put("jumlah", "0");
                    pengeluaranLainDetArray.put(paramsDet);

                    paramsPP.put("tanggal", tanggal);
                    paramsPP.put("nomor", Nomor);
                    paramsPP.put("outlet_uid", outletUid);
                    paramsPP.put("user_id", userId);
                    paramsPP.put("tipe_trans", JConst.TRANS_PENGELUARAN_LAIN_BARANG);
                    paramsPP.put("barang_uid", ListItems.get(i).getBarang_uid());
                    paramsPP.put("qty_primer", ListItems.get(i).getQty_primer());
                    paramsPP.put("keterangan", ListItems.get(i).getKeterangan().trim());
                    PenambahPengurangArray.put(paramsPP);
                } else {
                    JSONObject paramsDet = new JSONObject();
                    JSONObject paramsPP = new JSONObject();
                    paramsDet.put("outlet_uid", outletUid);
                    paramsDet.put("barang_uid", "");
                    paramsDet.put("satuan_uid", "");
                    paramsDet.put("qty", "0");
                    paramsDet.put("qty_primer", "0");
                    paramsDet.put("qty_konversi", "0");
                    paramsDet.put("keterangan", ListItems.get(i).getKeterangan().trim());
                    paramsMst.put("kas_bank_uid", kasbankUid);
                    paramsMst.put("jumlah", ListItems.get(i).getKeterangan());
                    pengeluaranLainDetArray.put(paramsDet);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("pengeluaran_lain_mst", paramsMst); // Attach array to the main JSON object
            params.put("pengeluaran_lain_det", pengeluaranLainDetArray); // Attach array to the main JSON object
            if (TipeInput.equals(JConst.TRANS_PENGELUARAN_LAIN_BARANG)) {
                params.put("penambah_pengurang_barang", PenambahPengurangArray); // Attach array to the main JSON object
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_transaksi() + "pengeluaran_lain_mst/save";
        String json = params.toString();
        //Toast.makeText(this, json.toString(), Toast.LENGTH_SHORT).show();
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    uuid = dataObject.getString("uid_pengeluaran_lain_mst");

                    Intent intent = getIntent();
                    intent.putExtra("uuid", uuid);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(this, "Penyimpanan berhasil.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            ApiHelper.handleError(PengeluaranLainInputActivity.this, error, () -> save());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("api_key", SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key"));
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);

        return true;
    }*/
    private void getNomor() {
        JSONObject params = new JSONObject();
        try {
            String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
            params.put("tanggal", tanggal);
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("tipe_trans", TipeInput);
            params.put("outlet_uid", outletUid);
            params.put("field_outlet", "outlet_uid");
            params.put("kodetrans", TipeInput);
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

                                    // Create the final Nomor string
                                    Nomor = TipeInput+"/" + formattedMonth + formattedYear + "/" + formattedNomor+"-"+kodeOutlet;

                                    txtNomor.setText(Nomor);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
//                                swipe.setRefreshing(false);
                            }
                        }
                    }
                },
                error -> {
                    ApiHelper.handleError(PengeluaranLainInputActivity.this, error, () -> getNomor());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


}