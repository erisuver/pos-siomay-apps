package com.orion.pos_crushty_android;

import static com.orion.pos_crushty_android.globals.Global.addDay;
import static com.orion.pos_crushty_android.globals.Global.getDateFormated;
import static com.orion.pos_crushty_android.globals.Global.serverNowWithoutTimeLong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainDetModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.databases.penjualan_bayar.PenjualanBayarModel;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;
import com.orion.pos_crushty_android.databases.penjualan_det_barang.PenjualanDetBarangModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.globals.FileUtil;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.ListValue;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangInputActivity;
import com.orion.pos_crushty_android.ui.login.LoginActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncData {
    private Context context;
    private ProgressDialog progressDialog;
    private ProgressDialog dialogGambar;
    private ArrayList<String> listTables;
    private int currentIndex = 0;
    private SQLiteDatabase db;
    public List<String> ListGambar;
    private int lastIdxGambar;
    SyncCallback vCallback;
    private boolean isSyncCallback;

    public SyncData(Context context) {
        this.context = context;
        listTables = ListValue.list_table_sync();
        this.db = JApplication.getInstance().db;
        ListGambar = new ArrayList<>();
    }

    public void SyncAll() {
        if (Global.CheckConnectionInternet(context)) {
            isSyncCallback = false;
            JApplication.isSyncAwal = true; //tambah ini agar selalu sync gambar dan muncul loading
            startSyncUp();
            startSyncDown();
        }
    }
    public void SyncAll(SyncCallback callback) {
        if (Global.CheckConnectionInternet(context)) {
            isSyncCallback = true;
            vCallback = callback;
            JApplication.isSyncAwal = false; //tambah ini agar tidak sync gambar dan tidak muncul loading
            startSyncUp();
            startSyncDown();
        }
    }

    public interface SyncCallback {
        void onSyncComplete();
    }

    public void syncPenjualan() {
        List<PenjualanMstModel> ListPenjualan = new PenjualanMstTable(context).getRecords();
        for (PenjualanMstModel data : ListPenjualan) {
            savePenjualan(data);
        }
    }

    public void syncPengeluaranLain() {
        List<PengeluaranLainMstModel> ListPengeluaran = new PengeluaranLainMstTable(context).getRecords4Sync();
        for (PengeluaranLainMstModel data : ListPengeluaran) {
            savePengeluaranLain(data);
        }
    }


    public void startSyncUp(){
        syncPenjualan();
        syncPengeluaranLain();
    }

    public void startSyncDown() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(listTables.size());
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Sinkronasi Data Awal");
        progressDialog.setMessage("Mohon Tunggu...");
        if (JApplication.isSyncAwal) progressDialog.show();
        deleteTable();    //xxx
        syncNextTable();
    }

    private void deleteTable(){
        db.delete("penambah_pengurang_barang", "is_kirim = ?", new String[]{"T"});
    }

    private void syncNextTable() {
        if (currentIndex < listTables.size()) {
            String tableName = listTables.get(currentIndex);
            int versiTerakhir = getMaxVersionToVersiData(tableName);
            fetchDataFromApi(tableName, versiTerakhir);
        } else {
            saveMaxVersionToVersiData();

            if (vCallback != null) {//jika ada callback maka langsung complete tanpa sinkgambar
                vCallback.onSyncComplete();
                return;
            }
            SinkGambar();
        }
    }

    private void fetchDataFromApi(final String tableName, int versiTerakhir) {
        String apiKey = SharedPrefsUtils.getStringPreference(context, "api_key");
        String url = "";
        JSONObject params = new JSONObject();
        try {
            /* tutup 080425 hanya tarik data master
            // Tentukan URL berdasarkan nama tabel
            if (tableName.equals("penjualan_mst") ||
                    tableName.equals("penjualan_det") ||
                    tableName.equals("penjualan_det_barang") ||
                    tableName.equals("penjualan_bayar") ||
                    tableName.equals("penambah_pengurang_barang")||
                    tableName.equals("pengeluaran_lain_mst")||
                    tableName.equals("pengeluaran_lain_det")
                ) {
                url = Routes.url_transaksi() + tableName + "/get";

                params.put("outlet_uid", SharedPrefsUtils.getStringPreference(context, "outlet_uid"));
                String date_apps = getDateFormated(addDay(serverNowWithoutTimeLong(), -7), "yyyy-MM-dd");
                params.put("date_apps", date_apps);
            } else {
                url = Routes.url_master() + tableName + "/get";
            }
            */
            if (tableName.equals("penambah_pengurang_barang")){
                url = Routes.url_transaksi() + tableName + "/get";

                params.put("outlet_uid", SharedPrefsUtils.getStringPreference(context, "outlet_uid"));
                String date_apps = getDateFormated(addDay(serverNowWithoutTimeLong(), -7), "yyyy-MM-dd");
                params.put("date_apps", date_apps);
            }else {
                url = Routes.url_master() + tableName + "/get";
            }
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("versi", versiTerakhir);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    JSONArray dataArray = response.optJSONArray("data");
                    if (dataArray != null) {
                        saveDataToSQLite(tableName, dataArray);
                    }
                    currentIndex++;
                    progressDialog.setProgress(currentIndex);
                    syncNextTable();
                },
                error -> {
//                    ApiHelper.handleError(context, error,() -> fetchDataFromApi(tableName, versiTerakhir));
                    if (isSyncCallback){
                        ApiHelper.handleError(context, error,() -> SyncAll(vCallback));
                    }else{
                        ApiHelper.handleError(context, error,() -> SyncAll());
                    }
                    progressDialog.dismiss();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", apiKey);
                return headers;
            }
        };

        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }


    private void saveDataToSQLite(String tableName, JSONArray dataArray) {
        db.beginTransaction();
        try {
            // Get the list of columns in the table
            Set<String> tableColumns = getTableColumns(tableName);

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject jsonObject = dataArray.getJSONObject(i);
                ContentValues values = jsonToContentValues(jsonObject, tableColumns, tableName);
                if (tableName.equals("penjualan_mst") || tableName.equals("pengeluaran_lain_mst")  || tableName.equals("penambah_pengurang_barang")) {
                    values.put("is_kirim", "T");
                }
//                db.insert(tableName, null, values);
                // Menggunakan CONFLICT_IGNORE untuk menghindari unique constraint error
                db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // Helper method to get the list of columns in the table
    private Set<String> getTableColumns(String tableName) {
        Set<String> columnSet = new HashSet<>();
        Cursor cursor = null;
        try {
            // Query to get all column names from the table
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Column name is in the second column (index 1) of the result
                    String columnName = cursor.getString(1);
                    columnSet.add(columnName);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columnSet;
    }


    private ContentValues jsonToContentValues(JSONObject jsonObject, Set<String> tableColumns, String tableName) {
        ContentValues values = new ContentValues();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                // Skip if the key is not in the table columns
                if (!tableColumns.contains(key)) {
                    continue; // Skip to the next key
                }

                Object value = jsonObject.get(key);
                if (key.equals("uid")) {
                    if (tableName.equals("master_barang_jual")) {
                        db.delete("master_barang_jual", "uid = '" + value + "'", null);
                        db.delete("detail_barang_jual", "master_uid = '" + value + "'", null);
                    } else if (tableName.equals("setting_komposisi_mst")) {
                        db.delete("setting_komposisi_mst", "uid = '" + value + "'", null);
                        db.delete("setting_komposisi_det", "master_uid = '" + value + "'", null);
                    } else if (tableName.equals("penjualan_mst")) {
                        db.delete("penjualan_mst", "uid = '" + value + "'", null);
                        db.delete("penjualan_det", "master_uid = '" + value + "'", null);
                        db.delete("penjualan_bayar", "master_uid = '" + value + "'", null);
                        db.delete("penjualan_det_barang", "master_uid = '" + value + "'", null);
                    } else if (!tableName.contains("detail") && !tableName.contains("det")) {
                        db.delete(tableName, "uid = '" + value + "'", null);
                    } else if (tableName.equals("pengeluaran_lain_mst")) {
                        db.delete("pengeluaran_lain_mst", "uid = '" + value + "'", null);
                        db.delete("pengeluaran_lain_det", "master_uid = '" + value + "'", null);
                    }
                }

                // Check if the key contains date-related words and convert the value to long
                if (key.contains("tgl") || key.contains("last_update") || key.contains("disable_date") || key.contains("tanggal") || key.contains("berlaku")) {
                    // Convert the date string to long using the specified format
                    long dateValue = Global.getMillisDateFmt(value.toString(), "yyyy-MM-dd hh:mm:ss"); // Adjust format if needed
                    values.put(key, dateValue);
                } else if (key.contains("gambar")) {
                    if (!value.equals("")) {
                        ListGambar.add(value.toString());
                    }
                    values.put(key, value.toString());
                } else if (value instanceof Integer) {
                    values.put(key, (Integer) value);
                } else if (value instanceof Long) {
                    values.put(key, (Long) value);
                } else if (value instanceof Double) {
                    values.put(key, (Double) value);
                } else if (value instanceof Boolean) {
                    values.put(key, (Boolean) value);
                } else {
                    values.put(key, value.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return values;
    }

    private void saveMaxVersionToVersiData() {
        db.delete("versi_data", null, null);
        for (String tableName : listTables) {
            if (tableName.equals("pengeluaran_lain_det")) continue;

            String query = "SELECT MAX(versi) AS maxVersi FROM " + tableName;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                String maxVersi = cursor.getString(cursor.getColumnIndexOrThrow("maxVersi"));
                ContentValues contentValues = new ContentValues();
                if (tableName.equals("master_barang_jual")) {
                    contentValues.put("nama_tabel", "master_barang_jual");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                    contentValues.put("nama_tabel", "detail_barang_jual");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                } else if (tableName.equals("setting_komposisi_mst")) {
                    contentValues.put("nama_tabel", "setting_komposisi_mst");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                    contentValues.put("nama_tabel", "setting_komposisi_det");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                } else if (tableName.equals("penjualan_mst")) {
                    contentValues.put("nama_tabel", "penjualan_mst");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                    contentValues.put("nama_tabel", "penjualan_det");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                    contentValues.put("nama_tabel", "penjualan_bayar");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                    contentValues.put("nama_tabel", "penjualan_det_barang");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                } else if (!tableName.contains("detail") && !tableName.contains("det")) {
                    contentValues.put("nama_tabel", tableName);
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                } else if (tableName.equals("pengeluaran_lain_mst")) {
                    contentValues.put("nama_tabel", "pengeluaran_lain_mst");
                    contentValues.put("versi_terakhir", maxVersi);
                    db.insert("versi_data", null, contentValues);
                }

            }
            cursor.close();
        }
    }

    public static int getMaxVersionToVersiData(String tableName) {
        int versi = 0;
        String sql = "SELECT versi_terakhir as versi FROM versi_data WHERE nama_tabel = '" + tableName + "'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            versi = cursor.getInt(cursor.getColumnIndexOrThrow("versi"));
        }
        cursor.close();
        return versi;
    }

    // Metode untuk mendapatkan ukuran file dari server
    private long getFileSizeFromServer(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return connection.getContentLengthLong(); // Mendapatkan ukuran file dari header response
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Jika terjadi kesalahan
        }
        return -1;
    }

    private String DownloadGambar(String namaGambar, final Boolean isDataAkhir) {
        // Validasi nama gambar terlebih dahulu
        if (namaGambar == null || namaGambar.isEmpty() || "null".equals(namaGambar)) {
            if (isDataAkhir) {
                progressDialog.dismiss();
                JApplication.isSyncAwal = false;
                MainActivity.setInitMainActivity(true);
            }else{
                lastIdxGambar += 1;
                progressDialog.setProgress(lastIdxGambar);
                DownloadGambar(ListGambar.get(lastIdxGambar), (lastIdxGambar == ListGambar.size() - 1));
            }
            return null;
        }

        String PathAsal = Routes.url_path_gambar_barang() + "/" + namaGambar; // URL sumber gambar
        String PathTujuan = JConst.downloadPath; // Lokasi folder tujuan penyimpanan

        // Membuat folder jika belum ada
        File folder = new File(PathTujuan);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        final String vPathAkhir = PathTujuan + namaGambar; // Lokasi lengkap untuk menyimpan gambar

        // Jalankan pengecekan dan download di background thread menggunakan ExecutorService
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Cek apakah file sudah ada
                File fileGambar = new File(vPathAkhir);
                long serverFileSize = getFileSizeFromServer(PathAsal); // Panggil fungsi getFileSizeFromServer

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!fileGambar.exists() || fileGambar.length() != serverFileSize) {
                            // Jika file belum ada atau ukurannya berbeda, lakukan download
                            ImageRequest request = new ImageRequest(PathAsal,
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            try (FileOutputStream out = new FileOutputStream(vPathAkhir)) {
                                                // Menyimpan gambar dalam format PNG
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            // Notifikasi jika ini adalah data akhir
                                            if (isDataAkhir) {
                                                progressDialog.dismiss();
                                                JApplication.isSyncAwal = false;
                                                MainActivity.setInitMainActivity(true);
                                            }else{
                                                lastIdxGambar += 1;
                                                progressDialog.setProgress(lastIdxGambar);
                                                DownloadGambar(ListGambar.get(lastIdxGambar), (lastIdxGambar == ListGambar.size() - 1));
                                            }
                                        }
                                    }, 0, 0, null,
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // Tangani kesalahan saat mengunduh
                                            error.printStackTrace();
                                            if (isDataAkhir) {
                                                progressDialog.dismiss();
                                                JApplication.isSyncAwal = false;
                                                MainActivity.setInitMainActivity(true);
                                            }else{
                                                lastIdxGambar += 1;
                                                progressDialog.setProgress(lastIdxGambar);
                                                DownloadGambar(ListGambar.get(lastIdxGambar), (lastIdxGambar == ListGambar.size() - 1));
                                            }
                                        }
                                    });

                            // Menambahkan request ke queue
                            JApplication.getInstance().addToRequestQueue(request);
                        } else {
                            // Jika file sudah ada dan ukurannya sama
                            if (isDataAkhir) {
                                progressDialog.dismiss();
                                JApplication.isSyncAwal = false;
                                MainActivity.setInitMainActivity(true);
                            }else{
                                lastIdxGambar += 1;
                                progressDialog.setProgress(lastIdxGambar);
                                DownloadGambar(ListGambar.get(lastIdxGambar), (lastIdxGambar == ListGambar.size() - 1));
                            }
                        }
                    }
                });
            }
        });

        // Mengembalikan path tujuan gambar
        return vPathAkhir;
    }

    public void SinkGambar() {
        if (JApplication.isSyncAwal) {
            fetchListGambarFromApi(() -> startDownloadGambar());
        }else{
            startDownloadGambar();
        }
    }

    public void startDownloadGambar() {
        if (ListGambar.size() > 0) {
            progressDialog.setMax(ListGambar.size());
            progressDialog.setProgress(0);
            progressDialog.setTitle("Sinkronasi Gambar");
            progressDialog.setMessage("Mohon Tunggu...");
//
//            for (int i = 0; i < ListGambar.size(); i++) {
//                DownloadGambar(ListGambar.get(i), (i == ListGambar.size() - 1));
//                lastIdxGambar = i;
//            }
            if (ListGambar.size() > 0) {
                lastIdxGambar = 0;
                DownloadGambar(ListGambar.get(lastIdxGambar), (lastIdxGambar == ListGambar.size() - 1));
            }
        } else {
            progressDialog.dismiss();
            MainActivity.setInitMainActivity(JApplication.isSyncAwal);
            JApplication.isSyncAwal = false;
        }
    }


    public void savePenjualan(PenjualanMstModel penjualanMstModel) {
        String url = Routes.url_transaksi() + "penjualan_mst/save";
        JSONObject requestBody = createPenjualanRequestBody(penjualanMstModel);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    String uidMst = dataObject.getString("uid_penjualan_mst");
                    this.db.execSQL("UPDATE penjualan_mst SET is_kirim = 'T' WHERE uid = '"+uidMst+"'");
                    this.db.execSQL("UPDATE penambah_pengurang_barang SET is_kirim = 'T' WHERE trans_uid = '"+uidMst+"'");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
//            ApiHelper.handleError(context, error, () -> savePenjualan(penjualanMstModel));
            if (isSyncCallback){
                ApiHelper.handleError(context, error,() -> SyncAll(vCallback));
            }else{
                ApiHelper.handleError(context, error,() -> SyncAll());
            }
//            NetworkResponse networkResponse = error.networkResponse;
//            if (networkResponse != null) {
//                String responseData = new String(networkResponse.data); // Convert response data to String
//                try {
//                    JSONObject jsonResponse = new JSONObject(responseData); // Parse the response data as JSON
//                    String keterangan = jsonResponse.getString("keterangan");
//                    Toast.makeText(context, keterangan, Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(context, "Error API", Toast.LENGTH_SHORT).show();
//            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // contoh header
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(context, "api_key")); // skip
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private JSONObject createPenjualanRequestBody(PenjualanMstModel penjualanMstModel) {
        //get data
        PenjualanBayarModel penjualanBayarModel = penjualanMstModel.getBayarIds();
        List<PenjualanDetModel> ListDet = penjualanMstModel.getDetIds();
        List<PenjualanDetBarangModel> ListDetBarang = penjualanMstModel.getDetBarangIds();
        try {
            // Membuat objek utama
            JSONObject requestBody = new JSONObject();
            requestBody.put("tipe_apps", JConst.TIPE_APPS_ANDROID);

            // Membuat objek penjualan_mst
            JSONObject mstObj = new JSONObject();
            mstObj.put("uid", penjualanMstModel.getUid());
            mstObj.put("tanggal", Global.getDateTimeFormatMysql(penjualanMstModel.getTanggal()));
            mstObj.put("last_update", Global.getDateTimeFormatMysql(penjualanMstModel.getLastUpdate()));
            mstObj.put("tgl_input", Global.getDateTimeFormatMysql(penjualanMstModel.getTglInput()));
            mstObj.put("tgl_edit", Global.getDateTimeFormatMysql(penjualanMstModel.getTglInput()));
            mstObj.put("user_id", penjualanMstModel.getUserId());
            mstObj.put("user_edit", penjualanMstModel.getUserEdit());
            mstObj.put("nomor", penjualanMstModel.getNomor());
            mstObj.put("outlet_uid", penjualanMstModel.getOutletUid());
            mstObj.put("karyawan_uid", penjualanMstModel.getKaryawanUid());
            mstObj.put("keterangan", penjualanMstModel.getKeterangan());
            mstObj.put("total", penjualanMstModel.getTotal());
            mstObj.put("bayar", penjualanMstModel.getBayar());
            mstObj.put("kembali", penjualanMstModel.getKembali());

            // Menambahkan mst ke objek utama
            requestBody.put("penjualan_mst", mstObj);

            // Membuat objek penjualan_bayar
            JSONArray penjualanBayarArray = new JSONArray();
            JSONObject bayarObj = new JSONObject();
            bayarObj.put("uid", penjualanBayarModel.getUid());
            bayarObj.put("seq", penjualanBayarModel.getSeq());
            bayarObj.put("last_update", Global.getDateTimeFormatMysql(penjualanBayarModel.getLastUpdate()));
            bayarObj.put("master_uid", penjualanBayarModel.getMasterUid());
            bayarObj.put("kas_bank_uid", penjualanBayarModel.getKasBankUid());
            bayarObj.put("tipe_bayar", penjualanBayarModel.getTipeBayar());
            if (penjualanBayarModel.getGambar() == null || penjualanBayarModel.getGambar().isEmpty()) {
                bayarObj.put("gambar", "");
            } else {
                bayarObj.put("gambar", Global.getImageAsByteArrayCompressed(penjualanBayarModel.getGambar()));
            }
            bayarObj.put("total", penjualanBayarModel.getTotal());
            bayarObj.put("bayar", penjualanBayarModel.getBayar());
            bayarObj.put("kembali", penjualanBayarModel.getKembali());
            bayarObj.put("versi", penjualanBayarModel.getVersi());
            penjualanBayarArray.put(bayarObj);
            // Menambahkan penjualan_bayar ke objek utama
            requestBody.put("penjualan_bayar", penjualanBayarArray);

            // Membuat array det
            JSONArray penjualanDetArray = new JSONArray();
            for (PenjualanDetModel detModel : ListDet) {
                JSONObject detObj = new JSONObject();
                detObj.put("uid", detModel.getUid());
                detObj.put("seq", detModel.getSeq());
                detObj.put("last_update", Global.getDateTimeFormatMysql(detModel.getLastUpdate()));
                detObj.put("master_uid", detModel.getMasterUid());
                detObj.put("barang_jual_uid", detModel.getBarangJualUid());
                detObj.put("qty", detModel.getQty());
                detObj.put("harga", detModel.getHarga());
                detObj.put("total", detModel.getTotal());
                detObj.put("keterangan", "");
                detObj.put("versi", detModel.getVersi());

                // Membuat array det barang
                JSONArray penjualanDetBarangArray = new JSONArray();
                for (PenjualanDetBarangModel detBarangModel : ListDetBarang) {
                    if (detBarangModel.getDetailUid().equals(detModel.getUid())) {
                        JSONObject detBarangObj = new JSONObject();
                        detBarangObj.put("uid", detBarangModel.getUid());
                        detBarangObj.put("seq", detBarangModel.getSeq());
                        detBarangObj.put("last_update", Global.getDateTimeFormatMysql(detBarangModel.getLastUpdate()));
                        detBarangObj.put("master_uid", detBarangModel.getMasterUid());
                        detBarangObj.put("detail_uid", detBarangModel.getDetailUid());
                        detBarangObj.put("barang_jual_uid", detBarangModel.getBarangJualUid());
                        detBarangObj.put("barang_uid", detBarangModel.getBarangUid());
                        detBarangObj.put("satuan_uid", detBarangModel.getSatuanUid());
                        detBarangObj.put("tipe", detBarangModel.getTipe());
                        detBarangObj.put("qty_barang_jual", detBarangModel.getQtyBarangJual());
                        detBarangObj.put("qty_barang", detBarangModel.getQtyBarang());
                        detBarangObj.put("qty_total", detBarangModel.getQtyTotal());
                        detBarangObj.put("qty_barang_primer", detBarangModel.getQtyBarangPrimer());
                        detBarangObj.put("qty_total_primer", detBarangModel.getQtyTotalPrimer());
                        detBarangObj.put("konversi", detBarangModel.getKonversi());
                        detBarangObj.put("versi", detBarangModel.getVersi());

                        // Menambahkan detBarangObj ke array
                        penjualanDetBarangArray.put(detBarangObj);
                    }
                }
                // Menambahkan array det_barang ke objek utama
                detObj.put("penjualan_det_barang", penjualanDetBarangArray);

                // Menambahkan detObj ke array
                penjualanDetArray.put(detObj);
            }
            // Menambahkan array det ke objek utama
            requestBody.put("penjualan_det", penjualanDetArray);

            // Membuat array penambah_pengurang_barang
            JSONArray penambahPengurangArray = new JSONArray();

            // Loop melalui setiap model dalam listModel
            for (PenambahPengurangBarangModel model : penjualanMstModel.getDetPenambahPengurangIds()) {
                JSONObject penambahPengurangObj = new JSONObject();
                penambahPengurangObj.put("uid", model.getUid());
                penambahPengurangObj.put("seq", model.getSeq());
                penambahPengurangObj.put("tanggal", Global.getDateTimeFormatMysql(model.getTanggal()));
                penambahPengurangObj.put("trans_uid", model.getTransUid());
                penambahPengurangObj.put("tipe_trans", model.getTipeTrans());
                penambahPengurangObj.put("nomor", model.getNomor());
                penambahPengurangObj.put("outlet_uid", model.getOutletUid());
                penambahPengurangObj.put("barang_uid", model.getBarangUid());
                penambahPengurangObj.put("qty_primer", model.getQtyPrimer());
                penambahPengurangObj.put("versi", model.getVersi());
                penambahPengurangObj.put("last_update", Global.getDateTimeFormatMysql(model.getLastUpdate()));
                penambahPengurangObj.put("user_id", model.getUserId());
                penambahPengurangObj.put("keterangan", "");

                // Menambahkan objek ke array
                penambahPengurangArray.put(penambahPengurangObj);
            }

            // Menambahkan array ke objek utama
            requestBody.put("penambah_pengurang_barang", penambahPengurangArray);

            /* tutup 080425 ganti metode sync - hilangkan delete transaksi
            //delete sesudah ambil
            JApplication.getInstance().db.execSQL("DELETE FROM penjualan_mst WHERE uid = '" + penjualanMstModel.getUid() + "'");
            JApplication.getInstance().db.execSQL("DELETE FROM penjualan_bayar WHERE master_uid = '" + penjualanMstModel.getUid() + "'");
            JApplication.getInstance().db.execSQL("DELETE FROM penjualan_det WHERE master_uid = '" + penjualanMstModel.getUid() + "'");
            JApplication.getInstance().db.execSQL("DELETE FROM penjualan_det_barang WHERE master_uid = '" + penjualanMstModel.getUid() + "'");
            */

            // Mengembalikan request body
            return requestBody;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePengeluaranLain(PengeluaranLainMstModel pengeluaranLainMstModel) {
        String url = Routes.url_transaksi() + "pengeluaran_lain_mst/save";
        JSONObject requestBody = createPengeluaranLainRequestBody(pengeluaranLainMstModel);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    String uidMst = dataObject.getString("uid_pengeluaran_lain_mst");
                    this.db.execSQL("UPDATE pengeluaran_lain_mst SET is_kirim = 'T' WHERE uid = '"+uidMst+"'");
                    this.db.execSQL("UPDATE penambah_pengurang_barang SET is_kirim = 'T' WHERE trans_uid = '"+uidMst+"'");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
//            ApiHelper.handleError(context, error, () -> savePengeluaranLain(pengeluaranLainMstModel));
            if (isSyncCallback){
                ApiHelper.handleError(context, error,() -> SyncAll(vCallback));
            }else{
                ApiHelper.handleError(context, error,() -> SyncAll());
            }
//            NetworkResponse networkResponse = error.networkResponse;
//            if (networkResponse != null) {
//                String responseData = new String(networkResponse.data); // Convert response data to String
//                try {
//                    JSONObject jsonResponse = new JSONObject(responseData); // Parse the response data as JSON
//                    String keterangan = jsonResponse.getString("keterangan");
//                    Toast.makeText(context, keterangan, Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(context, "Error API", Toast.LENGTH_SHORT).show();
//            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // contoh header
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(context, "api_key")); // skip
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private JSONObject createPengeluaranLainRequestBody(PengeluaranLainMstModel pengeluaranLainMstModel) {
        JSONObject paramsMst = new JSONObject();
        JSONArray pengeluaranLainDetArray = new JSONArray();
        JSONArray penambahPengurangArray = new JSONArray();
        List<PengeluaranLainDetModel> ListDet = pengeluaranLainMstModel.getDetIds();
        List<PenambahPengurangBarangModel> ListPP = pengeluaranLainMstModel.getPpIds();

        String dateNow = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());

        try {
            // Menambahkan data ke paramsMst
            paramsMst.put("uid", pengeluaranLainMstModel.getUid());
            paramsMst.put("outlet_uid", pengeluaranLainMstModel.getOutletUid());
            paramsMst.put("user_id", pengeluaranLainMstModel.getUserId());
            paramsMst.put("nomor", pengeluaranLainMstModel.getNomor());
            paramsMst.put("tanggal", Global.getDateTimeFormatMysql(pengeluaranLainMstModel.getTanggal()));
            paramsMst.put("keterangan", pengeluaranLainMstModel.getKeterangan());
            paramsMst.put("tipe", pengeluaranLainMstModel.getTipe());
            paramsMst.put("karyawan_uid", pengeluaranLainMstModel.getKaryawanUid());

            // Mengisi array pengeluaran_lain_det
            for (PengeluaranLainDetModel item : ListDet) {
                JSONObject paramsDet = new JSONObject();
                paramsDet.put("uid", item.getUid());
                paramsDet.put("master_uid", item.getMasterUid());
                paramsDet.put("barang_uid", item.getBarangUid());
                paramsDet.put("satuan_uid", item.getSatuanUid());
                paramsDet.put("qty", item.getQty());
                paramsDet.put("qty_primer", item.getQtyPrimer());
                paramsDet.put("konversi", item.getKonversi());
                paramsDet.put("kas_bank_uid", item.getKasBankUid());
                paramsDet.put("jumlah", item.getJumlah());
                paramsDet.put("keterangan", item.getKeterangan());
                pengeluaranLainDetArray.put(paramsDet);
            }

            //penambah pengurang
            if (ListPP != null) {
                for (PenambahPengurangBarangModel item : ListPP) {
                    JSONObject paramsPP = new JSONObject();
                    paramsPP.put("tanggal", Global.getDateTimeFormatMysql(pengeluaranLainMstModel.getTanggal()));
                    paramsPP.put("nomor", item.getNomor());
                    paramsPP.put("outlet_uid", item.getOutletUid());
                    paramsPP.put("user_id", item.getUserId());
                    paramsPP.put("tipe_trans", item.getTipeTrans());
                    paramsPP.put("barang_uid", item.getBarangUid());
                    paramsPP.put("qty_primer", item.getQtyPrimer());
                    paramsPP.put("keterangan", "");
                    penambahPengurangArray.put(paramsPP);
                }
            }

            // Menyusun objek utama
            JSONObject params = new JSONObject();
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("pengeluaran_lain_mst", paramsMst);
            params.put("pengeluaran_lain_det", pengeluaranLainDetArray);

            if (pengeluaranLainMstModel.getTipe().equals(JConst.TRANS_PENGELUARAN_LAIN_BARANG)) {
                params.put("penambah_pengurang_barang", penambahPengurangArray);
            }

            /* tutup 080425 ganti metode sync - hilangkan delete transaksi
            //delete sesudah ambil
            JApplication.getInstance().db.execSQL("DELETE FROM pengeluaran_lain_mst WHERE uid = '" + pengeluaranLainMstModel.getUid() + "'");
            JApplication.getInstance().db.execSQL("DELETE FROM pengeluaran_lain_det WHERE master_uid = '" + pengeluaranLainMstModel.getUid() + "'");
            JApplication.getInstance().db.execSQL("DELETE FROM penambah_pengurang_barang WHERE trans_uid = '" + pengeluaranLainMstModel.getUid() + "'");

             */


            return params;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchListGambarFromApi(Runnable runnable) {
        String apiKey = SharedPrefsUtils.getStringPreference(context, "api_key");
        String url = "";
        JSONObject params = new JSONObject();
        try {
            url = Routes.url_master() + "master_barang_jual/get";

            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("versi", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (!obj.getString("gambar").equals("")) {
                                    ListGambar.add(obj.getString("gambar"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        runnable.run();
                    }
                },
                error -> {
                    ApiHelper.handleError(context, error,() -> fetchDataFromApi("master_barang_jual", 0));
                    progressDialog.dismiss();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", apiKey);
                return headers;
            }
        };

        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

}


