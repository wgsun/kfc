package com.hua.back.common.act;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.base.XssEventInfo;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;


import com.hua.back.kfc.KfcSetting;
import com.hua.back.kfc.KfcSettingBom;

import com.tcn.background.R;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MeauSetting extends BaseBackActivity {
    String TAG = "MeauSetting";
    private RecyclerView recyclerView;
    String[] aarSettingKfc = new String[]{"落料模块调试", "油炸桁架模块调试"};

    private RvAdapter rvAdapter;

    @Override
    public int getlayout() {
        return R.layout.menusetting;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XssTrands.getInstanll().LoggerDebug(TAG, "onCreate");
        recyclerView = findViewById(R.id.menu_listview);
        initViewTemp();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        rvAdapter = new RvAdapter();
        recyclerView.setAdapter(rvAdapter);//设置数据
        XssTrands.getInstanll().registerListener(m_vendListener);
    }


    @Override
    protected void onDestroy() {
        XssTrands.getInstanll().unregisterListener(m_vendListener);

        XssTrands.getInstanll().LoggerDebug(TAG, "onDestroy");

        super.onDestroy();
    }

    private VendListener m_vendListener = new VendListener();

    private class VendListener implements XssTrands.VendEventListener {

        @Override
        public void VendEvent(XssEventInfo cEventInfo) {
            switch (cEventInfo.m_iEventID) {

            }

        }
    }


    class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvHolder> {

        @NonNull
        @Override
        public RvAdapter.RvHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu, viewGroup, false);

            return new RvAdapter.RvHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final RvAdapter.RvHolder rvHolder, @SuppressLint("RecyclerView") final int i) {
            rvHolder.tvName.setText(aarSettingKfc[i]);
            rvHolder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (XssSavaData.getInstance().getMacType()) {
                        case XssData.ShuTiaoZhan:
                            loadKfcAct(i);
                            break;
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return aarSettingKfc.length;
        }

        class RvHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            public RvHolder(@NonNull View view) {
                super(view);
                tvName = view.findViewById(R.id.tv_mean);
            }
        }
    }

    public void loadKfcAct(int pos) {

        Class clszz = null;
        switch (pos) {
            case 0:
                clszz = KfcSetting.class;

                break;
            case 1:
                clszz = KfcSettingBom.class;

                break;

        }
        if (clszz != null) {
            Intent intent = new Intent(MeauSetting.this, clszz);
            intent.putExtra(XssData.BgTitle, aarSettingKfc[pos]);
            startActivity(intent);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    View viewTemp;
    List<TextView> listChild = new ArrayList<>();
    List<TextView> listBowen = new ArrayList<>();
    List<TextView> listShuTiao = new ArrayList<>();

    public void initViewTemp() {
        findViewById(R.id.item_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XssTrands.getInstanll().stopAct();
            }
        });
        viewTemp = findViewById(R.id.view_temp);
        tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (viewTemp.getVisibility() != View.VISIBLE) {
                    viewTemp.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    viewTemp.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        listChild.clear();
        listBowen.clear();
        listShuTiao.clear();
        initList(findViewById(R.id.view_shutiao), 1);
        initList(findViewById(R.id.view_child), 2);
        initList(findViewById(R.id.view_bowen), 3);

    }

    public void initList(View view, int sele) {
        int localDoor = 0;
        int localClean = 0;
        List<TextView> list = null;
        String name = "";
        switch (sele) {
            case 1:
                name = XssData.seruiFoodName[0];
                list = listShuTiao;
                localDoor = 101;
                localClean = 11;
                break;
            case 2:
                name = XssData.seruiFoodName[2];
                list = listChild;
                localDoor = 105;
                localClean = 12;
                break;
            case 3:
                name = XssData.seruiFoodName[1];
                list = listBowen;
                localDoor = 103;
                localClean = 13;
                break;
            default:
                return;
        }

        list.add(view.findViewById(R.id.item_name));
        list.add(view.findViewById(R.id.item_open));
        list.add(view.findViewById(R.id.item_close));
        list.add(view.findViewById(R.id.item_clean));
        int localFinal = localDoor;
        int localFinalClean = localClean;
        list.get(0).setText(name);
        list.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XssTrands.getInstanll().actionHex(localFinal, XssUtility.getnumTwo("1") + XssUtility.getnumFour("500") + "00");
            }
        });
        list.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XssTrands.getInstanll().actionHex(localFinal, XssUtility.getnumTwo("0") + XssUtility.getnumFour("500") + "00");
            }
        });
        list.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XssTrands.getInstanll().actionHex(localFinalClean, "00000000");
            }
        });
    }
}
