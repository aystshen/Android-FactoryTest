package com.ayst.factorytest.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.LedItem;
import com.ayst.factorytest.model.PwmItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class LedItemAdapter extends BaseQuickAdapter<LedItem, BaseViewHolder> {
    public LedItemAdapter() {
        super(R.layout.layout_rv_item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, LedItem ledItem) {
        baseViewHolder.setText(R.id.tv_name, ledItem.getName());
        switch (ledItem.getState()) {
            case PwmItem.STATE_LOW:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.white));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.black));
                break;
            case PwmItem.STATE_HIGH:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.yellow));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.black));
                break;
        }
    }
}
