package com.orion.pos_crushty_android.databases.keranjang_detail;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.globals.JConst;

import java.util.ArrayList;

public class KeranjangDetailTable {
    private SQLiteDatabase db;
    private ArrayList<KeranjangDetailModel> records;
    private Context context;

    public KeranjangDetailTable(Context context) {
        this.context = context;
        this.db = new DBConn(context).getWritableDatabase();
        this.records = new ArrayList<>();
    }

    private ContentValues setValues(KeranjangDetailModel keranjangDetailModel) {
        ContentValues cv = new ContentValues();
        cv.put("master_seq", keranjangDetailModel.getMasterSeq());
        cv.put("barang_uid", keranjangDetailModel.getBarangUid());
        cv.put("qty", keranjangDetailModel.getQty());
        cv.put("qty_default", keranjangDetailModel.getQty_default());
        return cv;
    }

    public void insert(KeranjangDetailModel keranjangDetailModel) {
        ContentValues cv = this.setValues(keranjangDetailModel);
        this.db.insert("keranjang_detail", null, cv);
    }

    public void update(KeranjangDetailModel keranjangDetailModel) {
        ContentValues cv = this.setValues(keranjangDetailModel);
        this.db.update("keranjang_detail", cv, "seq = " + keranjangDetailModel.getSeq(), null);
    }

    public void delete(int seq) {
        this.db.delete("keranjang_detail", "seq = " + seq, null);
    }

    private void reloadList(int seq) {
        this.records.clear();
        String filter = seq == 0 ? "" : " AND kd.master_seq =  "+ seq;
        String sql = "SELECT kd.seq, kd.master_seq, kd.barang_uid, kd.qty, kd.qty_default, b.nama " +
                " FROM keranjang_detail kd JOIN keranjang_master km ON kd.master_seq = km.seq " +
                " JOIN detail_barang_jual bd ON kd.barang_uid = bd.barang_uid  " +
                " LEFT JOIN master_barang b ON bd.barang_uid = b.uid " +
                " WHERE bd.tipe = '"+ JConst.TIPE_BARANG_JUAL_TOPPING+"'"+filter+
                " GROUP BY kd.seq";
        Cursor cr = this.db.rawQuery(sql, null);

        KeranjangDetailModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new KeranjangDetailModel(
                        cr.getInt(cr.getColumnIndexOrThrow("seq")),
                        cr.getInt(cr.getColumnIndexOrThrow("master_seq")),
                        cr.getString(cr.getColumnIndexOrThrow("barang_uid")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty")),
                        cr.getDouble(cr.getColumnIndexOrThrow("qty_default"))
                );
                tempData.setNamaBarang(cr.getString(cr.getColumnIndexOrThrow("nama")));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        if (cr != null) {
            cr.close();
        }
    }

    public ArrayList<KeranjangDetailModel> getRecords() {
        this.reloadList(0);
        return this.records;
    }

    public ArrayList<KeranjangDetailModel> getRecordsBySeq(int seqMaster) {
        this.reloadList(seqMaster);
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("keranjang_detail", null, null);
    }


    public void updateQtyKeranjang(int seq, double qty) {
        // Melakukan update pada keranjang_master
        this.db.execSQL("UPDATE keranjang_detail SET qty = "+qty+" WHERE seq = " + seq);
    }
}

