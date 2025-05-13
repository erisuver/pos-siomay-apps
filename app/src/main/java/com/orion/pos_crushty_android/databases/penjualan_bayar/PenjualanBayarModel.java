package com.orion.pos_crushty_android.databases.penjualan_bayar;

public class PenjualanBayarModel {
    private String uid;
    private int seq;
    private long lastUpdate; // Tanggal dalam bentuk long (UNIX timestamp)
    private String masterUid;
    private String kasBankUid;
    private String tipeBayar;
    private String gambar;
    private double total;
    private double bayar;
    private double kembali;
    private int versi;

    // Constructor
    public PenjualanBayarModel(String uid, int seq, long lastUpdate, String masterUid, String kasBankUid, String tipeBayar, String gambar, double total, double bayar, double kembali, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.masterUid = masterUid;
        this.kasBankUid = kasBankUid;
        this.tipeBayar = tipeBayar;
        this.gambar = gambar;
        this.total = total;
        this.bayar = bayar;
        this.kembali = kembali;
        this.versi = versi;
    }

    public PenjualanBayarModel() {
        this.uid = "";
        this.seq = 0;
        this.lastUpdate = 0;
        this.masterUid = "";
        this.kasBankUid = "";
        this.tipeBayar = "";
        this.gambar = "";
        this.total = 0;
        this.bayar = 0;
        this.kembali = 0;
        this.versi = 0;
    }

    // Getters and Setters
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

    public String getKasBankUid() {
        return kasBankUid;
    }

    public void setKasBankUid(String kasBankUid) {
        this.kasBankUid = kasBankUid;
    }

    public String getTipeBayar() {
        return tipeBayar;
    }

    public void setTipeBayar(String tipeBayar) {
        this.tipeBayar = tipeBayar;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getBayar() {
        return bayar;
    }

    public void setBayar(double bayar) {
        this.bayar = bayar;
    }

    public double getKembali() {
        return kembali;
    }

    public void setKembali(double kembali) {
        this.kembali = kembali;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }
}

