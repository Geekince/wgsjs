package com.wwsl.wgsj.dialog;

import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.ToastUtil;

public class InputInviteDialog extends BasePopupView {


    private EditText etCode;

    public InputInviteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_input_invate;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        etCode = findViewById(R.id.etInvitedCode);

        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            String content = etCode.getText().toString().trim();
            HttpUtil.setInvitation(content, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        AppConfig.getInstance().getUserBean().setIsHaveCode("1");
                        ToastUtil.show("绑定成功");
                        dismiss();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        });
        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dismiss();
        });

    }
}
