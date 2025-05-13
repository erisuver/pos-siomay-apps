package com.orion.pos_crushty_android.databases.stok_opname_mst;

public class StokOpnameMstModel {
    private int seq ;
    private String Nomor;
    private String uid;
    private long tanggal;
    private String keterangan;
    private String OutletUid;

    public StokOpnameMstModel(int seq, String uid, String nomor,  long tanggal, String keterangan, String OutletUid) {
        this.seq = seq;
        this.Nomor = nomor;
        this.tanggal = tanggal;
        this.keterangan = keterangan;
        this.OutletUid = OutletUid;
        this.uid = uid;
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

    public String getNomor() {
        return Nomor;
    }

    public void setNomor(String nomor) {
        Nomor = nomor;
    }

    public long getTanggal() {
        return tanggal;
    }

    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getOutletUid() {
        return OutletUid;
    }

    public void setOutletUid(String outletUid) {
        OutletUid = outletUid;
    }
}
