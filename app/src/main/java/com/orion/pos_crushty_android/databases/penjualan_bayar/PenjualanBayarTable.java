package com.orion.pos_crushty_android.databases.penjualan_bayar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;

public class PenjualanBayarTable {
    private SQLiteDatabase db;
    private ArrayList<PenjualanBayarModel> records;

    public PenjualanBayarTable(Context context) {
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    private ContentValues setValues(PenjualanBayarModel model) {
        ContentValues cv = new ContentValues();
        cv.put("uid", model.getUid());
        cv.put("last_update", model.getLastUpdate());
        cv.put("master_uid", model.getMasterUid());
        cv.put("kas_bank_uid", model.getKasBankUid());
        cv.put("tipe_bayar", model.getTipeBayar());
        cv.put("gambar", model.getGambar());
        cv.put("total", model.getTotal());
        cv.put("bayar", model.getBayar());
        cv.put("kembali", model.getKembali());
        cv.put("versi", model.getVersi());
        return cv;
    }

    public void insert(PenjualanBayarModel model) {
        ContentValues cv = this.setValues(model);
        this.db.insert("penjualan_bayar", null, cv);
        this.reloadList();
    }

    public void update(PenjualanBayarModel model) {
        ContentValues cv = this.setValues(model);
        this.db.update("penjualan_bayar", cv, "uid = ?", new String[]{model.getUid()});
        this.reloadList();
    }

    public void delete(String uid) {
        this.db.delete("penjualan_bayar", "uid = ?", new String[]{uid});
        this.reloadList();
    }

    private void reloadList() {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM penjualan_bayar", null);

        if (cr != null && cr.moveToFirst()) {
            do {
                PenjualanBayarModel tempData = new PenjualanBayarModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("kas_bank_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe_bayar")),
                        cr.getString(cr.getColumnIndexOrThrow("gambar")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total")),
                        cr.getDouble(cr.getColumnIndexOrThrow("bayar")),
                        cr.getDouble(cr.getColumnIndexOrThrow("kembali")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }

        if (cr != null) {
            cr.close();
        }
    }

    public ArrayList<PenjualanBayarModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("penjualan_bayar", null, null);
    }

    public PenjualanBayarModel getRecordByMasterUid(String masterUid){
        Cursor cr = this.db.rawQuery("SELECT * FROM penjualan_bayar where master_uid = '"+masterUid+"'", null);

        PenjualanBayarModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new PenjualanBayarModel(
                    cr.getString(cr.getColumnIndexOrThrow("uid")),
                    cr.getInt(cr.getColumnIndexOrThrow("seq")),
                    cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                    cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("kas_bank_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("tipe_bayar")),
                    cr.getString(cr.getColumnIndexOrThrow("gambar")),
                    cr.getDouble(cr.getColumnIndexOrThrow("total")),
                    cr.getDouble(cr.getColumnIndexOrThrow("bayar")),
                    cr.getDouble(cr.getColumnIndexOrThrow("kembali")),
                    cr.getInt(cr.getColumnIndexOrThrow("versi"))
            );
            return tempData;
        }else{
            return null;
        }
    }
}


