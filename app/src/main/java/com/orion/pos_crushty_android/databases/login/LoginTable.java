package com.orion.pos_crushty_android.databases.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;
public class LoginTable {
    private SQLiteDatabase db;
    private ArrayList<LoginModel> records;
    private Context context;

    public LoginTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<LoginModel>();
    }

    private ContentValues setValues(LoginModel userModel){
        ContentValues cv = new ContentValues();
        cv.put("user_id", userModel.getUserId());
        cv.put("api_key", userModel.getApiKey());
        return cv;
    }

    public void insert(LoginModel userModel) {
        this.deleteAll();
        ContentValues cv = this.setValues(userModel);
        this.db.insert("login", null, cv);
    }

    public void deleteAll(){
        this.db.delete("login", null, null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT user_id FROM login ", null);

        LoginModel tempData;
        int idx = 0;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new LoginModel(
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("api_key"))
                );
                idx = idx + 1;
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public LoginModel getLoginTableByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<LoginModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public LoginModel getLastLogin(){
        Cursor cr = this.db.rawQuery("SELECT user_id, api_key FROM login ", null);

        LoginModel tempData = new LoginModel();
        int idx = 0;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new LoginModel(
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("api_key"))
                );
                idx = idx + 1;
                return tempData;
            } while(cr.moveToNext());
        }
        return tempData;
    }

    public ArrayList<LoginModel> getRecordsNotReload() {
        return this.records;
    }

}
