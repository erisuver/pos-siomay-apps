package com.orion.pos_crushty_android.databases.system_user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;
public class SystemUserTable {
    private SQLiteDatabase db;
    private ArrayList<SystemUserModel> records;
    private Context context;

    public SystemUserTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<SystemUserModel>();
    }

    private ContentValues setValues(SystemUserModel systemUserModel) {
        ContentValues cv = new ContentValues();
        cv.put("user_id", systemUserModel.getUserId());
        cv.put("passwd", systemUserModel.getPasswd());
        cv.put("last_update", systemUserModel.getLastUpdate());
        cv.put("disable_date", systemUserModel.getDisableDate());
        cv.put("outlet_input_uid", systemUserModel.getOutletInputUid());
        cv.put("user_input", systemUserModel.getUserInput());
        cv.put("tgl_input", systemUserModel.getTglInput());
        cv.put("karyawan_uid", systemUserModel.getKaryawanUid());
        cv.put("outlet_uid", systemUserModel.getOutletUid());
        cv.put("user_name", systemUserModel.getUserName());
        cv.put("access_level", systemUserModel.getAccessLevel());
        cv.put("is_apps", systemUserModel.getIsApps());
        cv.put("is_desktop", systemUserModel.getIsDesktop());
        cv.put("versi", systemUserModel.getVersi());
        return cv;
    }

    public void insert(SystemUserModel systemUserModel) {
        ContentValues cv = this.setValues(systemUserModel);
        this.db.insert("user_id", null, cv);
        this.reloadList();
    }

    public void update(SystemUserModel systemUserModel) {
        ContentValues cv = this.setValues(systemUserModel);
        this.db.update("user_id", cv, "user_id = '" + systemUserModel.getUserId() + "'", null);
        this.reloadList();
    }

    public void delete(String userId) {
        this.db.delete("user_id", "user_id = '" + userId + "'", null);
        this.reloadList();
    }

    private void reloadList() {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM user_id", null);

        SystemUserModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new SystemUserModel(
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("password")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getLong(cr.getColumnIndexOrThrow("disable_date")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_input_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("user_input")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("user_name")),
                        cr.getInt(cr.getColumnIndexOrThrow("access_level")),
                        cr.getString(cr.getColumnIndexOrThrow("is_apps")),
                        cr.getString(cr.getColumnIndexOrThrow("is_desktop")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<SystemUserModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("user_id", null, null);
    }
}
