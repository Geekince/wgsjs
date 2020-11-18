package com.wwsl.wgsj.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.maodou.ViewHistoryAdapter;
import com.wwsl.wgsj.bean.maodou.ViewVideoHistoryBean;
import com.wwsl.wgsj.bean.net.MdBaseDataBean;
import com.wwsl.wgsj.bean.net.NetTodayProcessBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.SpUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.views.AbsViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TodayViewHolder extends AbsViewHolder
    implements SwipeRecyclerView.LoadMoreListener {
  private ViewHistoryAdapter         adapter;
  private List<ViewVideoHistoryBean> data;
  private TextView                   tvTodaySettle;
  private TextView                   tvPercent;
  private SwipeRecyclerView          recycler;
  private int                        mPage      = 1;
  private ConstraintLayout           recyclerContainer;
  private ConstraintLayout           adLayout;
  private Button                     txAd;
  private boolean                    isAdFinish = false;
  private Activity                   mActivity;
  private int                        adTimes    = 0;

  public TodayViewHolder(Context context, ViewGroup parentView, Activity activity) {
    super(context, parentView);
    mActivity = activity;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_today;
  }

  @Override
  public void init() {
    recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
    tvTodaySettle = (TextView) findViewById(R.id.tvTodaySettle);
    recyclerContainer = (ConstraintLayout) findViewById(R.id.recyclerContainer);
    adLayout = (ConstraintLayout) findViewById(R.id.ad_layout);
    txAd = (Button) findViewById(R.id.txAd);

    tvPercent = (TextView) findViewById(R.id.tvPercent);
    data = new ArrayList<>();
    adapter = new ViewHistoryAdapter(new ArrayList<>());
    recycler.setLayoutManager(new LinearLayoutManager(mContext));
    recycler.setAdapter(adapter);
    adapter.setEmptyView(CommonUtil.getEmptyView("暂无观看记录", mContext, recyclerContainer));
    initData();
  }

  private void initData() {
    mPage = 1;

    initAdTimes();

    HttpUtil.getTodayRecord(new HttpCallback() {
      @Override
      public void onSuccess(int code, String msg, String[] info) {
        loadBack(code, msg, info, true);
      }
    });
    HttpUtil.getMDBaseInfo(new HttpCallback() {
      @Override
      public void onSuccess(int code, String msg, String[] info) {
        if (code == 200 && null != info && info.length > 0) {
          MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
          if (null != parse) {
            AppConfig.getInstance().setMdBaseDataBean(parse);
            tvTodaySettle.setText(String.format("已收令牌:%s", parse.getTodaySettlement()));
            tvPercent.setText(parse.getTodayRate());
          }
        }
      }

      @Override
      public void onError() {
        super.onError();
      }
    });

    txAd.setOnClickListener(v -> {
      RewardActivity.startActivity(mActivity);
    });
  }

  private void initAdTimes() {

    String finishTime = SpUtil.getInstance().getStringValue(Constants.AD_TAG_JILI_FINISH_TIME);

    String times = "0";
    if (!StrUtil.isEmpty(finishTime)) {
      long lTime = Long.parseLong(finishTime);
      DateTime date = DateUtil.date(lTime);
      DateTime now = DateUtil.date();
      long between = DateUtil.between(date, now, DateUnit.DAY, false);
      if (between == 0) {
        //当天
        int remain =
            Integer.parseInt(SpUtil.getInstance().getStringValue(Constants.AD_TAG_JILI_TIME));
        if (remain > 5) {
          remain = 5;
        }
        adTimes = remain;
        isAdFinish = (remain < 5);
        times = remain + "";
      } else if (between > 0) {
        //之前
        isAdFinish = false;
        times = "0";
        adTimes = 0;
      } else {
        //之后
        isAdFinish = true;
        times = "5";
        adTimes = 5;
      }
    }
    txAd.setEnabled(!isAdFinish);
    txAd.setText(mContext.getResources().getString(R.string.view_ad_video_times, times));
  }

  private void loadBack(int code, String msg, String[] info, boolean isFresh) {
    if (code == 200) {
      List<NetTodayProcessBean> netBeans =
          JSON.parseArray(Arrays.toString(info), NetTodayProcessBean.class);
      List<ViewVideoHistoryBean> parseBeans = NetTodayProcessBean.parse(netBeans);

      if (isFresh) {
        data.clear();
        data.addAll(parseBeans);
        adapter.setNewInstance(parseBeans);
      } else {
        data.addAll(parseBeans);
        adapter.addData(parseBeans);
      }

      recycler.loadMoreFinish(parseBeans.size() == 0, true);
    } else {
      ToastUtil.show(msg);
      recycler.loadMoreFinish(true, true);
    }
  }

  @Override
  public void onLoadMore() {
    mPage++;
    HttpUtil.getTodayRecord(new HttpCallback() {
      @Override
      public void onSuccess(int code, String msg, String[] info) {
        loadBack(code, msg, info, false);
      }
    });
  }

  public void adRewardBack(int times) {
    adTimes += times;
    txAd.setText(mContext.getResources().getString(R.string.view_ad_video_times, adTimes + ""));
    SpUtil.getInstance().setStringValue(Constants.AD_TAG_JILI_TIME, adTimes + "");
    SpUtil.getInstance().setStringValue(Constants.AD_TAG_JILI_FINISH_TIME,
            String.valueOf(Calendar.getInstance().getTimeInMillis()));
    if (adTimes == 5) {
      isAdFinish = true;
      txAd.setEnabled(false);
    }
  }
}
