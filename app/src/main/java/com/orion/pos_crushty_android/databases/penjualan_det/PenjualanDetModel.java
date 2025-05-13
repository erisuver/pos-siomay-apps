package com.orion.pos_crushty_android.databases.penjualan_det;

public class PenjualanDetModel {
    private String uid;
    private int seq;
    private long lastUpdate;
    private String masterUid;
    private String barangJualUid;
    private double qty;
    private double harga;
    private double total;
    private String keterangan;
    private int versi;
    //field bayangan
    private String namaBarang;

    public PenjualanDetModel(String uid, int seq, long lastUpdate, String masterUid, String barangJualUid,
                             double qty, double harga, double total, String keterangan, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.masterUid = masterUid;
        this.barangJualUid = barangJualUid;
        this.qty = qty;
        this.harga = harga;
        this.total = total;
        this.keterangan = keterangan;
        this.versi = versi;
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

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMasterUid() {
        return masterUid;
    }

    public void setMasterUid(String masterUid) {
        this.masterUid = masterUid;
    }

    public String getBarangJualUid() {
        return barangJualUid;
    }

    public void setBarangJualUid(String barangJualUid) {
        this.barangJualUid = barangJualUid;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }
}
