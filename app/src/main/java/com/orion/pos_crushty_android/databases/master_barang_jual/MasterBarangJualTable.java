package com.orion.pos_crushty_android.databases.master_barang_jual;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.detail_barang_jual.DetailBarangJualTable;
import com.orion.pos_crushty_android.databases.master_satuan.MasterSatuanModel;
import com.orion.pos_crushty_android.databases.setting_komposisi_det.SettingKomposisiDetModel;
import com.orion.pos_crushty_android.databases.setting_komposisi_det.SettingKomposisiDetTable;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;

import java.util.ArrayList;
public class MasterBarangJualTable {
    private SQLiteDatabase db;
    private ArrayList<MasterBarangJualModel> records;
    private Context context;
    private String search;

    public MasterBarangJualTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<MasterBarangJualModel>();
        this.search = "";
    }

    private ContentValues setValues(MasterBarangJualModel masterBarangJualModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", masterBarangJualModel.getUid());
        cv.put("last_update", masterBarangJualModel.getLastUpdate());
        cv.put("disable_date", masterBarangJualModel.getDisableDate());
        cv.put("user_id", masterBarangJualModel.getUserId());
        cv.put("tgl_input", masterBarangJualModel.getTglInput());
        cv.put("kode", masterBarangJualModel.getKode());
        cv.put("nama", masterBarangJualModel.getNama());
        cv.put("harga", masterBarangJualModel.getHarga());
        cv.put("keterangan", masterBarangJualModel.getKeterangan());
        cv.put("gambar", masterBarangJualModel.getGambar());
        cv.put("versi", masterBarangJualModel.getVersi());
        cv.put("kategori", masterBarangJualModel.getKategori());
        return cv;
    }

    public void insert(MasterBarangJualModel masterBarangJualModel) {
        ContentValues cv = this.setValues(masterBarangJualModel);
        this.db.insert("master_barang_jual", null, cv);
        this.reloadList();
    }

    public void update(MasterBarangJualModel masterBarangJualModel) {
        ContentValues cv = this.setValues(masterBarangJualModel);
        this.db.update("master_barang_jual", cv, "uid = '" + masterBarangJualModel.getUid() + "'", null);
        this.reloadList();
    }

    public void delete(String uid) {
        this.db.delete("master_barang_jual", "uid = '" + uid + "'", null);
        this.reloadList();
    }

    private void reloadList() {
        this.records.clear();
        String filter = "";
        if (!search.equals("")){
            filter += " and (nama like '%" + search.replace("'", "''") + "%' or kode like '%" + search.replace("'", "''") + "%') ";
        }
        String sql = "SELECT m.*, s.uid as komposisi_uid, s.harga as komposisi_harga " +
                " FROM master_barang_jual m " +
                " LEFT JOIN setting_komposisi_mst s ON s.barang_jual_uid = m.uid and (s.disable_date is null or s.disable_date = 0)  and s.outlet_uid = '"+ SharedPrefsUtils.getStringPreference(context, "outlet_uid") +"' " +
                "   AND s.berlaku_dari <= (strftime('%s', 'now') * 1000) " +
                "   AND s.berlaku_sampai >= (strftime('%s', 'now') * 1000) " +
                " WHERE (m.disable_date is null or m.disable_date = 0) " + filter +
                " GROUP BY m.uid" +
                " ORDER BY m.kategori, m.kode, m.nama";

        Cursor cr = this.db.rawQuery(sql, null);

        MasterBarangJualModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new MasterBarangJualModel(
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
                        cr.getString(cr.getColumnIndexOrThrow("gambar")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi")),
                        cr.getInt(cr.getColumnIndexOrThrow("kategori"))
                );
                tempData.setDetailIds(new DetailBarangJualTable(context).getRecordsByUid(tempData.getUid()));
                tempData.setKomposisiHarga(cr.getDouble(cr.getColumnIndexOrThrow("komposisi_harga")));

                String komposisiUid = cr.isNull(cr.getColumnIndexOrThrow("komposisi_uid")) ? "": cr.getString(cr.getColumnIndexOrThrow("komposisi_uid"));
                if (!komposisiUid.equals("")) {
                    tempData.setKomposisiIds(new SettingKomposisiDetTable(context).getRecordsByUid(komposisiUid));
                }
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<MasterBarangJualModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("master_barang_jual", null, null);
    }

    public void setSearchQuery(String searchQuery) {
        search = searchQuery;
    }
}
