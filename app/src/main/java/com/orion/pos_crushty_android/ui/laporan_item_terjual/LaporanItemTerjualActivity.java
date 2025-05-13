package com.orion.pos_crushty_android.ui.laporan_item_terjual;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.penjualan_mst.ItemTerjualModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukLapItemActivity;
import com.orion.pos_crushty_android.ui.laporan_penjualan.LaporanPenjualanActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangJualActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

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

public class LaporanItemTerjualActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvDate, tvCash, tvDebit, tvQRIS, tvOVO, tvTotal;
    private ImageButton btnDate;
    private ImageView imgFilter, imgPrint;
    private View filterTanggal;
    private Spinner spnStatusKirim;

    //var for adapter/list
    private LaporanItemTerjualAdapter mAdapter;
    public List<ItemTerjualModel> ListItems = new ArrayList<>();

    //var
    long dateLong;
    final int REQUEST_FILTER_BARANG = 1;
    private String barangJualUid, outletUid;
    private double vCash, vDebit, vQRIS, vOVO, vTotal, vTotalItem;
    ProgressDialog loading;
    private int idxStatusKirim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JApplication.currentActivity = this;
        setContentView(R.layout.activity_laporan_item_terjual);
        CreateView();
        InitClass();
        EventClass();
//        LoadData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("Laporan Item Terjual"); // Set title text
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvDate = findViewById(R.id.tvDate);
        btnDate = findViewById(R.id.btnDate);
        imgFilter = findViewById(R.id.imgFilter);
        filterTanggal = findViewById(R.id.filterTanggal);
        tvCash = findViewById(R.id.tvCash);
        tvDebit = findViewById(R.id.tvDebit);
        tvQRIS = findViewById(R.id.tvQRIS);
        tvOVO = findViewById(R.id.tvOVO);
        tvTotal = findViewById(R.id.tvTotal);
        imgPrint = findViewById(R.id.imgPrint);
        spnStatusKirim = findViewById(R.id.spnStatusKirim);

        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void InitClass() {
        Calendar calendar = Calendar.getInstance();
        dateLong = Global.getMilliesDateStartEnd(calendar.getTimeInMillis(), true);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        String formattedDate = sdf.format(dateLong);
        tvDate.setText(formattedDate); // Format Indonesia

        SetJenisTampilan();
        isiCombo();
        setEnableDisableView();

        barangJualUid = "";
        outletUid = SharedPrefsUtils.getStringPreference(this, "outlet_uid");
    }

    private void EventClass() {
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaporanItemTerjualActivity.this, LovBarangJualActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_FILTER_BARANG);
            }
        });

        filterTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        imgPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
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

        mAdapter = new LaporanItemTerjualAdapter(this, ListItems, R.layout.list_item_laporan_item_terjual);
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

    public void LoadData() {
        if (idxStatusKirim == 0) {
            // Jika "Sudah Upload" dipilih
            if (!Global.CheckConnectionInternet(LaporanItemTerjualActivity.this)){
                ShowDialog.infoDialog(LaporanItemTerjualActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                spnStatusKirim.setSelection(1);
                return;
            }
            LoadDataOnline();
        } else if (idxStatusKirim == 1) {
            // Jika "Belum Upload" dipilih
            LoadDataItem();
            LoadTotal();
        }
    }

    public void LoadDataItem() {
        // Bersihkan data sebelumnya di adapter
        mAdapter.removeAllModel();
        vTotalItem = 0;

        long startOfDay = Global.getMilliesDateStartEnd(dateLong, true);
        long endOfDay = Global.getMilliesDateStartEnd(dateLong, false);

        // Update filter
        String filter = "";
        if (dateLong != 0) {
            filter += " and m.tanggal >= " + startOfDay + " and m.tanggal <= " + endOfDay;
        }
        if (!barangJualUid.equals("")){
            filter += " and bj.uid = '"+barangJualUid+"' ";
        }
        if (!outletUid.equals("")){
            filter += " and m.outlet_uid = '"+outletUid+"' ";
        }

        // Inisialisasi list untuk menyimpan hasil query
        ArrayList<ItemTerjualModel> ArrPenjualan = new ArrayList<>();
        Cursor cr = null;
        try {
            // SQL query untuk mengambil data
            String sql ="SELECT bj.uid, bj.nama, SUM(d.qty) AS qtyTotal, d.harga as harga, SUM(d.qty) * d.harga as subTotal " +
                        " FROM penjualan_mst m " +
                        " JOIN penjualan_det d ON m.uid = d.master_uid " +
                        " JOIN master_barang_jual bj ON bj.uid = d.barang_jual_uid " +
                        " WHERE m.seq <> 0 and (is_kirim = 'F' or is_kirim is null)" + filter +
                        " GROUP BY bj.uid " +
                        " ORDER BY " +
                        "   CASE " +
                        "     WHEN bj.kategori = 1 THEN 1 " + // Makanan
                        "     WHEN bj.kategori = 3 THEN 2 " + // Paket
                        "     WHEN bj.kategori = 2 THEN 3 " + // Minuman
                        "     ELSE 4 " + // Kategori lainnya
                        "   END, " +
                        "   bj.nama";
            // Eksekusi query
            cr = JApplication.getInstance().db.rawQuery(sql, null);

            // Proses data dari cursor
            if (cr != null && cr.moveToFirst()) {
                do {
                    ItemTerjualModel tempData = new ItemTerjualModel(
                            cr.getString(cr.getColumnIndexOrThrow("uid")),
                            cr.getString(cr.getColumnIndexOrThrow("nama")),
                            cr.getDouble(cr.getColumnIndexOrThrow("qtyTotal")),
                            cr.getDouble(cr.getColumnIndexOrThrow("harga")),
                            cr.getDouble(cr.getColumnIndexOrThrow("subTotal"))
                    );
                    vTotalItem += tempData.getQty();
                    ArrPenjualan.add(tempData);
                } while (cr.moveToNext());
            }


        } catch (Exception e) {
            // Log error untuk debugging
            Log.e("LoadData", "Error saat mengambil data: ", e);
        } finally {
            // Pastikan cursor ditutup untuk menghindari memory leaks
            if (cr != null) {
                cr.close();
            }
        }

        // Tambahkan data ke adapter
        mAdapter.addModels(ArrPenjualan);
    }

    private void LoadTotal(){
        long startOfDay = Global.getMilliesDateStartEnd(dateLong, true);
        long endOfDay = Global.getMilliesDateStartEnd(dateLong, false);

        // Update filter
        String filter = "";
        if (dateLong != 0) {
            filter += " and m.tanggal >= " + startOfDay + " and m.tanggal <= " + endOfDay;
        }
        if (!outletUid.equals("")){
            filter += " and m.outlet_uid = '"+outletUid+"' ";
        }

        // Inisialisasi list untuk menyimpan hasil query
        Cursor cr = null;
        try {
            // SQL query untuk mengambil data
            String sql = "SELECT " +
                    "SUM(CASE WHEN d.tipe_bayar = 'T' THEN d.total ELSE 0 END) AS cash, " +
                    "SUM(CASE WHEN d.tipe_bayar = 'D' THEN d.total ELSE 0 END) AS debit, " +
                    "SUM(CASE WHEN d.tipe_bayar = 'Q' THEN d.total ELSE 0 END) AS qris, " +
                    "SUM(CASE WHEN d.tipe_bayar = 'O' THEN d.total ELSE 0 END) AS ovo " +
                    "FROM penjualan_mst m " +
                    "JOIN penjualan_bayar d ON m.uid = d.master_uid " +
                    "WHERE m.seq <> 0 and (is_kirim = 'F' or is_kirim is null) " + filter;
            // Eksekusi query
            cr = JApplication.getInstance().db.rawQuery(sql, null);

            // Proses data dari cursor
            if (cr != null && cr.moveToFirst()) {
                vCash = cr.getDouble(cr.getColumnIndexOrThrow("cash"));
                vDebit = cr.getDouble(cr.getColumnIndexOrThrow("debit"));
                vQRIS = cr.getDouble(cr.getColumnIndexOrThrow("qris"));
                vOVO = cr.getDouble(cr.getColumnIndexOrThrow("ovo")); // Diperbaiki
                vTotal = vCash+vDebit+vQRIS+vOVO;

                tvCash.setText(Global.FloatToStrFmt(vCash));
                tvDebit.setText(Global.FloatToStrFmt(vDebit));
                tvQRIS.setText(Global.FloatToStrFmt(vQRIS));
                tvOVO.setText(Global.FloatToStrFmt(vOVO));
                tvTotal.setText(Global.FloatToStrFmt(vTotal));
            }

        } catch (Exception e) {
            // Log error untuk debugging
            Log.e("Load Total", "Error saat mengambil data: ", e);
        } finally {
            // Pastikan cursor ditutup untuk menghindari memory leaks
            if (cr != null) {
                cr.close();
            }
        }
    }

    public void LoadDataOnline() {
        loading.show();
        // Bersihkan data sebelumnya
        mAdapter.removeAllModel();
        vTotalItem = 0;
        tvCash.setText(Global.FloatToStrFmt(0));
        tvDebit.setText(Global.FloatToStrFmt(0));
        tvQRIS.setText(Global.FloatToStrFmt(0));
        tvOVO.setText(Global.FloatToStrFmt(0));
        tvTotal.setText(Global.FloatToStrFmt(0));

        // URL API
        String url = Routes.url_laporan() + "laporan_item_terjual/get";


        long startOfDay = Global.getMilliesDateStartEnd(dateLong, true);
        long endOfDay = Global.getMilliesDateStartEnd(dateLong, false);

        // Membuat request body dalam bentuk JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid"));
            requestBody.put("tgl_dari", Global.getDateFormatMysql(startOfDay));
            requestBody.put("tgl_sampai", Global.getDateFormatMysql(endOfDay));
            requestBody.put("barang_jual_uid", barangJualUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat request dengan JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            ArrayList<ItemTerjualModel> itemDataModels = new ArrayList<>();
            try {
                JSONArray jsonArray;
                if (!response.isNull("data")) {
                    jsonArray = response.getJSONArray("data");
                    double vTotalPendapatan = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        ItemTerjualModel data = new ItemTerjualModel(
                                obj.getString("uid"),
                                obj.getString("nama"),
                                obj.getDouble("qtyTotal"),
                                obj.getDouble("harga"),
                                obj.getDouble("subTotal")
                        );
                        itemDataModels.add(data);

                        vTotalItem += data.getQty();

                        vCash = obj.getDouble("cash");
                        vDebit = obj.getDouble("debit");
                        vQRIS = obj.getDouble("qris");
                        vOVO = obj.getDouble("ovo");
                        vTotal = obj.getDouble("grandtotal");
                    }

                    tvCash.setText(Global.FloatToStrFmt(vCash));
                    tvDebit.setText(Global.FloatToStrFmt(vDebit));
                    tvQRIS.setText(Global.FloatToStrFmt(vQRIS));
                    tvOVO.setText(Global.FloatToStrFmt(vOVO));
                    tvTotal.setText(Global.FloatToStrFmt(vTotal));

                } else {
                    // Penanganan jika data bernilai null
                    String keterangan = response.getString("keterangan");
                    Toast.makeText(getApplicationContext(), keterangan, Toast.LENGTH_SHORT).show();
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
            ApiHelper.handleError(LaporanItemTerjualActivity.this, error, () -> LoadData());
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

    public void checkIsCanPrint(String tipePrint) {
        if (tipePrint.equals(JConst.PRINT_DAILY_X)){
            if (Global.getDateFormatMysql(dateLong).equals(Global.getDateFormatMysql(Global.serverNowLong()))){
                startIntentCetak(tipePrint);
            }else {
                ShowDialog.infoDialog(LaporanItemTerjualActivity.this, getApplicationContext().getString(R.string.information), "Print harus ditanggal hari ini.");
            }
        }else {
            loading = Global.createProgresSpinner(this, getString(R.string.loading));
            loading.show();

            // URL API
            String url = Routes.url_global() + "is_shift_end/get";

            // Membuat request body dalam bentuk JSONObject
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("tanggal", Global.getDateFormatMysql(dateLong));
                requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
                requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Membuat request dengan JsonObjectRequest
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
                try {
                    //ambil dari result json
                    JSONObject data = response.getJSONObject("data"); // Ambil objek data
                    if (data.has("hasil")) { // Pastikan key 'hasil' ada
                        int jmlShift = data.optInt("hasil");

                        if (jmlShift == 0) {    //shift kemarin / hariini
                            ShowDialog.infoDialog(LaporanItemTerjualActivity.this, getApplicationContext().getString(R.string.information), "Print harus ditanggal hari ini atau sudah masuk shift 2.");
                        } else if (jmlShift >= 1) {  //shift hari ini sedang berjalan shift 2
//                            startIntentCetak(tipePrint);
//                        } else if (jmlShift == 2) {
                            //cek dulu jika hari udah berganti maka reset jumlah print
                            long tglPrint = SharedPrefsUtils.getLongPreference(getApplicationContext(), "tgl_print", 0);
                            if (tglPrint != Global.serverNowWithoutTimeLong()) {
                                tglPrint = Global.serverNowWithoutTimeLong();
                                SharedPrefsUtils.setIntegerPreference(getApplicationContext(), "jumlah_print", 0);
                                SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_sudah_print_z", false);
                                SharedPrefsUtils.setLongPreference(getApplicationContext(), "tgl_print", tglPrint);
                            }

                            int jumlahPrint = SharedPrefsUtils.getIntegerPreference(getApplicationContext(), "jumlah_print", 0);
                            int maxPrint = 1;

                            if (jumlahPrint > maxPrint) {
                                ShowDialog.infoDialog(LaporanItemTerjualActivity.this, getApplicationContext().getString(R.string.information), "Sudah melebihi batas print");
                            } else {
                                SharedPrefsUtils.setIntegerPreference(getApplicationContext(), "jumlah_print", jumlahPrint + 1);
                                SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_sudah_print_z", true);
                                setEnableDisableView();
                                startIntentCetak(tipePrint);
                            }
                        }
                    }
                    //update ui
                    loading.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                } finally {
                    loading.dismiss();
                }
            }, error -> {
                ApiHelper.handleError(getApplicationContext(), error, () -> LoadData());
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
    }

    private void startIntentCetak(String tipePrint){
        Intent intent = new Intent(LaporanItemTerjualActivity.this, CetakStrukLapItemActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("date_long", dateLong);
        intent.putExtra("cash", vCash);
        intent.putExtra("debit", vDebit);
        intent.putExtra("qris", vQRIS);
        intent.putExtra("ovo", vOVO);
        intent.putExtra("total", vTotal);
        intent.putExtra("total_item", vTotalItem);
        intent.putExtra("tipe_print", tipePrint);
        intent.putExtra("status_kirim", idxStatusKirim);
        startActivity(intent);
    }

    private void showDatePicker() {
        // Ambil tanggal sekarang sebagai default
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tampilkan DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Simpan tanggal dalam format timestamp (epoch time)
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    dateLong = calendar.getTimeInMillis();

                    // Format tanggal dalam bahasa Indonesia
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                    String formattedDate = sdf.format(calendar.getTime());

                    // Tampilkan di TextView
                    tvDate.setText(formattedDate);

                    // Panggil function LoadData setelah memilih tanggal
                    LoadData();
                },
                year, month, day
        );

        datePickerDialog.show();
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

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.print_x) {
                    ShowDialog.confirmDialog(LaporanItemTerjualActivity.this, "Cetak", "Apakah Anda yakin ingin melakukan Print Daily X?",
                            "Ya", "Tidak", ()->checkIsCanPrint(JConst.PRINT_DAILY_X), ()->{});
                    return true;
                } else if (id == R.id.print_z) {
                    ShowDialog.confirmDialog(LaporanItemTerjualActivity.this, "Cetak", "Apakah Anda yakin ingin melakukan Print Daily Z?",
                            "Ya", "Tidak", ()->checkIsCanPrint(JConst.PRINT_DAILY_Z), ()->{});
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void setEnableDisableView(){
        boolean isSudahPrintZ = SharedPrefsUtils.getBooleanPreference(getApplicationContext(), "is_sudah_print_z", false);
        imgFilter.setEnabled(!isSudahPrintZ);
        filterTanggal.setEnabled(!isSudahPrintZ);
        imgPrint.setEnabled(!isSudahPrintZ);
        spnStatusKirim.setEnabled(!isSudahPrintZ);
    }

}