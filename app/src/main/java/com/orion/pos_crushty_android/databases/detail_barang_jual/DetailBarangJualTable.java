package com.orion.pos_crushty_android.databases.detail_barang_jual;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.master_satuan.MasterSatuanModel;

import java.util.ArrayList;
import java.util.List;

public class DetailBarangJualTable {
    private SQLiteDatabase db;
    private ArrayList<DetailBarangJualModel> records;
    private Context context;

    public DetailBarangJualTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<DetailBarangJualModel>();
    }

    private ContentValues setValues(DetailBarangJualModel detailBarangJualModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", detailBarangJualModel.getUid());
        cv.put("last_update", detailBarangJualModel.getLastUpdate());
        cv.put("master_uid", detailBarangJualModel.getMasterUid());
        cv.put("barang_uid", detailBarangJualModel.getBarangUid());
        cv.put("satuan_uid", detailBarangJualModel.getSatuanUid());
        cv.put("tipe", detailBarangJualModel.getTipe());
        cv.put("qty", detailBarangJualModel.getQty());
        cv.put("konversi", detailBarangJualModel.getKonversi());
        cv.put("qty_primer", detailBarangJualModel.getQtyPrimer());
        cv.put("versi", detailBarangJualModel.getVersi());
        return cv;
    }

    public void insert(DetailBarangJualModel detailBarangJualModel) {
        ContentValues cv = this.setValues(detailBarangJualModel);
        this.db.insert("detail_barang_jual", null, cv);
    }

    public void update(DetailBarangJualModel detailBarangJualModel) {
        ContentValues cv = this.setValues(detailBarangJualModel);
        this.db.update("detail_barang_jual", cv, "uid = '" + detailBarangJualModel.getUid() + "'", null);
    }

    public void delete(String uid) {
        this.db.delete("detail_barang_jual", "uid = '" + uid + "'", null);
    }

    private void reloadList(String uid) {
        this.records.clear();
        String filter = uid.isEmpty() ? "" : " AND master_uid = '" + uid + "'";
        String sql = "SELECT * FROM detail_barang_jual WHERE seq <> 0 " + filter;
        Cursor cr = this.db.rawQuery(sql, null);

        DetailBarangJualModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new DetailBarangJualModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                        cr.getDouble(cr.getColumnIndexOrThrow("konversi")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<DetailBarangJualModel> getRecords() {
        this.reloadList("");
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("detail_barang_jual", null, null);
    }

    public List<DetailBarangJualModel> getRecordsByUid(String uid) {
        this.reloadList(uid);
        return this.records;
    }
}

