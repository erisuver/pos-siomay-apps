package com.orion.pos_crushty_android.databases.master_satuan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;

public class MasterSatuanTable {
    private SQLiteDatabase db;
    private ArrayList<MasterSatuanModel> records;
    private Context context;
    private String search;

    public MasterSatuanTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<MasterSatuanModel>();
    }

    private ContentValues setValues(MasterSatuanModel masterSatuanModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", masterSatuanModel.getUid());
        cv.put("last_update", masterSatuanModel.getLastUpdate());
        cv.put("disable_date", masterSatuanModel.getDisableDate());
        cv.put("user_id", masterSatuanModel.getUserId());
        cv.put("tgl_input", masterSatuanModel.getTglInput());
        cv.put("kode", masterSatuanModel.getKode());
        cv.put("nama", masterSatuanModel.getNama());
        cv.put("keterangan", masterSatuanModel.getKeterangan());
        cv.put("versi", masterSatuanModel.getVersi());
        return cv;
    }

    public void insert(MasterSatuanModel masterSatuanModel) {
        ContentValues cv = this.setValues(masterSatuanModel);
        this.db.insert("master_satuan", null, cv);
        this.reloadList();
    }

    public void update(MasterSatuanModel masterSatuanModel) {
        ContentValues cv = this.setValues(masterSatuanModel);
        this.db.update("master_satuan", cv, "uid = '" + masterSatuanModel.getUid() + "'", null);
        this.reloadList();
    }

    public void delete(String uid) {
        this.db.delete("master_satuan", "uid = '" + uid + "'", null);
        this.reloadList();
    }

    private void reloadList() {
        this.records.clear();
        String filter = "";
        if (!search.equals("")){
            filter += "where "+search;
        }
        Cursor cr = this.db.rawQuery("SELECT * FROM master_satuan "+filter, null);

        MasterSatuanModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new MasterSatuanModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getLong(cr.getColumnIndexOrThrow("disable_date")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getString(cr.getColumnIndexOrThrow("kode")),
                        cr.getString(cr.getColumnIndexOrThrow("nama")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<MasterSatuanModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("master_satuan", null, null);
    }
    public void setSearchQuery(String searchQuery) {
        search = searchQuery;
    }
}

