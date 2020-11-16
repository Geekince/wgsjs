package com.wwsl.wgsj.activity.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.frame.fire.util.LogUtils;
import com.mob68.ad.RewardVideoAd;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.UserHomePageActivity;
import com.wwsl.wgsj.activity.message.ChatActivity;
import com.wwsl.wgsj.activity.message.MessageSecondActivity;
import com.wwsl.wgsj.activity.message.SysMessageActivity;
import com.wwsl.wgsj.adapter.MsgShortAdapter;
import com.wwsl.wgsj.bean.AdvertiseBean;
import com.wwsl.wgsj.bean.MsgShortBean;
import com.wwsl.wgsj.bean.net.MsgConservationNetBean;
import com.wwsl.wgsj.bean.net.RecommendUserBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.im.ImMessageUtil;
import com.wwsl.wgsj.interfaces.CommonCallback;
import com.wwsl.wgsj.utils.OutAdListener;
import com.wwsl.wgsj.views.AbsMainViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wwsl.wgsj.adapter.MsgShortAdapter.PAYLOAD_FOLLOW;

public class MainMessageViewHolder extends AbsMainViewHolder implements View.OnClickListener {

    private ImageView btnLike;
    private ImageView btnComment;
    private ImageView btnAtMe;
    private ImageView btnFans;
    private SwipeRecyclerView msgRecycler;
    private SmartRefreshLayout mRefreshLayout;
    private MsgShortAdapter msgShortAdapter;
    private List<MsgShortBean> beans;
    private List<MsgShortBean> recommendUser;
    private int recommendUserNum = 0;//用于刷新推荐用户
    private int mPage = 1;
    private int needRefreshData = 0;
    private RewardVideoAd mRewardVideoAd;

    public MainMessageViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnLike:
                forwardSecond(Constants.TYPE_LIKE);
                break;
            case R.id.btnDouding:
                forwardSecond(Constants.TYPE_COMMENT);
                break;
            case R.id.btnGift:
                forwardSecond(Constants.TYPE_AT_ME);
                break;
            case R.id.btnVipCenter:
                forwardSecond(Constants.TYPE_FANS);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_message;
    }

    @Override
    public void init() {
        findView();
        beans = new ArrayList<>();
        recommendUser = new ArrayList<>();

        msgShortAdapter = new MsgShortAdapter(beans);
        msgRecycler.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

        msgShortAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()) {
                    case R.id.imgRemove:
                        msgShortAdapter.removeAt(position);
                        recommendUserNum--;
                        break;
                    case R.id.btnFollow:
                        MsgShortBean msgShortBean = msgShortAdapter.getData().get(position);
                        HttpUtil.setAttention(Constants.FOLLOW_FROM_MAIN_MSG, msgShortBean.getUid(), new CommonCallback<Integer>() {
                            @Override
                            public void callback(Integer bean) {
                                if (bean == 1) {
                                    msgShortAdapter.getData().get(position).setFollow(true);
                                    msgShortAdapter.notifyItemChanged(position, PAYLOAD_FOLLOW);
                                }
                            }
                        });
                        break;
                }
            }
        });

        msgShortAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MsgShortBean msgShortBean = msgShortAdapter.getData().get(position);
                if (msgShortBean.getType() == Constants.MESSAGE_TYPE_MSG) {
                    if (msgShortBean.getSubType() >= 1 && msgShortBean.getSubType() <= 4) {
                        SysMessageActivity.forward(mContext, msgShortBean.getSubType());
                        if (!msgShortBean.getUnreadNum().equals("0")) {
                            HttpUtil.readSystemMessageList(msgShortBean.getSubType());
                            msgShortAdapter.notifyItemChanged(position, MsgShortAdapter.PAYLOAD_UNREAD_CLEAR);
                        }
                    } else if (msgShortBean.getSubType() == Constants.MESSAGE_SUBTYPE_FRIEND) {
                        ChatActivity.forward(mContext);
                    }
                } else if (msgShortBean.getType() == Constants.MESSAGE_TYPE_TEXT_RECOMMEND) {
                    UserHomePageActivity.forward(mContext, msgShortBean.getUid());
                } else if (msgShortBean.getType() == Constants.MESSAGE_TYPE_AD) {
                    if (mRewardVideoAd.isReady()) {
                        mRewardVideoAd.showAd();
                    } else {
                        LogUtils.e(TAG, "mRewardVideoAd: 还未获取到广告，请在启动app时就授权，并提前初始化");
                    }

//                    if (!StrUtil.isEmpty(msgShortBean.getAdUrl())) {
//                        WebViewActivity.forward(mContext, msgShortBean.getAdUrl());
//                    }
                }
            }
        });

        msgRecycler.setAdapter(msgShortAdapter);
        mRewardVideoAd = new RewardVideoAd(mContext, "2582", "3736", "sMeFqicE", new OutAdListener("消息"));
    }

    private final static String TAG = "MainMessageViewHolder";

    private void findView() {
        btnLike = (ImageView) findViewById(R.id.btnLike);
        btnComment = (ImageView) findViewById(R.id.btnDouding);
        btnAtMe = (ImageView) findViewById(R.id.btnGift);
        btnFans = (ImageView) findViewById(R.id.btnVipCenter);
        msgRecycler = (SwipeRecyclerView) findViewById(R.id.msgRecycler);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        mRefreshLayout.setRefreshHeader(new MaterialHeader(mContext));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        mRefreshLayout.setOnRefreshListener(this::refreshData);
//        mRefreshLayout.setOnLoadMoreListener(this::loadMoreData);
        btnLike.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnAtMe.setOnClickListener(this);
        btnFans.setOnClickListener(this);
    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        mPage++;
        loadRecommendUser(false);
    }

    private void refreshData(RefreshLayout refreshLayout) {
        mPage = 1;
        if (isFirstLoadData()) {
            initData();
        } else {
            loadData();
        }
    }

    /**
     * 我的粉丝
     */
    private void forwardSecond(int type) {
        MessageSecondActivity.forward(mContext, type, AppConfig.getInstance().getUid());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefreshData == 0) {
            needRefreshData = 10;
            mRefreshLayout.autoRefresh();
        } else {
            needRefreshData--;
        }
    }


    public void initData() {
        beans.clear();
        HttpUtil.getSystemMsgConservationList(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    //加载系统消息
                    List<MsgConservationNetBean> list = JSON.parseArray(Arrays.toString(info), MsgConservationNetBean.class);
                    msgShortAdapter.addData(MsgConservationNetBean.parse(list));
                    //我的好友item
                    msgShortAdapter.addData(MsgShortBean.builder().type(Constants.MESSAGE_TYPE_MSG).subType(Constants.MESSAGE_SUBTYPE_FRIEND).unreadNum(ImMessageUtil.getInstance().getAllUnReadMsgCount()).build());

                    //广告item
                    List<AdvertiseBean> adInnerList = AppConfig.getInstance().getConfig().getAdInnerList();
                    if (null != adInnerList && adInnerList.size() > 0) {
                        MsgShortBean adBean = new MsgShortBean();
                        adBean.setType(Constants.MESSAGE_TYPE_AD);
                        adBean.setAdThumb(adInnerList.get(0).getThumb());
                        adBean.setAdUrl(adInnerList.get(0).getUrl());
                        msgShortAdapter.addData(adBean);
                    }

                    if (msgShortAdapter.getData().size() > 0) {
                        //获取推荐用户
                        loadRecommendUser(true);
                    } else {
                        mRefreshLayout.finishRefresh();
                    }
                }
            }

            @Override
            public void onError() {
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void loadData() {
        //更新系统消息列表
        HttpUtil.getSystemMsgConservationList(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<MsgConservationNetBean> list = JSON.parseArray(Arrays.toString(info), MsgConservationNetBean.class);
                for (int i = 0; i < list.size(); i++) {
                    boolean update = false;
                    for (int j = 0; j < beans.size(); j++) {
                        if (beans.get(j).getType() == Constants.MESSAGE_TYPE_MSG && beans.get(j).getSubType() == list.get(i).getType()) {
                            beans.get(j).setContent(list.get(i).getContent());
                            beans.get(j).setTime(list.get(i).getTime());
                            beans.get(j).setUnreadNum(String.valueOf(list.get(i).getUnreadNum()));
                            msgShortAdapter.notifyItemChanged(j, MsgShortAdapter.PAYLOAD_UNREAD_UPDATE);
                            update = true;
                            break;
                        }
                    }
                    if (!update) {
                        //新的系统通知默认加到第一个
                        msgShortAdapter.addData(1, MsgConservationNetBean.parse(list.get(i)));
                    }
                }

                //加载5次 刷新一次推荐列表
                if (recommendUserNum == 0) {
                    loadRecommendUser(true);
                } else {
                    if (msgShortAdapter.getData().size() > 0) {
                        msgRecycler.scrollToPosition(0);
                    }
                    mRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void onError() {
                mRefreshLayout.finishRefresh();
            }
        });
    }

    private void loadRecommendUser(boolean isFresh) {
        HttpUtil.getRecommendUser(mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<RecommendUserBean> list = JSON.parseArray(Arrays.toString(info), RecommendUserBean.class);

                if (isFresh) {
                    //刷新列表
                    int lastIndex = msgShortAdapter.getData().size() - 1;
                    //去除之前加载的推荐用户
                    for (int i = 0; i < recommendUserNum; i++) {
                        msgShortAdapter.removeAt((lastIndex - i));
                    }

                    recommendUserNum = list.size();

                    if (list.size() > 0) {
                        //添加推荐关注标签
                        int size = msgShortAdapter.getData().size();
                        if (msgShortAdapter.getData().get(size - 1).getType() != Constants.MESSAGE_TYPE_TEXT_LABEL) {
                            msgShortAdapter.addData(MsgShortBean.builder().type(Constants.MESSAGE_TYPE_TEXT_LABEL).subType(0).build());
                        }
                    }
                    mRefreshLayout.finishRefresh();
                } else {
                    recommendUserNum += list.size();
                    mRefreshLayout.finishLoadMore();
                }

                List<MsgShortBean> recUser = new ArrayList<>();
                for (RecommendUserBean user : list) {
                    recUser.add(MsgShortBean.builder()
                            .type(Constants.MESSAGE_TYPE_TEXT_RECOMMEND)
                            .uid(user.getUid())
                            .age(user.getAge())
                            .originDes(user.getOriginDes())
                            .avatar(user.getAvatar())
                            .sex(user.getSex())
                            .name(user.getName())
                            .city(user.getCity())
                            .build());
                }


                recommendUser.addAll(recUser);
                msgShortAdapter.addData(recUser);

            }

            @Override
            public void onError() {
                if (isFresh) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadMore();
                }
            }
        });
    }


    public void updateUnreadNum(String num) {
        int index = -1;
        List<MsgShortBean> data = msgShortAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getType() == Constants.MESSAGE_TYPE_MSG && data.get(i).getSubType() == Constants.MESSAGE_SUBTYPE_FRIEND) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            msgShortAdapter.getData().get(index).setUnreadNum(num);
            msgShortAdapter.notifyItemChanged(index, MsgShortAdapter.PAYLOAD_UNREAD_UPDATE);
        }
    }
}
