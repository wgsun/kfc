package com.common.base.utils;

public interface XssData {
    public static final String PATH_SDCARD = "/mnt/sdcard";
    public static final String YEAR_HM = "yyyy-MM-dd  HH:mm";
    int SLotMax_csj = 7;


    String MACID = "MACID";
    String MACTYPEKEY = "MACTYPEKEY";
    String TimingReboot = "TimingReboot";
    String B_TimingReboot = "B_TimingReboot";
    String ChuCaiJi = "chuCaiJi";
    String ShuTiaoZhan = "ShuTiaoZhan";
    String ShuTiaoSignNum = "ShuTiaoSignNum";
    String FOODZhaCheng = "foodZhaCheng";//炸程选择
    String FOODMeatPlain="FOODMeatPlain";
    int FOODMeatPlainv=100;  //3 2 1     荤  素素

    String REBOOT_SCAN_FUWEI = "REBOOT_SCAN_FUWEI";
    int NumErrorBoom = 2000;
    int NumErrorBoom3 = 3000;
    int NumErrorDrop = 1000;
    int NumError10000 = 10000;
    int NumError20000 = 20000;
    int childBilie= 2250;



    String[] FOODSELES = new String[]{"1-裹粉薯条", "2-黄金鸡块", "3-波纹薯条", "4-红豆派", "5-LTO", "6-插单薯条"};
    String[] seruiFoodName = new String[]{"裹粉薯条", "波纹薯条", "黄金鸡块", "红豆派", "LTO", "插单薯条"};

    String[] portaar = new String[]{"port_qudongban", "port_qudongban_bote",
            "port_485", "port_485_bote",
            "port_rfid", "port_rfid_bote"};
    String BgTitle = "BgTitle";


    String[] BomiolMax = new String[]{"BomiolMax0", "BomiolMax1", "BomiolMax2", "BomiolMax3"};
    String[] BomiolExist = new String[]{"Bomiol0", "Bomiol1", "Bomiol2", "Bomiol3"};
}
