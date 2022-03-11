package com.ayst.factorytest.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.PwmItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class PwmItemAdapter extends BaseQuickAdapter<PwmItem, BaseViewHolder> {
    public PwmItemAdapter() {
        super(R.layout.layout_rv_item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, PwmItem pwmItem) {
        baseViewHolder.setText(R.id.tv_name, pwmItem.getName());
        switch (pwmItem.getState()) {
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
