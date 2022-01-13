package com.ayst.factorytest.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.TestItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class TestItemAdapter extends BaseQuickAdapter<TestItem, BaseViewHolder> {
    public TestItemAdapter() {
        super(R.layout.layout_test_item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TestItem romItem) {
        baseViewHolder.setText(R.id.tv_name, romItem.getName());
        switch (romItem.getState()) {
            case TestItem.STATE_FAILURE:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.red));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.white));
                break;
            case TestItem.STATE_SUCCESS:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.green));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.white));
                break;
            case TestItem.STATE_UNKNOWN:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.white));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.black));
                break;
        }
    }
}
