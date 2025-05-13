package com.orion.pos_crushty_android.ui.shift_kasir;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailTable;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BerlangsungShiftFragment extends Fragment {
    //var view
    private View v;
    private View viewNotStarted, viewStarted;
    private EditText txtOutlet, txtKasir, txtMulaiShift, txtDurasiShift;
    private Button btnShift;
    private ProgressDialog loading;

    //var
    private FragmentActivity thisActivity;
    private final int REQUEST_MULAI_SHIFT = 1;
    private final int REQUEST_AKHIRI_SHIFT = 2;
    private Handler handler;
    private long startTime; // Time when the timer started
    private long serverTime; // Current server time
    private long elapsedTime; // Elapsed time calculated from server
    private boolean isShiftStarted;
    private String uidShiftMaster;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_berlangsung_shift, container, false);
        v = view;
        JApplication.currentActivity = getActivity();
        CreateView();
        InitClass();
        EventClass();
        LoadData();

        return view;
    }

    private void CreateView() {
        viewNotStarted = v.findViewById(R.id.viewNotStarted);
        viewStarted = v.findViewById(R.id.viewStarted);
        txtOutlet = v.findViewById(R.id.txtOutlet);
        txtKasir = v.findViewById(R.id.txtKasir);
        txtMulaiShift = v.findViewById(R.id.txtMulaiShift);
        txtDurasiShift = v.findViewById(R.id.txtDurasiShift);
        btnShift = v.findViewById(R.id.btnShift);

        thisActivity = getActivity();
        handler = new Handler();
        loading = Global.createProgresSpinner(thisActivity, getString(R.string.loading));
    }

    private void InitClass() {
        isShiftStarted = false;
    }

    private void EventClass() {
        btnShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (!isShiftStarted){
                    intent = new Intent(thisActivity, ShiftSaldoAwalInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, REQUEST_MULAI_SHIFT);
                }else{
                    intent = new Intent(thisActivity, ShiftPenerimaanAktualInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("uid_shift_master", uidShiftMaster);
                    startActivityForResult(intent, REQUEST_AKHIRI_SHIFT);
                }


            }
        });
    }

    private void updateUI(boolean isAlreadyStarted){
        if (isAlreadyStarted){
            viewStarted.setVisibility(View.VISIBLE);
            viewNotStarted.setVisibility(View.GONE);
            btnShift.setText("Akhiri Shift");
            fetchServerTime();
        }else{
            viewStarted.setVisibility(View.GONE);
            viewNotStarted.setVisibility(View.VISIBLE);
            btnShift.setText("Mulai Shift");
        }
    }

    public void LoadData() {
        loading.show();

        // URL API
        String url = Routes.url_transaksi() + "shift_master/get";

        // Membuat request body dalam bentuk JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("outlet_uid", SharedPrefsUtils.getStringPreference(thisActivity, "outlet_uid"));
            requestBody.put("is_max_tanggal", "T");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat request dengan JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                String userIdLogin = SharedPrefsUtils.getStringPreference(thisActivity, "user_id");
                String userIdShift = "";
                //ambil dari result json
                JSONArray jsonArray;
                isShiftStarted = false;
                if (!response.isNull("data")) {
                    jsonArray = response.getJSONArray("data");
                    JSONObject obj = jsonArray.optJSONObject(0);    //ambil index ke 0 karna hasilnya pasti satu (pake max tanggal)

                    startTime = Global.getMillisDateFmt(obj.getString("shift_mulai"), "yyyy-MM-dd HH:mm:ss");
                    isShiftStarted = obj.isNull("shift_akhir") ||
                            obj.optString("shift_akhir", "").isEmpty() ||
                            obj.optString("shift_akhir").equals("0000-00-00 00:00:00");

                    uidShiftMaster = obj.getString("uid");
                    userIdShift = obj.getString("user_id");
                }
                txtOutlet.setText(GlobalTable.getNamaOutlet(thisActivity));
                if (isShiftStarted && !userIdLogin.equalsIgnoreCase(userIdShift)){
                    txtKasir.setText(GlobalTable.getKodeNamaKasir(userIdShift));
                    isShiftStarted = false; //ganti flag menjadi false karena bukan shift milik user login
                    updateUI(true); //ui ditampilkan sedang berjalan shift
                    btnShift.setEnabled(false);
                }else {
                    txtKasir.setText(GlobalTable.getKodeNamaKasir(userIdLogin));
                    updateUI(isShiftStarted);
                }

                SharedPrefsUtils.setBooleanPreference(getContext(), "is_shift_started", isShiftStarted);
                //update ui
                loading.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(thisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
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
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(thisActivity, "api_key")); // skip
                return headers;
            }
        };

        // Tambahkan request ke request queue
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private void fetchServerTime() {
        // Simulating fetching server time (replace with actual API call)
        serverTime = System.currentTimeMillis(); // Replace with actual server time

        // If startTime is not set, initialize it
        if (startTime == 0) {
            startTime = serverTime;
        }

        // waktu mulai
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy (HH:mm)", new Locale("id", "ID"));
        txtMulaiShift.setText(sdf.format(startTime));

        startTimer();
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Calculate elapsed time based on the current time and saved start time
                elapsedTime = System.currentTimeMillis() - startTime;
                updateTimer(elapsedTime);
                handler.postDelayed(this, 1000); // Repeat every second
            }
        }, 1000);
    }

    private void updateTimer(long millis) {
        long days = millis / (1000 * 60 * 60 * 24);
        long hours = (millis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millis % (1000 * 60)) / 1000;

        String timeFormatted = String.format("%02d Hari : %02d Jam : %02d Menit : %02d Detik", days, hours, minutes, seconds);
        txtDurasiShift.setText(timeFormatted);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Stop the timer when activity is destroyed
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = thisActivity;
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_MULAI_SHIFT:
                    LoadData();
                    break;
                case REQUEST_AKHIRI_SHIFT:
                    startTime = 0;
                    serverTime = 0;
                    elapsedTime = 0;

                    //hapus tabel keranjang
                    new KeranjangMasterTable(thisActivity).deleteAll();
                    new KeranjangDetailTable(thisActivity).deleteAll();
                    MainActivity.setInitMainActivity(false);

                    LoadData();
                    break;
            }
        }
    }
}

