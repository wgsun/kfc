package com.xss.kfc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.base.BeanInfo.KfcOrder;
import com.common.base.BeanInfo.PortFormat;
import com.common.base.MsgWhat;
import com.common.base.XssEventInfo;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.common.serial.kfc.KfcBomInfo;
import com.common.serial.kfc.KfcPortControl;
import com.common.serial.kfc.PaxLog;
import com.hua.back.common.act.MeauSetting;
import com.hua.back.kfc.base.YumControl;
import com.hua.back.kfc.base.YumResultCallback;
import com.xss.kfc.utils.ToastTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.common.base.MsgWhat.PORTDATA;

public class ServingUi {
    private static ServingUi produceUI;
    Activity mActivity;
    TextView tvNum, tvEight, tvNum1, tvEight1, tvNum2, tvEight2;
    TextView tvfoodbom0, tvfoodbom1, tvfoodbom2, tvfoodbom3;
    String actWeight = "";
    int foodMeat = 4;

    int orderWater;
    TextView tvError;
    String tvBomError = "";
    String LanNum = "";
    String errMsg = "";
    int unitJiKuai = 5;
    int unitShuTiao = 110;
    int unitShuTiaoBoWei = 110;
    int foodNum0, foodNum1, foodNum2;
    String[] numaar = new String[10];
    private RecyclerView rvInfo;
    private RecyclerView hisToryInfo;
    private Infoadapter infoadapter;
    private HistoryApter historyApter;

    private View cdMeat;
    private View cdPlan, cdThree;
    private TextView tv_zhalu;
    private TextView tv_history;

    List<KfcOrder> kfcOrderList = new ArrayList<>();
    List<KfcOrder> kfcHistoryList = new ArrayList<>();
    List<TextView> listStates = new ArrayList<>();
    private TextView tv__kfc_msg;


    public static ServingUi getInstall() {
        if (produceUI == null) {
            synchronized (ServingUi.class) {
                if (produceUI == null)
                    produceUI = new ServingUi();
            }
        }

        return produceUI;
    }

    public void onCreate(Activity mAc) {
        logx("onCreat", "");
        YumControl.getInstall().setHeatCallback(mActivity, new YumResultCallback() {
            @Override
            public void onSuccess(String message) {
                tv_history.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_history.setText(message);
                    }
                });
            }
        });
        YumControl.getInstall().deviceBootInit(mActivity, new YumResultCallback() {
            @Override
            public void onSuccess(String message) {
                logx("deviceBootInit: " + message);
                YumControl.getInstall().showHint(message, "初始化成功");
            }
        });
        this.mActivity = mAc;
        mActivity.setContentView(R.layout.servingui);
        for (int x = 1; x <= 10; x++) {
            numaar[x - 1] = String.valueOf(x);
        }
        mActivity.findViewById(R.id.iv_s).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, MeauSetting.class));
            }
        });

        TextView help = mActivity.findViewById(R.id.btn_help);
        tv__kfc_msg = mActivity.findViewById(R.id.tv__kfc_msg);
        help.setText("扫描篮子");
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YumControl.getInstall().scanBasket(mActivity, new YumResultCallback() {
                    @Override
                    public void onSuccess(String message) {
                        logx("scanBasket: " + message);
                        YumControl.getInstall().showHint(message, "扫描篮子成功");

                    }
                });
            }
        });
        help.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PaxLog.getInstall().cleanDIr();
                return true;
            }
        });


        TextView tvClenError = mActivity.findViewById(R.id.btn_clean_error);
        tvClenError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errMsg = "";
                setText();
                YumControl.getInstall().dismissWarning(mActivity, 0,0+"");
            }
        });


        TextView tvClen = mActivity.findViewById(R.id.btn_clean);
        tvClen.setText("取消订单");
        tvClen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KfcPortControl.getInstall().cleanOrder();

//                XssTrands.getInstanll().hideNavKey(true);
/*                XssTrands.getInstanll().stopAct();
                KfcPortControl.getInstall().cleanOrder();*/
            }
        });
        tvError = mActivity.findViewById(R.id.tv_error);
        tv_zhalu = mActivity.findViewById(R.id.tv_zhalu);
        addClick(mActivity.findViewById(R.id.icon_up));
        addClick(mActivity.findViewById(R.id.icon_down));
        tvNum = mActivity.findViewById(R.id.tv_food_num);
        tvEight = mActivity.findViewById(R.id.tv_food_eight);

        addClick(mActivity.findViewById(R.id.icon_up1));
        addClick(mActivity.findViewById(R.id.icon_down1));
        tvNum1 = mActivity.findViewById(R.id.tv_food_num1);
        tvEight1 = mActivity.findViewById(R.id.tv_food_eight1);
        addClick(mActivity.findViewById(R.id.icon_up2));
        addClick(mActivity.findViewById(R.id.icon_down2));
        tvNum2 = mActivity.findViewById(R.id.tv_food_num2);
        tvEight2 = mActivity.findViewById(R.id.tv_food_eight2);


        tvfoodbom0 = mActivity.findViewById(R.id.btn_peizha0);
        tvfoodbom1 = mActivity.findViewById(R.id.btn_peizha1);
        tvfoodbom2 = mActivity.findViewById(R.id.btn_peizha2);
        tvfoodbom3 = mActivity.findViewById(R.id.btn_peizha3);

        listStates.clear();
        listStates.add(mActivity.findViewById(R.id.food_staes0));
        listStates.add(mActivity.findViewById(R.id.food_staes1));
        listStates.add(mActivity.findViewById(R.id.food_staes2));
        addClick(tvfoodbom0);
        addClick(tvfoodbom0);
        addClick(tvfoodbom0);
        addClick(tvfoodbom1);
        addClick(tvfoodbom2);
        addClick(tvfoodbom3);

        cdMeat = mActivity.findViewById(R.id.chadan_meat);
        cdPlan = mActivity.findViewById(R.id.chadan_plan);
        cdThree = mActivity.findViewById(R.id.chadan_three);

        addClick(cdMeat);
        addClick(cdPlan);
        addClick(cdThree);
        setName(R.id.tv_name0, 0);
        setName(R.id.tv_name1, 1);
        setName(R.id.tv_name2, 2);
        foodNum0 = 1;
        foodNum1 = 1;
        foodNum2 = 1;

        setRv();
        setHistoryRv();
    }


    protected void setName(int id, int pos) {
        TextView tv = mActivity.findViewById(id);
        if (tv != null && pos < XssData.seruiFoodName.length) {
            tv.setText(XssData.seruiFoodName[pos]);

        }
    }

    public void setRv() {
        rvInfo = mActivity.findViewById(R.id.recy_showinfo);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvInfo.setLayoutManager(layoutManager);
        infoadapter = new Infoadapter();
        rvInfo.setAdapter(infoadapter);
    }

    public void setHistoryRv() {
        tv_history = mActivity.findViewById(R.id.tv_history);
        hisToryInfo = mActivity.findViewById(R.id.recy_showhistory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        hisToryInfo.setLayoutManager(layoutManager);
        historyApter = new HistoryApter();
        hisToryInfo.setAdapter(historyApter);
    }

    public void onDestroy() {

        logx("onDestroy", "");
    }


    public void addClick(View view) {
        if (view != null) {
            view.setOnClickListener(OnbtnClicker);
        }
    }

    public void setEight(int sele) {
        switch (sele) {
            case 0:
                tvNum.setText(foodNum0 + "份");
                tvEight.setText(foodNum0 * unitShuTiao + "g");
                break;
            case 1:
                tvNum1.setText(foodNum1 + "份");
                tvEight1.setText(foodNum1 * unitShuTiaoBoWei + "g");
                break;
            case 2:

                tvNum2.setText(foodNum2 + "份");
                tvEight2.setText(
                        foodNum2 * unitJiKuai + "块");
                break;
        }

    }


    public void bomFood(int local) {
        if (XssTrands.getInstanll().isContinClick("bomFood")) {
            return;
        }
        if (true) {
            String s = "";
            int type = 0;
            int num = 0;
            int unit = 0;
            if (local == 0) {
                type = 1;
                unit = foodNum0;
                num = foodNum0 * unitShuTiao;
            } else if (local == 1) {
                type = 2;
                unit = foodNum1;
                num = foodNum1 * unitShuTiaoBoWei;
            } else if (local == 2) {
                type = 3;
                unit = foodNum2;
                num = foodNum2 * unitJiKuai;
            } else if (local == 3) {
                if (KfcPortControl.getInstall().isCanBoosOrder(foodMeat)) {
                    s = YumControl.getInstall().createOrder(mActivity, num, num, foodMeat, orderWater++ + "_" + XssUtility.getTime14B());
                    YumControl.getInstall().showHint(s, getName(foodMeat) + "下单成功");
                }
                return;
            } else {
                return;
            }
            s = YumControl.getInstall().createOrder(mActivity, num, num, type, orderWater++ + "_" + XssUtility.getTime14B());
            String name=getName(type);
            if (type == 2) {
                name = getName(3);
            } else if(type==3) {
                name = getName(2);
            }
            YumControl.getInstall().showHint(s, name + "下单成功");

//            KfcPortControl.getInstall().shipOrer(orderWater++ + "_" + XssUtility.getTime14B(), unit, num, type);

            return;
        }
    }

    public void toast(int num, int type) {
        String name = KfcPortControl.getInstall().getTypeName(type);
//        new Toast(mActivity).setText();
//        ToastUtils.show(mActivity, num + "份 " + name + " 下单成功");
    }


    View.OnClickListener OnbtnClicker = new
            View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.btn_peizha0:
                            bomFood(0);
                            break;
                        case R.id.btn_peizha1:
                            bomFood(1);
                            break;
                        case R.id.btn_peizha2:
                            bomFood(2);
                            break;
                        case R.id.btn_peizha3:
                            bomFood(3);
                            break;

                        case R.id.chadan_meat:
                            foodMeat = 5;
                            setViewColor(cdMeat, cdPlan, cdThree);
                            break;
                        case R.id.chadan_plan:
                            foodMeat = 4;
                            setViewColor(cdPlan, cdMeat, cdThree);
                            break;
                        case R.id.chadan_three:
                            setViewColor(cdThree, cdPlan, cdMeat);
                            foodMeat = 4;

                            break;

                        case R.id.icon_up:
                            if (foodNum0 < 6) {
                                foodNum0++;
                                setEight(0);
                            }
                            break;
                        case R.id.icon_down:
                            if (foodNum0 > 1) {
                                foodNum0--;
                                setEight(0);
                            }
                            break;
                        case R.id.icon_up1:
                            if (foodNum1 < 6) {
                                foodNum1++;
                                setEight(1);
                            }
                            break;
                        case R.id.icon_down1:
                            if (foodNum1 > 1) {
                                foodNum1--;
                                setEight(1);
                            }
                            break;
                        case R.id.icon_up2:
                            if (foodNum2 < 9) {
                                foodNum2++;
                                setEight(2);
                            }
                            break;

                        case R.id.icon_down2:
                            if (foodNum2 > 1) {
                                foodNum2--;
                                setEight(2);
                            }
                            break;
                    }
                }
            };

    public void setViewColor(View sele, View v1, View v2) {
        v1.setBackgroundResource(R.drawable.shape_rect_gray_black);
        v2.setBackgroundResource(R.drawable.shape_rect_gray_black);
        sele.setBackgroundResource(R.drawable.shape_rect_green_black);
    }

    private XssTrands.VendEventListener m_vendListener = new XssTrands.VendEventListener() {

        @Override
        public void VendEvent(XssEventInfo cEventInfo) {
            switch (cEventInfo.m_iEventID) {
                case PORTDATA:
                    showParm(cEventInfo.m_lParam4);
                    break;
                case MsgWhat.TestDATA:
                    errMsg = cEventInfo.m_lParam4;
                    setText();
                case MsgWhat.PORTDATA_BOM:
                    KfcBomInfo temp = KfcPortControl.getInstall().parsingBomData(cEventInfo.m_lParam4);
                    if (temp != null) {
                        tv_zhalu.setText(temp.toString());
                        String s = XssTrands.getInstanll().getErrCode(temp.getErrcode(), true);
                        if (!TextUtils.isEmpty(s)) {
                            tvBomError = "code=" + temp.getErrcode() + "  " + s;
                        }
                    }
                    break;
                case MsgWhat.SHOWERROR:
                    tvError.setText(cEventInfo.m_lParam4);
                    break;
                case MsgWhat.SHIPORDERINFO:
                    showOredrMsg();
                    break;
                case MsgWhat.UPDATEStates:
                    PortFormat portFormat = KfcPortControl.getInstall().getPortFormat80();
                    if (portFormat != null && portFormat.getStatesDrop80() != null) {
                        PortFormat.StatesDrop80 info = portFormat.getStatesDrop80();
                        setFoodStates(2, info.getMeteChcken());
                        setFoodStates(1, info.getMeteChips2());
                        setFoodStates(0, info.getMeteChips());
                        String span = "    ";
                        StringBuffer sb = new StringBuffer("");
                        sb.append("直薯: " + info.getwShuTiao() + span);
                        sb.append("波纹: " + info.getwBoWei() + span);
                        sb.append("鸡块:" + info.getwChcken() + span);
                        LanNum = info.getLanNum() + "  ";
                        actWeight = sb.toString();
                    }
                    setText();
                    break;

                case MsgWhat.KFCHistory:
//                    showhistory();   不显示了
                    break;
                case MsgWhat.SHOWTOAST:
                    if (cEventInfo.m_lParam1 == 1) {
                        ToastTools.getInstance().error(mActivity, cEventInfo.m_lParam4);
                    } else if (cEventInfo.m_lParam1 == 0) {
                        ToastTools.getInstance().info(mActivity, cEventInfo.m_lParam4);
                    }
                    break;

            }
        }
    };


    public void showParm(String msg) {
//        020014A0
//        0000
//        5501
//        3C
//        0400
//        B0DEFDFF
//        3A6A
//        0302F5
        PortFormat portFormat = XssTrands.getInstanll().getPortFormat(msg);
        if (portFormat == null) {
            return;
        }
        String cmd = portFormat.getCmd();
        int addr = portFormat.getAddr();
        if (msg.length() < 22) {
            return;
        }
        String data = msg.substring(18, 22);
        if ("A1".equalsIgnoreCase(cmd)) {
//            setActHint(XssTrands.getInstanll().deal06(portFormat));
        } else if ("83".equals(cmd)) {
            int leng = msg.length();
            switch (addr) {
                case 14:
                case 15:
                case 16:
                    int one = Integer.parseInt(msg.substring(leng - 10, leng - 8), 16);
                    int two = Integer.parseInt(msg.substring(leng - 8, leng - 6), 16);
                    break;
                case 17:
                    int sudu = Integer.parseInt(msg.substring(leng - 10, leng - 6), 16);
                    LanNum = sudu + "";
                    setText();
                    break;
                default:

                    break;
            }

        }


    }

    public void setText() {
        if (KfcPortControl.getInstall().isScanLanNeed) {
            tv__kfc_msg.setText(actWeight + errMsg + " " + tvBomError);
        } else {
            tv__kfc_msg.setText(actWeight + " 篮子：" + LanNum + "  " + errMsg + " " + tvBomError);
        }
    }


    public void showOredrMsg() {

        kfcOrderList = KfcPortControl.getInstall().getAllList();
        infoadapter.notifyDataSetChanged();

    }

    public void onResume() {
        XssTrands.getInstanll().registerListener(m_vendListener);

        unitShuTiao = XssSavaData.getInstance().getData(XssData.ShuTiaoSignNum + 101, 170);
        unitJiKuai = XssSavaData.getInstance().getData(XssData.ShuTiaoSignNum + 102, 5);
        unitShuTiaoBoWei = XssSavaData.getInstance().getData(XssData.ShuTiaoSignNum + 103, 170);
        setEight(0);
        setEight(1);
        setEight(2);
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.UPDATEStates, "");
    }

    public void onPause() {
        XssTrands.getInstanll().unregisterListener(m_vendListener);

    }

    class Infoadapter extends RecyclerView.Adapter<Infoadapter.InfoHolder> {

        @NonNull
        @Override
        public Infoadapter.InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Infoadapter.InfoHolder holder = new Infoadapter.InfoHolder(LayoutInflater.from(
                    mActivity).inflate(R.layout.item_servui, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull Infoadapter.InfoHolder holder, @SuppressLint("RecyclerView") final int position) {
            if (kfcOrderList != null && kfcOrderList.size() > position) {
                KfcOrder kfcOrder = kfcOrderList.get(position);
                int type = kfcOrder.getType();
                String name = getName(type);
                String s = "订单号：" + kfcOrder.getOrder()
                        + "\n" + kfcOrder.getUnitNum() + " " + getUnit(type) + "   " + name + "：" + KfcPortControl.getInstall().getFlow(kfcOrder.getFlow())
                        + "\n实际重量：" + kfcOrder.getWeightNow() + "g"
                        + "\n" + "开始时间：" + XssUtility.getTimeShow(kfcOrder.getStartTime());
//                        + "\n" + "结束时间：" + XssUtility.getTimeShow(kfcOrder.getEndTime())
                holder.tvMsg.setText(s);
                String order = kfcOrder.getOrder();
                if (kfcOrder.getFlow() > 0) {
                    holder.tvMsg.setTextColor(Color.BLUE);
                    holder.btnDel.setVisibility(View.INVISIBLE);
                    holder.btnOne.setVisibility(View.INVISIBLE);
                } else {
                    holder.tvMsg.setTextColor(0x6D000000);

                    holder.btnDel.setVisibility(View.VISIBLE);
                    holder.btnOne.setVisibility(View.VISIBLE);
                    holder.btnDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dealKfcOrder(position, order, true);

                        }
                    });
                    holder.btnOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dealKfcOrder(position, order, false);
                        }
                    });
                }

            }
        }

        @Override
        public int getItemCount() {
            if (kfcOrderList != null) {
                return kfcOrderList.size();
            }
            return 0;
        }

        class InfoHolder extends RecyclerView.ViewHolder {
            TextView tvMsg, btnDel, btnOne;

            public InfoHolder(View itemView) {
                super(itemView);
                tvMsg = itemView.findViewById(R.id.item_tv_msg);
                btnDel = itemView.findViewById(R.id.item_btn_del);
                btnOne = itemView.findViewById(R.id.item_btn_one);
            }
        }
    }

    class HistoryApter extends RecyclerView.Adapter<HistoryApter.HistoryHolder> {
        @NonNull
        @Override
        public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            HistoryApter.HistoryHolder holder = new HistoryApter.HistoryHolder(LayoutInflater.from(
                    mActivity).inflate(R.layout.item_servui, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
            if (kfcHistoryList != null && kfcHistoryList.size() > position) {
                KfcOrder kfcOrder = kfcHistoryList.get(position);
                String name = getName(kfcOrder.getType());
                String hint = "";
      /*          if (kfcOrder.isErrorOrder()) {
                    hint = "未完成";
                    holder.tvMsg.setTextColor(Color.RED);

                } else {
                    holder.tvMsg.setTextColor(0xffff6100);
                }*/
                String s = name + "    " + kfcOrder.getUnitNum() + getUnit(kfcOrder.getType())
                        + "    时间：" + (kfcOrder.getEndTime() - kfcOrder.getYouzhaTime()) / 1000
                        + "s\n实际重量：" + kfcOrder.getWeightNow() + "g"
                        + "\n" + "结束：" + XssUtility.getTimeShow(kfcOrder.getEndTime())
                        + "\n" + "订单号：" + kfcOrder.getOrder() + "   " + hint;

                //+ "：" + KfcPortControl.getInstall().getFlow(kfcOrder.getFlow()) +
//                        "\n" + "开始时间：" + XssUtility.getTimeShow(kfcOrder.getStartTime()) +
//                        "\n" + "结束时间：" + XssUtility.get
//                        TimeShow(kfcOrder.getEndTime()) +
                holder.btnDel.setVisibility(View.GONE);
                holder.btnOne.setVisibility(View.GONE);
                holder.tvMsg.setText(s);

            }

        }

        @Override
        public int getItemCount() {
            if (kfcHistoryList != null) {
                return kfcHistoryList.size();
            }
            return 0;
        }

        class HistoryHolder extends RecyclerView.ViewHolder {
            TextView tvMsg, btnDel, btnOne;

            public HistoryHolder(View itemView) {
                super(itemView);
                tvMsg = itemView.findViewById(R.id.item_tv_msg);
                btnDel = itemView.findViewById(R.id.item_btn_del);
                btnOne = itemView.findViewById(R.id.item_btn_one);
            }

        }
    }

    protected void dealKfcOrder(int pos, String order, boolean isDeal) {
        if (XssTrands.getInstanll().isContinClick("dealKfcOrder")) {
            return;
        }
        KfcPortControl.getInstall().dealKfcOrder(pos, isDeal);
        showOredrMsg();
    }

    public void logx(String fun, String msg) {
        XssTrands.getInstanll().LoggerDebug(this.getClass().getSimpleName(), fun + ": " + msg);
    }

    public void logx(String msg) {
        XssTrands.getInstanll().LoggerDebug(this.getClass().getSimpleName(), msg);
    }

    class HistoryData {
        int num;
        int weight;
        int type;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public String getName(int type) {
        String name = "插单商品";
        switch (type) {
            case 1:
                name = XssData.seruiFoodName[0];
                break;
            case 2:
                name = XssData.seruiFoodName[2];
                break;
            case 3:
                name = XssData.seruiFoodName[1];

                break;
            default:
                if (type < XssData.seruiFoodName.length - 1) {
                    name = XssData.seruiFoodName[type - 1];
                }
                break;
        }


        return name;
    }

    public void setFoodStates(int tv, int state) {
        if (state == 0) {
            listStates.get(tv).setText("有料");
            listStates.get(tv).setTextColor(mActivity.getResources().getColor(R.color.green));
        } else {
            listStates.get(tv).setText("缺料");
            listStates.get(tv).setTextColor(mActivity.getResources().getColor(R.color.red));

        }

    }


    public String getUnit(int type) {
        if (type == 2) {
            return "份";
        }
        return "份";
    }
}
