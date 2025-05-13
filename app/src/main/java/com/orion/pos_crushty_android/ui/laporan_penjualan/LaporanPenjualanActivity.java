package com.orion.pos_crushty_android.ui.laporan_penjualan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcel;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.SyncData;
import com.orion.pos_crushty_android.databases.laporan_keuangan.LaporanKeuanganModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukActivity;
import com.orion.pos_crushty_android.ui.laporan_item_terjual.LaporanItemTerjualActivity;
import com.orion.pos_crushty_android.ui.laporan_keuangan.DrawableEventRenderer;
import com.orion.pos_crushty_android.ui.laporan_keuangan.LaporanKeuanganActivity;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangJualActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;
import com.orion.pos_crushty_android.utility.PrinterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarException;

public class LaporanPenjualanActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvDateFrom, tvDateTo, tvTotalPendapatan;
    private ImageButton btnDate;
    private ImageView imgFilter;
    private Spinner spnStatusKirim;

    //var for adapter/list
    private LaporanPenjualanAdapter mAdapter;
    public List<PenjualanMstModel> ListItems = new ArrayList<>();

    //var
    long dateFromLong, dateToLong;
    final int REQUEST_FILTER_BARANG = 1;
    private String barangJualUid, outletUid, karyawanUid;
    private ProgressDialog loading;
    private int idxStatusKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JApplication.currentActivity = this;
        setContentView(R.layout.activity_laporan_penjualan);
        CreateView();
        InitClass();
        EventClass();
        cekSync();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvDateFrom = findViewById(R.id.tvDateFrom);
        tvDateTo = findViewById(R.id.tvDateTo);
        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan);
        btnDate = findViewById(R.id.btnDate);
        imgFilter = findViewById(R.id.imgFilter);
        spnStatusKirim = findViewById(R.id.spnStatusKirim);

        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void InitClass() {
        Calendar calendar = Calendar.getInstance();
        dateFromLong = Global.getMilliesDateStartEnd(calendar.getTimeInMillis(), true);
        dateToLong = Global.getMilliesDateStartEnd(calendar.getTimeInMillis(), false);
        tvDateFrom.setText(Global.getDateFormated(dateFromLong, "dd-MM-yyyy"));
        tvDateTo.setText(Global.getDateFormated(dateToLong, "dd-MM-yyyy"));

        SetJenisTampilan();
        isiCombo();
        barangJualUid = "";
        outletUid = SharedPrefsUtils.getStringPreference(this, "outlet_uid");
        karyawanUid = SharedPrefsUtils.getStringPreference(this, "karyawan_uid");
    }

    private void EventClass() {
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaporanPenjualanActivity.this, LovBarangJualActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_FILTER_BARANG);
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
            }
        });

        spnStatusKirim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cek index yang dipilih
                idxStatusKirim = position;
                LoadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Jika tidak ada yang dipilih (opsional, bisa dikosongkan)
            }
        });


    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new LaporanPenjualanAdapter(this, ListItems, R.layout.list_item_laporan_penjualan);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    private void isiCombo(){
        // Buat daftar opsi
        List<String> statusList = Arrays.asList("Sudah Upload", "Belum Upload");
        // Buat adapter untuk Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set adapter ke Spinner
        spnStatusKirim.setAdapter(adapter);
        // Set default ke "belum Upload" (index 1)
        idxStatusKirim = 1;
        spnStatusKirim.setSelection(idxStatusKirim);
    }

    public void cekSync() {
        if (!Global.CheckConnectionInternet(this)){
            LoadData();
            return;
        }

        loading.show();
        int penjualanBelumSync = new PenjualanMstTable(this).getCountPenjualanBelumSync();
        int pengeluaranLainBelumSync = new PengeluaranLainMstTable(this).getCountPengeluaranLainBelumSync();

        if (penjualanBelumSync > 0 || pengeluaranLainBelumSync > 0) {
            new SyncData(this).SyncAll(new SyncData.SyncCallback() {
                @Override
                public void onSyncComplete() {
                    // Lanjutkan memuat data setelah sinkronisasi selesai
                    LoadData();
                    loading.dismiss();
                }
            });
        } else {
            // Jika tidak ada data yang perlu disinkronkan, lanjutkan langsung
            LoadData();
            loading.dismiss();
        }
    }

    public void LoadData(){
        if (idxStatusKirim == 0) {
            if (!Global.CheckConnectionInternet(LaporanPenjualanActivity.this)){
                ShowDialog.infoDialog(LaporanPenjualanActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                spnStatusKirim.setSelection(1);
                return;
            }
            // Jika "Sudah Upload" dipilih
            LoadDataOnline();
        } else if (idxStatusKirim == 1) {
            // Jika "Belum Upload" dipilih
            LoadDataOffline();
        }
    }

    public void LoadDataOffline() {
        //load data dari local
        mAdapter.removeAllModel();
        PenjualanMstTable penjualanMstTable = new PenjualanMstTable(this);
        penjualanMstTable.setFilter(dateFromLong, dateToLong, barangJualUid, outletUid, karyawanUid);
        mAdapter.addModels(penjualanMstTable.getRecordsForReport());

        //dapetin total pendapatan
        double totalPendapatan = penjualanMstTable.getTotalPendapatan();
        tvTotalPendapatan.setText(Global.FloatToStrFmt(totalPendapatan, true));

        //delete transaksi lebih dari seminggu
//        penjualanMstTable.deleteTransSeminggu();
        loading.dismiss();
    }


    public void LoadDataOnline() {
        if (!loading.isShowing()) {
            loading.show();
        }
        // Bersihkan data sebelumnya di adapter
        mAdapter.removeAllModel();
        tvTotalPendapatan.setText(Global.FloatToStrFmt(0, true));
        // URL API
        String url = Routes.url_laporan() + "laporan_penjualan/get";

        // Membuat request body dalam bentuk JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid"));
            requestBody.put("tgl_dari", Global.getDateFormatMysql(dateFromLong));
            requestBody.put("tgl_sampai", Global.getDateFormatMysql(dateToLong));
            requestBody.put("barang_jual_uid", barangJualUid);
        } catch (JSONException e) {
            e.printStackTrace();
            loading.dismiss();
        }

        // Membuat request dengan JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            ArrayList<PenjualanMstModel> itemDataModels = new ArrayList<>();
            try {
                JSONArray jsonArray;
                if (!response.isNull("data")) {
                    jsonArray = response.getJSONArray("data");
                    double vTotalPendapatan = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        PenjualanMstModel data = new PenjualanMstModel();
                        data.setUid(obj.getString("uid"));
                        data.setNomor(obj.getString("nomor"));
                        data.setTanggal(FungsiGeneral.getDateFromMysql(obj.getString("tanggal")));
                        data.setTotal(obj.getDouble("total"));
                        data.setTipeBayar(obj.getString("tipe_bayar"));
                        vTotalPendapatan += obj.getDouble("total");
                        itemDataModels.add(data);
                    }
                    tvTotalPendapatan.setText(Global.FloatToStrFmt(vTotalPendapatan, true));

                } else {
                    // Penanganan jika data bernilai null
                    String keterangan = response.getString("keterangan");
                    Toast.makeText(getApplicationContext(), keterangan, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            } finally {
                mAdapter.addModels(itemDataModels);
                loading.dismiss();
            }
        }, error -> {
            ApiHelper.handleError(LaporanPenjualanActivity.this, error, () -> LoadData());
            loading.dismiss();
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

    private void showDateRangePicker() {
        createDateRangePickerDialog().show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }


    private MaterialDatePicker<Pair<Long, Long>> createDateRangePickerDialog() {
        // Mendapatkan tanggal hari ini dan tanggal seminggu yang lalu
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        long oneWeekAgo = calendar.getTimeInMillis();

        // Mengatur batasan
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(oneWeekAgo) // default Tanggal awal (seminggu yang lalu)
                .setEnd(today) // Tanggal akhir (hari ini)
                .setValidator(new CalendarConstraints.DateValidator() {
                    @Override
                    public boolean isValid(long date) {
                        //tambahkan pengecekan tanggal yang dimunculkan
//                        return date <= today;
                        return date >= oneWeekAgo && date <= today;
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                    }
                });

        // Membuat Date Range Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Pilih Rentang Tanggal");
        builder.setSelection(new Pair<>(dateFromLong, dateToLong));
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> updateSelectedDates(selection));

        return dateRangePicker;
    }


    private void updateSelectedDates(Pair<Long, Long> selection) {
        // Format tanggal
        long startDate = selection.first;  // Tanggal mulai tidak perlu diubah
        long endDate = selection.second;   // Tanggal akhir

        // Modifikasi tanggal akhir menjadi jam 00:00:00 dan 23:59:59
        startDate = Global.getMilliesDateStartEnd(startDate, true);
        endDate = Global.getMilliesDateStartEnd(endDate, false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedStartDate = dateFormat.format(new Date(startDate));
        String formattedEndDate = dateFormat.format(new Date(endDate));

        // Menampilkan tanggal pada TextView
        tvDateFrom.setText(formattedStartDate);
        tvDateTo.setText(formattedEndDate);

        //tambahin ke var date long keperluan load local,
        dateFromLong = startDate;
        dateToLong = endDate;

        LoadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_FILTER_BARANG:
                if (resultCode == RESULT_OK && data != null) {
                    Bundle extra = data.getExtras();
                    barangJualUid = extra.getString("barang_jual_uid");
                    LoadData();
                    if (!extra.getString("barang_jual_uid").isEmpty()){
                        imgFilter.setColorFilter(getColor(R.color.generic_1), PorterDuff.Mode.SRC_IN);
                    } else {
                        imgFilter.clearColorFilter(); // Kembalikan ke warna aslinya
                    }
                }
                break;
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
}