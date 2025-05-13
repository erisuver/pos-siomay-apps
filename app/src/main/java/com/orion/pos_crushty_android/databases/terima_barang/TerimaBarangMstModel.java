package com.orion.pos_crushty_android.databases.terima_barang;

public class TerimaBarangMstModel {
    private String uid;
    private Integer seq;
    private String tanggal;
    private String nomor;
    private String kirim_uid;
    private String outlet_asal_uid;
    private String outlet_tujuan_uid;
    private String karyawan_uid;
    private String tanggal_kirim;
    private String nomor_kirim;
    private String nama_outlet_asal;
    private String nama_outlet_tujuan;
    private String nama_karyawan;
    private String keterangan;
    private String user_id;
    private String tgl_input;
    private String user_edit;
    private String tgl_edit;

    public TerimaBarangMstModel(String uid, Integer seq, String tanggal, String nomor, String kirim_uid, String outlet_asal_uid, String outlet_tujuan_uid, String karyawan_uid, String tanggal_kirim, String nomor_kirim, String nama_outlet_asal, String nama_outlet_tujuan, String nama_karyawan, String keterangan, String user_id, String tgl_input, String user_edit, String tgl_edit) {
        this.uid = uid;
        this.seq = seq;
        this.tanggal = tanggal;
        this.nomor = nomor;
        this.kirim_uid = kirim_uid;
        this.outlet_asal_uid = outlet_asal_uid;
        this.outlet_tujuan_uid = outlet_tujuan_uid;
        this.karyawan_uid = karyawan_uid;
        this.tanggal_kirim = tanggal_kirim;
        this.nomor_kirim = nomor_kirim;
        this.nama_outlet_asal = nama_outlet_asal;
        this.nama_outlet_tujuan = nama_outlet_tujuan;
        this.nama_karyawan = nama_karyawan;
        this.keterangan = keterangan;
        this.user_id = user_id;
        this.tgl_input = tgl_input;
        this.user_edit = user_edit;
        this.tgl_edit = tgl_edit;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
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

    public String getKirim_uid() {
        return kirim_uid;
    }

    public void setKirim_uid(String kirim_uid) {
        this.kirim_uid = kirim_uid;
    }

    public String getOutlet_asal_uid() {
        return outlet_asal_uid;
    }

    public void setOutlet_asal_uid(String outlet_asal_uid) {
        this.outlet_asal_uid = outlet_asal_uid;
    }

    public String getOutlet_tujuan_uid() {
        return outlet_tujuan_uid;
    }

    public void setOutlet_tujuan_uid(String outlet_tujuan_uid) {
        this.outlet_tujuan_uid = outlet_tujuan_uid;
    }

    public String getKaryawan_uid() {
        return karyawan_uid;
    }

    public void setKaryawan_uid(String karyawan_uid) {
        this.karyawan_uid = karyawan_uid;
    }

    public String getTanggal_kirim() {
        return tanggal_kirim;
    }

    public void setTanggal_kirim(String tanggal_kirim) {
        this.tanggal_kirim = tanggal_kirim;
    }

    public String getNomor_kirim() {
        return nomor_kirim;
    }

    public void setNomor_kirim(String nomor_kirim) {
        this.nomor_kirim = nomor_kirim;
    }

    public String getNama_outlet_asal() {
        return nama_outlet_asal;
    }

    public void setNama_outlet_asal(String nama_outlet_asal) {
        this.nama_outlet_asal = nama_outlet_asal;
    }

    public String getNama_outlet_tujuan() {
        return nama_outlet_tujuan;
    }

    public void setNama_outlet_tujuan(String nama_outlet_tujuan) {
        this.nama_outlet_tujuan = nama_outlet_tujuan;
    }

    public String getNama_karyawan() {
        return nama_karyawan;
    }

    public void setNama_karyawan(String nama_karyawan) {
        this.nama_karyawan = nama_karyawan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTgl_input() {
        return tgl_input;
    }

    public void setTgl_input(String tgl_input) {
        this.tgl_input = tgl_input;
    }

    public String getUser_edit() {
        return user_edit;
    }

    public void setUser_edit(String user_edit) {
        this.user_edit = user_edit;
    }

    public String getTgl_edit() {
        return tgl_edit;
    }

    public void setTgl_edit(String tgl_edit) {
        this.tgl_edit = tgl_edit;
    }
}
