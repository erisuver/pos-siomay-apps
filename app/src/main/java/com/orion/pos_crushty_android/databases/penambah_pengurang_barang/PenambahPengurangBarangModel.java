package com.orion.pos_crushty_android.databases.penambah_pengurang_barang;

public class PenambahPengurangBarangModel {
    private String uid;
    private int seq;
    private long tanggal; // Representasi TDateTime sebagai long
    private String transUid;
    private String tipeTrans;
    private String nomor;
    private String outletUid;
    private String barangUid;
    private double qtyPrimer;
    private int versi;
    private long lastUpdate; // Representasi TDateTime sebagai long
    private String userId;

    // Constructor
    public PenambahPengurangBarangModel(String uid, int seq, long tanggal, String transUid,
                                        String tipeTrans, String nomor, String outletUid, String barangUid,
                                        double qtyPrimer, int versi, long lastUpdate, String userId) {
        this.uid = uid;
        this.seq = seq;
        this.tanggal = tanggal;
        this.transUid = transUid;
        this.tipeTrans = tipeTrans;
        this.nomor = nomor;
        this.outletUid = outletUid;
        this.barangUid = barangUid;
        this.qtyPrimer = qtyPrimer;
        this.versi = versi;
        this.lastUpdate = lastUpdate;
        this.userId = userId;
    }

    public PenambahPengurangBarangModel() {
        this.uid = "";
        this.seq = 0;
        this.tanggal = 0;
        this.transUid = "";
        this.tipeTrans = "";
        this.nomor = "";
        this.outletUid = "";
        this.barangUid = "";
        this.qtyPrimer = 0;
        this.versi = 0;
        this.lastUpdate = 0;
        this.userId = "";
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

    public long getTanggal() {
        return tanggal;
    }

    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    public String getTransUid() {
        return transUid;
    }

    public void setTransUid(String transUid) {
        this.transUid = transUid;
    }

    public String getTipeTrans() {
        return tipeTrans;
    }

    public void setTipeTrans(String tipeTrans) {
        this.tipeTrans = tipeTrans;
    }

    public String getOutletUid() {
        return outletUid;
    }

    public void setOutletUid(String outletUid) {
        this.outletUid = outletUid;
    }

    public String getBarangUid() {
        return barangUid;
    }

    public void setBarangUid(String barangUid) {
        this.barangUid = barangUid;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }
}
