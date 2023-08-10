package com.hua.back.kfc.base;

/**
 * FileName: OrderResultCallback
 * Author: hua
 * Date: 2022/4/12 14:53
 * Description:  薯条站实际生产状态返回，监听方式，实时返回（监听各订单制作进度信息）
 */
public interface OrderResultCallback {
    void onSuccess(int code,String message);
    void onFail(int code,String message);
}

