package com.wwsl.wgsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.HtmlConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.UserHomePageActivity;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.adapter.BasePagerAdapter;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.bean.UserBean;
import com.wwsl.wgsj.bean.net.MdBaseDataBean;
import com.wwsl.wgsj.dialog.SimpleCenterDialog;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.StringUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.tiktok.NumUtils;

import java.util.ArrayList;

import cn.hutool.core.util.StrUtil;

public class FamilyActivity extends BaseActivity {

    private SimpleCenterDialog simpleCenterDialog;//校对

    private RoundedImageView ivAvatar;
    private TextView tvName;
    private TextView num1;
    private TextView num2;
    private TextView num3;
    private TextView txFamilyNum;
    private SlidingTabLayout tabLayout;
    private int curIndex = 0;
    private ViewPager viewPager;
    private ArrayList<Fragment> mFragments;
    private BasePagerAdapter pagerAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_family;
    }

    @Override
    protected void init() {
        initView();
        initData();
        tabLayout.setCurrentTab(curIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


    private void loadData() {
        showLoad();
        HttpUtil.getMDBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 200 && null != info && info.length > 0) {
                    MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
                    if (null != parse) {
                        AppConfig.getInstance().setMdBaseDataBean(parse);

                        num1.setText(StringUtil.checkNullNumberStr(parse.getTeamActive()));
                        num2.setText(StringUtil.checkNullNumberStr(parse.getHeroActive()));
                        num3.setText(StringUtil.checkNullNumberStr(parse.getFamilyActive()));
                        txFamilyNum.setText(String.format("活跃好友：%s人", StringUtil.checkNullNumberStr(parse.getFriendCount())));


//                        String authCount = String.format("已认证(%s)", NumUtils.numberFilter2(StringUtil.checkNullNumberStr(parse.getAuthCount())));
//                        String notAuthCount = String.format("未认证(%s)", NumUtils.numberFilter2(StringUtil.checkNullNumberStr(parse.getNotAuthCount())));

//                        tabLayout.getTitleView(0).setText(authCount);
//                        tabLayout.getTitleView(1).setText(notAuthCount);
//
                        tabLayout.getTitleView(0).setText("已认证");
                        tabLayout.getTitleView(1).setText("未认证");

                    }
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

    private void initData() {

        String[] mTitles = new String[2];
//        mTitles[0] = String.format("已认证(%s)", "0");
//        mTitles[1] = String.format("未认证(%s)", "0");

        mTitles[0] = "已认证";
        mTitles[1] = "未认证";

        mFragments = new ArrayList<>(2);
        mFragments.add(FamilyListFragment.newInstance(0));
        mFragments.add(FamilyListFragment.newInstance(1));
        pagerAdapter = new BasePagerAdapter(getSupportFragmentManager(), mTitles, mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setViewPager(viewPager, mTitles);
        tabLayout.setCurrentTab(0);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }


    public void openShareWindow(View view) {

    }

    public void backClick(View view) {
        finish();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, FamilyActivity.class);
        context.startActivity(intent);
    }

    //校对
    public void proofread(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (simpleCenterDialog == null) {
            simpleCenterDialog = DialogUtil.getSimpleCenterDialog(this, R.mipmap.icon_tanhao, 60, 60, "此功能用于校准团队活跃度数值，每天 刷新次数有限，请慎重操作哦");
            simpleCenterDialog.setImgMargin(20, 0, 0, 0);
            simpleCenterDialog.setListener((view1, object) -> {

                showLoadCancelable(false, "校对中...");
                HttpUtil.activeProofread(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        dismissLoad();
                        if (code == 0 && info != null && info.length > 0) {

                            JSONObject obj = JSON.parseObject(info[0]);
                            String friendCount = String.valueOf(obj.get("friend_count"));
                            String hyAgentCount = String.valueOf(obj.get("huoyuecountagent"));
                            String heroActive = String.valueOf(obj.get("heroactive"));
                            String familyActive = String.valueOf(obj.get("family_active"));
                            String authCount = String.valueOf(obj.get("is_auth_count") == null ? 0 : obj.get("is_auth_count"));
                            String notAuthCount = String.valueOf(obj.get("not_auth_count") == null ? 0 : obj.get("not_auth_count"));


                            //必须检查 一个标点符号都不能信后端
                            if (!StrUtil.isEmpty(friendCount)) {
                                txFamilyNum.setText(String.format("活跃好友：%s人", friendCount));
                            }

                            if (!StrUtil.isEmpty(hyAgentCount)) {
                                num1.setText(hyAgentCount);
                            }

                            if (!StrUtil.isEmpty(heroActive)) {
                                num2.setText(heroActive);
                            }

                            if (!StrUtil.isEmpty(familyActive)) {
                                num3.setText(familyActive);
                            }

                            authCount = String.format("已认证(%s)", NumUtils.numberFilter2(authCount));
                            notAuthCount = String.format("未认证(%s)", NumUtils.numberFilter2(notAuthCount));
                            tabLayout.getTitleView(0).setText(authCount);
                            tabLayout.getTitleView(1).setText(notAuthCount);
                        }

                        ToastUtil.show(msg);
                    }

                    @Override
                    public void onError() {
                        dismissLoad();
                    }
                });
            });
        }
        simpleCenterDialog.show();
    }

    private void initView() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvName = findViewById(R.id.tvName);
        txFamilyNum = findViewById(R.id.txFamilyNum);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        UserBean userBean = AppConfig.getInstance().getUserBean();

        if (userBean.getParentInfo() != null && !StringUtil.isEmpty(userBean.getParentInfo().getId())) {
            ImgLoader.displayAvatar(userBean.getParentInfo().getAvatar(), ivAvatar);
            tvName.setText(String.format("族长：%s", userBean.getParentInfo().getUsername()));
        } else {
            ImgLoader.displayAvatar(userBean.getAvatar(), ivAvatar);
            tvName.setText(String.format("族长：%s", userBean.getUsername()));
        }

        ivAvatar.setOnClickListener(v -> {
            if (userBean.getParentInfo() != null && !StringUtil.isEmpty(userBean.getParentInfo().getId())) {
                UserHomePageActivity.forward(FamilyActivity.this, userBean.getParentInfo().getId());
            } else {
                UserHomePageActivity.forward(FamilyActivity.this, userBean.getId());
            }
        });

    }

    public void showDes(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_HY_DES);
    }


}
