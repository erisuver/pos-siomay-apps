package com.orion.pos_crushty_android.databases.penjualan_mst;

import com.orion.pos_crushty_android.databases.detail_barang_jual.DetailBarangJualModel;
import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangModel;
import com.orion.pos_crushty_android.databases.penjualan_bayar.PenjualanBayarModel;
import com.orion.pos_crushty_android.databases.penjualan_det.PenjualanDetModel;
import com.orion.pos_crushty_android.databases.penjualan_det_barang.PenjualanDetBarangModel;

import java.util.List;

public class PenjualanMstModel {
    private String uid;
    private int seq;
    private long lastUpdate;
    private long tglInput;
    private String userId;
    private String userEdit;
    private long tglEdit;
    private long tanggal;
    private String nomor;
    private String outletUid;
    private String karyawanUid;
    private String keterangan;
    private double total;
    private double bayar;
    private double kembali;
    private int versi;
    private String is_kirim;
    //bayangan
    private String tipeBayar;
    private String namaOutlet;
    private String namaKaryawan;
    private PenjualanBayarModel bayarIds;
    private List<PenjualanDetModel> detIds;
    private List<PenjualanDetBarangModel> detBarangIds;
    private List<PenambahPengurangBarangModel> detPenambahPengurangIds;

    public PenjualanMstModel(String uid, int seq, long lastUpdate, long tglInput, String userId,
                             String userEdit, long tglEdit, long tanggal, String nomor, String outletUid, String karyawanUid,
                             String keterangan, double total, double bayar, double kembali, int versi) {
        this.uid = uid;
        this.seq = seq;
        this.lastUpdate = lastUpdate;
        this.tglInput = tglInput;
        this.userId = userId;
        this.userEdit = userEdit;
        this.tglEdit = tglEdit;
        this.tanggal = tanggal;
        this.nomor = nomor;
        this.outletUid = outletUid;
        this.karyawanUid = karyawanUid;
        this.keterangan = keterangan;
        this.total = total;
        this.bayar = bayar;
        this.kembali = kembali;
        this.versi = versi;
    }

    public PenjualanMstModel() {
        this.uid = "";
        this.seq = 0;
        this.lastUpdate = 0;
        this.tglInput = 0;
        this.userId = "";
        this.userEdit = "";
        this.tglEdit = 0;
        this.tanggal = 0;
        this.nomor = "";
        this.outletUid = "";
        this.karyawanUid = "";
        this.keterangan = "";
        this.total = 0;
        this.bayar = 0;
        this.kembali = 0;
        this.versi = 0;
        this.is_kirim = "";
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

    public long getTglInput() {
        return tglInput;
    }

    public void setTglInput(long tglInput) {
        this.tglInput = tglInput;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEdit() {
        return userEdit;
    }

    public void setUserEdit(String userEdit) {
        this.userEdit = userEdit;
    }

    public long getTglEdit() {
        return tglEdit;
    }

    public void setTglEdit(long tglEdit) {
        this.tglEdit = tglEdit;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getBayar() {
        return bayar;
    }

    public void setBayar(double bayar) {
        this.bayar = bayar;
    }

    public double getKembali() {
        return kembali;
    }

    public void setKembali(double kembali) {
        this.kembali = kembali;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }

    public String getIs_kirim() {
        return is_kirim;
    }

    public void setIs_kirim(String is_kirim) {
        this.is_kirim = is_kirim;
    }

    public PenjualanBayarModel getBayarIds() {
        return bayarIds;
    }

    public void setBayarIds(PenjualanBayarModel bayarIds) {
        this.bayarIds = bayarIds;
    }

    public List<PenjualanDetModel> getDetIds() {
        return detIds;
    }

    public void setDetIds(List<PenjualanDetModel> detIds) {
        this.detIds = detIds;
    }

    public List<PenjualanDetBarangModel> getDetBarangIds() {
        return detBarangIds;
    }

    public void setDetBarangIds(List<PenjualanDetBarangModel> detBarangIds) {
        this.detBarangIds = detBarangIds;
    }

    public List<PenambahPengurangBarangModel> getDetPenambahPengurangIds() {
        return detPenambahPengurangIds;
    }

    public void setDetPenambahPengurangIds(List<PenambahPengurangBarangModel> detPenambahPengurangIds) {
        this.detPenambahPengurangIds = detPenambahPengurangIds;
    }

    public String getTipeBayar() {
        return tipeBayar;
    }

    public void setTipeBayar(String tipeBayar) {
        this.tipeBayar = tipeBayar;
    }

    public String getNamaOutlet() {
        return namaOutlet;
    }

    public void setNamaOutlet(String namaOutlet) {
        this.namaOutlet = namaOutlet;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }
}
