package com.orion.pos_crushty_android.ui.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.login.LoginModel;
import com.orion.pos_crushty_android.databases.login.LoginTable;
import com.orion.pos_crushty_android.databases.master_outlet.MasterOutletModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.ListValue;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.laporan_keuangan.LaporanKeuanganActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private ImageButton btnLogin;
    private TextInputEditText txtUserId, txtPassword;
    private AutoCompleteTextView spnOutlet;
    private List<String> listOutletUID;
    private List<String> listOutletString;
    private ArrayAdapter<String> AdapterOutlet;
    private String outletUID;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        JApplication.currentActivity = this;
        GetPermission();
        CreateView();
        InitView();
        EventView();
    }

    private void CreateView() {
        btnLogin = findViewById(R.id.btnLogin);
        txtUserId = findViewById(R.id.txtUserId);
        txtPassword = findViewById(R.id.txtPassword);
        spnOutlet = findViewById(R.id.spnOutlet);
        loading = Global.createProgresSpinner(this, getString(R.string.loading));
    }

    private void InitView() {
        txtUserId.setText("");
        txtPassword.setText("");
        txtPassword.setHint("Password");
        txtUserId.setHint("User");
        Global.setEnabledClickAutoCompleteText(spnOutlet, false);
        IsiDataOutlet();
        if (JApplication.isRememberUser){
            txtUserId.setText(SharedPrefsUtils.getStringPreference(this, "user_id"));
        }
    }

    private void EventView() {
        btnLogin.setOnClickListener(view -> {
            if (isValid()) {
                Login();
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    txtPassword.setHint("");
                } else {
                    txtPassword.setHint("Password");
                }
            }
        });
        txtUserId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    txtUserId.setHint("");
                } else {
                    txtUserId.setHint("User");
                }
            }
        });

        spnOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnOutlet.showDropDown();
            }
        });

        spnOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                outletUID = listOutletUID.get(position);
            }
        });
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(txtUserId.getText().toString())) {
            // Set error jika teks kosong
            Snackbar.make(findViewById(android.R.id.content), "User wajib diisi.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(txtPassword.getText().toString())) {
            // Set error jika teks kosong
            Snackbar.make(findViewById(android.R.id.content), "Password wajib diisi.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (outletUID.equals("")) {
            // Set error jika teks kosong
            Snackbar.make(findViewById(android.R.id.content), "Outlet wajib diisi.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void save_login(String UserId, String key) {
        LoginTable loginTable = new LoginTable(LoginActivity.this);
        LoginModel loginModel = new LoginModel(UserId, key);
        loginTable.insert(loginModel);

    }

    private void Login() {
        final ProgressDialog loading = new ProgressDialog(LoginActivity.this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setMessage("Loading...");
        loading.show();

        String url = Routes.url_login();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    int statusCode = jObj.getInt("statusCode");
                    String keterangan = jObj.getString("keterangan");

                    if (statusCode == 200) {
                        // Mendapatkan data dari response
                        JSONObject data = jObj.getJSONObject("data");
                        String apiKey = data.optString("api_key");
                        String karyawanUid = data.optString("karyawan_uid");
                        String karyawanNama = data.optString("karyawan_nama");
                        String karyawanKode = data.optString("karyawan_kode");
                        String userId = data.optString("user_id");
                        boolean isShiftStarted = false;
                        if (data.optString("is_shift_started", "").equals(JConst.TRUE_STRING)) {
                            isShiftStarted = true;
                        }

                        // Simpan data login dan navigasi ke halaman utama
                        save_login(txtUserId.getText().toString(), apiKey);

                        //simpan sharepref
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "user_id", userId);
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "api_key", apiKey);
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "karyawan_uid", karyawanUid);
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "karyawan_nama", karyawanNama);
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "karyawan_kode", karyawanKode);
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "outlet_uid", outletUID);
                        SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_shift_started", isShiftStarted);
                        SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_logged_in", true);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ;
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                        loading.dismiss();

                    } else {
                        // Tangani kasus lainnya seperti statusCode 401 atau 500
                        btnLogin.setEnabled(true);
                        loading.dismiss();
                        Toast.makeText(LoginActivity.this, keterangan, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    btnLogin.setEnabled(true);
                    loading.dismiss();
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, JConst.MSG_API_GAGAL_LOAD, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnLogin.setEnabled(true);
                loading.dismiss();
                ApiHelper.handleError(LoginActivity.this, error, () -> Login());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<>();
                params.put("user", txtUserId.getText().toString());
                params.put("pass", txtPassword.getText().toString());
                params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
                params.put("outlet_uid", outletUID);
                return params;
            }
        };

        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    public void IsiDataOutlet() {
        // Tampilkan dialog loading
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setMessage("Loading...");
        loading.show();
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("status_aktif", "T");
            params.put("order","nama");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL API
        String url = Routes.url_master_outlet() + "/get";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    ArrayList<MasterOutletModel> itemDataModels = new ArrayList<>();
                    JSONArray jsonArray = response.optJSONArray("data");

                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if ((obj.getInt("seq") > 0) && !obj.getString("nama").equals("PUSAT")) {
                                    MasterOutletModel data = new MasterOutletModel(
                                            obj.getString("uid"),
                                            obj.getInt("seq"),
                                            Global.getMillisDateFmt(obj.getString("last_update"), "dd-MM-yyyy hh:mm:ss"),
                                            Global.getMillisDateFmt(obj.getString("disable_date"), "dd-MM-yyyy hh:mm:ss"),
                                            obj.getString("user_id"),
                                            Global.getMillisDateFmt(obj.getString("tgl_input"), "dd-MM-yyyy hh:mm:ss"),
                                            obj.getString("kode"),
                                            obj.getString("nama"),
                                            obj.getString("alamat"),
                                            obj.getString("penanggung_jawab"),
                                            obj.getString("kota"),
                                            obj.getString("keterangan"),
                                            obj.getString("telepon"),
                                            obj.getString("bank_uid"),
                                            obj.getString("kas_uid"),
                                            obj.optInt("versi", 0)
                                    );
                                    itemDataModels.add(data);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Terjadi kesalahan saat parsing data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    listOutletString = ListValue.list_outlet(itemDataModels);
                    listOutletUID = ListValue.list_outlet_uid(itemDataModels);
                    String[] mStringArrayOutlet = new String[listOutletString.size()];
                    mStringArrayOutlet = listOutletString.toArray(mStringArrayOutlet);
                    AdapterOutlet = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, mStringArrayOutlet);
                    spnOutlet.setAdapter(AdapterOutlet);

                    spnOutlet.setText("");
                    outletUID = "";

                    loading.dismiss();
                },
                error -> {
                    loading.dismiss();
                    ApiHelper.handleError(LoginActivity.this, error, () -> IsiDataOutlet());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", JConst.SKIP_CHECK_API_KEY);
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    private void GetPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
