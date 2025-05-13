package com.orion.pos_crushty_android.ui.kirim_barang;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcel;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.kirim_barang.KirimBarangMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangJualActivity;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangInputActivity;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangRekapAdapter;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainInputActivity;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainRekapActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameRekapActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;
import com.orion.pos_crushty_android.utility.PrinterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KirimBarangRekapActivity extends AppCompatActivity {
    private RecyclerView rcvLoad;
    private View v;
    private SwipeRefreshLayout swipe;
    private Activity ThisActivity;
    public KirimBarangRekapAdapter mAdapter;
    private TextView toolbarTitle, tvDateFrom, tvDateTo;
    private Button btnTambahKrm;
    private SearchView txtCari;
    private long tglDari, tglSampai;
    public List<KirimBarangMstModel> ListItems = new ArrayList<>();
    private Toolbar toolbar;
    private ImageButton btnDate;
    private ImageView imgFilter;
    //var
    //long dateFromLong, dateToLong;
    final int REQUEST_FILTER_BARANG = 1;
    private String barangUid = "";

    PrinterUtils printerUtils;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_barang_rekap);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("Kirim Barang"); // Set title text
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        txtCari = findViewById(R.id.txtSearchSOP);
        swipe = findViewById(R.id.swipe);
        toolbar = findViewById(R.id.toolbarTrans);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnTambahKrm = findViewById(R.id.btnTambahKrm);
        imgFilter = findViewById(R.id.imgFilterRekap);
        btnDate = findViewById(R.id.btnDateKrm);
        tvDateFrom = findViewById(R.id.tvDateFromKrm);
        tvDateTo = findViewById(R.id.tvDateToKrm);
        this.mAdapter = new KirimBarangRekapAdapter(this, ListItems, R.layout.list_item_kirim_barang_rekap);
        rcvLoad.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
    }

    private void InitClass() {
        this.setTitle("Kirim Barang");
        ThisActivity = this;
        barangUid = "";
        Calendar calendar = Calendar.getInstance();
        tglSampai = calendar.getTimeInMillis();
        tvDateTo.setText(Global.getDateFormated(tglSampai, "dd-MM-yyyy"));

        calendar.add(Calendar.DAY_OF_YEAR, -7);
        tglDari = calendar.getTimeInMillis();
        tvDateFrom.setText(Global.getDateFormated(tglDari, "dd-MM-yyyy"));

        txtCari.setQuery("", false);  // Clear the search query
        txtCari.clearFocus();         // Optional: Remove focus from the SearchView
        LoadData();
        rcvLoad.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

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
        calendar.add(Calendar.YEAR, -10);
        long tenYearAgo = calendar.getTimeInMillis();

        // Mengatur batasan (bisa dihapus atau diatur tanpa batas)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                // Hapus batasan ini untuk memungkinkan rentang tanggal penuh
                //.setStart(oneWeekAgo) // Tanggal awal
                //.setEnd(today) // Tanggal akhir
                .setValidator(new CalendarConstraints.DateValidator() {
                    @Override
                    public boolean isValid(long date) {
//                        return date <= today;
//                        return true; // Semua tanggal valid
                        return date >= tenYearAgo && date <= today;
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
        // Anda bisa mengatur tanggal awal dan akhir jika diperlukan, atau biarkan null
        builder.setSelection(new Pair<>(tglDari, tglSampai));
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> updateSelectedDates(selection));

        return dateRangePicker;
    }

    private void updateSelectedDates(Pair<Long, Long> selection) {
        // Format tanggal
        long startDate = selection.first;  // Tanggal mulai tidak perlu diubah
        long endDate = selection.second;   // Tanggal akhir

        // Modifikasi tanggal akhir menjadi jam 23:59:59
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(endDate);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endDate = endCalendar.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedStartDate = dateFormat.format(new Date(startDate));
        String formattedEndDate = dateFormat.format(new Date(endDate));

        // Menampilkan tanggal pada TextView
        tvDateFrom.setText(formattedStartDate);
        tvDateTo.setText(formattedEndDate);

        //tambahin ke var date long keperluan load local,
        tglDari = startDate;
        tglSampai = endDate;

        LoadData();
    }


    private void LoadData() {
        String apiKey = SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key");
        String dari = Global.getDateFormatMysql(tglDari);
        String sampai = Global.getDateFormatMysql(tglSampai);
        String OutletUID = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        String karyawanUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            if (!txtCari.getQuery().toString().equals("")){
                params.put("nomor", txtCari.getQuery().toString());
            }
            params.put("outlet_asal_uid", OutletUID);
            params.put("operator_dari", ">=");
            params.put("tanggal_dari", dari);
            params.put("operator_sampai", "<=");
            params.put("tanggal_sampai", sampai);
            if (!barangUid.equals("")){
                params.put("barang_uid", barangUid);
            }
            params.put("karyawan_uid", karyawanUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_transaksi() + "kirim_barang_mst/get";//" Filter;
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<KirimBarangMstModel> itemDataModels = new ArrayList<>();
                    itemDataModels.clear();
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String namaKaryawantemp = obj.getString("nama_karyawan");
                                String[] parts = namaKaryawantemp.split("\\|");
                                String kode = parts[0];
                                String nama = parts[1];
                                if (obj.optInt("seq",0) > 0) {
                                    KirimBarangMstModel Data = new KirimBarangMstModel(
                                            obj.getString("uid"),
                                            obj.getInt("seq"),
                                            FungsiGeneral.getTglFormat(FungsiGeneral.getDateFromMysql(obj.getString("tanggal"))),
                                            obj.getString("nomor"),
                                            obj.getString("outlet_asal_uid"),
                                            obj.getString("outlet_tujuan_uid"),
                                            obj.getString("keterangan"),
                                            obj.optString("user_acc",""),
                                            FungsiGeneral.getTglFormat(FungsiGeneral.getDateFromMysql(obj.optString("tgl_acc",""))),
                                            obj.getString("user_id"),
                                            FungsiGeneral.getTglFormat(FungsiGeneral.getDateFromMysql(obj.optString("tgl_input",""))),
                                            obj.getString("user_edit"),
                                            FungsiGeneral.getTglFormat(FungsiGeneral.getDateFromMysql(obj.optString("tgl_edit",""))),
                                            obj.getString("karyawan_uid"),
                                            obj.getString("nama_outlet_asal"),
                                            obj.getString("nama_outlet_tujuan"),
                                            nama,
                                            kode
                                    );
                                    itemDataModels.add(Data);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                swipe.setRefreshing(false);
                            }
                        }
                    }
                    ListItems = itemDataModels;
                    mAdapter.removeAllModel();
                    mAdapter.addModels(ListItems);
                    swipe.setRefreshing(false);
                },
                error -> {
                    ApiHelper.handleError(KirimBarangRekapActivity.this, error, () -> LoadData());
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

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KirimBarangRekapActivity.this, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_FILTER_BARANG);
            }
        });

        btnTambahKrm.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Global.checkShift(KirimBarangRekapActivity.this, () -> {
                       Intent s = new Intent(KirimBarangRekapActivity.this, KirimBarangInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                       s.putExtra("master_uid", "");
                       s.putExtra("keterangan", "");
                       KirimBarangRekapActivity.this.startActivityForResult(s, JConst.REQUEST_INPUT);  // Use activity context
                   });
               }
           }

        );

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
            }
        });
        txtCari.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                LoadData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    LoadData();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
//        printerUtils.handleActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            //ambil dari camera
            case REQUEST_FILTER_BARANG:
                if (resultCode == RESULT_OK && data != null) {
                    Bundle extra = data.getExtras();
                    barangUid = extra.getString("barang_uid");
                    LoadData();
                    if (!extra.getString("barang_uid").isEmpty()){
                        imgFilter.setColorFilter(getColor(R.color.generic_1), PorterDuff.Mode.SRC_IN);
                    } else {
                        imgFilter.clearColorFilter(); // Kembalikan ke warna aslinya
                    }
                }
                break;
            case JConst.REQUEST_INPUT :
                if (resultCode == RESULT_OK) {
//                    Bundle extra = data.getExtras();
//                    String uuid = extra.getString("uuid");
                    LoadData();
                }
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