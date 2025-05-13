package com.orion.pos_crushty_android.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.ui.shift_kasir.ShiftPenerimaanAktualInputActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiHelper {

    public static void handleError(Context context, VolleyError error, Runnable retryAction) {
        // Cek apakah ada respons dari jaringan
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null) {
            int statusCode = networkResponse.statusCode;
            String responseData = new String(networkResponse.data); // Convert response data to String

            // Cek apakah status code 401 (Unauthorized)
            if (statusCode == 401) {
                handleUnauthorized(JApplication.currentActivity);
                return; // Langsung keluar dari metode ini
            }

            try {
                // Parse respons menjadi JSON untuk mendapatkan keterangan error
                JSONObject jsonResponse = new JSONObject(responseData);
                String keterangan = jsonResponse.getString("keterangan");
                // Tampilkan pesan keterangan error
                ShowDialog.infoDialog(JApplication.currentActivity, JApplication.currentActivity.getString(R.string.information), keterangan);
            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Jika tidak ada respons, maka problem dari jaringan
            JApplication.getInstance().real_url = Routes.URL_API_AWAL;
//            ShowDialog.infoDialog(JApplication.currentActivity, JApplication.currentActivity.getString(R.string.information), "Jaringan bermasalah, silahkan cek koneksi anda.");

            ShowDialog.confirmDialog(JApplication.currentActivity, JApplication.currentActivity.getString(R.string.information),
                    "Ada kesalahan koneksi ke server, silahkan cek koneksi anda.", retryAction);
        }
    }

    // Metode tambahan untuk menangani Unauthorized (status code 401)
    private static void handleUnauthorized(Activity activity) {
        ShowDialog.infoDialog(activity, activity.getString(R.string.information),
                "Sesi login telah berakhir. Silahkan login kembali.", ()->{
                    JApplication.isSyncAwal = true;
                    JApplication.isRememberUser = true;
                    JApplication.isAutoLogin = false;
                    SharedPrefsUtils.setBooleanPreference(activity, "is_logged_in", false);
                    activity.startActivity(new Intent(activity, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    deleteUserLogon(activity);

                });
    }

    public static void deleteUserLogon(Activity activity) {
        String url = Routes.url_master() + "user_logon/delete";
        // Membuat request body dalam bentuk JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            requestBody.put("user_id", SharedPrefsUtils.getStringPreference(activity, "user_id"));
            requestBody.put("auto_delete", "T");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            ApiHelper.handleError(activity, error, () -> deleteUserLogon(activity));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // contoh header
                headers.put("Api-Key", JConst.SKIP_CHECK_API_KEY); // skip
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

}
