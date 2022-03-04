package com.ayst.factorytest.items;

import android.os.Bundle;
import android.view.View;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;

import java.util.HashMap;

import butterknife.OnClick;

public class TouchTestActivity extends ChildTestActivity {

    private static final int POINT_MAX = 5; //测试点总数

    private HashMap<Integer, Boolean> mClicked = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return 0;
    }

    @Override
    public int getFullscreenLayout() {
        return R.layout.content_touch_test;
    }

    @Override
    public void initViews() {
        super.initViews();

        mTitleBar.setVisibility(View.INVISIBLE);
        mContainerLayout.setVisibility(View.INVISIBLE);
        mSuccessBtn.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_touch1, R.id.btn_touch2, R.id.btn_touch3, R.id.btn_touch4, R.id.btn_touch5})
    public void onViewClicked(View view) {
        view.setSelected(true);
        mClicked.put(view.getId(), true);
        if (mClicked.size() >= POINT_MAX) {
            finish(TestItem.STATE_SUCCESS);
        }
    }
}