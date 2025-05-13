package com.orion.pos_crushty_android.databases.penjualan_det;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;

import java.util.ArrayList;
public class PenjualanDetTable {
    private SQLiteDatabase db;
    private ArrayList<PenjualanDetModel> records;
    private Context context;

    public PenjualanDetTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<PenjualanDetModel>();
    }

    private ContentValues setValues(PenjualanDetModel penjualanDetModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", penjualanDetModel.getUid());
        cv.put("seq", penjualanDetModel.getSeq());
        cv.put("last_update", penjualanDetModel.getLastUpdate());
        cv.put("master_uid", penjualanDetModel.getMasterUid());
        cv.put("barang_jual_uid", penjualanDetModel.getBarangJualUid());
        cv.put("qty", penjualanDetModel.getQty());
        cv.put("harga", penjualanDetModel.getHarga());
        cv.put("total", penjualanDetModel.getTotal());
        cv.put("keterangan", penjualanDetModel.getKeterangan());
        cv.put("versi", penjualanDetModel.getVersi());
        return cv;
    }

    public void insert(PenjualanDetModel penjualanDetModel) {
        ContentValues cv = this.setValues(penjualanDetModel);
        this.db.insert("penjualan_det", null, cv);
    }

    public void update(PenjualanDetModel penjualanDetModel) {
        ContentValues cv = this.setValues(penjualanDetModel);
        this.db.update("penjualan_det", cv, "uid = '" + penjualanDetModel.getUid() + "'", null);
    }

    public void delete(String uid) {
        this.db.delete("penjualan_det", "uid = '" + uid + "'", null);
    }

    private void reloadList(String masterUid) {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT p.uid, p.seq, p.last_update, p.master_uid, p.barang_jual_uid, p.qty, p.harga, p.total, p.keterangan, p.versi, b.nama " +
                " FROM penjualan_det p JOIN master_barang_jual b ON p.barang_jual_uid = b.uid WHERE p.master_uid = '"+masterUid+"'", null);

        PenjualanDetModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new PenjualanDetModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_jual_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                        cr.getDouble(cr.getColumnIndexOrThrow("harga")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                tempData.setNamaBarang(cr.getString(cr.getColumnIndexOrThrow("nama")));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<PenjualanDetModel> getRecords(String penjualanMstUid) {
        this.reloadList(penjualanMstUid);
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("penjualan_det", null, null);
    }
}
