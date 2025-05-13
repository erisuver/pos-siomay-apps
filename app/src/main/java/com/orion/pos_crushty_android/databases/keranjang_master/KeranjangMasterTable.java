package com.orion.pos_crushty_android.databases.keranjang_master;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;

import java.util.ArrayList;

public class KeranjangMasterTable {
    private SQLiteDatabase db;
    private ArrayList<KeranjangMasterModel> records;
    private Context context;

    public KeranjangMasterTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    private ContentValues setValues(KeranjangMasterModel keranjangMasterModel) {
        ContentValues cv = new ContentValues();
        cv.put("barang_jual_uid", keranjangMasterModel.getBarangJualUid());
        cv.put("qty", keranjangMasterModel.getQty());
        return cv;
    }

    public long insert(KeranjangMasterModel keranjangMasterModel) {
        ContentValues cv = this.setValues(keranjangMasterModel);
        return this.db.insert("keranjang_master", null, cv);
    }

    public void update(KeranjangMasterModel keranjangMasterModel) {
        ContentValues cv = this.setValues(keranjangMasterModel);
        this.db.update("keranjang_master", cv, "seq = " + keranjangMasterModel.getSeq(), null);
    }

    public void delete(int seq) {
        this.db.delete("keranjang_master", "seq = " + seq, null);
    }

    private void reloadList() {
        this.records.clear();
        String sql = "SELECT k.seq, k.barang_jual_uid, k.qty, b.nama, (CASE WHEN s.harga > 0 THEN s.harga ELSE b.harga END) as harga, b.gambar " +
                " FROM keranjang_master k JOIN master_barang_jual b ON k.barang_jual_uid = b.uid "+
                " LEFT JOIN setting_komposisi_mst s ON s.barang_jual_uid = b.uid and (s.disable_date is null or s.disable_date = 0) and s.outlet_uid = '"+ SharedPrefsUtils.getStringPreference(context, "outlet_uid") +"' " +
                " AND s.berlaku_dari <= (strftime('%s', 'now') * 1000) " +
                " AND s.berlaku_sampai >= (strftime('%s', 'now') * 1000) ";
        Cursor cr = this.db.rawQuery(sql, null);

        KeranjangMasterModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new KeranjangMasterModel(
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_jual_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty"))
                );
                tempData.setNamaBarang(cr.getString(cr.getColumnIndexOrThrow("nama")));
                tempData.setHarga(cr.getDouble(cr.getColumnIndexOrThrow("harga")));
                tempData.setGambar(cr.getString(cr.getColumnIndexOrThrow("gambar")));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        if (cr != null) {
            cr.close();
        }
    }

    public ArrayList<KeranjangMasterModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("keranjang_master", null, null);
    }

    public void updateQtyKeranjang(int seq, double qty) {
        // Melakukan update pada keranjang_master
        this.db.execSQL("UPDATE keranjang_master SET qty = "+qty+" WHERE seq = " + seq);

        // Melakukan update pada keranjang_detail
        this.db.execSQL("UPDATE keranjang_detail SET qty = "+qty+" WHERE master_seq = " + seq);
    }

    public void updateQtyKeranjangMasterDetail(int seq) {
        // Melakukan update pada keranjang_master
        this.db.execSQL("UPDATE keranjang_master SET qty = qty + 1 WHERE seq = " + seq);

        // Melakukan update pada keranjang_detail
        this.db.execSQL("UPDATE keranjang_detail SET qty = qty + 1 WHERE master_seq = " + seq);
    }

    public void updateQtyKeranjangMasterDetail(int seq, double qtyInput) {
        // Melakukan update pada keranjang_master
        this.db.execSQL("UPDATE keranjang_master SET qty = qty + "+qtyInput+" WHERE seq = " + seq);

        // Melakukan update pada keranjang_detail
        this.db.execSQL("UPDATE keranjang_detail SET qty = qty + "+qtyInput+" WHERE master_seq = " + seq);
    }

    // Metode baru untuk menghapus barang berdasarkan barang_jual_uid
    public void deleteKeranjang(int seq) {
        this.db.delete("keranjang_detail", "master_seq = "+seq, null);
        this.db.delete("keranjang_master", "seq = "+seq, null);
    }

    public double getTotalPesanan() {
        double total = 0;
        String sql = "SELECT SUM((CASE WHEN s.harga > 0 THEN s.harga ELSE b.harga END) * k.qty) AS total " +
                "FROM keranjang_master k JOIN master_barang_jual b ON k.barang_jual_uid = b.uid " +
                " LEFT JOIN setting_komposisi_mst s ON s.barang_jual_uid = b.uid and (s.disable_date is null or s.disable_date = 0)  and s.outlet_uid = '"+ SharedPrefsUtils.getStringPreference(context, "outlet_uid") +"' " +
                " AND s.berlaku_dari <= (strftime('%s', 'now') * 1000) " +
                " AND s.berlaku_sampai >= (strftime('%s', 'now') * 1000) ";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return total;
    }

    public int getSeqKeranjang(String barangJualUid) {
        int seq = 0;
        String sql = "SELECT seq FROM keranjang_master WHERE barang_jual_uid = '"+barangJualUid+"'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            seq = cursor.getInt(cursor.getColumnIndexOrThrow("seq"));
        }
        cursor.close();
        return seq;
    }
}

