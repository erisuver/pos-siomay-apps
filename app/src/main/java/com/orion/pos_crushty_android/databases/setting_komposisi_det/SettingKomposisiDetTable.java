package com.orion.pos_crushty_android.databases.setting_komposisi_det;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.master_barang.MasterBarangModel;

import java.util.ArrayList;
import java.util.List;

public class SettingKomposisiDetTable {
    private SQLiteDatabase db;
    private ArrayList<SettingKomposisiDetModel> records;
    private Context context;

    public SettingKomposisiDetTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    private void reloadList(String uid) {
        this.records.clear();
        String filter = uid.isEmpty() ? "" : " AND master_uid = '" + uid + "'";
        String sql = "SELECT * FROM setting_komposisi_det WHERE seq <> 0" + filter;
        Cursor cr = this.db.rawQuery(sql, null);

        if (cr != null && cr.moveToFirst()) {
            do {
                SettingKomposisiDetModel tempData = new SettingKomposisiDetModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                        cr.getDouble(cr.getColumnIndexOrThrow("konversi")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        if (cr != null) {
            cr.close();
        }
    }

    public ArrayList<SettingKomposisiDetModel> getRecords() {
        this.reloadList("");
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("setting_komposisi_det", null, null);
    }

    public List<SettingKomposisiDetModel> getRecordsByUid(String uid) {
        this.reloadList(uid);
        return this.records;
    }
}
