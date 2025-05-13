package com.orion.pos_crushty_android.databases.keranjang_detail;

public class KeranjangDetailModel {
    private int seq;
    private int masterSeq;
    private String barangUid;
    private double qty;
    private double qty_default;
    //field bayangan
    private String namaBarang;

    // Constructor


    public KeranjangDetailModel(int seq, int masterSeq, String barangUid, double qty, double qty_default) {
        this.seq = seq;
        this.masterSeq = masterSeq;
        this.barangUid = barangUid;
        this.qty = qty;
        this.qty_default = qty_default;
    }

    public KeranjangDetailModel() {
        this.seq = 0;
        this.masterSeq = 0;
        this.barangUid = "";
        this.qty = 0;
        this.qty_default = 0;
    }

    // Getters and Setters
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getMasterSeq() {
        return masterSeq;
    }

    public void setMasterSeq(int masterSeq) {
        this.masterSeq = masterSeq;
    }

    public String getBarangUid() {
        return barangUid;
    }

    public void setBarangUid(String barangUid) {
        this.barangUid = barangUid;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public double getQty_default() {
        return qty_default;
    }

    public void setQty_default(double qty_default) {
        this.qty_default = qty_default;
    }
}

