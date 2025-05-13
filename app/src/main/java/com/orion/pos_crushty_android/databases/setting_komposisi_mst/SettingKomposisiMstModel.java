package com.orion.pos_crushty_android.databases.setting_komposisi_mst;

public class SettingKomposisiMstModel {
    private String uid;
    private int seq;
    private long berlakuDari;
    private long berlakuSampai;
    private String outletUid;
    private String barangJualUid;
    private double harga;
    private String keterangan;
    private String userId;
    private long tglInput;
    private long disableDate;
    private int versi;
    private long lastUpdate;

    // Constructor, getters, and setters
    public SettingKomposisiMstModel(String uid, int seq, long berlakuDari, long berlakuSampai, String outletUid,
                                    String barangJualUid, double harga, String keterangan, String userId,
                                    long tglInput, long disableDate, int versi, long lastUpdate) {
        this.uid = uid;
        this.seq = seq;
        this.berlakuDari = berlakuDari;
        this.berlakuSampai = berlakuSampai;
        this.outletUid = outletUid;
        this.barangJualUid = barangJualUid;
        this.harga = harga;
        this.keterangan = keterangan;
        this.userId = userId;
        this.tglInput = tglInput;
        this.disableDate = disableDate;
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

    public long getBerlakuDari() {
        return berlakuDari;
    }

    public void setBerlakuDari(long berlakuDari) {
        this.berlakuDari = berlakuDari;
    }

    public long getBerlakuSampai() {
        return berlakuSampai;
    }

    public void setBerlakuSampai(long berlakuSampai) {
        this.berlakuSampai = berlakuSampai;
    }

    public String getOutletUid() {
        return outletUid;
    }

    public void setOutletUid(String outletUid) {
        this.outletUid = outletUid;
    }

    public String getBarangJualUid() {
        return barangJualUid;
    }

    public void setBarangJualUid(String barangJualUid) {
        this.barangJualUid = barangJualUid;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTglInput() {
        return tglInput;
    }

    public void setTglInput(long tglInput) {
        this.tglInput = tglInput;
    }

    public long getDisableDate() {
        return disableDate;
    }

    public void setDisableDate(long disableDate) {
        this.disableDate = disableDate;
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

