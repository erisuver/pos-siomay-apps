package com.orion.pos_crushty_android.databases.master_barang;

public class MasterBarangModel {
    private String uid;
    private int seq;
    private long lastUpdate;
    private long disableDate;
    private String userId;
    private long tglInput;
    private String kode;
    private String nama;
    private double harga;
    private String keterangan;
    private String satuanPrimerUid;
    private String satuanSekunderUid;
    private double qtyPrimer;
    private double qtySekunder;
    private String isBarangStok;
    private int versi;
    private String namaSatuanPrimer;
    private String isKontrolStok;

    public MasterBarangModel(String uid, int seq, long lastUpdate, long disableDate,
                             String userId, long tglInput, String kode, String nama, double harga,
                             String keterangan, String satuanPrimerUid, String satuanSekunderUid, double qtyPrimer,
                             double qtySekunder, String isBarangStok, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.disableDate = disableDate;
        this.userId = userId;
        this.tglInput = tglInput;
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.keterangan = keterangan;
        this.satuanPrimerUid = satuanPrimerUid;
        this.satuanSekunderUid = satuanSekunderUid;
        this.qtyPrimer = qtyPrimer;
        this.qtySekunder = qtySekunder;
        this.isBarangStok = isBarangStok;
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

    public String getSatuanPrimerUid() {
        return satuanPrimerUid;
    }

    public void setSatuanPrimerUid(String satuanPrimerUid) {
        this.satuanPrimerUid = satuanPrimerUid;
    }

    public String getSatuanSekunderUid() {
        return satuanSekunderUid;
    }

    public void setSatuanSekunderUid(String satuanSekunderUid) {
        this.satuanSekunderUid = satuanSekunderUid;
    }

    public double getQtyPrimer() {
        return qtyPrimer;
    }

    public void setQtyPrimer(double qtyPrimer) {
        this.qtyPrimer = qtyPrimer;
    }

    public double getQtySekunder() {
        return qtySekunder;
    }

    public void setQtySekunder(double qtySekunder) {
        this.qtySekunder = qtySekunder;
    }

    public String getIsBarangStok() {
        return isBarangStok;
    }

    public void setIsBarangStok(String isBarangStok) {
        this.isBarangStok = isBarangStok;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }


    public String getNamaSatuanPrimer() {
        return namaSatuanPrimer;
    }

    public void setNamaSatuanPrimer(String namaSatuanPrimer) {
        this.namaSatuanPrimer = namaSatuanPrimer;
    }

    public String getIsKontrolStok() {
        return isKontrolStok;
    }

    public void setIsKontrolStok(String isKontrolStok) {
        this.isKontrolStok = isKontrolStok;
    }
}