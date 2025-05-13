package com.orion.pos_crushty_android.databases.kirim_barang;

public class KirimBarangDetModel {
    private String uid;
    private int seq;
    private String masterUid;
    private String barangUid;
    private String satuanUid;
    private String keterangan;
    private double qty;
    private double konversi;
    private double qtyPrimer;
    private String kodeBarang;
    private String namaBarang;
    private String satuanPrimerUid;
    private String satuanSekunderUid;
    private String namaSatuan;
    private String namaSatPrimer;
    private String namaSatSekunder;
    private double konversiPrimer;
    private double konversiSekunder;
    private String isBarangStok;

    public KirimBarangDetModel() {
        this.uid = "";
        this.seq = 0;
        this.masterUid = "";
        this.barangUid = "";
        this.satuanUid = "";
        this.keterangan = "";
        this.qty = 0;
        this.konversi = 0;
        this.qtyPrimer = 0;
        this.kodeBarang = "";
        this.namaBarang = "";
        this.satuanPrimerUid = "";
        this.satuanSekunderUid = "";
        this.namaSatuan = "";
        this.namaSatPrimer = "";
        this.namaSatSekunder = "";
        this.konversiPrimer = 0;
        this.konversiSekunder = 0;
        this.isBarangStok = "";
    }

    public KirimBarangDetModel(String uid, int seq, String masterUid, String barangUid, String satuanUid, String keterangan, double qty, double konversi, double qtyPrimer, String kodeBarang, String namaBarang, String satuanPrimerUid, String satuanSekunderUid, String namaSatuan, String namaSatPrimer, String namaSatSekunder, double konversiPrimer, double konversiSekunder, String isBarangStok) {
        this.uid = uid;
        this.seq = seq;
        this.masterUid = masterUid;
        this.barangUid = barangUid;
        this.satuanUid = satuanUid;
        this.keterangan = keterangan;
        this.qty = qty;
        this.konversi = konversi;
        this.qtyPrimer = qtyPrimer;
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.satuanPrimerUid = satuanPrimerUid;
        this.satuanSekunderUid = satuanSekunderUid;
        this.namaSatuan = namaSatuan;
        this.namaSatPrimer = namaSatPrimer;
        this.namaSatSekunder = namaSatSekunder;
        this.konversiPrimer = konversiPrimer;
        this.konversiSekunder = konversiSekunder;
        this.isBarangStok = isBarangStok;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
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

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
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

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
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

    public String getNamaSatuan() {
        return namaSatuan;
    }

    public void setNamaSatuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }

    public String getNamaSatPrimer() {
        return namaSatPrimer;
    }

    public void setNamaSatPrimer(String namaSatPrimer) {
        this.namaSatPrimer = namaSatPrimer;
    }

    public String getNamaSatSekunder() {
        return namaSatSekunder;
    }

    public void setNamaSatSekunder(String namaSatSekunder) {
        this.namaSatSekunder = namaSatSekunder;
    }

    public double getKonversiPrimer() {
        return konversiPrimer;
    }

    public void setKonversiPrimer(double konversiPrimer) {
        this.konversiPrimer = konversiPrimer;
    }

    public double getKonversiSekunder() {
        return konversiSekunder;
    }

    public void setKonversiSekunder(double konversiSekunder) {
        this.konversiSekunder = konversiSekunder;
    }

    public String getIsBarangStok() {
        return isBarangStok;
    }

    public void setIsBarangStok(String isBarangStok) {
        this.isBarangStok = isBarangStok;
    }
}
