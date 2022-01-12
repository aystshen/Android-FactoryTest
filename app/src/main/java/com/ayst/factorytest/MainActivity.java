package com.ayst.factorytest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.ayst.factorytest.adapter.TestItemAdapter;
import com.ayst.factorytest.base.BaseActivity;
import com.ayst.factorytest.data.TestItemManager;
import com.blankj.utilcode.util.AppUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private TestItemAdapter mTestItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTitleBar.setTitle(getString(R.string.app_name));
        mTitleBar.setLeftText("");
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.exitApp();
            }
        });

        WidgetUtils.initGridRecyclerView(mItemsRv, 3, 1, getResources().getColor(R.color.gray));
        mTestItemAdapter = new TestItemAdapter();
        mTestItemAdapter.setList(TestItemManager.getInstance().getTestItems());
        mItemsRv.setAdapter(mTestItemAdapter);
        mTestItemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                mFlowTest = false;
//                handleTestTask(((RomItem) adapter.getData().get(position)));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}