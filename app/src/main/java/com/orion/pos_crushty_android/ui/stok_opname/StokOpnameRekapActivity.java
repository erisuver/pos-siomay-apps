package com.orion.pos_crushty_android.ui.stok_opname;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcel;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.stok_opname_mst.StokOpnameMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.laporan_penjualan.LaporanPenjualanActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangJualActivity;
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

public class StokOpnameRekapActivity extends AppCompatActivity {
    private RecyclerView rcvLoad;
    private View v;
    private SwipeRefreshLayout swipe;
    private Activity ThisActivity;
    public StokOpnameRekapAdapter mAdapter;
    private TextView txtDari, txtSampai, toolbarTitle, tvDateFrom, tvDateTo;
    private Button btnTambahSO;
    private SearchView txtCari;
    public List<StokOpnameMstModel> ListItems = new ArrayList<>();
    private Toolbar toolbar;
    private ImageButton btnDate;
    private ImageView imgFilter;
    private String SearchQuery = "";
    //var
    final int REQUEST_FILTER_BARANG = 1;
    final int REQUEST_INPUT  = 2;
    private String barangUid = "";
    long dateFromLong, dateToLong;

    PrinterUtils printerUtils;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_opname_rekap);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("Stok Opname"); // Set title text
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        txtDari = findViewById(R.id.txtDari);
        txtSampai = findViewById(R.id.txtSampai);
        txtCari = findViewById(R.id.txtSearchSOP);
        swipe = findViewById(R.id.swipe);
        toolbar = findViewById(R.id.toolbarTrans);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnTambahSO = findViewById(R.id.btnTambahSO);
        imgFilter = findViewById(R.id.imgFilterRekap);
        btnDate = findViewById(R.id.btnDateSO);
        tvDateFrom = findViewById(R.id.tvDateFromSO);
        tvDateTo = findViewById(R.id.tvDateToSO);
        this.mAdapter = new StokOpnameRekapAdapter(this, ListItems, R.layout.list_item_stok_opname_rekap);
        rcvLoad.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
    }

    private void InitClass() {
        this.setTitle("Stok Opname Rekap");
        ThisActivity = this;
        txtDari.setText(FungsiGeneral.serverNow());
        txtSampai.setText(FungsiGeneral.serverNow());

        Calendar calendar = Calendar.getInstance();
        dateToLong = calendar.getTimeInMillis();
        tvDateTo.setText(Global.getDateFormated(dateToLong, "dd-MM-yyyy"));

        calendar.add(Calendar.DAY_OF_YEAR, -7);
        dateFromLong = calendar.getTimeInMillis();
        tvDateFrom.setText(Global.getDateFormated(dateFromLong, "dd-MM-yyyy"));

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
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        long oneMonthAgo = calendar.getTimeInMillis();

        // Mengatur batasan
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(oneMonthAgo)
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
        builder.setSelection(new Pair<>(dateFromLong, dateToLong));
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

        //tambahin ke var date long keperluan load local, tidak perlu baris ini jika konek ke cloud
        dateFromLong = selection.first;
        dateToLong = selection.second;
        LoadData();
    }


    private void LoadData() {
        swipe.setRefreshing(true);
        String dari = FungsiGeneral.getTglFormatMySql(dateFromLong);
        String sampai = FungsiGeneral.getTglFormatMySql(dateToLong);
        String OutletUID = SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid");
        String userId = SharedPrefsUtils.getStringPreference(getApplicationContext(), "user_id");
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("nomor", txtCari.getQuery().toString());
            params.put("tanggal_dari", dari);
            params.put("operator_dari", ">=");
            params.put("tanggal_sampai", sampai);
            params.put("operator_sampai", "<=");
            params.put("outlet_uid",OutletUID);
//            params.put("order","tanggal DESC");
            params.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_transaksi() + "stok_opname_mst/get";//" Filter;
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<StokOpnameMstModel> itemDataModels = new ArrayList<>();
                    itemDataModels.clear();
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.getInt("seq") > 0) {
                                    StokOpnameMstModel Data = new StokOpnameMstModel(
                                            obj.getInt("seq"),
                                            obj.getString("uid"),
                                            obj.getString("nomor"),
                                            FungsiGeneral.getDateFromMysql(obj.getString("tanggal")),
                                            obj.getString("keterangan"),
                                            obj.getString("outlet_uid"));
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
                    ApiHelper.handleError(StokOpnameRekapActivity.this, error, () -> LoadData());
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
        txtDari = findViewById(R.id.txtDari);
        txtSampai = findViewById(R.id.txtSampai);
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });
        btnTambahSO.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent s = new Intent(StokOpnameRekapActivity.this, StokOpnameInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   StokOpnameRekapActivity.this.startActivityForResult(s, REQUEST_INPUT);  // Use activity context
                   LoadData();
               }
           }

        );

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StokOpnameRekapActivity.this, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_FILTER_BARANG);
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
                LoadData();
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
                    barangUid = extra.getString("barang_jual_uid");
                    LoadData();

                    if (!extra.getString("barang_jual_uid").isEmpty()){
                        imgFilter.setColorFilter(getColor(R.color.generic_1), PorterDuff.Mode.SRC_IN);
                    } else {
                        imgFilter.clearColorFilter(); // Kembalikan ke warna aslinya
                    }
                }
                break;

            case REQUEST_INPUT :
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