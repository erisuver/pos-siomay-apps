package com.orion.pos_crushty_android.ui.pengeluaran_lain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcel;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
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

public class PengeluaranLainRekapActivity extends AppCompatActivity {
    private RecyclerView rcvLoad;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvDateFrom, tvDateTo;
    private ImageButton btnDate;
    private ImageView imgFilter;
    private Button btnTambah;
    private SearchView txtCari;
    private PrinterUtils printerUtils;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private String userId = "";
    private Activity ThisActivity;
    private SwipeRefreshLayout swipe;


    //var for adapter/list
    private PengeluaranLainRekapAdapter mAdapter;
    public List<PengeluaranLainMstModel> ListItems = new ArrayList<>();

    //var
    private PengeluaranLainMstTable pengeluaranLainMstTable;
    long dateFromLong, dateToLong;
    private String barangUid = "";
    private String SearchQuery = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengeluaran_lain_rekap);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        LoadData();

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
        btnDate = findViewById(R.id.btnDate);
        imgFilter = findViewById(R.id.imgFilter);
        btnTambah = findViewById(R.id.btnTambahPL);
        txtCari = findViewById(R.id.txtSearchPL);
        swipe = findViewById(R.id.swipe);

        pengeluaranLainMstTable = new PengeluaranLainMstTable(this);
    }
    private void InitClass() {
        this.setTitle("Pengeluaran Lain Rekap");
        ThisActivity = this;

        Calendar calendar = Calendar.getInstance();
        dateToLong = calendar.getTimeInMillis();
        tvDateTo.setText(Global.getDateFormated(dateToLong, "dd-MM-yyyy"));

        calendar.add(Calendar.DAY_OF_YEAR, -7);
        dateFromLong = calendar.getTimeInMillis();
        tvDateFrom.setText(Global.getDateFormated(dateFromLong, "dd-MM-yyyy"));

        txtCari.setQuery("", false);  // Clear the search query
        txtCari.clearFocus();
        SetJenisTampilan();
        barangUid = "";
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
                Intent intent = new Intent(PengeluaranLainRekapActivity.this, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, JConst.REQUEST_FILTER_BARANG);
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Global.checkShift(PengeluaranLainRekapActivity.this, () -> {
                   Intent s = new Intent(PengeluaranLainRekapActivity.this, PengeluaranLainInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   PengeluaranLainRekapActivity.this.startActivityForResult(s, JConst.REQUEST_INPUT);  // Use activity context
               });
           }
       });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
            }
        });
        txtCari.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData();
            }
        });
        txtCari.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    SearchQuery = "";
                    LoadData();
                }else if (query.length() >= 2){
                    SearchQuery = query;
                    LoadData();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    LoadData();
                }else if (newText.length() >= 2){
                    LoadData();
                }
                return false;
            }
        });
    }
    private void LoadData(){
        String outletUID = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        String karyawanUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        pengeluaranLainMstTable.setFilter(dateFromLong, dateToLong, outletUID, karyawanUid);

        mAdapter.removeAllModel();
        mAdapter.addModels(pengeluaranLainMstTable.getRecords());
        swipe.setRefreshing(false);
    }
    /*
    private void LoadData() {
        String dari = FungsiGeneral.getTglFormatMySql(dateFromLong);
        String sampai = FungsiGeneral.getTglFormatMySql(dateToLong);
        String OutletUID = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        String karyawanUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("nomor", txtCari.getQuery().toString());
            params.put("tanggal_dari", dari);
            params.put("operator_dari", ">=");
            params.put("tanggal_sampai", sampai);
            params.put("operator_sampai", "<=");
            params.put("outlet_uid",OutletUID);
            params.put("barang_uid",barangUid);
            params.put("karyawan_uid", karyawanUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_transaksi() + "pengeluaran_lain_mst/get";//" Filter;
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<PengeluaranLainMstModel> itemDataModels = new ArrayList<>();
                    itemDataModels.clear();
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.getInt("seq") > 0) {
                                    PengeluaranLainMstModel Data = new PengeluaranLainMstModel(
                                            obj.getInt("seq"),
                                            obj.getString("uid"),
                                            obj.getString("nomor"),
                                            FungsiGeneral.getDateFromMysql(obj.getString("tanggal")),
                                            obj.getString("keterangan"),
                                            obj.getString("outlet_uid"),
                                            obj.getString("tipe"));
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
                    ApiHelper.handleError(PengeluaranLainRekapActivity.this, error, () -> LoadData());
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
    */
    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new PengeluaranLainRekapAdapter(this, ListItems, R.layout.list_item_pengeluaran_lain_rekap);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
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
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        long oneMonthAgo = calendar.getTimeInMillis();

        // Mengatur batasan
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(oneMonthAgo) // default Tanggal awal (seminggu yang lalu)
                .setEnd(today) // Tanggal akhir (hari ini)
                .setValidator(new CalendarConstraints.DateValidator() {
                    @Override
                    public boolean isValid(long date) {
                        //tambahkan pengecekan tanggal yang dimunculkan
//                        return date <= today;
                        return date >= oneMonthAgo && date <= today;
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
        builder.setSelection(new Pair<>(dateFromLong, dateToLong));  //atur default pick ke rentang seminggu
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> updateSelectedDates(selection));

        return dateRangePicker;
    }

    private void updateSelectedDates(Pair<Long, Long> selection) {
        // Format tanggal
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedStartDate = dateFormat.format(new Date(selection.first));
        String formattedEndDate = dateFormat.format(new Date(selection.second));

        // Menampilkan tanggal pada TextView
        tvDateFrom.setText(formattedStartDate);
        tvDateTo.setText(formattedEndDate);

        dateFromLong = selection.first;
        dateToLong = selection.second;
        LoadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
//        printerUtils.handleActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            //ambil dari camera
            case JConst.REQUEST_FILTER_BARANG:
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
                if (resultCode == RESULT_OK && data != null) {
                    Bundle extra = data.getExtras();
                    String uuid = extra.getString("uuid");
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