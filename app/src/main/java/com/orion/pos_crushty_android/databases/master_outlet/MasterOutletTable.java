package com.orion.pos_crushty_android.databases.master_outlet;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;

import java.util.ArrayList;

public class MasterOutletTable {
    private SQLiteDatabase db;
    private ArrayList<MasterOutletModel> records;
    private Context context;

    public MasterOutletTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    private ContentValues setValues(MasterOutletModel masterOutletModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", masterOutletModel.getUid());
        cv.put("seq", masterOutletModel.getSeq());
        cv.put("last_update", masterOutletModel.getLastUpdate());
        cv.put("disable_date", masterOutletModel.getDisableDate());
        cv.put("user_id", masterOutletModel.getUserId());
        cv.put("tgl_input", masterOutletModel.getTglInput());
        cv.put("kode", masterOutletModel.getKode());
        cv.put("nama", masterOutletModel.getNama());
        cv.put("alamat", masterOutletModel.getAlamat());
        cv.put("penanggung_jawab", masterOutletModel.getPenanggungJawab());
        cv.put("kota", masterOutletModel.getKota());
        cv.put("keterangan", masterOutletModel.getKeterangan());
        cv.put("telepon", masterOutletModel.getTelepon());
        cv.put("bank_uid", masterOutletModel.getBankUid());
        cv.put("kas_uid", masterOutletModel.getKasUid());
        cv.put("versi", masterOutletModel.getVersi());
        return cv;
    }

    public void insert(MasterOutletModel masterOutletModel) {
        ContentValues cv = this.setValues(masterOutletModel);
        this.db.insert("master_outlet", null, cv);
        this.reloadList();
    }

    public void update(MasterOutletModel masterOutletModel) {
        ContentValues cv = this.setValues(masterOutletModel);
        this.db.update("master_outlet", cv, "uid = ?", new String[]{masterOutletModel.getUid()});
        this.reloadList();
    }

    private void reloadList() {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM master_outlet", null);

        MasterOutletModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new MasterOutletModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getLong(cr.getColumnIndexOrThrow("disable_date")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getString(cr.getColumnIndexOrThrow("kode")),
                        cr.getString(cr.getColumnIndexOrThrow("nama")),
                        cr.getString(cr.getColumnIndexOrThrow("alamat")),
                        cr.getString(cr.getColumnIndexOrThrow("penanggung_jawab")),
                        cr.getString(cr.getColumnIndexOrThrow("kota")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getString(cr.getColumnIndexOrThrow("telepon")),
                        cr.getString(cr.getColumnIndexOrThrow("bank_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("kas_uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        if (cr != null) {
            cr.close();
        }
    }

    public ArrayList<MasterOutletModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("master_outlet", null, null);
    }

    public void delete(String uid) {
        this.db.delete("master_outlet", "uid = '"+uid+"'", null);
        this.reloadList();
    }
}
