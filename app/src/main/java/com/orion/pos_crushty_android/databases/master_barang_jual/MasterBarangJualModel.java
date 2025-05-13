package com.orion.pos_crushty_android.databases.master_barang_jual;


import android.os.Parcel;
import android.os.Parcelable;

import com.orion.pos_crushty_android.databases.detail_barang_jual.DetailBarangJualModel;
import com.orion.pos_crushty_android.databases.setting_komposisi_det.SettingKomposisiDetModel;

import java.util.ArrayList;
import java.util.List;

public class MasterBarangJualModel implements Parcelable {
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
    private String gambar;
    private int versi;
    private int kategori;
    //bayangan
    private double komposisiHarga;
    private List<DetailBarangJualModel> detailIds;
    private List<SettingKomposisiDetModel> komposisiIds;

    public MasterBarangJualModel(String uid, int seq, long lastUpdate, long disableDate, String userId,
                                 long tglInput, String kode, String nama, double harga,
                                 String keterangan, String gambar, int versi, int kategori) {
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
        this.gambar = gambar;
        this.versi = versi;
        this.kategori = kategori;
    }

    protected MasterBarangJualModel(Parcel in) {
        uid = in.readString();
        seq = in.readInt();
        lastUpdate = in.readLong();
        disableDate = in.readLong();
        userId = in.readString();
        tglInput = in.readLong();
        kode = in.readString();
        nama = in.readString();
        harga = in.readDouble();
        keterangan = in.readString();
        gambar = in.readString();
        versi = in.readInt();
        kategori = in.readInt();
        komposisiHarga = in.readDouble();
        detailIds = new ArrayList<>();
        in.readList(detailIds, DetailBarangJualModel.class.getClassLoader());
        komposisiIds = new ArrayList<>();
        in.readList(komposisiIds, SettingKomposisiDetModel.class.getClassLoader());
    }

    public static final Creator<MasterBarangJualModel> CREATOR = new Creator<MasterBarangJualModel>() {
        @Override
        public MasterBarangJualModel createFromParcel(Parcel in) {
            return new MasterBarangJualModel(in);
        }

        @Override
        public MasterBarangJualModel[] newArray(int size) {
            return new MasterBarangJualModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeInt(seq);
        dest.writeLong(lastUpdate);
        dest.writeLong(disableDate);
        dest.writeString(userId);
        dest.writeLong(tglInput);
        dest.writeString(kode);
        dest.writeString(nama);
        dest.writeDouble(harga);
        dest.writeString(keterangan);
        dest.writeString(gambar);
        dest.writeInt(versi);
        dest.writeInt(kategori);
        dest.writeDouble(komposisiHarga);
        dest.writeList(detailIds);
        dest.writeList(komposisiIds);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }

    public List<DetailBarangJualModel> getDetailIds() {
        return detailIds;
    }

    public void setDetailIds(List<DetailBarangJualModel> detailIds) {
        this.detailIds = detailIds;
    }

    public List<SettingKomposisiDetModel> getKomposisiIds() {
        return komposisiIds;
    }

    public void setKomposisiIds(List<SettingKomposisiDetModel> komposisiIds) {
        this.komposisiIds = komposisiIds;
    }

    public double getKomposisiHarga() {
        return komposisiHarga;
    }

    public void setKomposisiHarga(double komposisiHarga) {
        this.komposisiHarga = komposisiHarga;
    }

    public int getKategori() {
        return kategori;
    }

    public void setKategori(int kategori) {
        this.kategori = kategori;
    }
}
