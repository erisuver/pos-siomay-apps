package com.orion.pos_crushty_android.databases.system_user;

public class SystemUserModel {
    private String userId;
    private String passwd;
    private long lastUpdate;
    private long disableDate;
    private String outletInputUid;
    private String userInput;
    private long tglInput;
    private String karyawanUid;
    private String outletUid;
    private String userName;
    private int accessLevel;
    private String isApps;
    private String isDesktop;
    private int versi;

    public SystemUserModel(String userId, String passwd, long lastUpdate, long disableDate, String outletInputUid, String userInput, long tglInput, String karyawanUid, String outletUid, String userName, int accessLevel, String isApps, String isDesktop, int versi) {
        this.userId = userId;
        this.passwd = passwd;
        this.lastUpdate = lastUpdate;
        this.disableDate = disableDate;
        this.outletInputUid = outletInputUid;
        this.userInput = userInput;
        this.tglInput = tglInput;
        this.karyawanUid = karyawanUid;
        this.outletUid = outletUid;
        this.userName = userName;
        this.accessLevel = accessLevel;
        this.isApps = isApps;
        this.isDesktop = isDesktop;
        this.versi = versi;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
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

    public String getOutletInputUid() {
        return outletInputUid;
    }

    public void setOutletInputUid(String outletInputUid) {
        this.outletInputUid = outletInputUid;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public long getTglInput() {
        return tglInput;
    }

    public void setTglInput(long tglInput) {
        this.tglInput = tglInput;
    }

    public String getKaryawanUid() {
        return karyawanUid;
    }

    public void setKaryawanUid(String karyawanUid) {
        this.karyawanUid = karyawanUid;
    }

    public String getOutletUid() {
        return outletUid;
    }

    public void setOutletUid(String outletUid) {
        this.outletUid = outletUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getIsApps() {
        return isApps;
    }

    public void setIsApps(String isApps) {
        this.isApps = isApps;
    }

    public String getIsDesktop() {
        return isDesktop;
    }

    public void setIsDesktop(String isDesktop) {
        this.isDesktop = isDesktop;
    }

    public int getVersi() {
        return versi;
    }

    public void setVersi(int versi) {
        this.versi = versi;
    }
}
