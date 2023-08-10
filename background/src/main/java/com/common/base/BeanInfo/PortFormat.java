package com.common.base.BeanInfo;

import android.text.TextUtils;

import com.common.base.XssTrands;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: PortFormat
 * Author: hua
 * Date: 2022/1/5 15:42
 * Description:
 */
public class PortFormat {
    String leng;
    String cmd;
    String id;
    String sn;
    int addr;
    String otherData;
    String state;
    String cmdCheck;
    String errcode;
    int LanNum;
    String num;
    String data;
    boolean isSettingErrcode = false;
    StatesDrop80 statesDrop80;
    List<StatesActCmd> list;
    List<Integer> errorList;

    public List<Integer> getErrorList() {
        return errorList;
    }



    public StatesDrop80 getStatesDrop80() {
        return statesDrop80;
    }

    public void setStatesDrop80(StatesDrop80 statesDrop80) {
        this.statesDrop80 = statesDrop80;
    }

    public boolean isSettingErrcode() {
        return isSettingErrcode;
    }

    public void setSettingErrcode(boolean settingErrcode) {
        isSettingErrcode = settingErrcode;
    }

    public String getOtherData() {
        return otherData;
    }

    public void setOtherData(String otherData) {
        this.otherData = otherData;
    }

    public int getLanNum() {
        return LanNum;
    }

    public void setLanNum(int lanNum) {
        LanNum = lanNum;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCmdCheck() {
        return cmdCheck;
    }

    public void setCmdCheck(String cmdCheck) {
        this.cmdCheck = cmdCheck;
    }

    public int getErrcodeOne() {
        if (errorList== null ||errorList.size() < 1) {
            return 0;
        } else {
            return    errorList.get(0);
        }
    }

    public void setErrcode(String errcode) {
        setSettingErrcode(true);
        if (errorList != null) {
            errorList.clear();
        } else {
            errorList=new ArrayList<>();
        }
        if (!TextUtils.isEmpty(errcode)&&errcode.length()==12) {
            for (int x = 0; x <12; ) {
                int code=Integer.parseInt(errcode.substring(x,x=x+4),16);
                if (code > 0) {
                    errorList.add(code);
                }
            }
        }
        this.errcode = errcode;
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

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<StatesActCmd> getList() {
        return list;
    }

    public void setList(List<StatesActCmd> list) {
        this.list = list;
    }

    public static class StatesActCmd {
        int cmd;
        String check;
        String other;

        public int getCmd() {
            return cmd;
        }

        public void setCmd(int cmd) {
            this.cmd = cmd;
        }

        public String getCheck() {
            return check;
        }

        public void setCheck(String check) {
            this.check = check;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }
    }

    public static class StatesDrop80 {
        int temp;  //表示冰箱温度：0~100有效，表示对应的-40℃~60℃；
        int localChips;//0：直薯条关门位；1：直薯条开门位2：中间位置(在中间，既没到开门位也没到关门位)
        int localChcken;//：0：鸡块关门位；1：鸡块开门位；2：中间位置(在中间，既没到开门位也没到关门位)
        int localChip2;//0：波纹薯条关门位；1：波纹薯条开门位2：中间位置(在中间，既没到开门位也没到关门位
        int localScan;//0：扫描在右边；1：扫描在左边2：中间位置(在中间，既没到左边也没到右边)
        int localBasket;  //0：挂篮推杆缩回状态；1：挂篮推杆推出状态2：中间位置(在中间，既没到推出位也没到缩回位)
        int localChaDan;//0：插单推杆缩回状态；1：插单推杆推出状态2：中间位置(在中间，既没到推出位也没到缩回位)
        int meteChips;//1：直薯条缺料；0：直薯条有料
        int meteChcken;//1：鸡块关缺料；0：鸡块有料
        int meteChips2;//1：波纹薯条缺料；0：波纹薯条有料
        int meteIceBox;//1：冰箱门开；0：冰箱门关
        int wShuTiao;//1：直薯条重量
        int wBoWei;
        int wChcken;
        int lanNum;
        boolean power;  //是否重新上电

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public int getLocalChips() {
            return localChips;
        }

        public void setLocalChips(int localChips) {
            this.localChips = localChips;
        }

        public int getLocalChcken() {
            return localChcken;
        }

        public void setLocalChcken(int localChcken) {
            this.localChcken = localChcken;
        }

        public int getLocalChip2() {
            return localChip2;
        }

        public void setLocalChip2(int localChip2) {
            this.localChip2 = localChip2;
        }

        public int getLocalScan() {
            return localScan;
        }

        public void setLocalScan(int localScan) {
            this.localScan = localScan;
        }

        public int getLocalBasket() {
            return localBasket;
        }

        public void setLocalBasket(int localBasket) {
            this.localBasket = localBasket;
        }

        public int getLocalChaDan() {
            return localChaDan;
        }

        public void setLocalChaDan(int localChaDan) {
            this.localChaDan = localChaDan;
        }

        public int getMeteChips() {
            return meteChips;
        }

        public void setMeteChips(int meteChips) {
            this.meteChips = meteChips;
        }

        public int getMeteChcken() {
            return meteChcken;
        }

        public void setMeteChcken(int meteChcken) {
            this.meteChcken = meteChcken;
        }

        public int getMeteChips2() {
            return meteChips2;
        }

        public void setMeteChips2(int meteChips2) {
            this.meteChips2 = meteChips2;
        }

        public int getMeteIceBox() {
            return meteIceBox;
        }

        public void setMeteIceBox(int meteIceBox) {
            this.meteIceBox = meteIceBox;
        }

        public int getwShuTiao() {
            return wShuTiao;
        }

        public void setwShuTiao(int wShuTiao) {
            this.wShuTiao = wShuTiao;
        }

        public int getwBoWei() {
            return wBoWei;
        }

        public void setwBoWei(int wBoWei) {
            this.wBoWei = wBoWei;
        }

        public int getwChcken() {
            return wChcken;
        }

        public void setwChcken(int wChcken) {
            this.wChcken = wChcken;
        }

        public int getLanNum() {
            return lanNum;
        }

        public void setLanNum(int lanNum) {
            this.lanNum = lanNum;
        }

        public boolean isPower() {
            return power;
        }

        public void setPower(boolean power) {
            this.power = power;
        }
    }

    @Override
    public String toString() {
        return "PortFormat{" +
                "leng='" + leng + '\'' +
                ", cmd='" + cmd + '\'' +
                ", id='" + id + '\'' +
                ", sn='" + sn + '\'' +
                ", addr=" + addr +
                ", otherData='" + otherData + '\'' +
                ", state='" + state + '\'' +
                ", cmdCheck='" + cmdCheck + '\'' +
                ", errcode='" + errcode + '\'' +
                ", num='" + num + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String toSimapString() {
        return
                "cmd='" + cmd + '\'' +
                        ", addr=" + addr +
                        ", state='" + state + '\'' +
                        ", cmdCheck='" + cmdCheck + '\'' +
                        ", errcode='" + errcode + '\'' +
                        ", num='" + num + '\'' +
                        ", data='" + data;

    }
}
