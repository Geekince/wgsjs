package com.wwsl.wgsj.activity.me;

import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.WordUtil;

/**
 * Created by cxf on 2018/9/29.
 * 设置签名
 */

public class EditSignActivity extends AbsActivity implements View.OnClickListener {
    private EditText mEditText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_sign;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile_update_sign));
        mEditText = findViewById(R.id.edit);
        mEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(20)
        });
        findViewById(R.id.ivClear).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        String content = getIntent().getStringExtra(Constants.SIGN);
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > 20) {
                content = content.substring(0, 20);
            }
            mEditText.setText(content);
            mEditText.setSelection(content.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClear:
                mEditText.setText("");
                break;
            case R.id.btn_save:
                if (!canClick()) {
                    return;
                }
                final String content = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.edit_profile_sign_empty);
                    return;
                }

                HttpUtil.updateUserData(HttpConst.USER_INFO_SIGNATURE, content, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == HttpConst.SUCCESS) {
                            ToastUtil.show("修改成功");
                            AppConfig.getInstance().getUserBean().setSignature(content);
                            Intent intent = getIntent();
                            intent.putExtra(Constants.SIGN, content);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
                break;
        }
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.UPDATE_FIELDS);
        super.onDestroy();
    }
}
