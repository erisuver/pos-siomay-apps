package com.orion.pos_crushty_android.databases.pengeluaran_lain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangTable;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetTable;
import com.orion.pos_crushty_android.globals.JConst;

import java.util.ArrayList;
import java.util.UUID;

public class PengeluaranLainMstTable {
    private SQLiteDatabase db;
    private ArrayList<PengeluaranLainMstModel> records;
    private Context context;
    private String filter;

    public PengeluaranLainMstTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
        this.filter = "";
    }

    public PengeluaranLainMstTable(Context context, SQLiteDatabase db) {
        this.context = context;
        this.db = db;
        this.records = new ArrayList<>();
        this.filter = "";
    }

    private ContentValues setValues(PengeluaranLainMstModel model) {
        ContentValues cv = new ContentValues();
        cv.put("uid", model.getUid());
        cv.put("tanggal", model.getTanggal());
        cv.put("nomor", model.getNomor());
        cv.put("tipe", model.getTipe());
        cv.put("outlet_uid", model.getOutletUid());
        cv.put("karyawan_uid", model.getKaryawanUid());
        cv.put("keterangan", model.getKeterangan());
        cv.put("user_id", model.getUserId());
        cv.put("tgl_input", model.getTglInput());
        cv.put("last_update", model.getLastUpdate());
        cv.put("versi", model.getVersi());
        cv.put("is_kirim", "F");
        return cv;
    }

    public void insert(PengeluaranLainMstModel model) {
        ContentValues cv = setValues(model);
        db.insert("pengeluaran_lain_mst", null, cv);
    }

    public void update(PengeluaranLainMstModel model) {
        ContentValues cv = setValues(model);
        db.update("pengeluaran_lain_mst", cv, "uid = ?", new String[]{model.getUid()});
    }

    public void delete(String uid) {
        db.delete("pengeluaran_lain_mst", "uid = ?", new String[]{uid});
    }

    public void deleteAll() {
        db.delete("pengeluaran_lain_mst", null, null);
    }

    public ArrayList<PengeluaranLainMstModel> getRecords() {
        records.clear();
        Cursor cr = db.rawQuery("SELECT * FROM pengeluaran_lain_mst", null);

        if (cr != null && cr.moveToFirst()) {
            do {
                PengeluaranLainMstModel tempData = new PengeluaranLainMstModel(
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                tempData.setDetIds(new PengeluaranLainDetTable(context).getRecords(cr.getString(cr.getColumnIndexOrThrow("uid"))));

                records.add(tempData);
            } while (cr.moveToNext());
            cr.close();
        }
        return records;
    }

    public ArrayList<PengeluaranLainMstModel> getRecords4Sync() {
        records.clear();
        Cursor cr = db.rawQuery("SELECT * FROM pengeluaran_lain_mst WHERE is_kirim = 'F' or is_kirim is null", null);

        if (cr != null && cr.moveToFirst()) {
            do {
                PengeluaranLainMstModel tempData = new PengeluaranLainMstModel(
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                tempData.setDetIds(new PengeluaranLainDetTable(context).getRecords(cr.getString(cr.getColumnIndexOrThrow("uid"))));
                if (cr.getString(cr.getColumnIndexOrThrow("tipe")).equals(JConst.TRANS_PENGELUARAN_LAIN_BARANG)) {
                    tempData.setPpIds(new PenambahPengurangBarangTable(context).getRecords(cr.getString(cr.getColumnIndexOrThrow("uid")), JConst.TRANS_PENGELUARAN_LAIN_BARANG));
                }
                records.add(tempData);
            } while (cr.moveToNext());
            cr.close();
        }
        return records;
    }

    public PengeluaranLainMstModel getRecordByUid(String uid) {
        Cursor cr = db.rawQuery("SELECT * FROM pengeluaran_lain_mst WHERE uid = ?", new String[]{uid});
        if (cr != null && cr.moveToFirst()) {
            PengeluaranLainMstModel model = new PengeluaranLainMstModel(
                    cr.getInt(cr.getColumnIndexOrThrow("seq")),
                    cr.getString(cr.getColumnIndexOrThrow("uid")),
                    cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                    cr.getString(cr.getColumnIndexOrThrow("nomor")),
                    cr.getString(cr.getColumnIndexOrThrow("tipe")),
                    cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                    cr.getString(cr.getColumnIndexOrThrow("user_id")),
                    cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                    cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                    cr.getInt(cr.getColumnIndexOrThrow("versi"))
            );
            cr.close();
            return model;
        }
        return null;
    }

    public int getCountPengeluaranLainBelumSync() {
        int result = 0;
        String sql = "SELECT COUNT(*) as jml FROM pengeluaran_lain_mst m WHERE is_kirim = 'F' or is_kirim is null";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow("jml"));
        }
        cursor.close();
        return result;
    }


    public void setFilter(long dateFromLong, long dateToLong, String outletUid, String karyawanUid) {
        this.filter = "";
        if (dateFromLong != 0 && dateToLong != 0) {
            this.filter += " and m.tanggal >= " + dateFromLong + " and m.tanggal <= " + dateToLong;
        }
        if (!outletUid.equals("")){
            this.filter += " and m.outlet_uid = '"+outletUid+"' ";
        }
        if (!karyawanUid.equals("")){
            this.filter += " and m.karyawan_uid = '"+karyawanUid+"' ";
        }
    }
}
