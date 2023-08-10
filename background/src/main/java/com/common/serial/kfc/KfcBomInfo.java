package com.common.serial.kfc;

/**
 * FileName: KfcBomInfo
 * Author: hua
 * Date: 2022/1/8 9:18
 * Description:
 */
public class KfcBomInfo {
    String leng;
    String cmd;
    String id;
    String sn;
    String state;
    String license_meat;
    String license_plain;
    String revState;
    String insertState;
    String state1;
    String state2;
    String state3;
    String state4;
    String zhalu1;
    String zhalu2;
    String zhalu3;
    String zhalu4;
    String errcode;
    String ignoreErrcode;

    public KfcBomInfo() {

    }
    public String getLeng() {
        return leng;
    }

    public void setLeng(String leng) {
        this.leng = leng;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLicense_meat() {
        return license_meat;
    }

    public void setLicense_meat(String license_meat) {
        this.license_meat = license_meat;
    }

    public String getLicense_plain() {
        return license_plain;
    }

    public void setLicense_plain(String license_plain) {
        this.license_plain = license_plain;
    }

    public String getRevState() {
        return revState;
    }

    public void setRevState(String revState) {
        this.revState = revState;
    }

    public String getInsertState() {
        return insertState;
    }

    public void setInsertState(String insertState) {
        this.insertState = insertState;
    }

    public String getState1() {
        return state1;
    }

    public void setState1(String state1) {
        this.state1 = state1;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public String getState3() {
        return state3;
    }

    public void setState3(String state3) {
        this.state3 = state3;
    }

    public String getState4() {
        return state4;
    }

    public void setState4(String state4) {
        this.state4 = state4;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getIgnoreErrcode() {
        return ignoreErrcode;
    }

    public String getZhalu1() {
        return zhalu1;
    }

    public void setZhalu1(String zhalu1) {
        this.zhalu1 = zhalu1;
    }

    public String getZhalu2() {
        return zhalu2;
    }

    public void setZhalu2(String zhalu2) {
        this.zhalu2 = zhalu2;
    }

    public String getZhalu3() {
        return zhalu3;
    }

    public void setZhalu3(String zhalu3) {
        this.zhalu3 = zhalu3;
    }

    public String getZhalu4() {
        return zhalu4;
    }

    public void setZhalu4(String zhalu4) {
        this.zhalu4 = zhalu4;
    }

    public void setIgnoreErrcode(String ignoreErrcode) {
        this.ignoreErrcode = ignoreErrcode;
    }

    @Override
    public String toString() {
        return
                "cmd='" + cmd +
                        ", state='" + state +
                        ", meat='" + license_meat +
                        ", plain='" + license_plain +
                        ", revState='" + revState +
//                        ", insertState='" + insertState +
                        ", state1='" + state1 +
                        ", state2='" + state2 +
                        ", state3='" + state3 +
                        ", state4='" + state4 +
                        ", zhalu1='" + zhalu1 +
                        ", zhalu2='" + zhalu2 +
                        ", zhalu3='" + zhalu3 +
                        ", zhalu4='" + zhalu4 +
                        ", errcode='" + errcode +
                        ", ignore='" + ignoreErrcode;
    }
}
