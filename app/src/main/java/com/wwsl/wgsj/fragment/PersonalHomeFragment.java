package com.wwsl.wgsj.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.lxj.xpopup.XPopup;
import com.permissionx.guolindev.PermissionX;
import com.wang.avi.AVLoadingIndicatorView;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.message.ChatRoomActivity;
import com.wwsl.wgsj.adapter.CommPagerAdapter;
import com.wwsl.wgsj.base.BaseFragment;
import com.wwsl.wgsj.bean.UserBean;
import com.wwsl.wgsj.bean.UserDetailBean;
import com.wwsl.wgsj.bean.UserLabelBean;
import com.wwsl.wgsj.custom.DrawableTextView;
import com.wwsl.wgsj.event.FollowEvent;
import com.wwsl.wgsj.event.MainPageChangeEvent;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.interfaces.CommonCallback;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.DpUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.tiktok.NumUtils;
import com.wwsl.wgsj.views.CircleImageView;
import com.wwsl.wgsj.views.IconFontTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.hutool.core.util.StrUtil;


/**
 * @author :
 * @date : 2020/6/17 15:51
 * @description : 个人主页fragment
 */
public class PersonalHomeFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.rl_dropdown)
    RelativeLayout rlDropdown;
    @BindView(R.id.ll_focus)
    LinearLayout llFocus;
    @BindView(R.id.ll_fans)
    LinearLayout llFans;
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_focus)
    TextView tvFocus;
    @BindView(R.id.tvCopy)
    TextView tvCopy;
    @BindView(R.id.iv_more)
    IconFontTextView ivMore;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.appbarlayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.tv_getlike_count)
    TextView tvGetLikeCount;
    @BindView(R.id.tv_focus_count)
    TextView tvFocusCount;
    @BindView(R.id.txMessage)
    TextView txMessage;
    @BindView(R.id.tv_fans_count)
    TextView tvFansCount;
    @BindView(R.id.tv_addfocus)
    TextView tvAddFocus;

    @BindView(R.id.tvUid)
    TextView tvUid;

    @BindView(R.id.userTagLinear)
    LinearLayout tagLinear;

    @BindView(R.id.tvPhone)
    DrawableTextView tvPhone;

    @BindView(R.id.goodsLayout)
    ConstraintLayout goodsLayout;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;

    private ArrayList<WorkFragment> fragments = new ArrayList<>();
    private CommPagerAdapter pagerAdapter;
    private UserDetailBean curUserBean;
    private String userId = "";


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_personal_home;
    }

    @Override
    protected void init() {

        //解决toolbar左边距问题
        toolbar.setContentInsetsAbsolute(0, 0);

        setAppbarLayoutPercent();

        ivReturn.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        llFocus.setOnClickListener(this);
        llFans.setOnClickListener(this);
        tvAddFocus.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        txMessage.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
        tvUid.setOnClickListener(this);
        tvPhone.setOnClickListener(this);

        initTabLayout();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initialData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void loadUserInfo() {
        avi.show();
        HttpUtil.getUserHome(userId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                avi.hide();
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    curUserBean = JSON.toJavaObject(obj, UserDetailBean.class);
                    if ("0".equals(curUserBean.getId())) {
                        ToastUtil.show("用户不存在");
                        showErrorPage();
                    } else {
                        ImgLoader.display(curUserBean.getAvatar(), ivBg);
                        ImgLoader.display(curUserBean.getAvatar(), ivHead);
                        tvUid.setText(String.format("ID:%s", curUserBean.getId()));
                        tvNickname.setText(curUserBean.getUsername());
                        tvSign.setText(curUserBean.getSignature());
                        tvTitle.setText(curUserBean.getUsername());
                        String subCount = NumUtils.numberFilter2(curUserBean.getDzNames());
                        String focusCount = NumUtils.numberFilter2(curUserBean.getFollows());
                        String fansCount = NumUtils.numberFilter2(curUserBean.getFans());

                        //获赞 关注 粉丝
                        tvGetLikeCount.setText(subCount);
                        tvFocusCount.setText(focusCount);
                        tvFansCount.setText(fansCount);

                        //关注状态
                        onAttention(curUserBean.getAttention());
                        tvPhone.setText(curUserBean.getMobile());
                        tvPhone.setVisibility(1 == curUserBean.getIsPhonePublic() ? View.VISIBLE : View.GONE);
                        updateProduct();
                        updateTags();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                avi.hide();
                showErrorPage();
            }
        });
    }

    private void showErrorPage() {
//        tvAddFocus.setClickable(false);
//        txMessage.setClickable(false);
//        ivMore.setClickable(false);
//        tvFocus.setClickable(false);
    }


    private void updateTags() {
        tagLinear.removeAllViews();
        List<UserLabelBean> labels = curUserBean.getLabel();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (UserLabelBean label : labels) {
            TextView textview = new TextView(getContext());
            textview.setText(label.getLabel());
            textview.setPadding(5, 2, 5, 2);
            textview.setTextColor(getResources().getColor(R.color.color_tag));
            textview.setTextSize(12);
            textview.setMaxWidth(DpUtil.dp2px(150));
            textview.setBackgroundResource(R.drawable.shape_round_halfwhite);
            textview.setLines(1);
            textview.setEllipsize(TextUtils.TruncateAt.END);
            layoutParams.setMarginStart(5);
            textview.setLayoutParams(layoutParams);
            tagLinear.addView(textview);
        }
        tagLinear.setVisibility(View.VISIBLE);
    }


    private void onAttention(int attention) {
        if (attention == 1) {
            tvAddFocus.setText("已关注");
            tvAddFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
            tvFocus.setText("已关注");
            tvFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
        } else {
            tvAddFocus.setText("关注");
            tvAddFocus.setBackgroundResource(R.drawable.shape_round_red);
            tvFocus.setText("关注");
            tvFocus.setBackgroundResource(R.drawable.shape_round_red);
        }
    }

    public void updateUserInfo(String uid) {
        if (StrUtil.isEmpty(uid)) return;

        clearUI();
        coordinatorLayoutBackTop();
        if (StrUtil.isEmpty(uid) && userId.equals(uid)) return;
        this.userId = uid;
        loadUserInfo();
    }

    private void clearUI() {
        ImgLoader.display(R.mipmap.icon_avatar_placeholder, ivBg);
        ImgLoader.display(R.mipmap.img_default_bg, ivHead);

        tvUid.setText(String.format("ID:%s", "0"));

        tvNickname.setText("");
        tvSign.setText("");
        tvTitle.setText("");

        String subCount = NumUtils.numberFilter2(0);
        String focusCount = NumUtils.numberFilter2(0);
        String fansCount = NumUtils.numberFilter2(0);

        //获赞 关注 粉丝
        tvGetLikeCount.setText(subCount);
        tvFocusCount.setText(focusCount);
        tvFansCount.setText(fansCount);

        //关注状态
        tvAddFocus.setText("已关注");
        tvAddFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
        tvFocus.setText("已关注");
        tvFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);

        tvPhone.setText("");
        tvPhone.setVisibility(View.GONE);
        clearProduct();
        tagLinear.setVisibility(View.INVISIBLE);
    }

    private void updateProduct() {
        List<String> titles = new ArrayList<>();
        titles.add("作品" + NumUtils.numberFilter2(curUserBean.getVideoNum()));
        titles.add("动态" + NumUtils.numberFilter2(curUserBean.getTrends()));
        titles.add("喜欢" + NumUtils.numberFilter2(curUserBean.getLikeVideoNum()));

        for (int i = 0; i < 3; i++) {
            tabLayout.getTitleView(i).setText(titles.get(i));
            fragments.get(i).setNewData(curUserBean.getId());
        }

        fragments.get(0).loadData();

        tabLayout.setCurrentTab(0);
    }

    private void clearProduct() {
        List<String> titles = new ArrayList<>();
        titles.add("作品" + NumUtils.numberFilter2(0));
        titles.add("动态" + NumUtils.numberFilter2(0));
        titles.add("喜欢" + NumUtils.numberFilter2(0));

        for (int i = 0; i < 3; i++) {
            tabLayout.getTitleView(i).setText(titles.get(i));
            fragments.get(i).clearData();
        }

        tabLayout.setCurrentTab(0);
    }

    /**
     * 过渡动画跳转页面
     *
     * @param view
     */
    public void transitionAnim(ImageView view, String res) {
        new XPopup.Builder(mContext)
                .asImageViewer(view, res, new ImgLoader())
                .show();
    }

    private void initTabLayout() {
        String[] titles = new String[3];
        titles[0] = "作品0";
        titles[1] = "动态0";
        titles[2] = "喜欢0";

        int i = 1;
        fragments.clear();
        List<String> tList = new ArrayList<>();
        for (String title : titles) {
            fragments.add(WorkFragment.newInstance("" + (i++), ""));
            tList.add(title);
        }


        pagerAdapter = new CommPagerAdapter(getChildFragmentManager(), fragments, tList);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setViewPager(viewPager, titles);
    }

    /**
     * 根据滚动比例渐变view
     */
    private void setAppbarLayoutPercent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());  //滑动比例

            if (percent > 0.8) {
                tvTitle.setVisibility(View.VISIBLE);
                tvFocus.setVisibility(View.VISIBLE);

                float alpha = 1 - (1 - percent) * 5;  //渐变变换
                tvTitle.setAlpha(alpha);
                tvFocus.setAlpha(alpha);
            } else {
                tvTitle.setVisibility(View.GONE);
                tvFocus.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 自动回顶部
     */
    private void coordinatorLayoutBackTop() {
        CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.setTopAndBottomOffset(0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCopy:
            case R.id.tvUid:
                if (StrUtil.isEmpty(userId) || null == curUserBean) return;
                CommonUtil.copyText(mContext, curUserBean.getId());
                break;
            case R.id.iv_return:
                EventBus.getDefault().post(new MainPageChangeEvent(-1, 0));
                break;
            case R.id.iv_head:
                transitionAnim(ivHead, curUserBean.getAvatar());
                break;
            case R.id.iv_bg:
                break;
            case R.id.tvPhone:
                callUser();
                break;
            case R.id.ll_focus:
            case R.id.ll_fans:
//                startActivity(new Intent(getActivity(), FollowActivity.class));
                break;
            case R.id.tv_addfocus:
                followUser();
                break;
            case R.id.iv_more:
                openReport();
                break;
            case R.id.txMessage:
                if (StrUtil.isEmpty(userId) || null == curUserBean) return;
                ChatRoomActivity.forward(getContext(), UserBean.builder().id(curUserBean.getId()).avatar(curUserBean.getAvatar()).username(curUserBean.getUsername()).build(), curUserBean.getAttention() == 1);
                break;
        }
    }

    private void callUser() {


        String phone = curUserBean.getMobile();
        if (!StrUtil.isEmpty(phone)) {
            PermissionX.init(getActivity())
                    .permissions(Manifest.permission.CALL_PHONE)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("tel:" + phone));
                            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.show("未授权无法拨打电话");
                        }
                    });
        }
    }

    private void openReport() {

    }

    private void followUser() {
        if (StrUtil.isEmpty(userId) || curUserBean == null) return;
        HttpUtil.setAttention(Constants.FOLLOW_FROM_HOME, userId, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                onAttention(isAttention);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e.getToUid().equals(userId)) {
            onAttention(e.getIsAttention());
        }
    }

}
