package com.wwsl.wgsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.bean.AdvertiseBean;
import com.wwsl.wgsj.bean.net.MdBaseDataBean;
import com.wwsl.wgsj.dialog.ChargeDialogFragment;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.ToastUtil;

import java.util.List;

/**
 * 元宝
 */
public class MyBalanceActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MyBalanceActivity";

    private TextView tvMoney;
    private TextView tvCharge;
    private TextView tvFreezeMoney;
    private TextView tvTitle;
    private TextView tvMoneyA;
    private TextView tvDeposit;
    private RoundedImageView ivAd1;
    private RoundedImageView ivAd2;

    private String adUrl1;
    private String adUrl2;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_my_balance;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        tvMoney = findViewById(R.id.tvMoney);
        tvCharge = findViewById(R.id.tvCharge);
        tvMoneyA = findViewById(R.id.tvMoneyA);
        tvDeposit = findViewById(R.id.tvDeposit);
        ivAd1 = findViewById(R.id.ivAd1);
        ivAd2 = findViewById(R.id.ivAd2);


        tvFreezeMoney = findViewById(R.id.tvFreezeMoney);
        tvTitle = findViewById(R.id.title);
        tvCharge.setOnClickListener(this);
        tvDeposit.setOnClickListener(this);
        tvTitle.setText(AppConfig.getInstance().getCoinName());
        tvMoney.setText(AppConfig.getInstance().getUserBean().getCoin());
        findViewById(R.id.ll_mall_welfare).setOnClickListener(this);
        findViewById(R.id.ll_farmer_welfare).setOnClickListener(this);
        findViewById(R.id.ll_partner_welfare).setOnClickListener(this);
        findViewById(R.id.ll_bill).setOnClickListener(this);
        ivAd1.setOnClickListener(this);
        ivAd2.setOnClickListener(this);

        List<AdvertiseBean> adInnerList = AppConfig.getInstance().getConfig().getAdInnerList();
        if (adInnerList != null && adInnerList.size() >= 2) {
//            ivAd1.setVisibility(View.VISIBLE);
            adUrl1 = adInnerList.get(1).getUrl();
            ImgLoader.display(adInnerList.get(1).getThumb(), ivAd1);
        } else {
            ivAd1.setVisibility(View.GONE);
        }

        if (adInnerList.size() >= 3) {
//            ivAd2.setVisibility(View.VISIBLE);
            adUrl2 = adInnerList.get(2).getUrl();
            ImgLoader.display(adInnerList.get(2).getThumb(), ivAd2);
        } else {
            ivAd2.setVisibility(View.GONE);
        }

    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MyBalanceActivity.class);
        context.startActivity(intent);
    }

    private ChargeDialogFragment fragment;

    public void openChargeDialog() {
        if (null == fragment) {
            fragment = new ChargeDialogFragment();
        }
        fragment.show(getSupportFragmentManager(), "ChargeDialogFragment");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCharge:
                openChargeDialog();
                break;
            case R.id.tvDeposit:

                if (AppConfig.getInstance().getUserBean().getIsIdIdentify() != 1) {
                    ToastUtil.show("暂未身份认证,请先进行身份认证");
                    UserIdentifyActivity.forward(MyBalanceActivity.this);
                } else {
                    CoinExchangeActivity.forward(this, Constants.EXCHANGE_TYPE_DD_DEPOSIT);
                }

                break;
            case R.id.ll_mall_welfare://商城福利
                MallWelfareActivity.invoke(this);
                break;
            /*case R.id.ll_farmer_welfare://助农福利
                FarmerWelfareActivity.invoke(this);
                break;*/
            case R.id.ll_partner_welfare://代理福利
                PartnerWelfareActivity.invoke(this);
                break;
            case R.id.ll_bill://账单
                MoneyDetailActivity.forward(this, HttpConst.DETAIL_ACTION_ALL, HttpConst.DETAIL_ACTION_ALL);
                break;
            case R.id.ivAd1:
                //adClickFir();
                break;
            case R.id.ivAd2:
                //adClickSec();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    tvMoney.setText(coin);
                    tvMoneyA.setText(AppConfig.getInstance().getUserBean().getCommission());
                }
            }
        });

        HttpUtil.getMDBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200 && null != info && info.length > 0) {
                    MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
                    if (null != parse) {
                        AppConfig.getInstance().setMdBaseDataBean(parse);
                        tvFreezeMoney.setText(parse.getMaodouFrozen());
                    }
                }
            }
        });

    }
}
