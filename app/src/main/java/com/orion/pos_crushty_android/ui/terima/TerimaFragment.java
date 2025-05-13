package com.orion.pos_crushty_android.ui.terima;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangMstModel;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.ListValue;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.calculateWidth;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameRekapActivity;
import com.orion.pos_crushty_android.ui.terima.TerimaAdapter;
import com.orion.pos_crushty_android.utility.ApiHelper;

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

public class TerimaFragment extends Fragment {
    //var componen/view
    private View v;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private SearchView txtSearch;
    private TextView  tvDateFrom, tvDateTo;
    private ImageButton btnDate;
    long dateFromLong, dateToLong;
    public String uidBarang;


    //var for adapter/list
    private TerimaAdapter mAdapter;
    public List<TerimaBarangMstModel> ListItems = new ArrayList<>();

    //var global
    private FragmentActivity thisActivity;
    private int NoOfColumns;
    private LinearLayoutManager linearLayoutManager;
    final int REQUEST_FILTER_BARANG = 1;
    final int REQUEST_INPUT  = 2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_terima, container, false);
        v = view;
        JApplication.currentActivity = getActivity();

        CreateView();
        InitClass();
        EventClass();
        LoadData();
        return view;
    }

    private void CreateView() {
        rcvLoad = v.findViewById(R.id.rcvLoad);
        swipe = v.findViewById(R.id.swipe);
        txtSearch = v.findViewById(R.id.txtSearch);
        tvDateFrom = v.findViewById(R.id.tvDateFromTrm);
        tvDateTo = v.findViewById(R.id.tvDateToTrm);
        btnDate = v.findViewById(R.id.btnDateTrm);

        thisActivity = getActivity();
    }

    private void InitClass() {
        uidBarang = "";
        Calendar calendar = Calendar.getInstance();
        dateToLong = calendar.getTimeInMillis();
        tvDateTo.setText(Global.getDateFormated(dateToLong, "dd-MM-yyyy"));

        calendar.add(Calendar.DAY_OF_YEAR, -7);
        dateFromLong = calendar.getTimeInMillis();
        tvDateFrom.setText(Global.getDateFormated(dateFromLong, "dd-MM-yyyy"));

        SetJenisTampilan();
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rcvLoad.scrollToPosition(0);
                LoadData();
                swipe.setRefreshing(false);
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
            }
        });
        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                LoadData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                        LoadData();
                    }else if (s.length() >= 2){
                        LoadData();
                    }
                return false;
            }
        });

        txtSearch.setOnCloseListener(() -> {
            LoadData();
            return true;
        });
//
//        clTerima.setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View view) {
//                                               Intent s = new Intent(StokOpnameRekapActivity.this, StokOpnameInputActivity.class);
//                                               StokOpnameRekapActivity.this.startActivity(s);  // Use activity context
//                                           }
//                                       }
//
//        );
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
        dateFromLong = startDate;
        dateToLong = endDate;

        LoadData();
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
        builder.setSelection(new Pair<>(dateFromLong, dateToLong));
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> updateSelectedDates(selection));

        return dateRangePicker;
    }


    private void showDateRangePicker() {
        createDateRangePickerDialog().show(thisActivity.getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }

    public void LoadData() {
        String OutletUID = SharedPrefsUtils.getStringPreference(getContext(), "outlet_uid");
        String ApiKey = SharedPrefsUtils.getStringPreference(getContext(), "api_key");
        String karyawanUid = SharedPrefsUtils.getStringPreference(getContext(), "karyawan_uid");
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("outlet_tujuan_uid", OutletUID);
            if (!uidBarang.equals("")){
                params.put("barang_uid", uidBarang);
            }
            if (!txtSearch.getQuery().toString().equals("")){
                params.put("nomor", txtSearch.getQuery().toString());
            }

            params.put("tanggal_dari", Global.getDateFormatMysql(dateFromLong));
            params.put("operator_dari", ">=");
            params.put("tanggal_sampai", Global.getDateFormatMysql(dateToLong));
            params.put("operator_sampai", "<=");
//            params.put("order","tanggal DESC");
            params.put("karyawan_uid", karyawanUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Routes.url_transaksi()+"terima_barang_mst/getkirim";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<TerimaBarangMstModel> itemDataModels = new ArrayList<>();
                    JSONArray jsonArray = response.optJSONArray("data");

                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.optInt("seqkirim", 0) > 0) { // Safe handling
                                    TerimaBarangMstModel data = new TerimaBarangMstModel(
                                            obj.optString("uid", ""),
                                            obj.optInt("seq", 0),
                                            FungsiGeneral.getTglFormat(FungsiGeneral.getDateFromMysql(obj.getString("tanggal"))),
                                            obj.optString("nomor", ""),
                                            obj.optString("kirim_uid", ""),
                                            obj.optString("outlet_asal_uid", ""),
                                            obj.optString("outlet_tujuan_uid", ""),
                                            obj.optString("karyawan_uid", ""),
                                            FungsiGeneral.getTglFormat(FungsiGeneral.getDateFromMysql(obj.getString("tanggal_kirim"))),
                                            obj.optString("nomor_kirim", ""),
                                            obj.optString("nama_outlet_asal", ""),
                                            obj.optString("nama_outlet_tujuan", ""),
                                            obj.optString("nama_karyawan", ""),
                                            obj.optString("keterangan", ""),
                                            obj.optString("user_id", ""),
                                            Long.toString(Global.getMillisDateFmt(obj.optString("tgl_input", ""), "dd-MM-yyyy hh:mm:ss")),
                                            obj.optString("user_edit", ""),
                                            Long.toString(Global.getMillisDateFmt(obj.optString("tgl_edit", ""), "dd-MM-yyyy hh:mm:ss"))
                                    );
                                    itemDataModels.add(data);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Terjadi kesalahan saat parsing data", Toast.LENGTH_SHORT).show();
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
                    ApiHelper.handleError(thisActivity, error, () -> LoadData());
                    swipe.setRefreshing(false);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", ApiKey);
                return headers;
            }
        };

        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void SetJenisTampilan() {
        NoOfColumns = 1;
        mAdapter = new TerimaAdapter(this, ListItems, R.layout.list_item_terima);
        rcvLoad.setLayoutManager(new GridLayoutManager(getContext(), NoOfColumns, GridLayoutManager.VERTICAL, false));
        linearLayoutManager = (LinearLayoutManager) rcvLoad.getLayoutManager();
        rcvLoad.setAdapter(mAdapter);
    }

    private void ResetData(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = thisActivity;
        switch(requestCode) {
            case REQUEST_INPUT :
                if (resultCode == thisActivity.RESULT_OK && data != null) {
                    Bundle extra = data.getExtras();
                    String uuid = extra.getString("uuid");
                    LoadData();
                }
                break;
        }
    }

}