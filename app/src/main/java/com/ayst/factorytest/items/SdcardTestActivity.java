package com.ayst.factorytest.items;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.format.Formatter;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.StringItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.blankj.utilcode.util.SDCardUtils;
import com.google.gson.Gson;
import com.xuexiang.xui.utils.WidgetUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SdcardTestActivity extends ChildTestActivity {
    private static final String TAG = "SdcardTestActivity";

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private Gson mGson = new Gson();
    private StringItemAdapter mStringItemAdapter;
    private ArrayList<String> mItems = new ArrayList<>();
    private BroadcastReceiver mMediaStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSdcard();
                }
            }, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_sdcard_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    protected void initViews() {
        super.initViews();

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mStringItemAdapter = new StringItemAdapter();
        mStringItemAdapter.setList(mItems);
        mItemsRv.setAdapter(mStringItemAdapter);

        getSdcard();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file"); // 必须添加，否则无法监听到SD卡插拔广播

        registerReceiver(mMediaStateChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mMediaStateChangeReceiver);

        super.onStop();
    }

    private void getSdcard() {
        mItems.clear();

        List<SDCardUtils.SDCardInfo> cardInfos = SDCardUtils.getSDCardInfo();
        for (SDCardUtils.SDCardInfo cardInfo : cardInfos) {
            StringBuilder builder = new StringBuilder();
            builder.append("路径: " + cardInfo.getPath() + "\n空间: "
                    + Formatter.formatFileSize(this, cardInfo.getAvailableSize())
                    + "(共" + Formatter.formatFileSize(this, cardInfo.getTotalSize()) + ")");
            mItems.add(builder.toString());
        }
        updateResult(mGson.toJson(cardInfos));

        mStringItemAdapter.setList(mItems);
        mStringItemAdapter.notifyDataSetChanged();
    }
}