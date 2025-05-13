package com.orion.pos_crushty_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.laporan_keuangan.LaporanKeuanganActivity;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangRekapActivity;
import com.orion.pos_crushty_android.ui.laporan_penjualan.LaporanPenjualanActivity;
import com.orion.pos_crushty_android.ui.laporan_item_terjual.LaporanItemTerjualActivity;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainRekapActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.ui.penjualan.PenjualanFragment;
import com.orion.pos_crushty_android.ui.penjualan_keranjang.PenjualanKeranjangActivity;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameRekapActivity;
import com.orion.pos_crushty_android.ui.shift_kasir.ShiftKasirFragment;
import com.orion.pos_crushty_android.ui.terima.TerimaFragment;
import com.orion.pos_crushty_android.utility.ApiHelper;
import com.orion.pos_crushty_android.utility.FileDownloadHelper;
import com.orion.pos_crushty_android.utility.PrinterUtils;
import com.orion.pos_crushty_android.utility.VolleyMultipartRequest;
import com.orion.pos_crushty_android.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    public TextView toolbarTitle, jmlCart, tvOutlet, tvNamaKasir, tvTanggal;
    private BottomNavigationView bottomNavigationView;
    private final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private String tipe_fragment;
    private final String TIPE_FRAGMENT_TERIMA = "T";

    PenjualanFragment penjualanFragment = new PenjualanFragment();
    ShiftKasirFragment shiftKasirFragment = new ShiftKasirFragment();
    TerimaFragment terimaFragment = new TerimaFragment();
    private ImageView imgCart, imgFilter;
    PrinterUtils printerUtils;
    private static MainActivity instance;

    //kebutuhan sync
    int delaySync = (10) * 60 * 1000;  //10 menit
    int delaySyncBackground = (10) * 60 * 1000;  //10 menit
    public Handler handlerSync = new Handler();
    private boolean isDoSync = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        JApplication.getInstance().real_url = Routes.URL_API_AWAL;
        JApplication.currentActivity = this;

        //handle kiriman saat selesai melakukan penjualan
        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.containsKey("do_sync")) {
            isDoSync = extra.getBoolean("do_sync", false);
        } else {
            isDoSync = true; // Nilai default jika key tidak ditemukan
        }

        if (Global.CheckConnectionInternet(MainActivity.this)) {
            //cek versi
            //jika harus update maka jalankan updatean. jika tidak maka langsung startview()
            ValidasiVersi();
        }else{
            startView();
        }
    }

    private void startView(){
        boolean isLoggedIn = SharedPrefsUtils.getBooleanPreference(getApplicationContext(), "is_logged_in", false);
        boolean isShiftStarted = SharedPrefsUtils.getBooleanPreference(MainActivity.this, "is_shift_started", false);

        if (!isLoggedIn) {
            // Jika belum login, langsung ke login
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        } else if (!isShiftStarted && JApplication.isAutoLogin) {
            // Jika sudah login tapi shift belum dimulai
            JApplication.isAutoLogin = false; //flag ganti false karena belum mulai shift
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        } else {
            //Memanggil class SyncData dan memulai proses sinkronisasi
            if (isDoSync) {
                SyncData syncData = new SyncData(MainActivity.this);
                syncData.SyncAll();
            }

           /* //panggil handler untuk sync setiap waktu yang ditentukan
            handlerSync.removeCallbacks(runTaskSync);
            handlerSync = new Handler();
            handlerSync.postDelayed(runTaskSync, delaySync);*/

            CreateView();
            InitClass();
            EventClass();
            JApplication.isAutoLogin = true;
        }
    }

    /*private final Runnable runTaskSync = new Runnable() {
        public void run() {
            if (SharedPrefsUtils.getBooleanPreference(getApplicationContext(), "is_logged_in", false)) {
                //Memanggil class SyncData dan memulai proses sinkronisasi
                SyncData syncData = new SyncData(MainActivity.this);
                syncData.SyncAll();

                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                ActivityManager.getMyMemoryState(myProcess);
                boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                if (isInBackground) {
                    handlerSync.postDelayed(this, delaySyncBackground);
                } else {
                    handlerSync.postDelayed(this, delaySync);
                }
            }
        }
    };*/

    private void CreateView() {
        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.drawer);
        bottomNavigationView = findViewById(R.id.navigation);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        imgCart = findViewById(R.id.imgCart);
        imgFilter = findViewById(R.id.imgFilter);
        jmlCart = findViewById(R.id.jmlCart);

        View headerView = navigationView.getHeaderView(0);
        tvOutlet = headerView.findViewById(R.id.tvOutlet);
        tvNamaKasir = headerView.findViewById(R.id.tvNamaKasir);
        tvTanggal = headerView.findViewById(R.id.tvTanggal);

        printerUtils = new PrinterUtils(MainActivity.this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
    }

    private void InitClass() {
        //bottom navigation
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_penjualan);
        setInitMainActivity(true);
    }

    private void EventClass() {
        //toggle button
        actionBarDrawerToggle = new ActionBarDrawerToggle(this , drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //when an item is selected from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sync_data:
                        performActionAfterDrawerClosed(() -> {
                            SyncData syncData = new SyncData(MainActivity.this);
                            syncData.SyncAll();
                        });
                        break;

                    case R.id.penjualan:
                        performActionAfterDrawerClosed(() -> {
                            bottomNavigationView.setSelectedItemId(R.id.navigation_penjualan);
                            loadFragment(penjualanFragment);
                        });
                        break;

                    case R.id.terima_barang:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)){
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }
                            bottomNavigationView.setSelectedItemId(R.id.navigation_terima);
                            loadFragment(terimaFragment);
                        });
                        break;

                    case R.id.shift_kasir:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)){
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }
                            bottomNavigationView.setSelectedItemId(R.id.navigation_shift);
                            loadFragment(shiftKasirFragment);
                        });
                        break;

                    case R.id.pengeluaran_lain:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)){
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }
                            startActivityForResult(new Intent(MainActivity.this, PengeluaranLainRekapActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    JConst.REQUEST_LOAD_PENGELUARAN_LAIN);
                        });
                        break;

                    case R.id.kirim_barang:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)){
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }
                            startActivityForResult(new Intent(MainActivity.this, KirimBarangRekapActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    JConst.REQUEST_LOAD_KIRIM_BARANG);
                        });
                        break;

                    case R.id.stok_opname:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)){
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }
                            startActivityForResult(new Intent(MainActivity.this, StokOpnameRekapActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    JConst.REQUEST_LOAD_STOK_OPNAME);
                        });
                        break;

                    case R.id.laporan_penjualan:
                        performActionAfterDrawerClosed(() -> {
                            startActivityForResult(new Intent(MainActivity.this, LaporanPenjualanActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    JConst.REQUEST_LOAD_PENJUALAN);
                        });
                        break;

                    case R.id.laporan_penjualan_permenu:
                        performActionAfterDrawerClosed(() -> {
                            startActivityForResult(new Intent(MainActivity.this, LaporanItemTerjualActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    JConst.REQUEST_LOAD_PENJUALAN);
                        });
                        break;

                    case R.id.laporan_keuangan:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)){
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }
                            startActivityForResult(new Intent(MainActivity.this, LaporanKeuanganActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                    JConst.REQUEST_LOAD_PENJUALAN);
                        });
                        break;

                    case R.id.sync_printer:
                        performActionAfterDrawerClosed(() -> {
                            printerUtils.connectDisconnect();
                        });
                        break;

                    case R.id.cash_drawer:
                        performActionAfterDrawerClosed(() -> {
                            openCashDrawer();
                        });
                        break;

                    case R.id.kirim_db:
                        performActionAfterDrawerClosed(() -> {
                            if (!Global.CheckConnectionInternet(MainActivity.this)) {
                                ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                                return;
                            }

                            Runnable runConfirm = new Runnable() {
                                @Override
                                public void run() {
                                    kirimDatabase();
                                }
                            };
                            ShowDialog.confirmDialog(MainActivity.this, getString(R.string.confirmation), "Apakah Anda yakin ingin melakukan kirim data?", "Kirim", "Batal",runConfirm, ()->{});
                        });
                        break;

                    case R.id.logout:
                        boolean isShiftStarted = SharedPrefsUtils.getBooleanPreference(MainActivity.this, "is_shift_started", false);
                        if (isShiftStarted) {
                            ShowDialog.infoDialog(MainActivity.this, "Informasi", "Mohon akhiri shift terlebih dahulu.");
                        } else {
                            performActionAfterDrawerClosed(() -> {
                                Global.clearDatabase();
                                JApplication.isSyncAwal = true;
                                JApplication.isAutoLogin = false;
                                SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_logged_in", false);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                ApiHelper.deleteUserLogon(MainActivity.this);
                                SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "is_sudah_print_z", false);
                            });
                        }
                        break;
                }

                return true;
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_terima:
//                        loadFragment(terimaFragment);
//                        terimaFragment.LoadData();
                        if (!Global.CheckConnectionInternet(MainActivity.this)){
                            ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                            return false;
                        }
                        return loadFragment(terimaFragment);
                    case R.id.navigation_penjualan: {
                        loadFragment(new PenjualanFragment());
                        imgCart.setVisibility(View.VISIBLE);
                        jmlCart.setVisibility(View.VISIBLE);
                        return true;
                        }
                    case R.id.navigation_shift:{
                        if (!Global.CheckConnectionInternet(MainActivity.this)){
                            ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                            return false;
                        }
                        loadFragment(new ShiftKasirFragment());
                        imgCart.setVisibility(View.GONE);
                        jmlCart.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PenjualanKeranjangActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, JConst.REQUEST_FILTER_BARANG);
            }
        });

    }

    public void openCashDrawer(){
        try {
            OutputStream os = JApplication.getInstance().mBluetoothSocket.getOutputStream();

            // === Tambahkan Perintah ESC/POS untuk Buka Cash Drawer ===
            byte[] openDrawerCommand = {27, 112, 48, 55, 121}; // ESC p 48 55 121
            os.write(openDrawerCommand);

            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performActionAfterDrawerClosed(Runnable action) {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Jalankan aksi setelah drawer tertutup
                action.run();
                // Hapus listener agar tidak terus-menerus memantau
                drawerLayout.removeDrawerListener(this);
            }
        });
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            tipe_fragment = "";
            imgCart.setVisibility(View.VISIBLE);
            imgFilter.setVisibility(View.GONE);
            jmlCart.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            if (fragment == terimaFragment) {
                tipe_fragment = TIPE_FRAGMENT_TERIMA;
                imgCart.setVisibility(View.GONE);
                imgFilter.setVisibility(View.VISIBLE);
                jmlCart.setVisibility(View.GONE);
            }
            return true;
        }
        return false;
    }

    public static void setInitMainActivity(boolean isLoadPenjualan) {
        instance.jmlCart.setText(String.valueOf(GlobalTable.getTotalKeranjang()));
        String namaOutlet = GlobalTable.getNamaOutlet(instance.getApplicationContext());
        String namaKasir = SharedPrefsUtils.getStringPreference(instance.getApplicationContext(), "karyawan_nama");

        instance.toolbarTitle.setText(namaOutlet); // Set title text
        instance.tvOutlet.setText(namaOutlet);
        instance.tvNamaKasir.setText(namaKasir);

        Date currentDate = new Date(); // Dapatkan tanggal saat ini
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        instance.tvTanggal.setText(sdf.format(currentDate)); // Format Date dan set ke TextView

        if (isLoadPenjualan) instance.loadFragment(new PenjualanFragment());
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Tekan lagi untuk keluar.", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
        switch(requestCode) {
            case JConst.REQUEST_LOAD_PENJUALAN:
                if (resultCode == RESULT_OK) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_penjualan);
//                    loadFragment(penjualanFragment);
                }
                break;

            case JConst.REQUEST_PERMISSION_ENABLE_BT:
                printerUtils.connectDisconnect();
                break;
            case JConst.REQUEST_ENABLE_BT:
            case JConst.REQUEST_CONNECT_DEVICE:
                printerUtils.handleActivityResult(requestCode, resultCode, data);
                break;
            case JConst.REQUEST_FILTER_BARANG:
                if (resultCode == RESULT_OK && data != null && tipe_fragment == TIPE_FRAGMENT_TERIMA) {
                    Bundle extra = data.getExtras();
                    terimaFragment.uidBarang = extra.getString("barang_uid");
                    terimaFragment.LoadData();
                    if (!extra.getString("barang_uid").isEmpty()){
                        imgFilter.setColorFilter(getColor(R.color.generic_1), PorterDuff.Mode.SRC_IN);
                    } else {
                        imgFilter.clearColorFilter(); // Kembalikan ke warna aslinya
                    }
                }
                break;
            case JConst.REQUEST_LOAD_TERIMA_BARANG:
                if (resultCode == RESULT_OK) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_terima);
                    loadFragment(new TerimaFragment());
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        printerUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void ValidasiVersi(){
        String url = Routes.url_get_versi_app();
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int versiLama = Global.convertVersiToInt(JApplication.getInstance().getVersionName());
                int versiCloud = Global.convertVersiToInt(response);
                if (versiLama < versiCloud){
                    final Runnable runOk = new Runnable() {
                        @Override
                        public void run() {
                            new FileDownloadHelper(MainActivity.this).downloadFile(Routes.url_apk(), "pos_crushty.apk");
                        }
                    };

                    Global.inform(MainActivity.this, "Anda menggunakan versi lama, klik OK untuk mendownload versi terbaru", "Pemberitahuan Update", runOk);
                }else{
                    startView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.inform(MainActivity.this, JConst.MSG_GAGAL_KONEKSI_KE_SERVER, String.valueOf(getTitle()), new Runnable() {
                    @Override
                    public void run() {
                        finishAffinity();
                    }
                });
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public void kirimDatabase(){
        ProgressDialog pDialog = Global.createProgresSpinner(MainActivity.this, getString(R.string.loading));
        pDialog.setMessage("Uploading...");
        pDialog.show();
        String url = Routes.url_kirim_database();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    if (status.equals(JConst.STATUS_API_SUCCESS)) {
                        pDialog.dismiss();
                        ShowDialog.infoDialog(MainActivity.this, getString(R.string.information), "Kirim Data Sukses");
                    }else{
                        Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ApiHelper.handleError(MainActivity.this, error, () -> kirimDatabase());
                pDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(getApplicationContext(), "api_key")); // skip
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                //masukan ke param
                String namaOutlet = GlobalTable.getNamaOutlet(instance.getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama_outlet", namaOutlet);
                params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                //upload DML
                String fieldName = "db";
                DataPart dataPart = new DataPart(getString(R.string.database_name), getFileDataFromPath(JApplication.getInstance().lokasi_db), "application/x-sqlite3");
                params.put(fieldName, dataPart);

                return params;
            }
        };
        int timeout = 5*60*1000; // 5 menit
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private byte[] getFileDataFromPath(String path) {
        File file = new File(path);
        byte[] data = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

}