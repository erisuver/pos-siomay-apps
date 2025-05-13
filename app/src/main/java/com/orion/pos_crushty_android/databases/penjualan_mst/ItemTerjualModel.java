package com.orion.pos_crushty_android.databases.penjualan_mst;

public class ItemTerjualModel {
    private String uidBarang;
    private String namaBarang;
    private double qty;
    private double harga;
    private double total;

    public ItemTerjualModel(String uidBarang, String namaBarang, double qty, double harga, double total) {
        this.uidBarang = uidBarang;
        this.namaBarang = namaBarang;
        this.qty = qty;
        this.harga = harga;
        this.total = total;
    }

    public String getUidBarang() {
        return uidBarang;
    }

    public void setUidBarang(String uidBarang) {
        this.uidBarang = uidBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
