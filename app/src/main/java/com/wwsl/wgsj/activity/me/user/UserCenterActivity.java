package com.wwsl.wgsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.HtmlConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.HuoYueActivity;
import com.wwsl.wgsj.activity.UserPartnerActivity;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.activity.maodou.FamilyActivity;
import com.wwsl.wgsj.activity.maodou.MaoDouFoundationActivity;
import com.wwsl.wgsj.activity.maodou.MdStallP2PActivity;
import com.wwsl.wgsj.activity.me.QRShareActivity;
import com.wwsl.wgsj.activity.me.SettingActivity;
import com.wwsl.wgsj.activity.me.UserVipActivity;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.bean.UserBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.BaseEncryptHelper;
import com.wwsl.wgsj.utils.ToastUtil;

import java.util.HashMap;

import cn.hutool.core.util.StrUtil;

public class UserCenterActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnHy;
    private ImageView btnDouding;
    private ImageView btnGift;
    private ImageView btnVipCenter;
    private ImageView btnMdFoundation;
    private ImageView btnMarket;
    private ImageView btnFamily;
    private ConstraintLayout shopLayout;
    private ConstraintLayout decorateLayout;
    private ConstraintLayout qrLayout;
    private ConstraintLayout partnerLayout;
    private ConstraintLayout settingLayout;
    private ConstraintLayout exchangeLayout;
    private ConstraintLayout serverLayout;
    private ConstraintLayout znApplyLayout;
    //    private KfStartHelper helper;
    private UserBean userBean;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_center;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
//        helper = new KfStartHelper(this);
        btnHy = findViewById(R.id.btnHy);
        btnDouding = findViewById(R.id.btnDouding);
        btnGift = findViewById(R.id.btnGift);
        btnVipCenter = findViewById(R.id.btnVipCenter);
        btnMdFoundation = findViewById(R.id.btnMdFoundation);
        btnMarket = findViewById(R.id.btnMarket);
        btnFamily = findViewById(R.id.btnFamily);
        shopLayout = findViewById(R.id.shopLayout);
        decorateLayout = findViewById(R.id.decorateLayout);
        qrLayout = findViewById(R.id.qrLayout);
        partnerLayout = findViewById(R.id.partnerLayout);
        settingLayout = findViewById(R.id.settingLayout);
        exchangeLayout = findViewById(R.id.exchangeLayout);
        serverLayout = findViewById(R.id.serverLayout);
        znApplyLayout = findViewById(R.id.znApplyLayout);
        btnHy.setOnClickListener(this);
        btnDouding.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnVipCenter.setOnClickListener(this);
        btnMdFoundation.setOnClickListener(this);
        btnMarket.setOnClickListener(this);
        btnFamily.setOnClickListener(this);
        shopLayout.setOnClickListener(this);
        decorateLayout.setOnClickListener(this);
        qrLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        partnerLayout.setOnClickListener(this);
        serverLayout.setOnClickListener(this);
        exchangeLayout.setOnClickListener(this);
        znApplyLayout.setOnClickListener(this);
        findViewById(R.id.idLayout).setOnClickListener(this);

        userBean = AppConfig.getInstance().getUserBean();
//        exchangeLayout.setVisibility(userBean.getCanTransMd() == 1 ? View.VISIBLE : View.GONE);
//        serverLayout.setVisibility("1".equals(AppConfig.getInstance().getConfig().getIsOpenKf()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHy:
                HuoYueActivity.forward(this);
                break;
            case R.id.btnDouding:
                MyBalanceActivity.forward(this);
                break;
            case R.id.btnGift:
                UserGiftActivity.forward(this);
                break;
            case R.id.btnVipCenter:
                UserVipActivity.forward(this);
                break;
            case R.id.btnMdFoundation:
                MaoDouFoundationActivity.forward(this);
                break;
            case R.id.btnMarket:
                int tradeType = AppConfig.getInstance().getConfig().getTradeType();
                if (tradeType == 99) {
                    ToastUtil.show("集市暂未开放");
                } else if (tradeType == 12 || tradeType == 11 || tradeType == 21) {
                    MdStallP2PActivity.forward(this);
                }
//                MdStallP2PActivity.forward(this);
                break;
            case R.id.btnFamily:
                FamilyActivity.forward(this);
                break;
            case R.id.shopLayout:
                String marketUrl = AppConfig.getInstance().getMarketUrl();
                WebViewActivity.forward3(this, marketUrl, 1);
                break;
            case R.id.decorateLayout:
                WebViewActivity.forward(this, HtmlConfig.WEB_LINK_DECORATION);
                break;
            case R.id.partnerLayout:
                UserPartnerActivity.forward(this);
                break;
            case R.id.exchangeLayout:
                FriendExchangeActivity.forward(this);
                break;
            case R.id.qrLayout:
                openQrShareActivity();
                break;
            case R.id.settingLayout:
                SettingActivity.forward(this);
                break;
            case R.id.serverLayout:
                openServer();
                break;
            case R.id.znApplyLayout:
                openZnApply();
                break;
            case R.id.idLayout://实名认证
                UserIdentifyActivity.forward(this);
//                if (AppConfig.getInstance().getUserBean().getIsIdIdentify() != 1) {
//                    UserIdentifyActivity.forward(this);
//                } else {
//                    ToastUtil.show("已实名");
//                }
                break;
        }
    }

    private void openZnApply() {
        String znUrl = AppConfig.getInstance().getUserBean().getZnUrl();
        if (!StrUtil.isEmpty(znUrl)) {
            WebViewActivity.forward2(this, znUrl);
        }
    }


    private void openServer() {
        HashMap<String, String> updateInfo = new HashMap<>();
        updateInfo.put("name", AppConfig.getInstance().getUserBean().getUsername());
        updateInfo.put("tel", AppConfig.getInstance().getUserBean().getMobile());
        updateInfo.put("avatar", AppConfig.getInstance().getUserBean().getAvatar());
        Intent intent = new MQIntentBuilder(this)
                .setCustomizedId(Constants.MQ_PREFIX + AppConfig.getInstance().getUid())
                .updateClientInfo(updateInfo)
                .build();
        startActivity(intent);

//        helper.initSdkChat("660f8c60-dd3d-11ea-a34f-e79e324f5387", AppConfig.getInstance().getUserBean().getUsername(), AppConfig.getInstance().getUid());
    }

    private void openQrShareActivity() {
        if (AppConfig.getInstance().getUserBean().getIsIdIdentify() > 0) {
            showLoadCancelable(false, "获取数据中...");
            HttpUtil.getUserRealInfo(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    dismissLoad();
                    String name = null;
                    if (code == 0 && info != null && info.length > 0) {
                        String str = info[0];
                        String decode = BaseEncryptHelper.decode(str);
                        if (!StrUtil.isEmpty(decode)) {
                            String[] data = decode.split("-");
                            if (data.length >= 2) {
                                name = data[0];
                            }
                        }
                    }

                    if (!StrUtil.isEmpty(name)) {
                        QRShareActivity.forward(UserCenterActivity.this, userBean.getAvatar(), name, userBean.getTgCode());
                    } else {
                        QRShareActivity.forward(UserCenterActivity.this, userBean.getAvatar(), userBean.getUsername(), userBean.getTgCode());
                    }
                }

                @Override
                public void onError() {
                    dismissLoad();
                    QRShareActivity.forward(UserCenterActivity.this, userBean.getAvatar(), userBean.getUsername(), userBean.getTgCode());
                }
            });
        } else {
            QRShareActivity.forward(this, userBean.getAvatar(), userBean.getUsername(), userBean.getTgCode());
        }
    }

    @Override
    protected void onResume() {
        HttpUtil.getMDBaseInfo();
        super.onResume();
    }
}
