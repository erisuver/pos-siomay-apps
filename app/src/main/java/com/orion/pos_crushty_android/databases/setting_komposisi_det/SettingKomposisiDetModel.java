package com.orion.pos_crushty_android.databases.setting_komposisi_det;

public class SettingKomposisiDetModel {
    private String uid;
    private int seq;
    private String masterUid;
    private String barangUid;
    private String satuanUid;
    private String tipe;
    private double qty;
    private double konversi;
    private double qtyPrimer;
    private int versi;
    private long lastUpdate;

    // Constructor, getters, and setters
    public SettingKomposisiDetModel(String uid, int seq, String masterUid, String barangUid, String satuanUid,
                                    String tipe, double qty, double konversi, double qtyPrimer, int versi,
                                    long lastUpdate) {
        this.uid = uid;
        this.seq = seq;
        this.masterUid = masterUid;
        this.barangUid = barangUid;
        this.satuanUid = satuanUid;
        this.tipe = tipe;
        this.qty = qty;
        this.konversi = konversi;
        this.qtyPrimer = qtyPrimer;
        this.versi = versi;
        this.lastUpdate = lastUpdate;
    }

    // Getters and Setters...

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

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
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

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

