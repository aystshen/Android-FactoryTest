package com.ayst.factorytest.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.TestItem;
import com.xuexiang.xui.widget.actionbar.TitleBar;

/**
 * 测试子模块基类
 */
public abstract class ChildTestActivity extends BaseActivity {

    protected TitleBar mTitleBar;
    protected FrameLayout mFullscreenLayout;
    protected FrameLayout mContainerLayout;
    protected LinearLayout mButtonLayout;
    protected Button mSuccessBtn;
    protected Button mFailureBtn;

    protected TestItem mTestItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_base);

        mTestItem = (TestItem) getIntent().getSerializableExtra("item");

        initViews();
    }

    public void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        mFullscreenLayout = findViewById(R.id.layout_fullscreen);
        mContainerLayout = findViewById(R.id.layout_container);
        mButtonLayout = findViewById(R.id.layout_btn);
        mSuccessBtn = findViewById(R.id.btn_success);
        mFailureBtn = findViewById(R.id.btn_failure);

        LayoutInflater.from(this).inflate(getContentLayout(), mContainerLayout, true);

        mTitleBar.setTitle(mTestItem.getName());
        mTitleBar.getLeftText().setFocusable(false);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSuccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClickSuccess();
                finish();
            }
        });

        mFailureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClickFailure();
                finish();
            }
        });
    }

    public abstract void onBtnClickFailure();

    public abstract void onBtnClickSuccess();

    public abstract int getContentLayout();
}
