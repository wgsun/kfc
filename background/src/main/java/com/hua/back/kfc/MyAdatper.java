package com.hua.back.kfc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tcn.background.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/5/25.
 */

public class MyAdatper extends RecyclerView.Adapter<MyAdatper.Myholder> {
    Context con;
    ONBtnClick onBtnClick;

    public interface ONBtnClick {
        void onclick(int pos);

    }

    public void setOnBtnClick(ONBtnClick onBtnClick) {
        this.onBtnClick = onBtnClick;
    }

    public MyAdatper(Context context) {
        con = context;
    }

    public MyAdatper(Context context, String[] data) {
        this.data=data;
        con = context;
    }
//
//    String[] data = {"查询状态及异常信息", "出货命令", "回原点",
//            " 小履带转", " 小履带停","清除异常","防夹手光检检测","开门","关门","更新程序","串口设置"};
    String[] data = {"登入", "登出", "扣款",
            "确认", " 小履带停","清除异常","防夹手光检检测","开门","关门","更新程序","串口设置"};

    @Override
    public MyAdatper.Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        Myholder holder = new Myholder(LayoutInflater.from(
                con).inflate(R.layout.test_item_home, parent,
                false));
        return holder;
    }

    Handler mHandler = new Handler();

    @Override
    public void onBindViewHolder(final MyAdatper.Myholder holder, @SuppressLint("RecyclerView") int position) {
        holder.btn.setText(data[position]);
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnClick != null)
                    onBtnClick.onclick(position);
                holder.btn.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1500);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.btn.setClickable(true);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class Myholder extends RecyclerView.ViewHolder {
        Button btn;

        public Myholder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.btn_shipment);
        }
    }
}
