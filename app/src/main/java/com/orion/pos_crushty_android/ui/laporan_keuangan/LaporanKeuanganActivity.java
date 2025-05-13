package com.orion.pos_crushty_android.ui.laporan_keuangan;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.SyncData;
import com.orion.pos_crushty_android.databases.laporan_keuangan.LaporanKeuanganModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangInputActivity;
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

public class LaporanKeuanganActivity extends AppCompatActivity implements CalendarPickerController {

    private static final String LOG_TAG = "Laporan Keuangan";

    Toolbar toolbar;
    AgendaCalendarView mAgendaCalendarView;
    List<CalendarEvent> eventList = new ArrayList<>();
    Calendar minDate, maxDate;
    ProgressDialog loading;
    ImageView imgDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_keuangan);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();
        cekSync();//LoadData();
    }

    private void CreateView() {
        toolbar = findViewById(R.id.toolbar);
        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view);
        imgDatePicker = findViewById(R.id.imgDatePicker);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void InitClass() {
        // minimum and maximum date of our calendar
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.YEAR, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
//        maxDate.add(Calendar.DAY_OF_MONTH, 1);
        maxDate.setTimeInMillis(System.currentTimeMillis()); // Default max date to today
    }

    private void EventClass() {
        imgDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthYearPickerDialog();
            }
        });
    }

    public void showMonthYearPickerDialog() {
        // Inflate custom layout with NumberPickers
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);

        final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);
        final AppCompatButton btnReset = dialogView.findViewById(R.id.btnReset);

        // Array of month names in Indonesian
        final String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

        // Set month range (1-12)
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(months);  // Display names of months in Indonesian

        // Set year range (adjust based on requirement)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 2); // min 2 years back
        yearPicker.setMaxValue(currentYear); // max this year
        yearPicker.setValue(currentYear); // set current year as default

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, which) -> {
                    int selectedMonth = monthPicker.getValue(); // Get selected month (1-12)
                    int selectedYear = yearPicker.getValue(); // Get selected year

                    // Update minDate and maxDate
                    updateMinMaxDate(selectedMonth, selectedYear);
                })
                .setNegativeButton("Cancel", null)
                .create();

        // Set onClickListener for Reset button to dismiss dialog and reset date
        btnReset.setOnClickListener(view -> {
            dialog.dismiss(); // Close the dialog
            resetDate(); // Call reset date logic
        });

        // Show the dialog
        dialog.show();
    }

    private void updateMinMaxDate(int selectedMonth, int selectedYear) {
        // Set minDate to the 1st day of the selected month/year
        minDate.set(Calendar.YEAR, selectedYear);
        minDate.set(Calendar.MONTH, selectedMonth - 1); // Calendar month is zero-based
        minDate.set(Calendar.DAY_OF_MONTH, 1);

        // Set maxDate to the last day of the selected month/year
        maxDate.set(Calendar.YEAR, selectedYear);
        maxDate.set(Calendar.MONTH, selectedMonth - 1);
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Now apply minDate and maxDate wherever needed
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        LoadData();
    }

    private void resetDate() {
        // minimum and maximum date of our calendar
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.YEAR, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.setTimeInMillis(System.currentTimeMillis()); // Default max date to today
        LoadData();
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
                    LoadData();
                }
            });
        } else {
            // Jika tidak ada data yang perlu disinkronkan, lanjutkan langsung
            LoadData();
        }
    }

    private void LoadData() {
//        loading.show();
        // URL API
        String url = Routes.url_laporan() + "lap_keuangan/get_list_nominal_keuangan";
        // Membuat request body dalam bentuk JSONObject
        String karyawanUid = SharedPrefsUtils.getStringPreference(getApplicationContext(), "karyawan_uid");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(getApplicationContext(), "outlet_uid"));
            requestBody.put("tgl_dari", Global.getDateFormatMysql(minDate.getTimeInMillis()));
            requestBody.put("tgl_sampai", Global.getDateFormatMysql(maxDate.getTimeInMillis()));
//            requestBody.put("karyawan_uid", karyawanUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat request dengan JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            ArrayList<LaporanKeuanganModel> itemDataModels = new ArrayList<>();
            try {
                JSONArray jsonArray;
                if (!response.isNull("data")) {
                    jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String[] nameParts = obj.getString("namess").split("#");
                        String date = nameParts[0];
                        String uid = nameParts[1];
                        String tipeBayar = nameParts[2];
                        double total = obj.getDouble("total");

                        LaporanKeuanganModel data = new LaporanKeuanganModel(
                                date,
                                uid,
                                tipeBayar,
                                total
                        );
                        itemDataModels.add(data);
                    }
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
                eventList = new ArrayList<>();
                mockList(eventList, itemDataModels);
                mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
                mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());
                loading.dismiss();
            }
        }, error -> {
            ApiHelper.handleError(LaporanKeuanganActivity.this, error, () -> LoadData());
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

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Log.d(LOG_TAG, String.format("Selected event: %s", event));
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        }
    }

    private void mockList(List<CalendarEvent> eventList, List<LaporanKeuanganModel> eventDataList) {

        // Grup data berdasarkan tanggal
        Map<String, List<LaporanKeuanganModel>> groupedData = groupByDate(eventDataList);

        // Set minDate menjadi tanggal 1 dari bulan yang sama dengan MinDate
        Calendar vMinDate = (Calendar) minDate.clone();
        vMinDate.set(Calendar.DAY_OF_MONTH, 1); // Set tanggal 1
        vMinDate.set(Calendar.DAY_OF_WEEK, minDate.getFirstDayOfWeek()); // Set ke hari pertama di minggu itu (misal Senin)


        // Set maxDate menjadi tanggal terakhir dari bulan yang sama dengan MaxDate
        Calendar vMaxDate = (Calendar) maxDate.clone();
        vMaxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH)); // Set tanggal terakhir bulan
//        vMaxDate.set(Calendar.DAY_OF_WEEK, maxDate.getFirstDayOfWeek() + 6); // Set ke hari terakhir minggu (misal Minggu)

        // Iterasi melalui setiap tanggal dalam rentang minDate hingga maxDate
        Calendar currentDate = (Calendar) vMinDate.clone();
        while (!currentDate.after(vMaxDate)) {
            String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate.getTime());
            List<LaporanKeuanganModel> eventsOnDate = groupedData.get(dateStr);

            if (eventsOnDate == null || eventsOnDate.isEmpty()) {
                // Jika tidak ada transaksi pada tanggal ini, buat event "Tidak Ada Penjualan"
                DrawableCalendarEvent noSaleEvent = new DrawableCalendarEvent(
                        "Tidak Ada Penjualan", "", "",
                        ContextCompat.getColor(this, R.color.gray),
                        (Calendar) currentDate.clone(), (Calendar) currentDate.clone(), true
                );
                eventList.add(noSaleEvent);
            } else {
                // Jika ada transaksi, buat event berdasarkan tipeBayar
                double tunaiTotal = 0;
                double debitTotal = 0;
                double qrisTotal = 0;
                double ovoTotal = 0;

                // Assign nilai total berdasarkan tipeBayar
                for (LaporanKeuanganModel event : eventsOnDate) {
                    switch (event.getTipeBayar()) {
                        case JConst.TIPE_BAYAR_TUNAI:
                            tunaiTotal = event.getTotal();
                            break;
                        case JConst.TIPE_BAYAR_DEBIT:
                            debitTotal = event.getTotal();
                            break;
                        case JConst.TIPE_BAYAR_QRIS:
                            qrisTotal = event.getTotal();
                            break;
                        case JConst.TIPE_BAYAR_OVO:
                            ovoTotal = event.getTotal();
                            break;
                    }
                }

                // Konversi total ke string dengan format
                String strTunaiTotal = Global.FloatToStrFmt(tunaiTotal, true);
                String strDebitTotal = Global.FloatToStrFmt(debitTotal, true);
                String strQrisTotal = Global.FloatToStrFmt(qrisTotal, true);
                String strOVOTotal = Global.FloatToStrFmt(ovoTotal, true);

                // Buat event QRIS
                DrawableCalendarEvent qrisEvent = new DrawableCalendarEvent(
                        "QRIS", "", strQrisTotal,
                        ContextCompat.getColor(this, R.color.generic_3),
                        (Calendar) currentDate.clone(), (Calendar) currentDate.clone(), true
                );
                eventList.add(qrisEvent);

                // Buat event Debit
                DrawableCalendarEvent debitEvent = new DrawableCalendarEvent(
                        "Debit", "", strDebitTotal,
                        ContextCompat.getColor(this, R.color.generic_2),
                        (Calendar) currentDate.clone(), (Calendar) currentDate.clone(), true
                );
                eventList.add(debitEvent);

                // Buat event Tunai
                DrawableCalendarEvent tunaiEvent = new DrawableCalendarEvent(
                        "Tunai", "", strTunaiTotal,
                        ContextCompat.getColor(this, R.color.generic_1),
                        (Calendar) currentDate.clone(), (Calendar) currentDate.clone(), true
                );
                eventList.add(tunaiEvent);

                // Buat event OVO
                DrawableCalendarEvent ovoEvent = new DrawableCalendarEvent(
                        "OVO", "", strOVOTotal,
                        ContextCompat.getColor(this, R.color.generic_4),
                        (Calendar) currentDate.clone(), (Calendar) currentDate.clone(), true
                );
                eventList.add(ovoEvent);
            }

            // Pindah ke tanggal berikutnya
            currentDate.add(Calendar.DATE, 1);
        }

    }

    // Helper function to group by date
    private Map<String, List<LaporanKeuanganModel>> groupByDate(List<LaporanKeuanganModel> eventDataList) {
        Map<String, List<LaporanKeuanganModel>> groupedData = new HashMap<>();

        for (LaporanKeuanganModel event : eventDataList) {
            String date = event.getDate();
            if (!groupedData.containsKey(date)) {
                groupedData.put(date, new ArrayList<>());
            }
            groupedData.get(date).add(event);
        }

        return groupedData;
    }

    private long parseDateToMillis(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null ? parsedDate.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
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