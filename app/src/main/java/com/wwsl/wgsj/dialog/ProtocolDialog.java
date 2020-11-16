package com.wwsl.wgsj.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.views.dialog.OnDialogCallBackListener;

public class ProtocolDialog extends BasePopupView implements View.OnClickListener {


    private TextView txCancel;
    private TextView txDone;
    private TextView tvUserPro;
    private TextView tvPrivatePro;
    private OnDialogCallBackListener listener;
    private boolean isCancelable = true;

    public ProtocolDialog(@NonNull Context context,OnDialogCallBackListener listener) {
        super(context);
        this.listener = listener;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_protocol_update;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        tvUserPro = findViewById(R.id.tv_content_temp_2);
        tvPrivatePro = findViewById(R.id.tv_content_temp_4);
        txCancel = findViewById(R.id.tvCancel);
        txDone = findViewById(R.id.tvDone);
        txCancel.setOnClickListener(this);
        txDone.setOnClickListener(this);
        tvUserPro.setOnClickListener(this);
        tvPrivatePro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_content_temp_2:
                if (listener != null) {
                    listener.onDialogViewClick(null, 0);
                }
                break;
            case R.id.tv_content_temp_4:
                if (listener != null) {
                    listener.onDialogViewClick(null, 1);
                }
                break;
            case R.id.tvCancel:
                dismiss();
                if (listener != null) {
                    listener.onDialogViewClick(null, 2);
                }
                break;
            case R.id.tvDone:
                dismiss();
                if (listener != null) {
                    listener.onDialogViewClick(null, 3);
                }
                break;
        }
    }
}
