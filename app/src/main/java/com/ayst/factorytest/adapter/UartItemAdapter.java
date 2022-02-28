package com.ayst.factorytest.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.ayst.factorytest.model.TestItem;
import com.ayst.factorytest.model.UartItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class UartItemAdapter extends BaseQuickAdapter<UartItem, BaseViewHolder> {
    public UartItemAdapter() {
        super(R.layout.layout_rv_item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, UartItem uartItem) {
        baseViewHolder.setText(R.id.tv_name, uartItem.getDevice());
        switch (uartItem.getState()) {
            case UartItem.STATE_FAILURE:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.red));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.white));
                break;
            case UartItem.STATE_SUCCESS:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.green));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.white));
                break;
            case UartItem.STATE_UNKNOWN:
                baseViewHolder.setBackgroundColor(R.id.tv_name, getContext().getColor(R.color.white));
                baseViewHolder.setTextColor(R.id.tv_name, getContext().getColor(R.color.black));
                break;
        }
    }
}
