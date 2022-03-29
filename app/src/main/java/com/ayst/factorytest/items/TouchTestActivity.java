package com.ayst.factorytest.items;

import android.os.Bundle;
import android.view.View;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.view.TouchTestView;

import java.util.HashMap;

import butterknife.BindView;

public class TouchTestActivity extends ChildTestActivity implements TouchTestView.CallBack {

    @BindView(R.id.touch_test)
    TouchTestView mTouchTestView;

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
        mFailureBtn.setVisibility(View.GONE);

        mTouchTestView.setCallBack(this);
    }

    @Override
    public void onTestCompleted() {
        finish(TestItem.STATE_SUCCESS);
    }
}