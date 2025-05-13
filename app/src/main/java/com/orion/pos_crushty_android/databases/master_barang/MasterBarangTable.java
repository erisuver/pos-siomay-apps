package com.orion.pos_crushty_android.databases.master_barang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;

import java.util.ArrayList;

public class MasterBarangTable {
    private SQLiteDatabase db;
    private ArrayList<MasterBarangModel> records;
    private Context context;
    private String search;
    private boolean isMunculNonStok;

    public MasterBarangTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
        this.search = "";
        this.isMunculNonStok = true;
    }

    private ContentValues setValues(MasterBarangModel masterBarangModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", masterBarangModel.getUid());
        cv.put("last_update", masterBarangModel.getLastUpdate());
        cv.put("disable_date", masterBarangModel.getDisableDate());
        cv.put("user_id", masterBarangModel.getUserId());
        cv.put("tgl_input", masterBarangModel.getTglInput());
        cv.put("kode", masterBarangModel.getKode());
        cv.put("nama", masterBarangModel.getNama());
        cv.put("harga", masterBarangModel.getHarga());
        cv.put("keterangan", masterBarangModel.getKeterangan());
        cv.put("satuan_primer_uid", masterBarangModel.getSatuanPrimerUid());
        cv.put("satuan_sekunder_uid", masterBarangModel.getSatuanSekunderUid());
        cv.put("qty_primer", masterBarangModel.getQtyPrimer());
        cv.put("qty_sekunder", masterBarangModel.getQtySekunder());
        cv.put("is_barang_stok", masterBarangModel.getIsBarangStok());
        cv.put("versi", masterBarangModel.getVersi());
        return cv;
    }

    public void insert(MasterBarangModel masterBarangModel) {
        ContentValues cv = this.setValues(masterBarangModel);
        this.db.insert("master_barang", null, cv);
        this.reloadList();
    }

    public void update(MasterBarangModel masterBarangModel) {
        ContentValues cv = this.setValues(masterBarangModel);
        this.db.update("master_barang", cv, "seq = ?", new String[]{String.valueOf(masterBarangModel.getSeq())});
        this.reloadList();
    }

    private void reloadList() {
        this.records.clear();
        String filter = "";
        if (!search.equals("")){
            filter += " and (m.nama like '%" + search.replace("'", "''") + "%' or m.kode like '%" + search.replace("'", "''") + "%') ";
        }
        if (!isMunculNonStok){
            filter += " and m.is_kontrol_stok != 'F'";
        }
        String sql = "SELECT m.*, s.nama as nama_satuan " +
                     " FROM master_barang m LEFT JOIN master_satuan s ON (m.satuan_primer_uid = s.uid) " +
                     " WHERE m.disable_date is null or m.disable_date = 0 "+filter+
                     " ORDER BY m.kode asc, m.nama asc";
        Cursor cr = this.db.rawQuery(sql, null);

        if (cr != null && cr.moveToFirst()) {
            do {
                MasterBarangModel tempData = new MasterBarangModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getLong(cr.getColumnIndexOrThrow("disable_date")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getString(cr.getColumnIndexOrThrow("kode")),
                        cr.getString(cr.getColumnIndexOrThrow("nama")),
                        cr.getDouble(cr.getColumnIndexOrThrow("harga")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_primer_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("satuan_sekunder_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_primer")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_sekunder")),
                        cr.getString(cr.getColumnIndexOrThrow("is_barang_stok")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                tempData.setNamaSatuanPrimer(cr.getString(cr.getColumnIndexOrThrow("nama_satuan")));
                tempData.setIsKontrolStok(cr.getString(cr.getColumnIndexOrThrow("is_kontrol_stok")));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        if (cr != null) {
            cr.close();
        }
    }

    public ArrayList<MasterBarangModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("master_barang", null, null);
        this.records.clear();
    }

    public void setSearchQuery(String searchQuery) {
        search = searchQuery;
    }

    public void setMunculNonStok(boolean munculNonStok) {
        isMunculNonStok = munculNonStok;
    }
}
