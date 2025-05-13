package com.orion.pos_crushty_android.databases.penjualan;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pos_crushty_android.databases.DBConn;
import com.orion.pos_crushty_android.databases.penjualan_bayar.PenjualanBayarModel;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;

import java.util.ArrayList;
import java.util.List;

public class PenjualanTable {
    private SQLiteDatabase db;
    private String tipeBayar;
    private List<PenjualanDetModel> ListPenjualanDet;

    public PenjualanTable(Context context, String tipeBayar) {
        this.db = new DBConn(context).getWritableDatabase();
        this.tipeBayar = tipeBayar;
    }

    public void saveAll() {

    }
}
