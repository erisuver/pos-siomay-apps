package com.orion.pos_crushty_android.databases.stok_opname_det;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;

public class StokOpnameDirtyTable {
    private SQLiteDatabase db;
    private ArrayList<StokOpnameDetModel> records;
    private Context context;

    public StokOpnameDirtyTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<StokOpnameDetModel>();
    }

    private ContentValues setValues(StokOpnameDetModel stokOpnameDetModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", stokOpnameDetModel.getUid());
        cv.put("master_uid", stokOpnameDetModel.getMaster_uid());
        cv.put("barang_uid", stokOpnameDetModel.getBarang_uid());
        cv.put("satuan_uid", stokOpnameDetModel.getSatuan_uid());
        cv.put("nama_barang", stokOpnameDetModel.getNama_brg());
        cv.put("nama_satuan", stokOpnameDetModel.getSatuan());
        cv.put("qty", stokOpnameDetModel.getQty());
        cv.put("qty_program", stokOpnameDetModel.getqty_program());
        cv.put("qty_selisih", stokOpnameDetModel.getqty_selisih());
        cv.put("keterangan", stokOpnameDetModel.getKeterangan());
        return cv;
    }

    public void insert(StokOpnameDetModel stokOpnameDetModel) {
        ContentValues cv = this.setValues(stokOpnameDetModel);
        this.db.insert("stok_opname_dirty", null, cv);
        this.reloadList();
    }

    public void update(StokOpnameDetModel stokOpnameDetModel) {
        ContentValues cv = this.setValues(stokOpnameDetModel);
        this.db.update("stok_opname_dirty", cv, "uid = '" + stokOpnameDetModel.getUid() + "'", null);
        this.reloadList();
    }

    public void delete(String uid) {
        this.db.delete("stok_opname_dirty", "uid = '" + uid + "'", null);
        this.reloadList();
    }

    public void deleteAll() {
        this.db.delete("stok_opname_dirty", null, null);
    }

    private void reloadList() {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM stok_opname_dirty ", null);

        StokOpnameDetModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new StokOpnameDetModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("nama_barang")),
                        cr.getString(cr.getColumnIndexOrThrow("nama_satuan")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_program")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_selisih")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan"))
                );
                tempData.setIs_detail("F");
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<StokOpnameDetModel> getRecords() {
        this.reloadList();
        return this.records;
    }
}

