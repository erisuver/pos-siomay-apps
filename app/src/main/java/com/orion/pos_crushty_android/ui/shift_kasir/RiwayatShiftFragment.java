package com.orion.pos_crushty_android.ui.shift_kasir;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.master_outlet.MasterOutletModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.shift_master.ShiftMasterModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.laporan_penjualan.LaporanPenjualanAdapter;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RiwayatShiftFragment extends Fragment {
    //var view
    private View v;
    private RecyclerView rcvLoad;
    private TextView tvDateFrom, tvDateTo;
    private ImageButton btnDate;

    //var for adapter/list
    private RiwayatShiftAdapter mAdapter;
    public List<ShiftMasterModel> ListItems = new ArrayList<>();

    //var global
    private FragmentActivity thisActivity;
    private String periodeDari, periodeSampai;
    private ProgressDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_riwayat_shift, container, false);
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
        tvDateFrom = v.findViewById(R.id.tvDateFrom);
        tvDateTo = v.findViewById(R.id.tvDateTo);
        btnDate = v.findViewById(R.id.btnDate);

        thisActivity = getActivity();
        loading = Global.createProgresSpinner(thisActivity, getString(R.string.loading));
    }

    private void InitClass() {
        long dateFromLong, dateToLong;
        Calendar calendar = Calendar.getInstance();
        dateToLong = calendar.getTimeInMillis();
        tvDateTo.setText(Global.getDateFormated(dateToLong, "dd-MM-yyyy"));
        periodeSampai = Global.getDateFormated(dateToLong, "yyyy-MM-dd");

        calendar.add(Calendar.DAY_OF_YEAR, -7);
        dateFromLong = calendar.getTimeInMillis();
        tvDateFrom.setText(Global.getDateFormated(dateFromLong, "dd-MM-yyyy"));
        periodeDari = Global.getDateFormated(dateFromLong, "yyyy-MM-dd");

        SetJenisTampilan();
    }

    private void EventClass() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangePicker();
            }
        });
    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thisActivity);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new RiwayatShiftAdapter(this, ListItems, R.layout.list_item_riwayat_shift);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    private void showDateRangePicker() {
        createDateRangePickerDialog().show(thisActivity.getSupportFragmentManager(), "DATE_RANGE_PICKER");
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
        builder.setSelection(new Pair<>(oneWeekAgo, today));
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> updateSelectedDates(selection));

        return dateRangePicker;
    }


    private void updateSelectedDates(Pair<Long, Long> selection) {
        // Format tanggal
        long startDate = selection.first;  // Tanggal mulai tidak perlu diubah
        long endDate = selection.second;   // Tanggal akhir

        // Menampilkan tanggal pada TextView
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedStartDate = dateFormat.format(new Date(startDate));
        String formattedEndDate = dateFormat.format(new Date(endDate));
        tvDateFrom.setText(formattedStartDate);
        tvDateTo.setText(formattedEndDate);

        //masukan ke var filter periode
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        periodeDari = dateFormat2.format(new Date(startDate));
        periodeSampai = dateFormat2.format(new Date(endDate));

        LoadData();
    }

    public void LoadData() {
        mAdapter.removeAllModel();
        loading.show();
        // URL API
        String url = Routes.url_transaksi() + "shift_master/get";
        // Membuat request body dalam bentuk JSONObject
        String karyawanUid = SharedPrefsUtils.getStringPreference(getContext(), "karyawan_uid");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(getContext(), "outlet_uid"));
            requestBody.put("periode_awal", periodeDari);
            requestBody.put("periode_akhir", periodeSampai);
            requestBody.put("is_riwayat", "T");
            requestBody.put("order", "m.seq desc");
            requestBody.put("karyawan_uid", karyawanUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat request dengan JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                ArrayList<ShiftMasterModel> itemDataModels = new ArrayList<>();
                JSONArray jsonArray;
                if (!response.isNull("data")) {
                    jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj.getInt("seq") > 0) {
                            ShiftMasterModel data = new ShiftMasterModel(
                                    obj.getString("uid"),
                                    obj.getString("tanggal"),
                                    obj.getString("outlet_uid"),
                                    obj.getString("karyawan_uid"),
                                    formatTanggal(obj.getString("shift_mulai")),
                                    formatTanggal(obj.getString("shift_akhir")),
                                    obj.getDouble("saldo_awal"),
                                    obj.getDouble("tunai_aktual"),
                                    obj.getDouble("non_tunai_aktual"),
                                    obj.getDouble("pengeluaran_lain"),
                                    obj.getDouble("setor_tunai"),
                                    obj.getDouble("sisa_kas_tunai"),
                                    obj.getDouble("program_tunai"),
                                    Math.abs(obj.getDouble("selisih")),
                                    obj.getString("keterangan"),
                                    obj.getString("user_id"),
                                    obj.getString("tgl_input")
                            );
                            data.setNamaKasir(obj.getString("nama_kasir"));
                            itemDataModels.add(data);
                        }
                    }
                } else {
                    // Penanganan jika data bernilai null
                    String keterangan = response.getString("keterangan");
                    Toast.makeText(thisActivity, keterangan, Toast.LENGTH_SHORT).show();
                }

                mAdapter.addModels(itemDataModels);
                loading.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            } finally {
                loading.dismiss();
            }
        }, error -> {
            ApiHelper.handleError(thisActivity, error, () -> LoadData());
            loading.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // contoh header
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(getContext(), "api_key")); // skip
                return headers;
            }
        };

        // Tambahkan request ke request queue
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    public String formatTanggal(String jsonDate) {
        try {
            // Buat SimpleDateFormat untuk parsing tanggal dari format asli
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // Parse tanggal dari string menjadi objek Date
            Date date = originalFormat.parse(jsonDate);

            // Buat SimpleDateFormat baru untuk format yang diinginkan
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy (HH:mm)", new Locale("id", "ID"));

            // Format ulang tanggal menjadi format yang diinginkan
            return targetFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Atau bisa return string lain jika terjadi error
        }
    }


}
