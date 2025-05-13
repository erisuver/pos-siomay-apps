package com.orion.pos_crushty_android.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.orion.pos_crushty_android.R;

public class DBConn extends SQLiteOpenHelper {
    Context ctx;
    public DBConn(Context context) {
        // Jika ada perubahan struktural, naikkan versi database (misalnya ubah dari 2 ke 3)
        super(context, context.getString(R.string.database_name), null, 12);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Panggil semua pembuatan tabel di sini
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            // Menambahkan kolom kategori pada tabel master_barang_jual
            String alterTable = "ALTER TABLE master_barang_jual ADD COLUMN kategori INTEGER DEFAULT 0";
            sqLiteDatabase.execSQL(alterTable);
        }
        if (oldVersion < 9) {
            // Menambahkan kolom kategori pada tabel master_barang
            String alterTable = "ALTER TABLE master_barang ADD COLUMN is_kontrol_stok TEXT";
            sqLiteDatabase.execSQL(alterTable);
        }
        if (oldVersion < 10) {
            String alterTable = "ALTER TABLE master_outlet ADD COLUMN is_stok_bersama TEXT";
            sqLiteDatabase.execSQL(alterTable);

            alterTable = "ALTER TABLE master_barang ADD COLUMN no_urut TEXT";
            sqLiteDatabase.execSQL(alterTable);
        }
        if (oldVersion < 11){
            // Table: stok_opname_dirty - untuk cache stok opname
            String sql = "CREATE TABLE IF NOT EXISTS stok_opname_dirty (" +
                    "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uid TEXT, " +
                    "master_uid TEXT, " +
                    "barang_uid TEXT, " +
                    "satuan_uid TEXT, " +
                    "nama_barang TEXT, " +
                    "nama_satuan TEXT, " +
                    "qty DOUBLE DEFAULT 0 NOT NULL, "+
                    "qty_program DOUBLE DEFAULT 0 NOT NULL, "+
                    "qty_selisih DOUBLE DEFAULT 0 NOT NULL, "+
                    "keterangan TEXT " +
                    ");";
            sqLiteDatabase.execSQL(sql);
        }
        if (oldVersion < 12){
            String sql = "";
            // Table: pengeluaran_lain_mst
            sql = "CREATE TABLE IF NOT EXISTS pengeluaran_lain_mst (" +
                    "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uid TEXT, " +
                    "tanggal INTEGER, " +
                    "nomor TEXT, " +
                    "tipe TEXT, " +
                    "outlet_uid TEXT, " +
                    "karyawan_uid TEXT, " +
                    "keterangan TEXT, " +
                    "user_id TEXT, " +
                    "tgl_input INTEGER, " +
                    "versi INTEGER, " +
                    "last_update INTEGER," +
                    "is_kirim TEXT"+
                    ");";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS idx_outlet_uid ON pengeluaran_lain_mst (outlet_uid);";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS idx_karyawan_uid ON pengeluaran_lain_mst (karyawan_uid);";
            sqLiteDatabase.execSQL(sql);

            // TABLE: pengeluaran_lain_det
            sql = "CREATE TABLE IF NOT EXISTS pengeluaran_lain_det (" +
                    "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uid TEXT, " +
                    "last_update INTEGER, " +
                    "master_uid TEXT, " +
                    "barang_uid TEXT, " +
                    "satuan_uid TEXT, " +
                    "keterangan TEXT, " +
                    "qty DOUBLE, " +
                    "konversi DOUBLE, " +
                    "qty_primer DOUBLE, " +
                    "kas_bank_uid TEXT, " +
                    "jumlah DOUBLE" +
                    ");";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS idx_pengeluaran_lain_det_master_uid ON pengeluaran_lain_det (master_uid);";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS idx_pengeluaran_lain_det_barang_uid ON pengeluaran_lain_det (barang_uid);";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS idx_pengeluaran_lain_det_satuan_uid ON pengeluaran_lain_det (satuan_uid);";
            sqLiteDatabase.execSQL(sql);

            String alterTable = "ALTER TABLE penambah_pengurang_barang ADD COLUMN is_kirim TEXT";
            sqLiteDatabase.execSQL(alterTable);
        }

    }


    // Buat method terpisah untuk membuat semua tabel
    private void createTables(SQLiteDatabase sqLiteDatabase) {
        String sql;

        // Table: versi_data
        sql = "CREATE TABLE IF NOT EXISTS versi_data("+
                "nama_tabel text,"+
                "versi_terakhir INTEGER)";
        sqLiteDatabase.execSQL(sql);

        // Table: login
        sql = "CREATE TABLE IF NOT EXISTS login("+
                "user_id text,"+
                "api_key text)";
        sqLiteDatabase.execSQL(sql);

        // Table: system_user
        sql = "CREATE TABLE IF NOT EXISTS system_user (" +
                "user_id TEXT, " +
                "passwd TEXT, " +
                "last_update INTEGER, " +
                "disable_date INTEGER, " +
                "user_input TEXT, " +
                "tgl_input INTEGER, " +
                "karyawan_uid TEXT, " +
                "outlet_uid TEXT, " +
                "user_name TEXT, " +
                "access_level INTEGER, " +
                "is_apps TEXT, " +
                "is_desktop TEXT, " +
                "versi INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_system_user_karyawan_uid ON system_user (karyawan_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_system_user_outlet_uid ON system_user (outlet_uid);";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS master_outlet (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "disable_date INTEGER, " +
                "user_id TEXT, " +
                "tgl_input INTEGER, " +
                "kode TEXT, " +
                "nama TEXT, " +
                "alamat TEXT, " +
                "penanggung_jawab TEXT, " +
                "kota TEXT, " +
                "keterangan TEXT, " +
                "telepon TEXT, " +
                "bank_uid TEXT, " +
                "kas_uid TEXT, " +
                "versi INTEGER, " +
                "is_stok_bersama TEXT " +
                ");";
        sqLiteDatabase.execSQL(sql);

        // Table: master_barang
        sql = "CREATE TABLE IF NOT EXISTS master_barang (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "disable_date INTEGER, " +
                "user_id TEXT, " +
                "tgl_input INTEGER, " +
                "kode TEXT, " +
                "nama TEXT, " +
                "harga DOUBLE DEFAULT 0 NOT NULL, " +
                "keterangan TEXT, " +
                "satuan_primer_uid TEXT, " +
                "satuan_sekunder_uid TEXT, " +
                "qty_primer DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_sekunder DOUBLE DEFAULT 0 NOT NULL, " +
                "is_barang_stok TEXT, " +
                "versi INTEGER, "+
                "is_kontrol_stok TEXT, "+
                "no_urut TEXT"+
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_master_barang_satuan_primer_uid ON master_barang (satuan_primer_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_master_barang_satuan_sekunder_uid ON master_barang (satuan_sekunder_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: master_barang_jual
        sql = "CREATE TABLE IF NOT EXISTS master_barang_jual (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "disable_date INTEGER, " +
                "user_id TEXT, " +
                "tgl_input INTEGER, " +
                "kode TEXT, " +
                "nama TEXT, " +
                "harga DOUBLE DEFAULT 0 NOT NULL, " +
                "keterangan TEXT, " +
                "gambar TEXT, " +
                "versi INTEGER," +
                "kategori INTEGER DEFAULT 0"+
                ");";
        sqLiteDatabase.execSQL(sql);

        // Table: detail_barang_jual
        sql = "CREATE TABLE IF NOT EXISTS detail_barang_jual (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "master_uid TEXT, " +
                "barang_uid TEXT, " +
                "satuan_uid TEXT, " +
                "tipe TEXT, " +
                "qty DOUBLE DEFAULT 0 NOT NULL, " +
                "konversi DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_primer DOUBLE DEFAULT 0 NOT NULL, " +
                "versi INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_detail_barang_jual_master_uid ON detail_barang_jual (master_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_detail_barang_jual_barang_uid ON detail_barang_jual (barang_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_detail_barang_jual_satuan_uid ON detail_barang_jual (satuan_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: master_satuan
        sql = "CREATE TABLE IF NOT EXISTS master_satuan (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "disable_date INTEGER, " +
                "user_id TEXT, " +
                "tgl_input INTEGER, " +
                "kode TEXT, " +
                "nama TEXT, " +
                "keterangan TEXT, " +
                "versi INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        // Table: penjualan_mst
        sql = "CREATE TABLE IF NOT EXISTS penjualan_mst (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "tgl_input INTEGER, " +
                "user_id TEXT, " +
                "user_edit TEXT, " +
                "tgl_edit INTEGER, " +
                "tanggal INTEGER, " +
                "nomor TEXT, " +
                "outlet_uid TEXT, " +
                "karyawan_uid TEXT, " +
                "keterangan TEXT, " +
                "total DOUBLE DEFAULT 0 NOT NULL, " +
                "bayar DOUBLE DEFAULT 0 NOT NULL, " +
                "kembali DOUBLE DEFAULT 0 NOT NULL, " +
                "versi INTEGER, " +
                "is_kirim TEXT" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_mst_outlet_uid ON penjualan_mst (outlet_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_mst_karyawan_uid ON penjualan_mst (karyawan_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: penjualan_det
        sql = "CREATE TABLE IF NOT EXISTS penjualan_det (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "master_uid TEXT, " +
                "barang_jual_uid TEXT, " +
                "qty DOUBLE DEFAULT 0 NOT NULL, " +
                "harga DOUBLE DEFAULT 0 NOT NULL, " +
                "total DOUBLE DEFAULT 0 NOT NULL, " +
                "keterangan TEXT, " +
                "versi INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_master_uid ON penjualan_det (master_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_barang_jual_uid ON penjualan_det (barang_jual_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: penjualan_det_barang
        sql = "CREATE TABLE IF NOT EXISTS penjualan_det_barang (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "master_uid TEXT, " +
                "detail_uid TEXT, " +
                "barang_jual_uid TEXT, " +
                "barang_uid TEXT, " +
                "satuan_uid TEXT, " +
                "tipe TEXT, " +
                "qty_barang_jual DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_barang DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_total DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_barang_primer DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_total_primer DOUBLE DEFAULT 0 NOT NULL, " +
                "konversi DOUBLE DEFAULT 0 NOT NULL, " +
                "versi INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_barang_master_uid ON penjualan_det_barang (master_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_barang_detail_uid ON penjualan_det_barang (detail_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_barang_barang_jual_uid ON penjualan_det_barang (barang_jual_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_barang_barang_uid ON penjualan_det_barang (barang_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_det_barang_satuan_uid ON penjualan_det_barang (satuan_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: penjualan_bayar
        sql = "CREATE TABLE IF NOT EXISTS penjualan_bayar (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update INTEGER, " +
                "master_uid TEXT, " +
                "kas_bank_uid TEXT, " +
                "tipe_bayar TEXT, " +
                "gambar TEXT, " +
                "total DOUBLE DEFAULT 0 NOT NULL, " +
                "bayar DOUBLE DEFAULT 0 NOT NULL, " +
                "kembali DOUBLE DEFAULT 0 NOT NULL, " +
                "versi INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_bayar_master_uid ON penjualan_bayar (master_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penjualan_bayar_kas_bank_uid ON penjualan_bayar (kas_bank_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: setting_komposisi_mst
        sql = "CREATE TABLE IF NOT EXISTS setting_komposisi_mst (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "berlaku_dari INTEGER, " +
                "berlaku_sampai INTEGER, " +
                "outlet_uid TEXT, " +
                "barang_jual_uid TEXT, " +
                "harga DOUBLE DEFAULT 0 NOT NULL, " +
                "keterangan TEXT, " +
                "user_id TEXT, " +
                "tgl_input INTEGER, " +
                "disable_date INTEGER, " +
                "versi INTEGER, " +
                "last_update INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_setting_komposisi_mst_outlet_uid ON setting_komposisi_mst (outlet_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_setting_komposisi_mst_barang_jual_uid ON setting_komposisi_mst (barang_jual_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: setting_komposisi_det
        sql = "CREATE TABLE IF NOT EXISTS setting_komposisi_det (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "master_uid TEXT, " +
                "barang_uid TEXT, " +
                "satuan_uid TEXT, " +
                "tipe TEXT, " +
                "qty DOUBLE DEFAULT 0 NOT NULL, " +
                "konversi DOUBLE DEFAULT 0 NOT NULL, " +
                "qty_primer DOUBLE DEFAULT 0 NOT NULL, " +
                "versi INTEGER, " +
                "last_update INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_setting_komposisi_det_master_uid ON setting_komposisi_det (master_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_setting_komposisi_det_barang_uid ON setting_komposisi_det (barang_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_setting_komposisi_det_satuan_uid ON setting_komposisi_det (satuan_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: penambah_pengurang_barang
        sql = "CREATE TABLE IF NOT EXISTS penambah_pengurang_barang (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tanggal INTEGER, " +
                "trans_uid TEXT, " +
                "tipe_trans TEXT, " +
                "nomor TEXT, " +
                "outlet_uid TEXT, " +
                "barang_uid TEXT, " +
                "qty_primer DOUBLE DEFAULT 0 NOT NULL, " +
                "versi INTEGER, " +
                "last_update INTEGER, " +
                "user_id," +
                "is_kirim TEXT"+
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_penambah_pengurang_barang_trans_uid ON penambah_pengurang_barang (trans_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penambah_pengurang_barang_outlet_uid ON penambah_pengurang_barang (outlet_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_penambah_pengurang_barang_barang_uid ON penambah_pengurang_barang (barang_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: keranjang_master
        sql = "CREATE TABLE IF NOT EXISTS keranjang_master (" +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "barang_jual_uid TEXT, " +
                "qty DOUBLE DEFAULT 0 NOT NULL "+
                ");";
        sqLiteDatabase.execSQL(sql);

        // Table: keranjang_detail
        sql = "CREATE TABLE IF NOT EXISTS keranjang_detail (" +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "master_seq INTEGER, " +
                "barang_uid TEXT, " +
                "qty DOUBLE DEFAULT 0 NOT NULL, "+
                "qty_default DOUBLE DEFAULT 0 NOT NULL "+
                ");";
        sqLiteDatabase.execSQL(sql);

        // Table: sync_info
        sql = "CREATE TABLE IF NOT EXISTS sync_info (" +
                "uid TEXT, " +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "model TEXT, " +
                "content blob, " +
                "command TEXT, "+
                "last_sync integer "+
                ");";
        sqLiteDatabase.execSQL(sql);


        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS sync_info(id integer PRIMARY KEY AUTOINCREMENT, model varchar(50), content blob, command varchar(50), last_sync integer, uuid varchar)");

        // Table: master_karyawan
        sql = "CREATE TABLE IF NOT EXISTS master_karyawan (" +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uid TEXT, " +
                "versi INTEGER, " +
                "last_update INTEGER, " +
                "disable_date INTEGER, " +
                "tgl_input INTEGER, " +
                "user_id TEXT, " +
                "outlet_uid TEXT, " +
                "kode TEXT, " +
                "nama TEXT, " +
                "jenis_kelamin TEXT, " +
                "jabatan TEXT, " +
                "keterangan TEXT, " +
                "telepon TEXT, " +
                "alamat TEXT, " +
                "NIK TEXT" +   // Penambahan field NIK
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_master_karyawan_outlet_uid ON master_karyawan (outlet_uid);";
        sqLiteDatabase.execSQL(sql);

        // Table: stok_opname_dirty - untuk cache stok opname
        sql = "CREATE TABLE IF NOT EXISTS stok_opname_dirty (" +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uid TEXT, " +
                "master_uid TEXT, " +
                "barang_uid TEXT, " +
                "satuan_uid TEXT, " +
                "nama_barang TEXT, " +
                "nama_satuan TEXT, " +
                "qty DOUBLE DEFAULT 0 NOT NULL, "+
                "qty_program DOUBLE DEFAULT 0 NOT NULL, "+
                "qty_selisih DOUBLE DEFAULT 0 NOT NULL, "+
                "keterangan TEXT " +
                ");";
        sqLiteDatabase.execSQL(sql);

        // Table: pengeluaran_lain_mst
        sql = "CREATE TABLE IF NOT EXISTS pengeluaran_lain_mst (" +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uid TEXT, " +
                "tanggal INTEGER, " +
                "nomor TEXT, " +
                "tipe TEXT, " +
                "outlet_uid TEXT, " +
                "karyawan_uid TEXT, " +
                "keterangan TEXT, " +
                "user_id TEXT, " +
                "tgl_input INTEGER, " +
                "versi INTEGER, " +
                "last_update INTEGER," +
                "is_kirim TEXT"+
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_outlet_uid ON pengeluaran_lain_mst (outlet_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_karyawan_uid ON pengeluaran_lain_mst (karyawan_uid);";
        sqLiteDatabase.execSQL(sql);

        // TABLE: pengeluaran_lain_det
        sql = "CREATE TABLE IF NOT EXISTS pengeluaran_lain_det (" +
                "seq INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uid TEXT, " +
                "last_update INTEGER, " +
                "master_uid TEXT, " +
                "barang_uid TEXT, " +
                "satuan_uid TEXT, " +
                "keterangan TEXT, " +
                "qty DOUBLE, " +
                "konversi DOUBLE, " +
                "qty_primer DOUBLE, " +
                "kas_bank_uid TEXT, " +
                "jumlah DOUBLE" +
                ");";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE INDEX IF NOT EXISTS idx_pengeluaran_lain_det_master_uid ON pengeluaran_lain_det (master_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_pengeluaran_lain_det_barang_uid ON pengeluaran_lain_det (barang_uid);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE INDEX IF NOT EXISTS idx_pengeluaran_lain_det_satuan_uid ON pengeluaran_lain_det (satuan_uid);";
        sqLiteDatabase.execSQL(sql);

    }

    public String getDatabaseLocation() {
        String location = ctx.getDatabasePath(ctx.getString(R.string.database_name)).toString();
        return location;
    }

}
