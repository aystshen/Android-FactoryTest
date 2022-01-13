package com.ayst.factorytest.items;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

import butterknife.BindView;

public class LcdTestActivity extends ChildTestActivity {

    @BindView(R.id.bg)
    FrameLayout mBgView;
    @BindView(R.id.tv_tips)
    TextView mTipsTv;

    private int mClickCnt = 0;

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
        return R.layout.content_lcd_test;
    }

    @Override
    public void initViews() {
        super.initViews();

        mTitleBar.setVisibility(View.INVISIBLE);
        mContainerLayout.setVisibility(View.INVISIBLE);
        mButtonLayout.setVisibility(View.GONE);

        mBgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mClickCnt) {
                    case 0:
                        update("点击屏幕继续", 0xffffffff, 0xffff0000);
                        break;
                    case 1:
                        update("点击屏幕继续", 0xffffffff, 0xff00ff00);
                        break;
                    case 2:
                        update("点击屏幕继续", 0xffffffff, 0xff0000ff);
                        break;
                    case 3:
                        update("以上步骤是否分别显示红、绿、蓝界面？", 0xff000000,
                                getResources().getColor(R.color.white));
                        mButtonLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
                mClickCnt++;
            }
        });
    }

    private void update(String tips, int textColor, int bgColor) {
        mTipsTv.setText(tips);
        mTipsTv.setTextColor(textColor);
        mBgView.setBackgroundColor(bgColor);
    }
}