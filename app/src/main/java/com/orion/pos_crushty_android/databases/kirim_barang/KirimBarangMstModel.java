package com.orion.pos_crushty_android.databases.kirim_barang;

public class KirimBarangMstModel {
    private String uid;
    private int seq;
    private String tanggal;
    private String nomor;
    private String outletAsalUid;
    private String outletTujuanUid;
    private String keterangan;
    private String userAcc;
    private String tglAcc;
    private String userId;
    private String tglInput;
    private String userEdit;
    private String tglEdit;
    private String karyawanUid;
    private String namaOutletAsal;
    private String namaOutletTujuan;
    private String namaPengirim;
    private String kodePengirim;

    public KirimBarangMstModel() {
        this.uid = "";
        this.seq = 0;
        this.tanggal = "";
        this.nomor = "";
        this.outletAsalUid = "";
        this.outletTujuanUid = "";
        this.keterangan = "";
        this.userAcc = "";
        this.tglAcc = "";
        this.userId = "";
        this.tglInput = "";
        this.userEdit = "";
        this.tglEdit = "";
        this.karyawanUid = "";
        this.namaOutletAsal = "";
        this.namaOutletTujuan = "";
        this.namaPengirim = "";
        this.kodePengirim = "";
    }
    public KirimBarangMstModel(String uid, int seq, String tanggal, String nomor, String outletAsalUid, String outletTujuanUid, String keterangan, String userAcc, String tglAcc, String userId, String tglInput, String userEdit, String tglEdit, String karyawanUid, String namaOutletAsal, String namaOutletTujuan, String namaPengirim, String kodePengirim) {
        this.uid = uid;
        this.seq = seq;
        this.tanggal = tanggal;
        this.nomor = nomor;
        this.outletAsalUid = outletAsalUid;
        this.outletTujuanUid = outletTujuanUid;
        this.keterangan = keterangan;
        this.userAcc = userAcc;
        this.tglAcc = tglAcc;
        this.userId = userId;
        this.tglInput = tglInput;
        this.userEdit = userEdit;
        this.tglEdit = tglEdit;
        this.karyawanUid = karyawanUid;
        this.namaOutletAsal = namaOutletAsal;
        this.namaOutletTujuan = namaOutletTujuan;
        this.namaPengirim = namaPengirim;
        this.kodePengirim = kodePengirim;
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getOutletAsalUid() {
        return outletAsalUid;
    }

    public void setOutletAsalUid(String outletAsalUid) {
        this.outletAsalUid = outletAsalUid;
    }

    public String getOutletTujuanUid() {
        return outletTujuanUid;
    }

    public void setOutletTujuanUid(String outletTujuanUid) {
        this.outletTujuanUid = outletTujuanUid;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getUserAcc() {
        return userAcc;
    }

    public void setUserAcc(String userAcc) {
        this.userAcc = userAcc;
    }

    public String getTglAcc() {
        return tglAcc;
    }

    public void setTglAcc(String tglAcc) {
        this.tglAcc = tglAcc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTglInput() {
        return tglInput;
    }

    public void setTglInput(String tglInput) {
        this.tglInput = tglInput;
    }

    public String getUserEdit() {
        return userEdit;
    }

    public void setUserEdit(String userEdit) {
        this.userEdit = userEdit;
    }

    public String getTglEdit() {
        return tglEdit;
    }

    public void setTglEdit(String tglEdit) {
        this.tglEdit = tglEdit;
    }

    public String getKaryawanUid() {
        return karyawanUid;
    }

    public void setKaryawanUid(String karyawanUid) {
        this.karyawanUid = karyawanUid;
    }

    public String getNamaOutletAsal() {
        return namaOutletAsal;
    }

    public void setNamaOutletAsal(String namaOutletAsal) {
        this.namaOutletAsal = namaOutletAsal;
    }

    public String getNamaOutletTujuan() {
        return namaOutletTujuan;
    }

    public void setNamaOutletTujuan(String namaOutletTujuan) {
        this.namaOutletTujuan = namaOutletTujuan;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getKodePengirim() {
        return kodePengirim;
    }

    public void setKodePengirim(String kodePengirim) {
        this.kodePengirim = kodePengirim;
    }
}
