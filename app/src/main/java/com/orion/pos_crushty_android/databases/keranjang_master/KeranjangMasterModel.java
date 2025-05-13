package com.orion.pos_crushty_android.databases.keranjang_master;

public class KeranjangMasterModel {
    //field tabel keranjang
    private int seq;
    private String barangJualUid;
    private double qty;
    //field bayangan
    private String namaBarang;
    private double harga;
    private String gambar;


    // Constructor
    public KeranjangMasterModel(int seq, String barangJualUid, double qty) {
        this.seq = seq;
        this.barangJualUid = barangJualUid;
        this.qty = qty;
    }

    public KeranjangMasterModel() {
        this.seq = 0;
        this.barangJualUid = "";
        this.qty = 0;
    }

    // Getters and Setters
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getBarangJualUid() {
        return barangJualUid;
    }

    public void setBarangJualUid(String barangJualUid) {
        this.barangJualUid = barangJualUid;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
