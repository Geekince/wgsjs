package com.wwsl.wgsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.bean.ShareBean;
import com.wwsl.wgsj.dialog.LiveShareDialogFragment;
import com.wwsl.wgsj.fragment.ShareDialog;
import com.wwsl.wgsj.share.ShareHelper;
import com.wwsl.wgsj.utils.BitmapUtil;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.FileUtil;
import com.wwsl.wgsj.utils.SnackBarUtil;
import com.wwsl.wgsj.views.dialog.OnDialogCallBackListener;

import java.io.File;

import cn.hutool.core.util.StrUtil;

/**
 * @author:
 * @date: 2020/5/7 10:00
 * @description : xxxxx
 */
public class QRShareActivity extends AbsActivity implements LiveShareDialogFragment.ActionListener {

    private ImageView ivLeftBack;
    private TextView tgCode;
    //    private TextView txName;
    private ImageView ivQrCode;
    private Bitmap mBitmap;
    private String content;
    //    private String avatarUrl;
    private String code;
    //    private String name;
//    private CircleImageView avatar;
//    private RelativeLayout tempLayout;
    private NestedScrollView tempLayout;

    public static void forward(Context context, String avatar, String name, String tgCode) {
        Intent intent = new Intent(context, QRShareActivity.class);
        intent.putExtra("avatar", avatar);
        intent.putExtra("name", name);
        intent.putExtra("tgCode", tgCode);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qr_code_card;
    }

    @Override
    protected void main() {
        getIntentData();
        ivLeftBack = findViewById(R.id.iv_left_back);
        ivQrCode = findViewById(R.id.iv_qrCode);
        tempLayout = findViewById(R.id.temp_content);
        tgCode = findViewById(R.id.tgCode);
//        avatar = findViewById(R.id.avatar);
//        txName = findViewById(R.id.name);
//        txName.setText(String.format("我是: %s", name));
//        ImgLoader.displayAvatar(AppConfig.getInstance().getUserBean().getAvatar(), avatar);
        tgCode.setText(String.format("邀请码:%s", code));
        showQRImage();
        ivLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.tvCopy).setOnClickListener(v -> {
            if (code.length() == 0) return;
            CommonUtil.copyText(mContext, code);
        });
    }

    private void getIntentData() {
        content = AppConfig.getInstance().getQRContent();
//        avatarUrl = getIntent().getStringExtra("avatar");
        code = getIntent().getStringExtra("tgCode");
//        name = getIntent().getStringExtra("name");
    }

    private void showQRImage() {
        mBitmap = CodeUtils.createImage(content, 280, 280, null);
//        mBitmap = CodeUtils.createImage(content, 280, 280, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ivQrCode.setImageBitmap(mBitmap);
    }


    /**
     * 打开分享窗口
     */
    ShareDialog shareDialog;

    public void openShareWindow(View view) {
        if (shareDialog == null) {
            shareDialog = new ShareDialog(1);
            shareDialog.setListener(new OnDialogCallBackListener() {
                @Override
                public void onDialogViewClick(View view, Object object) {
                    if (object instanceof ShareBean) {
                        ShareBean shareBean = (ShareBean) object;
                        if (shareBean.getType() == Constants.SAVE_LOCAL) {
                            //保存本地
                            Bitmap bitmap = captureView(tempLayout);
                            String str = BitmapUtil.getInstance().saveBitmap(bitmap);
                            FileUtil.saveImage(QRShareActivity.this, new File(str));
                            if (!StrUtil.isEmpty(str)) {
                                SnackBarUtil.ShortSnackbar(tempLayout, String.format("保存成功:位置[%s]", str), SnackBarUtil.Info).show();
                            }
                        } else {
                            Bitmap bitmap = captureView(tempLayout);
                            ShareHelper.shareTextWithImg(QRShareActivity.this, shareBean, bitmap);
                        }
                    }
                    shareDialog.dismiss();
                }
            });
        }


        shareDialog.show(getSupportFragmentManager(), "QRShareActivity");
    }

    @Override
    public void onItemClick(String type) {
        if (Constants.LINK.equals(type)) {

        } else {

        }
    }


    public Bitmap captureView(View view) {
        // 根据View的宽高创建一个空的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.RGB_565);
        // 利用该Bitmap创建一个空的Canvas
        Canvas canvas = new Canvas(bitmap);
        // 绘制背景(可选)
        canvas.drawColor(Color.WHITE);
        // 将view的内容绘制到我们指定的Canvas上
        view.draw(canvas);
        return bitmap;
    }
}

