package com.orion.pos_crushty_android.databases.penjualan_mst;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualModel;
import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangTable;
import com.orion.pos_crushty_android.databases.penjualan_bayar.PenjualanBayarTable;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetTable;
import com.orion.pos_crushty_android.databases.penjualan_det_barang.PenjualanDetBarangTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;

import java.util.ArrayList;

public class PenjualanMstTable {
    private SQLiteDatabase db;
    private ArrayList<PenjualanMstModel> records;
    private Context context;
    private String filter;

    public PenjualanMstTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<PenjualanMstModel>();
        this.filter = "";
    }

    private ContentValues setValues(PenjualanMstModel penjualanMstModel) {
        ContentValues cv = new ContentValues();
        cv.put("uid", penjualanMstModel.getUid());
        cv.put("last_update", penjualanMstModel.getLastUpdate());
        cv.put("tgl_input", penjualanMstModel.getTglInput());
        cv.put("user_id", penjualanMstModel.getUserId());
        cv.put("user_edit", penjualanMstModel.getUserEdit());
        cv.put("tgl_edit", penjualanMstModel.getTglEdit());
        cv.put("tanggal", penjualanMstModel.getTanggal());
        cv.put("nomor", penjualanMstModel.getNomor());
        cv.put("outlet_uid", penjualanMstModel.getOutletUid());
        cv.put("karyawan_uid", penjualanMstModel.getKaryawanUid());
        cv.put("keterangan", penjualanMstModel.getKeterangan());
        cv.put("total", penjualanMstModel.getTotal());
        cv.put("bayar", penjualanMstModel.getBayar());
        cv.put("kembali", penjualanMstModel.getKembali());
        cv.put("versi", penjualanMstModel.getVersi());
        if (penjualanMstModel.getIs_kirim().equals("")) {
            cv.put("is_kirim", "T");
        }
        return cv;
    }

    public void insert(PenjualanMstModel penjualanMstModel) {
        ContentValues cv = this.setValues(penjualanMstModel);
        this.db.insert("penjualan_mst", null, cv);
    }

    public void update(PenjualanMstModel penjualanMstModel) {
        ContentValues cv = this.setValues(penjualanMstModel);
        this.db.update("penjualan_mst", cv, "uid = '" + penjualanMstModel.getUid() + "'", null);
    }

    public void delete(String uid) {
        this.db.delete("penjualan_mst", "uid = '" + uid + "'", null);
    }

    private void reloadList() {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM penjualan_mst WHERE is_kirim = 'F' or is_kirim is null", null);

        PenjualanMstModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new PenjualanMstModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("user_edit")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_edit")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total")),
                        cr.getDouble(cr.getColumnIndexOrThrow("bayar")),
                        cr.getDouble(cr.getColumnIndexOrThrow("kembali")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                tempData.setBayarIds(new PenjualanBayarTable(context).getRecordByMasterUid(cr.getString(cr.getColumnIndexOrThrow("uid"))));
                tempData.setDetIds(new PenjualanDetTable(context).getRecords(cr.getString(cr.getColumnIndexOrThrow("uid"))));
                tempData.setDetBarangIds(new PenjualanDetBarangTable(context).getRecords(cr.getString(cr.getColumnIndexOrThrow("uid"))));
                tempData.setDetPenambahPengurangIds(new PenambahPengurangBarangTable(context).getRecords(cr.getString(cr.getColumnIndexOrThrow("uid")), JConst.TIPE_TRANS_PENJUALAN));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
    }

    public ArrayList<PenjualanMstModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("penjualan_mst", null, null);
    }

    public void deleteTransSeminggu() {
        String whereClause = "tanggal < strftime('%s', 'now', '-7 days')";
        this.db.delete("penjualan_mst", whereClause, null);
    }


    public PenjualanMstModel getRecordByUid(String uid) {
        Cursor cr = this.db.rawQuery("SELECT m.*, o.nama as nama_outlet, k.nama as nama_karyawan " +
                "FROM penjualan_mst m " +
                "LEFT JOIN master_outlet o ON o.uid = m.outlet_uid " +
                "LEFT JOIN master_karyawan k ON (m.karyawan_uid = k.uid) " +
                "where m.uid = '" + uid + "'", null);

        PenjualanMstModel tempData;
        if (cr != null && cr.moveToFirst()) {
            tempData = new PenjualanMstModel(
                    cr.getString(cr.getColumnIndexOrThrow("uid")),
                    cr.getInt(cr.getColumnIndexOrThrow("seq")),
                    cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                    cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                    cr.getString(cr.getColumnIndexOrThrow("user_id")),
                    cr.getString(cr.getColumnIndexOrThrow("user_edit")),
                    cr.getLong(cr.getColumnIndexOrThrow("tgl_edit")),
                    cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                    cr.getString(cr.getColumnIndexOrThrow("nomor")),
                    cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                    cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                    cr.getDouble(cr.getColumnIndexOrThrow("total")),
                    cr.getDouble(cr.getColumnIndexOrThrow("bayar")),
                    cr.getDouble(cr.getColumnIndexOrThrow("kembali")),
                    cr.getInt(cr.getColumnIndexOrThrow("versi"))
            );
            tempData.setNamaOutlet(cr.getString(cr.getColumnIndexOrThrow("nama_outlet")));
            tempData.setNamaKaryawan(cr.getString(cr.getColumnIndexOrThrow("nama_karyawan")));
            return tempData;
        } else {
            return null;
        }
    }

    public ArrayList<PenjualanMstModel> getRecordsForReport() {
        this.records.clear();
        String sql = "SELECT m.*, b.tipe_bayar FROM penjualan_mst m JOIN penjualan_bayar b ON b.master_uid = m.uid WHERE m.seq <> 0 and (is_kirim = 'F' or is_kirim is null) " + filter
                        +" ORDER BY m.tanggal desc, m.nomor asc";
        Cursor cr = this.db.rawQuery(sql, null);

        PenjualanMstModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new PenjualanMstModel(
                        cr.getString(cr.getColumnIndexOrThrow("uid")),
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_input")),
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("user_edit")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_edit")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor")),
                        cr.getString(cr.getColumnIndexOrThrow("outlet_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("karyawan_uid")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total")),
                        cr.getDouble(cr.getColumnIndexOrThrow("bayar")),
                        cr.getDouble(cr.getColumnIndexOrThrow("kembali")),
                        cr.getInt(cr.getColumnIndexOrThrow("versi"))
                );
                tempData.setTipeBayar(cr.getString(cr.getColumnIndexOrThrow("tipe_bayar")));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

    public void setFilter(long dateFromLong, long dateToLong, String barangJualUid, String outletUid, String karyawanUid) {
        this.filter = "";
        if (dateFromLong != 0 && dateToLong != 0) {
            this.filter += " and m.tanggal >= " + dateFromLong + " and m.tanggal <= " + dateToLong;
        }
        if (!barangJualUid.equals("")){
            this.filter += " and exists (select d.master_uid from penjualan_det d where d.master_uid = m.uid and d.barang_jual_uid = '"+barangJualUid+"') ";
        }
        if (!outletUid.equals("")){
            this.filter += " and m.outlet_uid = '"+outletUid+"' ";
        }
//        if (!karyawanUid.equals("")){
//            this.filter += " and m.karyawan_uid = '"+karyawanUid+"' ";
//        }
    }

    public double getTotalPendapatan() {
        double total = 0;
        String sql = "SELECT SUM(total) as total FROM penjualan_mst m WHERE seq <> 0 and (is_kirim = 'F' or is_kirim is null) " + filter;
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return total;
    }

    public int getCountPenjualanBelumSync() {
        int result = 0;
        String sql = "SELECT COUNT(*) as jml FROM penjualan_mst m WHERE is_kirim = 'F' or is_kirim is null ";
        Cursor cursor = JApplication.getInstance().db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow("jml"));
        }
        cursor.close();
        return result;
    }
}
