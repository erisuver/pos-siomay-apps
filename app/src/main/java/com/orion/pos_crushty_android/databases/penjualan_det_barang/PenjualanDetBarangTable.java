package com.orion.pos_crushty_android.databases.penjualan_det_barang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;

import java.util.ArrayList;
public class PenjualanDetBarangTable {
    private SQLiteDatabase db;
    private ArrayList<PenjualanDetBarangModel> records;
    private Context context;

    public PenjualanDetBarangTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<PenjualanDetBarangModel>();
    }

    private ContentValues setValues(PenjualanDetBarangModel penjualanDetBarangModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", penjualanDetBarangModel.getUid());
        cv.put("last_update", penjualanDetBarangModel.getLastUpdate());
        cv.put("master_uid", penjualanDetBarangModel.getMasterUid());
        cv.put("detail_uid", penjualanDetBarangModel.getDetailUid());
        cv.put("barang_jual_uid", penjualanDetBarangModel.getBarangJualUid());
        cv.put("barang_uid", penjualanDetBarangModel.getBarangUid());
        cv.put("satuan_uid", penjualanDetBarangModel.getSatuanUid());
        cv.put("tipe", penjualanDetBarangModel.getTipe());
        cv.put("qty_barang_jual", penjualanDetBarangModel.getQtyBarangJual());
        cv.put("qty_barang", penjualanDetBarangModel.getQtyBarang());
        cv.put("qty_total", penjualanDetBarangModel.getQtyTotal());
        cv.put("versi", penjualanDetBarangModel.getVersi());
        cv.put("qty_barang_primer", penjualanDetBarangModel.getQtyBarang());
        cv.put("qty_total_primer", penjualanDetBarangModel.getQtyTotal());
        return cv;
    }

    public void insert(PenjualanDetBarangModel penjualanDetBarangModel) {
        ContentValues cv = this.setValues(penjualanDetBarangModel);
        this.db.insert("penjualan_det_barang", null, cv);
    }

    public void update(PenjualanDetBarangModel penjualanDetBarangModel) {
        ContentValues cv = this.setValues(penjualanDetBarangModel);
        this.db.update("penjualan_det_barang", cv, "uid = '" + penjualanDetBarangModel.getUid() + "'", null);
    }

    public void delete(String uid) {
        this.db.delete("penjualan_det_barang", "uid = '" + uid + "'", null);
    }

    private void reloadList(String masterUid) {
        this.records.clear();
        String filter = masterUid.isEmpty() ? "" : " AND master_uid = '" + masterUid + "'";
        String sql = "SELECT * FROM penjualan_det_barang WHERE seq <> 0 " + filter;
        Cursor cr = this.db.rawQuery(sql, null);

        PenjualanDetBarangModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new PenjualanDetBarangModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("detail_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_jual_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_barang_jual")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_barang")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_total")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_barang_primer")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_total_primer")),
                        cr.getDouble(cr.getColumnIndexOrThrow("konversi")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<PenjualanDetBarangModel> getRecords(String masterUid) {
        this.reloadList(masterUid);
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("penjualan_det_barang", null, null);
    }
}
