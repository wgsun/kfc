package com.common.base.BeanInfo;

/**
 * FileName: KfcOrder
 * Author: hua
 * Date: 2022/1/4 18:22
 * Description:
 */
public class KfcOrder {
    int slot;
    int type;
    int weight;
    int unitNum;
    String order;
    int ismeat;  //0素，1荤）
    boolean isHighOrder;
    int flow;//1  称重  2称重完成； 3，移动       8  插单开始处理
    //  0x01转运中； 0x02烹炸中； 0x03滤油中
    //0x04回蓝中； 0x05已完成； 0xE0故障
    long startTime;
    long youzhaTime;
    long endTime;
    int weightNow;
    int bomId;
    int ErrorCode;
    int yumStates;
    boolean isMidEnd;//订单是否中止


    public KfcOrder() {
        ErrorCode = 0;
        isMidEnd = false;
    }

    public int getYumStates() {
        return yumStates;
    }

    public void setYumStates(int yumStates) {
        this.yumStates = yumStates;
    }

    public boolean isMidEnd() {
        return isMidEnd;
    }

    public void setMidEnd(boolean midEnd) {
        isMidEnd = midEnd;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }

    public int getBomId() {
        return bomId;
    }

    public void setBomId(int bomId) {
        this.bomId = bomId;
    }

    public int getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(int unitNum) {
        this.unitNum = unitNum;
    }

    public int getIsmeat() {
        return ismeat;
    }

    public void setIsmeat(int ismeat) {
        this.ismeat = ismeat;
    }

    public boolean isHighOrder() {
        return isHighOrder;
    }

    public void setHighOrder(boolean highOrder) {
        isHighOrder = highOrder;
    }

    public long getYouzhaTime() {
        return youzhaTime;
    }

    public void setYouzhaTime(long youzhaTime) {
        this.youzhaTime = youzhaTime;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getWeightNow() {
        return weightNow;
    }

    public void setWeightNow(int weightNow) {
        this.weightNow = weightNow;
    }

    @Override
    public String toString() {
        return "KfcOrder{" +
                "slot=" + slot +
                ", type=" + type +
                ", weight=" + weight +
                ", unitNum=" + unitNum +
                ", order='" + order + '\'' +
                ", ismeat=" + ismeat +
                ", isHighOrder=" + isHighOrder +
                ", flow=" + flow +
                ", yumStates=" + yumStates +
                ", startTime=" + startTime +
                ", youzhaTime=" + youzhaTime +
                ", endTime=" + endTime +
                ", weightNow=" + weightNow +
                ", bomId=" + bomId +
                ", ErrorCode=" + ErrorCode +
                '}';
    }
}
