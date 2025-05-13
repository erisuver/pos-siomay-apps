package com.orion.pos_crushty_android;

public class Routes {
    public static String IP_ADDRESS = "http://192.168.18.5";//"http://192.168.18.81";//"http://192.168.61.167";//"http://192.168.132.65";//
//    public static String IP_ADDRESS = "http://pasienqu.com";


    public static String NAMA_API = "pos_crushty_api";
    public static String URL_API_AWAL = IP_ADDRESS + "/"+NAMA_API+"/public/";
    public static String URL_GET_REAL_API = "http://orionbdg.xyz/internal_orion/public/setting_ip/get_real_ip_address?nama_api="+NAMA_API;
    public static String URL_DRIVE_PROFILE = "https://drive.google.com/u/0/uc?id=18cUONBHYxCq4SumXrnA6f2SQJWHJTbdw&export=download";

    // Gambar or FILE URL
    public static String url_path_gambar_barang() {
        return JApplication.getInstance().real_url + "uploads/gambar/barang";
    }
    public static String url_kirim_database (){
        return JApplication.getInstance().real_url + "global/kirim_db";
    }

    //Versi
    public static String url_get_versi_app() {
        return JApplication.getInstance().real_url + "versi_data_android.php";
    }
    public static String url_apk() {
        return JApplication.getInstance().real_url + "pos_crushty.apk";
    }

    //LOGIN
    public static String url_api_key  = "api_key/";
    public static String url_login() {
        return JApplication.getInstance().real_url + url_api_key + "login";
    }
//=========================================MASTER================================================
    public static String url_master() {
        return JApplication.getInstance().real_url + "master/";
    }
    //Outlet
    public static String url_master_outlet() {
        return url_master()+"master_outlet";
    }


//=========================================TRANSAKSI================================================
    public static String url_transaksi() {
        return JApplication.getInstance().real_url + "transaksi/";
    }

    public static String url_laporan() {
        return JApplication.getInstance().real_url + "laporan/";
    }
    public static String url_terima_barang_mst() {
        return url_transaksi()+"terima_barang_mst";
    }
    public static String url_terima_barang_det() {
        return url_transaksi()+"terima_barang_det";
    }
    public static String url_kirim_barang_mst() {
        return url_transaksi()+"kirim_barang_mst";
    }
    public static String url_kirim_barang_det() {
        return url_transaksi()+"kirim_barang_det";
    }

    public static String url_get_saldo_perbrg(){return JApplication.getInstance().real_url +"global/saldo/get_perbarang";}
    public static String url_get_stok(){return JApplication.getInstance().real_url +"global/saldo/get_stok";}

    public static String url_global() {
        return JApplication.getInstance().real_url + "global/";
    }
}
