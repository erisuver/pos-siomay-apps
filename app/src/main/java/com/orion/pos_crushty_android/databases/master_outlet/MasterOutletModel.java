package com.orion.pos_crushty_android.databases.master_outlet;

public class MasterOutletModel {
    private String uid;
    private int seq;
    private long lastUpdate;
    private long disableDate;
    private String userId;
    private long tglInput;
    private String kode;
    private String nama;
    private String alamat;
    private String penanggungJawab;
    private String kota;
    private String keterangan;
    private String telepon;
    private String bankUid;
    private String kasUid;
    private int versi;

    public MasterOutletModel(String uid, int seq, long lastUpdate, long disableDate, String userId, long tglInput, String kode, String nama, String alamat, String penanggungJawab, String kota, String keterangan, String telepon, String bankUid, String kasUid, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.disableDate = disableDate;
        this.userId = userId;
        this.tglInput = tglInput;
        this.kode = kode;
        this.nama = nama;
        this.alamat = alamat;
        this.penanggungJawab = penanggungJawab;
        this.kota = kota;
        this.keterangan = keterangan;
        this.telepon = telepon;
        this.bankUid = bankUid;
        this.kasUid = kasUid;
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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPenanggungJawab() {
        return penanggungJawab;
    }

    public void setPenanggungJawab(String penanggungJawab) {
        this.penanggungJawab = penanggungJawab;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getBankUid() {
        return bankUid;
    }

    public void setBankUid(String bankUid) {
        this.bankUid = bankUid;
    }

    public String getKasUid() {
        return kasUid;
    }

    public void setKasUid(String kasUid) {
        this.kasUid = kasUid;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }
}