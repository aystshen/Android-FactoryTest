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
    @BindView(R.id.view_black)
    FrameLayout mBlackView;
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
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xffff0000);
                        break;
                    case 1:
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xff00ff00);
                        break;
                    case 2:
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xff0000ff);
                        break;
                    case 3:
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xffffff00);
                        break;
                    case 4:
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xffa9a9a9);
                        break;
                    case 5:
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xff000000);
                        break;
                    case 6:
                        mBlackView.setVisibility(View.VISIBLE);
                        update(getString(R.string.lcd_test_click), 0xffffffff, 0xffa9a9a9);
                        break;
                    case 7:
                        mBlackView.setVisibility(View.INVISIBLE);
                        update(getString(R.string.lcd_test_prompt), 0xff000000,
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