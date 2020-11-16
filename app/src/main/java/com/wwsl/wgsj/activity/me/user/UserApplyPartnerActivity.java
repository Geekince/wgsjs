package com.wwsl.wgsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.luck.picture.lib.entity.LocalMedia;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.CommonSuccessActivity;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.base.OnOpenAlbumResultListener;
import com.wwsl.wgsj.bean.ConfigBean;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.interfaces.CommonCallback;
import com.wwsl.wgsj.upload.PictureUploadCallback;
import com.wwsl.wgsj.upload.PictureUploadQnImpl;
import com.wwsl.wgsj.utils.FileUriHelper;
import com.wwsl.wgsj.utils.StringUtil;
import com.wwsl.wgsj.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserApplyPartnerActivity extends BaseActivity {
    private FileUriHelper fileUriHelper;
    private int type = 0;
    private String address;
    private EditText editRealName;
    private EditText editPhone;
    private EditText editIdCard;
    private ImageView ivFront;
    private ImageView ivBack;
    private com.rey.material.widget.EditText etRemark;
    private LocalMedia frontImg;
    private LocalMedia backImg;
    private String frontUrl;
    private String backUrl;

    private ConfigBean mConfigBean;
    private PictureUploadQnImpl nPictureUploadStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_apply_partner;
    }

    @Override
    protected void init() {
        type = getIntent().getIntExtra("type", 0);
        address = getIntent().getStringExtra("address");
        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
            }
        });
        fileUriHelper = new FileUriHelper(this);
        initView();

        this.openAlbumResultListener = new OnOpenAlbumResultListener() {
            @Override
            public void onResult(int requestCode, List<LocalMedia> result) {
                if (result != null && result.size() > 0) {
                    if (requestCode == REQUEST_FRONT) {
                        frontImg = result.get(0);
                        ImgLoader.display(frontImg.getPath(), ivFront);
                    } else if (requestCode == REQUEST_BACK) {
                        backImg = result.get(0);
                        ImgLoader.display(backImg.getPath(), ivBack);
                    }
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void forward(Context context, int type, String address) {
        Intent intent = new Intent(context, UserApplyPartnerActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        editRealName = findViewById(R.id.editRealName);
        editPhone = findViewById(R.id.editPhone);
        editIdCard = findViewById(R.id.editIdCard);
        ivFront = findViewById(R.id.ivFront);
        ivBack = findViewById(R.id.ivBack);
        etRemark = findViewById(R.id.etRemark);
    }

    private void submit() {

        String name = editRealName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = editPhone.getText().toString().trim();
        if (!StringUtil.isInteger(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        String idCard = editIdCard.getText().toString().trim();
        if (TextUtils.isEmpty(idCard)) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            return;
        }


        if (null == frontImg || null == backImg) {
            Toast.makeText(this, "请选择身份证照片", Toast.LENGTH_SHORT).show();
            return;
        }

        String remark = etRemark.getText().toString().trim();

        if (mConfigBean != null) {
            if (nPictureUploadStrategy == null) {
                nPictureUploadStrategy = new PictureUploadQnImpl(mConfigBean);
            }

            List<File> files = new ArrayList<>();

            files.add(new File(fileUriHelper.getFilePathByUri(Uri.parse(frontImg.getPath()))));
            files.add(new File(fileUriHelper.getFilePathByUri(Uri.parse(backImg.getPath()))));
            showLoadCancelable(false, "发布中...");
            nPictureUploadStrategy.upload(files, new PictureUploadCallback() {
                @Override
                public void onSuccess(String url) {
                    String[] urls = url.split(",");
                    if (urls.length >= 2) {
                        frontUrl = urls[0];
                        backUrl = urls[1];

                        if (TextUtils.isEmpty(frontUrl) || TextUtils.isEmpty(backUrl)) {
                            Toast.makeText(UserApplyPartnerActivity.this, "上传视频失败请稍后再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        HttpUtil.applyPartner(name, phone, idCard, frontUrl, backUrl, remark, type, address, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                dismissLoad();
                                if (code == 0) {
                                    ToastUtil.show(msg);
                                    CommonSuccessActivity.forward(UserApplyPartnerActivity.this, "成为代理", "提交成功，等待工作人员审核", Constants.SUCCESS_PAGE_TYPE_PARTNER);
                                    finish();
                                } else {
                                    ToastUtil.show(msg);
                                }
                            }

                            @Override
                            public void onError() {
                                super.onError();
                                dismissLoad();
                            }
                        });
                    }
                }

                @Override
                public void onFailure() {
                    ToastUtil.show("发布失败");
                    dismissLoad();
                }
            });
        }
    }


    public static final int REQUEST_FRONT = 1;
    public static final int REQUEST_BACK = 2;

    public void addFront(View view) {
        openAlbum(1, null, REQUEST_FRONT);
    }

    public void addBack(View view) {
        openAlbum(1, null, REQUEST_BACK);
    }

    private final static String TAG = "UserApplyPartnerActivity";

    public void doApply(View view) {
        submit();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    private void release() {
        HttpUtil.cancel(HttpConst.APPLY_PARTNER);
        if (nPictureUploadStrategy != null) {
            nPictureUploadStrategy.cancel();
        }
    }
}
