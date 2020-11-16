package com.wwsl.wgsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.views.dialog.InputPwdDialog;
import com.wwsl.wgsj.views.dialog.OnDialogCallBackListener;

/**
 * @author :
 * @date : 2020/7/6 19:06
 * @description : 好友互转界面
 */
public class FriendExchangeActivity extends BaseActivity {

    private TextView tvMoney;
    private EditText etInput;
    private TextView tvMax;
    private EditText etUserId;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_friend_md_exchange;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initData() {
        tvMoney.setText(String.format("拥有:%s", AppConfig.getInstance().getUserBean().getMaodou()));
        tvMax.setText(String.format("最大可以转%s颗令牌", AppConfig.getInstance().getUserBean().getMaxTransNum()));
    }

    public void backClick(View view) {
        finish();
    }


    public void exchangeDone(View view) {
        new XPopup.Builder(FriendExchangeActivity.this)
                .hasShadowBg(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new InputPwdDialog(FriendExchangeActivity.this, "", new OnDialogCallBackListener() {
                    @Override
                    public void onDialogViewClick(View view, Object object) {
                        submit((String) object);
                    }
                }))
                .show();
    }


    public static void forward(Context context) {
        Intent intent = new Intent(context, FriendExchangeActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        tvMoney = findViewById(R.id.tvMoney);
        etInput = findViewById(R.id.etInput);
        tvMax = findViewById(R.id.tvMax);
        etUserId = findViewById(R.id.etUserId);
    }

    private void submit(String pwd) {
        // validate
        String money = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = etUserId.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "请输入用户Id(只能转账下级)", Toast.LENGTH_SHORT).show();
            return;
        }
        HttpUtil.transFriends(money, userId, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    HttpUtil.getBaseInfo(null);
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }
}
