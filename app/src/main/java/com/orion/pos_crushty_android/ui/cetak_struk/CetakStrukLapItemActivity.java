package com.orion.pos_crushty_android.ui.cetak_struk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.ItemTerjualModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.laporan_item_terjual.LaporanItemTerjualActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;
import com.orion.pos_crushty_android.utility.PrinterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CetakStrukLapItemActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Button btnPrint;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    //var for adapter/list
    private CetakStrukLapItemAdapter mAdapter;
    public List<CetakStrukModel> ListItems = new ArrayList<>();
    private long dateLong;
    private double vCash, vDebit, vQRIS, vOVO, vTotal, vTotalItem;
    private String outletUid, tipePrint;
    PrinterUtils printerUtils;
    private int idxStatusKirim;
    //var


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetak_struk);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        btnPrint = findViewById(R.id.btnPrint);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        printerUtils = new PrinterUtils(CetakStrukLapItemActivity.this);
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        dateLong = extra.getLong("date_long");
        vCash = extra.getDouble("cash");
        vDebit = extra.getDouble("debit");
        vQRIS = extra.getDouble("qris");
        vOVO = extra.getDouble("ovo");
        vTotal = extra.getDouble("total");
        vTotalItem = extra.getDouble("total_item");
        tipePrint = extra.getString("tipe_print");
        idxStatusKirim = extra.getInt("status_kirim");

        outletUid = SharedPrefsUtils.getStringPreference(this, "outlet_uid");
        btnPrint.setVisibility(View.VISIBLE);
        SetJenisTampilan();
        if (idxStatusKirim == 0){   // Jika "Sudah Upload" dipilih
            LoadDataOnline();
        }else {
            LoadData();
        };
    }

    private void EventClass() {
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cetak();
            }
        });
    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new CetakStrukLapItemAdapter(this, ListItems, R.layout.list_item_cetak_struk);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        // Bersihkan data sebelumnya di adapter
        mAdapter.removeAllModel();

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
        ArrayList<ItemTerjualModel> ArrPenjualan = new ArrayList<>();
        Cursor cr = null;
        try {
            // SQL query untuk mengambil data
            String sql ="SELECT bj.uid, bj.nama, SUM(d.qty) AS qtyTotal, d.harga as harga, SUM(d.qty) * d.harga as subTotal " +
                    " FROM penjualan_mst m " +
                    " JOIN penjualan_det d ON m.uid = d.master_uid " +
                    " JOIN master_barang_jual bj ON bj.uid = d.barang_jual_uid " +
                    " WHERE m.seq <> 0 " + filter +
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
                    ArrPenjualan.add(tempData);
                } while (cr.moveToNext());
            }

            isiDataCetak(ArrPenjualan);

        } catch (Exception e) {
            // Log error untuk debugging
            Log.e("LoadData", "Error saat mengambil data: ", e);
        } finally {
            // Pastikan cursor ditutup untuk menghindari memory leaks
            if (cr != null) {
                cr.close();
            }
        }
    }

    public void LoadDataOnline() {
        // Bersihkan data sebelumnya
        mAdapter.removeAllModel();

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
                    }

                } else {
                    // Penanganan jika data bernilai null
                    String keterangan = response.getString("keterangan");
                    Toast.makeText(getApplicationContext(), keterangan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            } finally {
                isiDataCetak(itemDataModels);
            }
        }, error -> {
            ApiHelper.handleError(CetakStrukLapItemActivity.this, error, () -> LoadDataOnline());
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

    public void isiDataCetak(ArrayList<ItemTerjualModel> ArrPenjualan) {
        mAdapter.removeAllModel();

        List<CetakStrukModel> itemDataModels = new ArrayList<>();
        CetakStrukModel temp = null;
        String tempIsi;
        String user = GlobalTable.getNamaKasir(SharedPrefsUtils.getStringPreference(this, "user_id"));
        String namaOutlet = GlobalTable.getNamaOutlet(this);

        toolbarTitle.setText("Cetak Laporan Item Terjual");

        tempIsi = tambah_spasi_jarak_center("CRUSHTY", 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_HEADER);
        itemDataModels.add(temp);

        tempIsi = tambah_spasi_jarak_center(namaOutlet, 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_HEADER);
        itemDataModels.add(temp);

        temp = new CetakStrukModel("", JConst.TIPE_BARIS_HEADER);
        itemDataModels.add(temp);

        tempIsi = tipePrint +" Tanggal : "+Global.getDateFormated(dateLong);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        tempIsi = tipePrint +" DAILY "+tipePrint;
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        for (ItemTerjualModel item : ArrPenjualan) {
            tempIsi = item.getNamaBarang();
            tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(item.getQty()), 30);
            temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
            itemDataModels.add(temp);
//            tempIsi = tambah_spasi_jarak_left(tempIsi, 30);

//            temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
//            itemDataModels.add(temp);

//            tempIsi = Global.FloatToStrFmt(item.getQty()) + " x @"+Global.FloatToStrFmt(item.getHarga()) ;
            tempIsi = "";
            tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(item.getTotal()), 30);
            temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
            itemDataModels.add(temp);

        }
        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        //TL item
        tempIsi = "TL";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(vTotalItem), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        //cash
        tempIsi = "Cash";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(vCash), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        //Debit
        tempIsi = "Debit";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(vDebit), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        //QRIS
        tempIsi = "QRIS";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(vQRIS), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        //OVO
        tempIsi = "OVO";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(vOVO), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        //total
        tempIsi = "Total";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(vTotal), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);


        user = "Kasir : "+user;
        temp = new CetakStrukModel(user, JConst.TIPE_BARIS_PRINT_PEMBATAS);

        itemDataModels.add(temp);

        mAdapter.addModels(itemDataModels);

    }

    private String tambah_spasi_jarak(String awal, String akhir, int maxpanjang){
        String hasil = awal;
        int jumlahpanjang = maxpanjang - (awal.length() + akhir.length());
        for (int i = 0; i < jumlahpanjang; i++){
            hasil = hasil + " ";
        }
        hasil = hasil + akhir;
        return hasil;
    }

    public String tambah_spasi_jarak_center(String awal, int maxpanjang){
        String hasil = awal;
        int jumlahpanjang = maxpanjang - (awal.length());
        int kirikanan = jumlahpanjang / 2;

        for (int i = 0; i < kirikanan; i++){
            hasil = " " +hasil + " ";
        }
        return hasil;
    }

    private String tambah_spasi_jarak_left(String awal, int maxpanjang) {
        StringBuilder hasil = new StringBuilder();
        String[] words = awal.split(" "); // Memecah teks menjadi kata-kata
        int panjangSaatIni = 0;

        for (String word : words) {
            // Jika penambahan kata melebihi batas panjang
            if (panjangSaatIni + word.length() > maxpanjang) {
                hasil.append("\n"); // Tambahkan baris baru
                panjangSaatIni = 0; // Reset panjang saat ini
            }

            // Tambahkan kata dan spasi
            hasil.append(word).append(" ");
            panjangSaatIni += word.length() + 1; // Tambah panjang saat ini (kata + spasi)
        }

        // Menghilangkan spasi tambahan di akhir baris terakhir
        if (hasil.length() > 0) {
            hasil.setLength(hasil.length() - 1);
        }

        return hasil.toString();
    }


    public void cetak_old() {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = JApplication.getInstance().mBluetoothSocket
                            .getOutputStream();

                    // Remove or provide valid byte data here
                    // os.write(); // This line is unnecessary or needs proper byte array data
                    String isi = "";
                    for (int i = 0; i < ListItems.size(); i++) {
                        isi = ListItems.get(i).getIsi();
                        os.write(isi.getBytes());  // Send the content as bytes
                        String newLine = "\n";     // Add newline
                        os.write(newLine.getBytes());
                    }
                    isi = "\n\n\n";
                    os.write(isi.getBytes());

                    os.flush();  // Ensure the data is sent out

                } catch (Exception e) {
                    Log.e("PrintActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    public void cetak() {
        Thread t = new Thread() {
            public void run() {
                try {
                    if (JApplication.getInstance().mBluetoothSocket == null ||
                            !JApplication.getInstance().mBluetoothSocket.isConnected()) {

                        // Coba koneksi ulang ke printer via Bluetooth
                        printerUtils.connectDisconnect();

                        // Jika masih gagal, keluar dari fungsi
                        if (JApplication.getInstance().mBluetoothSocket == null ||
                                !JApplication.getInstance().mBluetoothSocket.isConnected()) {
                            Log.e("PrintActivity", "Bluetooth not connected, unable to print.");
                            return;  // Stop printing if reconnect failed
                        }
                    }

                    OutputStream os = JApplication.getInstance().mBluetoothSocket.getOutputStream();

                    String isi = "";
                    for (int i = 0; i < ListItems.size(); i++) {
                        isi = ListItems.get(i).getIsi();
                        os.write(isi.getBytes());  // Mengirim konten sebagai byte
                        String newLine = "\n";     // Tambahkan baris baru
                        os.write(newLine.getBytes());
                    }
                    isi = "\n\n\n";
                    os.write(isi.getBytes());

                    os.flush();  // Pastikan data dikirim
                    setResult(RESULT_OK);
                    finish();

                } catch (Exception e) {
                    Log.e("PrintActivity", "Error during printing: ", e);
                }
            }
        };
        t.start();
    }


    private void reconnectBluetooth() {
        try {
            // Tambahkan logika koneksi ulang ke Bluetooth di sini
            // Misalnya, scan perangkat, pilih printer, atau coba kembali menggunakan alamat yang tersimpan
            BluetoothDevice printerDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("Printer_MAC_Address");

            // Membuat socket baru dan mencoba koneksi ulang
//            JApplication.getInstance().mBluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(YOUR_UUID);
//            JApplication.getInstance().mBluetoothSocket.connect();

            Log.d("PrintActivity", "Reconnected to Bluetooth printer.");

        } catch (Exception e) {
            Log.e("PrintActivity", "Failed to reconnect to Bluetooth printer: ", e);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
        switch(requestCode) {
            case JConst.REQUEST_PERMISSION_ENABLE_BT:
                printerUtils.connectDisconnect();
                break;
            case JConst.REQUEST_ENABLE_BT:
            case JConst.REQUEST_CONNECT_DEVICE:
                printerUtils.handleActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        printerUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}