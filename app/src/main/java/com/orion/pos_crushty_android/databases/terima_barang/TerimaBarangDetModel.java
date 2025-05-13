package com.orion.pos_crushty_android.databases.terima_barang;

public class TerimaBarangDetModel {
    private String uid;
    private int seq;
    private String masterUid;
    private String barangUid;
    private String satuanUid;
    private String keterangan;
    private double qty;
    private double konversi;
    private double qtyPrimer;
    private String kodeBarang;
    private String namaBarang;
    private String namaSatuan;

    public TerimaBarangDetModel() {
        this.uid = "";
        this.seq = 0;
        this.masterUid = "";
        this.barangUid = "";
        this.satuanUid = "";
        this.keterangan = "";
        this.qty = 0;
        this.konversi = 0;
        this.qtyPrimer = 0;
        this.kodeBarang = "";
        this.namaBarang = "";
        this.namaSatuan = "";
    }

    public TerimaBarangDetModel(String uid, int seq, String masterUid, String barangUid, String satuanUid, String keterangan, double qty, double konversi, double qtyPrimer, String kodeBarang, String namaBarang, String namaSatuan) {
        this.uid = uid;
        this.seq = seq;
        this.masterUid = masterUid;
        this.barangUid = barangUid;
        this.satuanUid = satuanUid;
        this.keterangan = keterangan;
        this.qty = qty;
        this.konversi = konversi;
        this.qtyPrimer = qtyPrimer;
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.namaSatuan = namaSatuan;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getNamaSatuan() {
        return namaSatuan;
    }

    public void setNamaSatuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }
}