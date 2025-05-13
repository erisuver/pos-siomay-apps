package com.orion.pos_crushty_android.databases.stok_opname_det;

public class StokOpnameDetModel {
    private String uid;
    private String master_uid;
    private String barang_uid;
    private String satuan_uid;
    private String nama_brg;
    private String satuan;
    private double qty;
    private double qty_program;
    private double qty_selisih;
    private String keterangan;
    private String is_detail;

    public StokOpnameDetModel(String uid, String master_uid, String barang_uid, String satuan_uid, String nama_brg, String satuan, double qty, double qty_program, double qty_selisih, String keterangan) {
        this.uid = uid;
        this.master_uid = master_uid;
        this.barang_uid = barang_uid;
        this.satuan_uid = satuan_uid;
        this.nama_brg = nama_brg;
        this.satuan = satuan;
        this.qty = qty;
        this.qty_program = qty_program;
        this.qty_selisih = qty_selisih;
        this.keterangan = keterangan;
    }

    public StokOpnameDetModel() {
        this.uid = "";
        this.master_uid = "";
        this.barang_uid = "";
        this.nama_brg = "";
        this.satuan = "";
        this.qty = 0;
        this.qty_program = 0;
        this.qty_selisih = 0;
        this.satuan_uid = "";
        this.keterangan = "";
        this.is_detail = "F";
    }

    public String getIs_detail() {
        return is_detail;
    }

    public void setIs_detail(String is_detail) {
        this.is_detail = is_detail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMaster_uid() {
        return master_uid;
    }

    public void setMaster_uid(String master_uid) {
        this.master_uid = master_uid;
    }

    public String getBarang_uid() {
        return barang_uid;
    }

    public void setBarang_uid(String barang_uid) {
        this.barang_uid = barang_uid;
    }

    public String getNama_brg() {
        return nama_brg;
    }

    public void setNama_brg(String nama_brg) {
        this.nama_brg = nama_brg;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getqty_program() {
        return qty_program;
    }

    public void setqty_program(double qty_program) {
        this.qty_program = qty_program;
    }

    public double getqty_selisih() {
        return qty_selisih;
    }

    public void setqty_selisih(double qty_selisih) {
        this.qty_selisih = qty_selisih;
    }

    public String getSatuan_uid() {
        return satuan_uid;
    }

    public void setSatuan_uid(String satuan_uid) {
        this.satuan_uid = satuan_uid;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
