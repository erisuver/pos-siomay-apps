package com.orion.pos_crushty_android.databases.pengeluaran_lain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PengeluaranLainDetTable {
    private SQLiteDatabase db;
    private ArrayList<PengeluaranLainDetModel> records;
    private Context context;

    public PengeluaranLainDetTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    public PengeluaranLainDetTable(Context context, SQLiteDatabase db) {
        this.context = context;
        this.db = db;
        this.records = new ArrayList<>();
    }

    private ContentValues setValues(PengeluaranLainDetModel model) {
        ContentValues cv = new ContentValues();
        cv.put("uid", model.getUid());
        cv.put("master_uid", model.getMasterUid());
        cv.put("barang_uid", model.getBarangUid());
        cv.put("satuan_uid", model.getSatuanUid());
        cv.put("keterangan", model.getKeterangan());
        cv.put("qty", model.getQty());
        cv.put("konversi", model.getKonversi());
        cv.put("qty_primer", model.getQtyPrimer());
        cv.put("kas_bank_uid", model.getKasBankUid());
        cv.put("jumlah", model.getJumlah());
        return cv;
    }

    public void insert(PengeluaranLainDetModel model) {
        ContentValues cv = setValues(model);
        db.insert("pengeluaran_lain_det", null, cv);
    }

    public void insert(List<PengeluaranLainDetModel> models) {
        db.beginTransaction();
        try {
            for (PengeluaranLainDetModel model : models) {
                ContentValues cv = setValues(model);
                db.insert("pengeluaran_lain_det", null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void update(PengeluaranLainDetModel model) {
        ContentValues cv = setValues(model);
        db.update("pengeluaran_lain_det", cv, "uid = ?", new String[]{model.getUid()});
    }

    public void delete(String uid) {
        db.delete("pengeluaran_lain_det", "uid = ?", new String[]{uid});
    }

    public void deleteAll() {
        db.delete("pengeluaran_lain_det", null, null);
    }

    public ArrayList<PengeluaranLainDetModel> getRecords(String masterUid) {
        records.clear();
        String sql = "SELECT p.*, b.nama as nama_barang, s.nama as nama_satuan " +
                        "FROM pengeluaran_lain_det p " +
                        "LEFT JOIN master_barang b ON p.barang_uid = b.uid " +
                        "LEFT JOIN master_satuan s ON p.satuan_uid = s.uid " +
                        "WHERE p.master_uid = '"+masterUid+"'";
        Cursor cr = db.rawQuery(sql, null);

        if (cr != null && cr.moveToFirst()) {
            do {
                PengeluaranLainDetModel tempData = new PengeluaranLainDetModel(
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                        cr.getDouble(cr.getColumnIndexOrThrow("konversi")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                        cr.getString(cr.getColumnIndexOrThrow("kas_bank_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("jumlah"))
                );
                tempData.setBarang(cr.getString(cr.getColumnIndexOrThrow("nama_barang")));
                tempData.setSatuan(cr.getString(cr.getColumnIndexOrThrow("nama_satuan")));

                records.add(tempData);
            } while (cr.moveToNext());
            cr.close();
        }
        return records;
    }

    public PengeluaranLainDetModel getRecordByUid(String masterUid) {
        String sql = "SELECT p.*, b.nama as nama_barang, s.nama as nama_satuan " +
                "FROM pengeluaran_lain_det p " +
                "LEFT JOIN master_barang b ON p.barang_uid = b.uid " +
                "LEFT JOIN master_satuan s ON p.satuan_uid = s.uid " +
                "WHERE p.master_uid = '"+masterUid+"'";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()) {
            PengeluaranLainDetModel model = new PengeluaranLainDetModel(
                    cr.getInt(cr.getColumnIndexOrThrow("seq")),
                    cr.getString(cr.getColumnIndexOrThrow("uid")),
                    cr.getString(cr.getColumnIndexOrThrow("master_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("satuan_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                    cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                    cr.getDouble(cr.getColumnIndexOrThrow("konversi")),
                    cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                    cr.getString(cr.getColumnIndexOrThrow("kas_bank_uid")),
                    cr.getDouble(cr.getColumnIndexOrThrow("jumlah"))
            );
            cr.close();
            return model;
        }
        return null;
    }
}
