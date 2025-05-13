package com.orion.pos_crushty_android.ui.cetak_struk;

public class CetakStrukModel {
    private String isi;
    private String tipe;

    public CetakStrukModel(String isi, String tipe) {
        this.isi = isi;
        this.tipe = tipe;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
