package com.wwsl.wgsj.views;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.wwsl.beauty.bean.FilterBean;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.beauty.FilterAdapter;
import com.wwsl.wgsj.interfaces.OnItemClickListener;
import com.wwsl.wgsj.utils.BitmapUtil;

/**
 * Created by cxf on 2018/12/8.
 * 视频编辑 滤镜
 */

public class VideoEditFilterViewHolder extends AbsViewHolder implements OnItemClickListener<FilterBean>, View.OnClickListener {

    private ActionListener mActionListener;
    private boolean mShowed;
    private Bitmap mBitmap;


    public VideoEditFilterViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_edit_filter;
    }

    @Override
    public void init() {
        findViewById(R.id.root).setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        FilterAdapter adapter = new FilterAdapter(mContext);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(FilterBean bean, int position) {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        if (mActionListener != null) {
            int filterSrc = bean.getFilterSrc();
            if (filterSrc != 0) {
                Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
                if (bitmap != null) {
                    mBitmap = bitmap;
                    mActionListener.onFilterChanged(bitmap);
                } else {
                    mActionListener.onFilterChanged(null);
                }
            } else {
                mActionListener.onFilterChanged(null);
            }
        }
    }

    public void show() {
        mShowed = true;
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        mShowed = false;
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
        if (mActionListener != null) {
            mActionListener.onHide();
        }
    }

    @Override
    public void onClick(View v) {
        hide();
    }

    public interface ActionListener {
        void onHide();

        void onFilterChanged(Bitmap bitmap);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        mActionListener = null;
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = null;
    }

    public boolean isShowed() {
        return mShowed;
    }
}