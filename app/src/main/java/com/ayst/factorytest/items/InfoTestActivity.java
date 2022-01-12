package com.ayst.factorytest.items;

import android.os.Bundle;

import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

public class InfoTestActivity extends ChildTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBtnClickFailure() {

    }

    @Override
    public void onBtnClickSuccess() {

    }

    @Override
    public int getContentLayout() {
        return R.layout.content_info_test;
    }
}