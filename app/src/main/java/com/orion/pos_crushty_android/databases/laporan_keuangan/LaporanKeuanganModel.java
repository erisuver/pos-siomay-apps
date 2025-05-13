package com.orion.pos_crushty_android.databases.laporan_keuangan;

public class LaporanKeuanganModel {
    private String date;
    private String uid;
    private String tipeBayar;
    private double total;

    public LaporanKeuanganModel(String date, String uid, String tipeBayar, double total) {
        this.date = date;
        this.uid = uid;
        this.tipeBayar = tipeBayar;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTipeBayar() {
        return tipeBayar;
    }

    public void setTipeBayar(String tipeBayar) {
        this.tipeBayar = tipeBayar;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
