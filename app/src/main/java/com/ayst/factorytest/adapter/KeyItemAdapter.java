package com.ayst.factorytest.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.KeyItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class KeyItemAdapter extends BaseQuickAdapter<KeyItem, BaseViewHolder> {
    public KeyItemAdapter() {
        super(R.layout.layout_rv_item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, KeyItem item) {
        baseViewHolder.setText(R.id.tv_name, item.getName());
        switch (item.getState()) {
            case KeyItem.STATE_SUCCESS:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.green));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.white));
                break;
            case KeyItem.STATE_UNKNOWN:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.white));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.black));
                break;
        }
    }
}
