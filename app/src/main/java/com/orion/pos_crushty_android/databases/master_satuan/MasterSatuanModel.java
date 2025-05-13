package com.orion.pos_crushty_android.databases.master_satuan;

public class MasterSatuanModel {
    private String uid;
    private int seq;
    private long lastUpdate;
    private long disableDate;
    private String userId;
    private long tglInput;
    private String kode;
    private String nama;
    private String keterangan;
    private int versi;

    public MasterSatuanModel(String uid, int seq, long lastUpdate, long disableDate,
                             String userId, long tglInput, String kode, String nama, String keterangan, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.disableDate = disableDate;
        this.userId = userId;
        this.tglInput = tglInput;
        this.kode = kode;
        this.nama = nama;
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

    public long getDisableDate() {
        return disableDate;
    }

    public void setDisableDate(long disableDate) {
        this.disableDate = disableDate;
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

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

}
