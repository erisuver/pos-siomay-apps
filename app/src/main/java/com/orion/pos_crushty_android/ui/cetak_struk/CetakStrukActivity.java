package com.orion.pos_crushty_android.ui.cetak_struk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailModel;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailTable;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterModel;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.ui.penjualan_keranjang.CustomPesananAdapter;
import com.orion.pos_crushty_android.utility.PrinterUtils;

import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CetakStrukActivity extends AppCompatActivity {
    //var componen/view
    private RecyclerView rcvLoad;
    private Button btnPrint;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    //var for adapter/list
    private CetakStrukAdapter mAdapter;
    public List<CetakStrukModel> ListItems = new ArrayList<>();
    private String tipeBayar = "";
    private String penjualanMstUid = "";
    private boolean isFromLaporan = false;
    PrinterUtils printerUtils;
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
        printerUtils = new PrinterUtils(CetakStrukActivity.this);
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        tipeBayar = extra.getString("tipe_bayar");
        penjualanMstUid = extra.getString("penjualan_mst_uid");
        isFromLaporan = extra.getBoolean("is_laporan");

        if (isFromLaporan){
            btnPrint.setVisibility(View.GONE);
        }
        SetJenisTampilan();
        LoadData();
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

        mAdapter = new CetakStrukAdapter(this, ListItems, R.layout.list_item_cetak_struk);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        mAdapter.removeAllModel();
        PenjualanMstModel penjualanMstModel = new PenjualanMstTable(this).getRecordByUid(penjualanMstUid);
        PenjualanDetTable penjualanDetTable = new PenjualanDetTable(this);
        ArrayList<PenjualanDetModel> ListPenjualanDet = penjualanDetTable.getRecords(penjualanMstUid);

        List<CetakStrukModel> itemDataModels = new ArrayList<>();
        CetakStrukModel temp = null;
        String tempIsi;
        double total = 0;
        String user = penjualanMstModel.getNamaKaryawan();

        toolbarTitle.setText(penjualanMstModel.getNomor());

        tempIsi = tambah_spasi_jarak_center("CRUSHTY", 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_HEADER);
        itemDataModels.add(temp);

        tempIsi = tambah_spasi_jarak_center(penjualanMstModel.getNamaOutlet(), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_HEADER);
        itemDataModels.add(temp);

        temp = new CetakStrukModel("", JConst.TIPE_BARIS_HEADER);
        itemDataModels.add(temp);

        tempIsi = "Tipe Pembayaran : ";
        if (Objects.equals(tipeBayar, JConst.TIPE_BAYAR_TUNAI)){
            tempIsi += JConst.TIPE_BAYAR_TUNAI_TEXT;
        } else if (Objects.equals(tipeBayar, JConst.TIPE_BAYAR_DEBIT)){
            tempIsi += JConst.TIPE_BAYAR_DEBIT_TEXT;
        } else if (Objects.equals(tipeBayar, JConst.TIPE_BAYAR_QRIS)){
            tempIsi += JConst.TIPE_BAYAR_QRIS_TEXT;
        } else if (Objects.equals(tipeBayar, JConst.TIPE_BAYAR_OVO)){
            tempIsi += JConst.TIPE_BAYAR_OVO_TEXT;
        }
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        tempIsi = "Tanggal : "+Global.getDateTimeFormated(penjualanMstModel.getTanggal());
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        for (PenjualanDetModel item : ListPenjualanDet) {
            total += item.getQty() * item.getHarga();


            tempIsi = item.getNamaBarang();
            tempIsi = tambah_spasi_jarak_left(tempIsi, 30);
            temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
            itemDataModels.add(temp);

            tempIsi = Global.FloatToStrFmt(item.getQty()) + " x @"+Global.FloatToStrFmt(item.getHarga()) ;

            tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(item.getQty() * item.getHarga()), 30);
            temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
            itemDataModels.add(temp);

        }
        tempIsi = "------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        //total
        tempIsi = "Total";
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(total), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        tempIsi = "Uang Diterima";
        double uangDiterima = penjualanMstModel.getTotal() + penjualanMstModel.getKembali();
        tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(uangDiterima), 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_TOTAL);
        itemDataModels.add(temp);

        double uangKembali = penjualanMstModel.getKembali();
        if (uangKembali > 0){
            tempIsi = "Kembalian";
            tempIsi = tambah_spasi_jarak(tempIsi, Global.FloatToStrFmt(uangKembali), 30);
            temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
            itemDataModels.add(temp);
        }
        tempIsi = "------------------------------";//"----------------------------------------------------";
        //tempIsi = "--------------------------------------------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);


        user = "Kasir : "+user;
        temp = new CetakStrukModel(user, JConst.TIPE_BARIS_PRINT_PEMBATAS);

        itemDataModels.add(temp);

        tempIsi = "------------------------------";//"----------------------------------------------------";
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_PRINT_PEMBATAS);
        itemDataModels.add(temp);

        tempIsi = tambah_spasi_jarak_center("TERIMA KASIH", 30);
        temp = new CetakStrukModel(tempIsi, JConst.TIPE_BARIS_HEADER);
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
/*
    public void cetak() {
        Thread t = new Thread() {
            public void run() {
                try {
                    if (JApplication.getInstance().mBluetoothSocket == null ||
                            !JApplication.getInstance().mBluetoothSocket.isConnected()) {

                        // Coba koneksi ulang ke printer via Bluetooth
//                        reconnectBluetooth();
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
*/
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

                    // === Tambahkan Perintah ESC/POS untuk Buka Cash Drawer ===
                    byte[] openDrawerCommand = {27, 112, 0, 60, (byte) 255}; // ESC p 0 60 255
                    os.write(openDrawerCommand);
                    os.flush();

                    // === Cetak Struk Seperti Biasa ===
                    String isi = "";
                    for (int i = 0; i < ListItems.size(); i++) {
                        isi = ListItems.get(i).getIsi();
                        os.write(isi.getBytes());  // Mengirim konten sebagai byte
                        os.write("\n".getBytes()); // Tambahkan baris baru
                    }
                    os.write("\n\n\n".getBytes()); // Baris kosong untuk kertas maju

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