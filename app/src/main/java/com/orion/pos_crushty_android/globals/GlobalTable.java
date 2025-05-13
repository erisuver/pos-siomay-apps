package com.orion.pos_crushty_android.globals;

import android.content.Context;
import android.database.Cursor;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.SyncData;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailTable;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.databases.penjualan_bayar.PenjualanBayarModel;
import com.orion.pos_crushty_android.databases.penjualan_bayar.PenjualanBayarTable;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class GlobalTable {
    public static String getNomor(Context context) {
        String outletUid = SharedPrefsUtils.getStringPreference(context, "outlet_uid");
        String kodeKaryawan = SharedPrefsUtils.getStringPreference(context, "karyawan_kode");
        String userId = SharedPrefsUtils.getStringPreference(context, "user_id");

        // 1. Ambil kode outlet dari tabel master_outlet
        String kodeOutlet = "";
        String sql = "SELECT kode FROM master_outlet WHERE uid = '"+outletUid+"' ";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            kodeOutlet = cursor.getString(cursor.getColumnIndexOrThrow("kode"));
            cursor.close();
        }

        // 3. Dapatkan tanggal saat ini dalam format 'ddMMyy'
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String tanggal = sdf.format(new Date());

        // 4. Dapatkan nomor urut terakhir dari sistem atau logika nomor urut
        String nomorUrut = getNomorUrut(outletUid, userId);  // Implementasikan fungsi untuk mengambil nomor urut unik

        // 5. Gabungkan format menjadi 'kode_outlet-ddmmyynourut-kode_karyawan'
        String nomor = kodeOutlet + "-" + tanggal + nomorUrut + "-" + kodeKaryawan + "-" +JConst.TIPE_APPS_ANDROID;

        return nomor;
    }

    public static String getNomorUrut(String outletUid, String userId) {
        // Implementasi untuk mendapatkan nomor urut berdasarkan outletUid
        // Contoh: Hitung jumlah transaksi pada hari ini untuk outlet dan user tersebut dan tambahkan 1
        String sql = "SELECT COUNT(*) FROM penjualan_mst WHERE outlet_uid = ? " +
                "AND user_id = ? " +
                "AND strftime('%Y-%m-%d', tanggal / 1000, 'unixepoch') = date('now', 'localtime')";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, new String[]{outletUid, userId});

        int nomorUrut = 1;
        if (cursor != null && cursor.moveToFirst()) {
            nomorUrut += cursor.getInt(0);  // Tambahkan hasil query dengan nomor urut
            cursor.close();
        }
        String hasil = String.format("%03d", nomorUrut);
        return hasil;
    }

    public static String savePenjualan(Context context, String tipeBayar, double total, double bayar, double kembali, String attachFile){
        String sql = "";
        int versi = 0;
        long dateNow = Global.serverNowLong();
        String user_id = SharedPrefsUtils.getStringPreference(context, "user_id");
        String outletUid = SharedPrefsUtils.getStringPreference(context, "outlet_uid");
        String karyawanNama = SharedPrefsUtils.getStringPreference(context, "karyawan_nama");
        String karyawanUid = SharedPrefsUtils.getStringPreference(context, "karyawan_uid");
        String nomor = GlobalTable.getNomor(context);

        //penjualan mst
        PenjualanMstModel penjualanMstModel = new PenjualanMstModel();
        PenjualanMstTable penjualanMstTable = new PenjualanMstTable(context);
        versi = SyncData.getMaxVersionToVersiData("penjualan_mst") +1;
        String masterUid = UUID.randomUUID().toString();

        penjualanMstModel.setUid(masterUid);
        penjualanMstModel.setLastUpdate(dateNow);
        penjualanMstModel.setTglInput(dateNow);
        penjualanMstModel.setUserId(user_id);
        penjualanMstModel.setTanggal(dateNow);
        penjualanMstModel.setNomor(nomor);
        penjualanMstModel.setOutletUid(outletUid);
        penjualanMstModel.setKaryawanUid(karyawanUid);
        penjualanMstModel.setTotal(total);
        penjualanMstModel.setBayar(bayar);
        penjualanMstModel.setKembali(kembali);
        penjualanMstModel.setVersi(versi);
        penjualanMstModel.setIs_kirim("F");
        //insert
        penjualanMstTable.insert(penjualanMstModel);

        //penjualan bayar
        PenjualanBayarModel penjualanBayarModel = new PenjualanBayarModel();
        PenjualanBayarTable penjualanBayarTable = new PenjualanBayarTable(context);
        versi = SyncData.getMaxVersionToVersiData("penjualan_bayar") +1;
        String bayarUid = UUID.randomUUID().toString();

        penjualanBayarModel.setUid(bayarUid);
        penjualanBayarModel.setLastUpdate(dateNow);
        penjualanBayarModel.setMasterUid(masterUid);
        penjualanBayarModel.setKasBankUid(GlobalTable.getKasbankUid(context, tipeBayar));
        penjualanBayarModel.setTipeBayar(tipeBayar);
        penjualanBayarModel.setGambar(attachFile);
        penjualanBayarModel.setTotal(total);
        penjualanBayarModel.setBayar(bayar);
        penjualanBayarModel.setKembali(kembali);
        penjualanBayarModel.setVersi(versi);
        //insert
        penjualanBayarTable.insert(penjualanBayarModel);

        //penjualan det
        versi = SyncData.getMaxVersionToVersiData("penjualan_det") +1;

        sql = "INSERT INTO penjualan_det (uid, last_update, master_uid, barang_jual_uid, qty, harga, total, versi) " +
                "SELECT lower(substr(hex(randomblob(16)), 1, 8) || '-' || substr(hex(randomblob(16)), 9, 4) || '-' || substr(hex(randomblob(16)), 13, 4) || '-' || substr(hex(randomblob(16)), 17, 4) || '-' || substr(hex(randomblob(16)), 21, 12)) as uid, " +
                "strftime('%s', 'now') * 1000 as last_update, " +
                "'"+masterUid+"' as master_uid, b.uid, k.qty, (CASE WHEN s.harga > 0 THEN s.harga ELSE b.harga END) as harga, " +
                "(k.qty * (CASE WHEN s.harga > 0 THEN s.harga ELSE b.harga END)) as total, "+versi+" as versi "+
                " FROM keranjang_master k JOIN master_barang_jual b ON k.barang_jual_uid = b.uid " +
                " LEFT JOIN setting_komposisi_mst s ON s.barang_jual_uid = b.uid and (s.disable_date is null or s.disable_date = 0)  and s.outlet_uid = '"+ outletUid +"'"+
                " AND s.berlaku_dari <= (strftime('%s', 'now') * 1000) " +
                " AND s.berlaku_sampai >= (strftime('%s', 'now') * 1000) ";
        JApplication.getInstance().db.execSQL(sql);

        // penjualan det barang
        versi = SyncData.getMaxVersionToVersiData("penjualan_det_barang") +1;

        sql = "INSERT INTO penjualan_det_barang (uid, last_update, master_uid, detail_uid, barang_jual_uid, " +
                "barang_uid, satuan_uid, tipe, qty_barang_jual, qty_barang, qty_total, qty_barang_primer, qty_total_primer, konversi, versi) " +
                "SELECT lower(substr(hex(randomblob(16)), 1, 8) || '-' || substr(hex(randomblob(16)), 9, 4) || '-' || substr(hex(randomblob(16)), 13, 4) || '-' || substr(hex(randomblob(16)), 17, 4) || '-' || substr(hex(randomblob(16)), 21, 12)) as uid, " +
                "strftime('%s', 'now') * 1000 as last_update, pd.master_uid as master_uid, " +
                "pd.uid as detail_uid, km.barang_jual_uid, bd.barang_uid, bd.satuan_uid, bd.tipe, kd.qty as qty_barang_jual, " +
                "kd.qty_default as qty_barang, kd.qty * kd.qty_default as qty_total, bd.qty_primer as qty_barang_primer, kd.qty * kd.qty_default * bd.konversi as qty_total_primer, bd.konversi, "+versi+" as versi "+
                " FROM keranjang_detail kd JOIN keranjang_master km ON kd.master_seq = km.seq " +
                " JOIN penjualan_det pd ON pd.barang_jual_uid = km.barang_jual_uid and pd.master_uid = '"+masterUid+"'" +
                " JOIN detail_barang_jual bd ON kd.barang_uid = bd.barang_uid and pd.barang_jual_uid = bd.master_uid " +
                " GROUP BY kd.seq";
        JApplication.getInstance().db.execSQL(sql);

        //penambah pengurang
        versi = SyncData.getMaxVersionToVersiData("penambah_pengurang_barang") +1;
//        String penambahPengurangUid = UUID.randomUUID().toString();
        sql = "INSERT INTO penambah_pengurang_barang (uid, tanggal, trans_uid, tipe_trans, nomor, outlet_uid, barang_uid, qty_primer, versi, last_update, user_id) " +
                "SELECT lower(substr(hex(randomblob(16)), 1, 8) || '-' || substr(hex(randomblob(16)), 9, 4) || '-' || substr(hex(randomblob(16)), 13, 4) || '-' || substr(hex(randomblob(16)), 17, 4) || '-' || substr(hex(randomblob(16)), 21, 12)) as uid, " +
                "strftime('%s', 'now') * 1000 as tanggal, '"+masterUid+"' as trans_uid, '"+ JConst.TIPE_TRANS_PENJUALAN +"' as tipe_trans, " +
                "'"+nomor+"' as nomor, '"+outletUid+"' as outlet_uid, pd.barang_uid, -pd.qty_total_primer as qty_primer, "+versi+" as versi, strftime('%s', 'now') as last_update, '"+user_id+"' as user_id " +
                "FROM penjualan_det_barang pd " +
                "WHERE pd.master_uid = '"+masterUid+"'";
        JApplication.getInstance().db.execSQL(sql);


        //delete keranjang
        new KeranjangMasterTable(context).deleteAll();
        new KeranjangDetailTable(context).deleteAll();

        return masterUid;
    }

    public static String getNamaOutlet(Context context) {
        String namaOutlet = "";
        String outletUid = SharedPrefsUtils.getStringPreference(context, "outlet_uid");
        String sql = "SELECT nama FROM master_outlet WHERE uid = '"+outletUid+"'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            namaOutlet = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
        }
        cursor.close();
        return namaOutlet;
    }

    public static String getKodeOutlet(Context context) {
        String kodeOutlet = "";
        String outletUid = SharedPrefsUtils.getStringPreference(context, "outlet_uid");
        String sql = "SELECT kode FROM master_outlet WHERE uid = '"+outletUid+"'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            kodeOutlet = cursor.getString(cursor.getColumnIndexOrThrow("kode"));
        }
        cursor.close();
        return kodeOutlet;
    }

    public static String getKasbankUid(Context context, String tipeBayar) {
        String kas_bank_uid = "";
        String outletUid = SharedPrefsUtils.getStringPreference(context, "outlet_uid");
        String field = "";
        if (tipeBayar.equals(JConst.TIPE_BAYAR_TUNAI)){
            field = "kas_uid";
        }else{
            field = "bank_uid";
        }

        String sql = "SELECT "+field+" as kas_bank_uid FROM master_outlet WHERE uid = '"+outletUid+"'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            kas_bank_uid = cursor.getString(cursor.getColumnIndexOrThrow("kas_bank_uid"));
        }
        cursor.close();
        return kas_bank_uid;
    }

    public static int getTotalKeranjang() {
        int hasil = 0;

        String sql = "SELECT COUNT(*) FROM keranjang_master ";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            hasil = cursor.getInt(0);
            cursor.close();
        }
        return hasil;
    }

    public static String getKodeNamaKasir(String userId) {
        String result = "";
        String sql = "SELECT k.kode, k.nama FROM master_karyawan k JOIN system_user su ON k.uid = su.karyawan_uid WHERE su.user_id = '"+userId+"'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow("kode")) +" - "+cursor.getString(cursor.getColumnIndexOrThrow("nama"));
        }
        cursor.close();
        return result;
    }

    public static String getNamaKasir(String userId) {
        String result = "";
        String sql = "SELECT k.nama FROM master_karyawan k JOIN system_user su ON k.uid = su.karyawan_uid WHERE su.user_id = '"+userId+"'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
        }
        cursor.close();
        return result;
    }


    public static boolean getIsStokBersama(Context context) {
        String kodeOutlet = "";
        String outletUid = SharedPrefsUtils.getStringPreference(context, "outlet_uid");
        String sql = "SELECT is_stok_bersama FROM master_outlet WHERE uid = '"+outletUid+"'";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            kodeOutlet = cursor.getString(cursor.getColumnIndexOrThrow("is_stok_bersama"));
        }
        cursor.close();
        return kodeOutlet.equals(JConst.TRUE_STRING);
    }

}
