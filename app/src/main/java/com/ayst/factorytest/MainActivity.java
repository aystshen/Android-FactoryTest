package com.ayst.factorytest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ayst.factorytest.adapter.TestItemAdapter;
import com.ayst.factorytest.base.BaseActivity;
import com.ayst.factorytest.data.TestItemManager;
import com.ayst.factorytest.model.ResultEvent;
import com.ayst.factorytest.model.TestItem;
import com.blankj.utilcode.util.AppUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private ArrayList<TestItem> mTestItems;
    private TestItemAdapter mTestItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        mTitleBar.setTitle(getString(R.string.app_name));
        mTitleBar.setLeftText("");
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.exitApp();
            }
        });

        WidgetUtils.initGridRecyclerView(mItemsRv, 4, 1, getResources().getColor(R.color.gray));
        mTestItems = TestItemManager.getInstance().getTestItems();
        mTestItemAdapter = new TestItemAdapter();
        mTestItemAdapter.setList(mTestItems);
        mItemsRv.setAdapter(mTestItemAdapter);
        mTestItemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(MainActivity.this,
                        ((TestItem) adapter.getData().get(position)).getActivity());
                intent.putExtra("item", (TestItem) adapter.getData().get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ResultEvent event) {
        TestItem item = event.getTestItem();
        Log.i(TAG, "onResultEvent, " + item.toString());
        for (int i=0; i<mTestItems.size(); i++) {
            if (TextUtils.equals(item.getName(), mTestItems.get(i).getName())) {
                mTestItems.get(i).setState(item.getState());
                mTestItemAdapter.notifyDataSetChanged();
            }
        }
    }
}