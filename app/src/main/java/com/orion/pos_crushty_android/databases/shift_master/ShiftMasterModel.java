package com.orion.pos_crushty_android.databases.shift_master;

public class ShiftMasterModel {
    private String uid;
    private String tanggal;
    private String outletUid;
    private String karyawanUid;
    private String shiftMulai;
    private String shiftAkhir;
    private double saldoAwal;
    private double tunaiAktual;
    private double nonTunaiAktual;
    private double pengeluaranLain;
    private double setorTunai;
    private double sisaKasTunai;
    private double programTunai;
    private double selisih;
    private String keterangan;
    private String userId;
    private String tglInput;
    //bayangan
    private String namaKasir;
    private double tunaiSistem;
    private double nonTunaiSistem;
    private double pengeluaranLainSistem;
    private double ovoSistem;

    // Constructor


    public ShiftMasterModel(String uid, String tanggal, String outletUid, String karyawanUid,
                            String shiftMulai, String shiftAkhir, double saldoAwal, double tunaiAktual,
                            double nonTunaiAktual, double pengeluaranLain, double setorTunai,
                            double sisaKasTunai, double programTunai, double selisih, String keterangan,
                            String userId, String tglInput) {
        this.uid = uid;
        this.tanggal = tanggal;
        this.outletUid = outletUid;
        this.karyawanUid = karyawanUid;
        this.shiftMulai = shiftMulai;
        this.shiftAkhir = shiftAkhir;
        this.saldoAwal = saldoAwal;
        this.tunaiAktual = tunaiAktual;
        this.nonTunaiAktual = nonTunaiAktual;
        this.pengeluaranLain = pengeluaranLain;
        this.setorTunai = setorTunai;
        this.sisaKasTunai = sisaKasTunai;
        this.programTunai = programTunai;
        this.selisih = selisih;
        this.keterangan = keterangan;
        this.userId = userId;
        this.tglInput = tglInput;
    }

    public ShiftMasterModel() {
        this.uid = "";
        this.tanggal = "";
        this.outletUid = "";
        this.karyawanUid = "";
        this.shiftMulai = "";
        this.shiftAkhir = "";
        this.saldoAwal = 0;
        this.tunaiAktual = 0;
        this.nonTunaiAktual = 0;
        this.pengeluaranLain = 0;
        this.setorTunai = 0;
        this.sisaKasTunai = 0;
        this.programTunai = 0;
        this.selisih = 0;
        this.keterangan = "";
        this.userId = "";
        this.tglInput = "";
    }

    // Getter methods

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
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

    public String getShiftMulai() {
        return shiftMulai;
    }

    public void setShiftMulai(String shiftMulai) {
        this.shiftMulai = shiftMulai;
    }

    public String getShiftAkhir() {
        return shiftAkhir;
    }

    public void setShiftAkhir(String shiftAkhir) {
        this.shiftAkhir = shiftAkhir;
    }

    public double getSaldoAwal() {
        return saldoAwal;
    }

    public void setSaldoAwal(double saldoAwal) {
        this.saldoAwal = saldoAwal;
    }

    public double getTunaiAktual() {
        return tunaiAktual;
    }

    public void setTunaiAktual(double tunaiAktual) {
        this.tunaiAktual = tunaiAktual;
    }

    public double getNonTunaiAktual() {
        return nonTunaiAktual;
    }

    public void setNonTunaiAktual(double nonTunaiAktual) {
        this.nonTunaiAktual = nonTunaiAktual;
    }

    public double getPengeluaranLain() {
        return pengeluaranLain;
    }

    public void setPengeluaranLain(double pengeluaranLain) {
        this.pengeluaranLain = pengeluaranLain;
    }

    public double getSetorTunai() {
        return setorTunai;
    }

    public void setSetorTunai(double setorTunai) {
        this.setorTunai = setorTunai;
    }

    public double getSisaKasTunai() {
        return sisaKasTunai;
    }

    public void setSisaKasTunai(double sisaKasTunai) {
        this.sisaKasTunai = sisaKasTunai;
    }

    public double getProgramTunai() {
        return programTunai;
    }

    public void setProgramTunai(double programTunai) {
        this.programTunai = programTunai;
    }

    public double getSelisih() {
        return selisih;
    }

    public void setSelisih(double selisih) {
        this.selisih = selisih;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
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

    public String getNamaKasir() {
        return namaKasir;
    }

    public void setNamaKasir(String namaKasir) {
        this.namaKasir = namaKasir;
    }

    public double getTunaiSistem() {
        return tunaiSistem;
    }

    public void setTunaiSistem(double tunaiSistem) {
        this.tunaiSistem = tunaiSistem;
    }

    public double getNonTunaiSistem() {
        return nonTunaiSistem;
    }

    public void setNonTunaiSistem(double nonTunaiSistem) {
        this.nonTunaiSistem = nonTunaiSistem;
    }

    public double getPengeluaranLainSistem() {
        return pengeluaranLainSistem;
    }

    public void setPengeluaranLainSistem(double pengeluaranLainSistem) {
        this.pengeluaranLainSistem = pengeluaranLainSistem;
    }

    public double getOvoSistem() {
        return ovoSistem;
    }

    public void setOvoSistem(double ovoSistem) {
        this.ovoSistem = ovoSistem;
    }
}

