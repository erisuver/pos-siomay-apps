package com.orion.pos_crushty_android.databases.pengeluaran_lain;

public class PengeluaranLainDetModel {
    private int seq;
    private String uid;
    private String masterUid;
    private String barangUid;
    private String satuanUid;
    private String keterangan;
    private double qty;
    private double konversi;
    private double qtyPrimer;
    private String kasBankUid;
    private double jumlah;

    //bayangan
    private String barang;
    private String satuan;

    public PengeluaranLainDetModel(int seq, String uid, String masterUid, String barangUid, String satuanUid, String keterangan, double qty, double konversi, double qtyPrimer, String kasBankUid, double jumlah) {
        this.seq = seq;
        this.uid = uid;
        this.masterUid = masterUid;
        this.barangUid = barangUid;
        this.satuanUid = satuanUid;
        this.keterangan = keterangan;
        this.qty = qty;
        this.konversi = konversi;
        this.qtyPrimer = qtyPrimer;
        this.kasBankUid = kasBankUid;
        this.jumlah = jumlah;
    }

    public PengeluaranLainDetModel() {
        this.seq = 0;
        this.uid = "";
        this.masterUid = "";
        this.barangUid = "";
        this.satuanUid = "";
        this.keterangan = "";
        this.qty = 0;
        this.konversi = 0;
        this.qtyPrimer = 0;
        this.kasBankUid = "";
        this.jumlah = 0;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMasterUid() {
        return masterUid;
    }

    public void setMasterUid(String masterUid) {
        this.masterUid = masterUid;
    }

    public String getBarangUid() {
        return barangUid;
    }

    public void setBarangUid(String barangUid) {
        this.barangUid = barangUid;
    }

    public String getSatuanUid() {
        return satuanUid;
    }

    public void setSatuanUid(String satuanUid) {
        this.satuanUid = satuanUid;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getKonversi() {
        return konversi;
    }

    public void setKonversi(double konversi) {
        this.konversi = konversi;
    }

    public double getQtyPrimer() {
        return qtyPrimer;
    }

    public void setQtyPrimer(double qtyPrimer) {
        this.qtyPrimer = qtyPrimer;
    }

    public String getKasBankUid() {
        return kasBankUid;
    }

    public void setKasBankUid(String kasBankUid) {
        this.kasBankUid = kasBankUid;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getBarang() {
        return barang;
    }

    public void setBarang(String barang) {
        this.barang = barang;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
}
