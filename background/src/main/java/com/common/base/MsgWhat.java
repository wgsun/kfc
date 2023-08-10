package com.common.base;

/**
 * FileName: MsgWhat
 * Author: hua
 * Date: 2021/8/18 10:38
 * Description:
 */
public interface MsgWhat {
    int ERR_INT = -999;
    int UPDATE_TIME = 1;
    int SERIALDATA = 2;
    int RFIDDATA = 3;
    int PORTDATA = 4;
    int PORTDATA_BOM = 5;
    int NEXTDATA = 6;
    int TestDATA = 7;
    int ERRORCODE = 8;
    int SQLDATA_SLOT = 21;

    int CMDSTATS06 = 42;

    int SHIPORDERINFO = 41;


    int SHOWTOAST = 91;
    int SHOWERROR = 92;
    int UPDATEStates = 93;
    int KFCHistory = 1002;
    int KFCStart = 1003;
    int SERVER_MQTT = 11;

    int errorCode11205 = 11205;
    int errorCode9001 = 19001;
    int errorCode9002 = 19002;
    int errorCode9003 = 19003;
    int errorCode9004 = 19004;
    int errorCode9005 = 19005;
    int errorCode9006 = 19006;
    int errorCode9007 = 19007;
    int errorCode9008 = 19008;
    int errorCode9009 = 19009;
    int errorCode90010 = 19010;
    int errorCode9011 = 19011;
    int errorCode9012 = 19012;
    int errorCode9013 = 19013;
    int errorCode9014 = 29014;
    int errorCode9015 = 19015;
    int errorCode9016 = 29016;
    int errorCode9017 = 19017;
    int errorCode9018 = 19018;
    int errorCode9019 = 19019;
    int errorCode9020 = 29020;
    int errorCode9021 = 29021;
/*    19001	接口参数异常
19002	落料模块串口通讯故障
19003	油炸桁架模块通讯异常
19004	订单号异常
19005	参数格式校验失败
19006	程序初始化失败
19007	油炸中，无法进行下一步操作
19008	油炸类型产品不存在
19009	生成指令参数异常
19010	设置自动模式参数异常
19011	配置荤素炸锅参数异常
19012	油炸订单失败
19013	订单异常
29014	机器正在扫描篮子，请稍后进行油炸
19015	机器暂未发现可以落料油炸的篮子
29016	订单已经在执行中（不影响流程，但这边会执行原订单，新订单会忽略）
19017 机器断电重启，该订单中止
19018 机器断电
19019 机器上电初始化中...
errorCode9020 炸炉准备中......
29021  冰箱温度异常

*/


}
