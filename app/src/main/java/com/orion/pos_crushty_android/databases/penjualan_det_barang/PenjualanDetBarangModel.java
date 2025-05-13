package com.orion.pos_crushty_android.databases.penjualan_det_barang;

public class PenjualanDetBarangModel {
    private String uid;
    private int seq;
    private long lastUpdate;
    private String masterUid;
    private String detailUid;
    private String barangJualUid;
    private String barangUid;
    private String satuanUid;
    private String tipe;
    private double qtyBarangJual;
    private double qtyBarang;
    private double qtyTotal;
    private double qtyBarangPrimer;
    private double qtyTotalPrimer;
    private double konversi;
    private int versi;

    public PenjualanDetBarangModel(String uid, int seq, long lastUpdate, String masterUid, String detailUid,
                                   String barangJualUid, String barangUid, String satuanUid, String tipe, double qtyBarangJual,
                                   double qtyBarang, double qtyTotal, double qtyBarangPrimer, double qtyTotalPrimer, double konversi, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.masterUid = masterUid;
        this.detailUid = detailUid;
        this.barangJualUid = barangJualUid;
        this.barangUid = barangUid;
        this.satuanUid = satuanUid;
        this.tipe = tipe;
        this.qtyBarangJual = qtyBarangJual;
        this.qtyBarang = qtyBarang;
        this.qtyTotal = qtyTotal;
        this.qtyBarangPrimer = qtyBarangPrimer;
        this.qtyTotalPrimer = qtyTotalPrimer;
        this.konversi = konversi;
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

    public String getDetailUid() {
        return detailUid;
    }

    public void setDetailUid(String detailUid) {
        this.detailUid = detailUid;
    }

    public String getBarangJualUid() {
        return barangJualUid;
    }

    public void setBarangJualUid(String barangJualUid) {
        this.barangJualUid = barangJualUid;
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

    public double getQtyBarangJual() {
        return qtyBarangJual;
    }

    public void setQtyBarangJual(double qtyBarangJual) {
        this.qtyBarangJual = qtyBarangJual;
    }

    public double getQtyBarang() {
        return qtyBarang;
    }

    public void setQtyBarang(double qtyBarang) {
        this.qtyBarang = qtyBarang;
    }

    public double getQtyTotal() {
        return qtyTotal;
    }

    public void setQtyTotal(double qtyTotal) {
        this.qtyTotal = qtyTotal;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }

    public double getQtyBarangPrimer() {
        return qtyBarangPrimer;
    }

    public void setQtyBarangPrimer(double qtyBarangPrimer) {
        this.qtyBarangPrimer = qtyBarangPrimer;
    }

    public double getQtyTotalPrimer() {
        return qtyTotalPrimer;
    }

    public void setQtyTotalPrimer(double qtyTotalPrimer) {
        this.qtyTotalPrimer = qtyTotalPrimer;
    }

    public double getKonversi() {
        return konversi;
    }

    public void setKonversi(double konversi) {
        this.konversi = konversi;
    }
}

