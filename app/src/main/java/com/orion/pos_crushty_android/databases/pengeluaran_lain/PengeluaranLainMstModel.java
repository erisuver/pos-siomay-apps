package com.orion.pos_crushty_android.databases.pengeluaran_lain;

import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangModel;

import java.util.List;

public class PengeluaranLainMstModel {
    private int seq ;
    private String uid;
    private long tanggal;
    private String nomor;
    private String tipe;
    private String outletUid;
    private String karyawanUid;
    private String keterangan;
    private String userId;
    private long tglInput;
    private long lastUpdate;
    private int versi;
    private List<PengeluaranLainDetModel> detIds;
    private List<PenambahPengurangBarangModel> ppIds;

    public PengeluaranLainMstModel(int seq, String uid, long tanggal, String nomor, String tipe, String outletUid, String karyawanUid, String keterangan, String userId, long tglInput, long lastUpdate, int versi) {
        this.seq = seq;
        this.uid = uid;
        this.tanggal = tanggal;
        this.nomor = nomor;
        this.tipe = tipe;
        this.outletUid = outletUid;
        this.karyawanUid = karyawanUid;
        this.keterangan = keterangan;
        this.userId = userId;
        this.tglInput = tglInput;
        this.lastUpdate = lastUpdate;
        this.versi = versi;
    }

    public PengeluaranLainMstModel() {
        this.seq = 0;
        this.uid = "";
        this.tanggal = 0;
        this.nomor = "";
        this.tipe = "";
        this.outletUid = "";
        this.karyawanUid = "";
        this.keterangan = "";
        this.userId = "";
        this.tglInput = 0;
        this.lastUpdate = 0;
        this.versi = 0;
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

    public long getTanggal() {
        return tanggal;
    }

    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getOutletUid() {
        return outletUid;
    }

    public void setOutletUid(String outletUid) {
        this.outletUid = outletUid;
    }

    public String getKaryawanUid() {
        return karyawanUid;
    }

    public void setKaryawanUid(String karyawanUid) {
        this.karyawanUid = karyawanUid;
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

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }

    public List<PengeluaranLainDetModel> getDetIds() {
        return detIds;
    }

    public void setDetIds(List<PengeluaranLainDetModel> detIds) {
        this.detIds = detIds;
    }

    public List<PenambahPengurangBarangModel> getPpIds() {
        return ppIds;
    }

    public void setPpIds(List<PenambahPengurangBarangModel> ppIds) {
        this.ppIds = ppIds;
    }
}
