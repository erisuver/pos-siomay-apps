package com.orion.pos_crushty_android.databases.penambah_pengurang_barang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.detail_barang_jual.DetailBarangJualTable;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualModel;

import java.util.ArrayList;
import java.util.List;

public class PenambahPengurangBarangTable {
    private SQLiteDatabase db;
    private ArrayList<PenambahPengurangBarangModel> records;
    private Context context;

    public PenambahPengurangBarangTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    public PenambahPengurangBarangTable(Context context, SQLiteDatabase db) {
        this.context = context;
        this.db = db;
        this.records = new ArrayList<>();
    }

    // Set ContentValues for insertion and update
    private ContentValues setValues(PenambahPengurangBarangModel model) {
        ContentValues cv = new ContentValues();
        cv.put("uid", model.getUid());
        cv.put("tanggal", model.getTanggal());
        cv.put("trans_uid", model.getTransUid());
        cv.put("tipe_trans", model.getTipeTrans());
        cv.put("nomor", model.getNomor());
        cv.put("outlet_uid", model.getOutletUid());
        cv.put("barang_uid", model.getBarangUid());
        cv.put("qty_primer", model.getQtyPrimer());
        cv.put("versi", model.getVersi());
        cv.put("last_update", model.getLastUpdate());
        cv.put("user_id", model.getUserId());
        return cv;
    }

    // Insert method
    public void insert(PenambahPengurangBarangModel model) {
        ContentValues cv = this.setValues(model);
        this.db.insert("penambah_pengurang_barang", null, cv);
        this.reloadList();
    }

    // Update method
    public void update(PenambahPengurangBarangModel model) {
        ContentValues cv = this.setValues(model);
        this.db.update("penambah_pengurang_barang", cv, "uid = '" + model.getUid() + "'", null);
        this.reloadList();
    }

    // Delete method
    public void delete(String uid) {
        this.db.delete("penambah_pengurang_barang", "uid = '" + uid + "'", null);
        this.reloadList();
    }

    // Reload records from the database
    private void reloadList() {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM penambah_pengurang_barang", null);

        if (cr != null && cr.moveToFirst()) {
            do {
                PenambahPengurangBarangModel model = new PenambahPengurangBarangModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("trans_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe_trans")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id"))
                );
                this.records.add(model);
            } while (cr.moveToNext());
        }

        if (cr != null) {
            cr.close();
        }
    }

    // Get all records
    public ArrayList<PenambahPengurangBarangModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    // Delete all records
    public void deleteAll() {
        this.db.delete("penambah_pengurang_barang", null, null);
    }

    public ArrayList<PenambahPengurangBarangModel>  getRecords(String transUid, String tipeTrans) {
        this.reloadList(transUid, tipeTrans);
        return this.records;
    }
    private void reloadList(String transUid, String tipeTrans) {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM penambah_pengurang_barang WHERE trans_uid = '"+transUid+"' and tipe_trans = '"+tipeTrans+"'" , null);

        if (cr != null && cr.moveToFirst()) {
            do {
                PenambahPengurangBarangModel model = new PenambahPengurangBarangModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("trans_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("tipe_trans")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id"))
                );
                this.records.add(model);
            } while (cr.moveToNext());
        }

        if (cr != null) {
            cr.close();
        }
    }

    public String cekBarangMinus(String barangUidIn, String outletUid, long tanggal) {
        String sql = "SELECT b.nama AS barang, s.nama AS satuan, b.uid, " +
                " SUM(pb.qty_primer) AS stok, abs(SUM(pb.qty_primer)) AS saldoakhir " +
                " FROM penambah_pengurang_barang pb " +
                " LEFT JOIN master_barang b ON pb.barang_uid = b.uid " +
                " LEFT JOIN master_satuan s ON b.satuan_primer_uid = s.uid " +
                " WHERE pb.uid IS NOT NULL AND b.uid IN (" + barangUidIn + ") " +
                " AND pb.outlet_uid = '"+ outletUid +"'"+
                " AND tanggal <= " + tanggal +
                " GROUP BY pb.outlet_uid, b.nama, s.nama, b.uid " +
                " HAVING stok < 0";

        Cursor cursor = db.rawQuery(sql, null);
        StringBuilder hasil = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                String barang = cursor.getString(cursor.getColumnIndexOrThrow("barang"));
                String saldoAkhir = cursor.getString(cursor.getColumnIndexOrThrow("saldoakhir"));
                String satuan = cursor.getString(cursor.getColumnIndexOrThrow("satuan"));

                hasil.append("\n").append(barang).append(" kurang ").append(saldoAkhir).append(" ").append(satuan);
            } while (cursor.moveToNext());
        }

        cursor.close();

        if (hasil.length() > 0) {
            return "Stok tidak mencukupi: " + hasil;
        }

        return "";
    }

}

