package com.orion.pos_crushty_android.globals;

import android.os.Environment;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;

public class JConst {

    //MISSAGE
    public static final String MSG_API_KEY_UNAUTORIZED = "Sesi login telah berakhir, silahkan login ulang";
    public static final String MSG_NOT_CONNECTION = "Tidak tersambung ke server, pastikan terkoneksi ke internet";
    public static final String KONDISI_HARUS_UPDATE = "harus update";
    public static final String KONDISI_TERUPDATE = "terupdate";
    public static final String MSG_HARUS_UPDATE     = "Versi baru telah tersedia, update aplikasi sekarang?";
    public static String MSG_API_GAGAL_LOAD    = "Terjadi kesalahan koneksi ke server";
    public static String MSG_GAGAL_KONEKSI_KE_SERVER = "Gagal koneksi ke server";
    public static String STATUS_API_SUCCESS     = "success";
    public static String STATUS_API_FAILED      = "failed";
    public static String TRUE_STRING = "T";
    public static String FALSE_STRING = "F";

    public static String mediaLocationPath = Environment.getExternalStorageDirectory() + "/Android/Data/"+JApplication.getInstance().getPackageName()+ "/Media/";
//    public static String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ "/" + JApplication.getInstance().getString(R.string.app_name)+ "/";
    public static String downloadPath = JApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            + "/" + JApplication.getInstance().getString(R.string.app_name) + "/";

    public static final String tag_json_obj = "json_obj_req";

    public static final String CG_DB_GLOBAL = "1";
    public static final String CG_DB_BANDUNG = "2";
    public static final String CG_DB_CIREBON = "3";
    public static final String CG_DB_KARAWANG = "4";
    public static final String CG_DB_GLOBAL_TEXT = "inv_sinar_surya_global";
    public static final String CG_DB_BANDUNG_TEXT = "inv_sinar_surya";
    public static final String CG_DB_CIREBON_TEXT = "inv_sinar_surya_cirebon";
    public static final String CG_DB_KARAWANG_TEXT = "inv_sinar_surya_karawang";

    //status
    public static final String TEXT_STATUS_BELUM_DIPROSES = "Belum Diproses";
    public static final String TEXT_STATUS_DIPROSES = "Diproses";
    public static final String TEXT_STATUS_SELESAI = "Selesai";
    public static final String TEXT_STATUS_LUNAS = "Lunas";
    public static final String TEXT_STATUS_DIBATALKAN = "Dibatalkan";
    public static final String TEXT_STATUS_DARI_APPS = "Dari Apps Customer";
    public static final String TEXT_STATUS_DITOLAK = "Ditolak";

    public static final String STATUS_BELUM_DIPROSES = "BP";
    public static final String STATUS_DIPROSES = "P";
    public static final String STATUS_SELESAI = "S";
    public static final String STATUS_LUNAS = "L";
    public static final String STATUS_DIBATALKAN = "B";
    public static final String STATUS_DARI_APPS = "A";
    public static final String STATUS_DITOLAK = "T";


    public static final String PATH_PDF = "pdf_sinar_surya";

    //TIPE APPS
    public static final String TIPE_APPS_ANDROID  = "A";
    //API
    public static final String SKIP_CHECK_API_KEY = "SKIP_CHECK_API_KEY";

    //TIPE BARANG
    public static final String TIPE_BARANG_JUAL_TOPPING = "TP";
    public static final String TIPE_BARANG_JUAL_BAHAN_UTAMA   = "BU";

    //TIPE PEMBAYARAN
    public static final String TIPE_BAYAR_TUNAI = "T";
    public static final String TIPE_BAYAR_DEBIT = "D";
    public static final String TIPE_BAYAR_QRIS = "Q";
    public static final String TIPE_BAYAR_OVO = "O";
    public static final String TIPE_BAYAR_TUNAI_TEXT = "Tunai";
    public static final String TIPE_BAYAR_DEBIT_TEXT = "Debit";
    public static final String TIPE_BAYAR_QRIS_TEXT = "QRIS";
    public static final String TIPE_BAYAR_OVO_TEXT = "OVO";


    public static final String TIPE_BARIS_HEADER = "1";
    public static final String TIPE_BARIS_PRINT_BARANG = "2";
    public static final String TIPE_BARIS_PRINT_HARGA = "3";
    public static final String TIPE_BARIS_PRINT_PEMBATAS = "4";
    public static final String TIPE_BARIS_PRINT_TOTAL = "5";

    //tipe trans
    public static final String TIPE_TRANS_PENJUALAN = "FPJ";
    public static final String TRANS_STOK_OPNAME = "SOP";
    public static final String TRANS_PENGELUARAN_LAIN = "PBL";
    public static final String TRANS_PENGELUARAN_LAIN_BARANG = "PBL";
    public static final String TRANS_PENGELUARAN_LAIN_KAS = "PKL";
    public static final String TRANS_TERIMA_BARANG = "TRM";
    public static final String TRANS_KIRIM_BARANG = "KRM";

    //request code
    public static final int REQUEST_LOAD_TERIMA_BARANG = 1;
    public static final int REQUEST_LOAD_PENJUALAN = 2;
    public static final int REQUEST_LOAD_SHIFT = 3;
    public static final int REQUEST_ENABLE_BT = 4;
    public static final int REQUEST_CONNECT_DEVICE = 5;
    public static final int REQUEST_LOAD_STOK_OPNAME = 6;
    public static final int REQUEST_LOAD_PENGELUARAN_LAIN = 7;
    public static final int REQUEST_LOAD_KIRIM_BARANG = 8;
    public static final int REQUEST_FILTER_BARANG = 9;
    public static final int REQUEST_INPUT  = 10;
    public static final int REQUEST_PERMISSION_ENABLE_BT = 11;
    public static final int REQUEST_CETAK_STRUK = 12;
    public static final String CG_UID_PUSAT  = "095ad785-807a-11ef-9804-42010a940007";

    public static final String MODE_ADD = "A";
    public static final String MODE_EDIT = "E";
    public static final String MODE_DETAIL = "D";

    //tipe print
    public static final String PRINT_DAILY_X = "X";
    public static final String PRINT_DAILY_Z = "Z";

}


