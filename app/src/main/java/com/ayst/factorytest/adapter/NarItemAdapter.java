package com.ayst.factorytest.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ayst.factorytest.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class NarItemAdapter extends BaseQuickAdapter<Double, BaseViewHolder> {
    public NarItemAdapter() {
        super(R.layout.layout_rv_item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Double value) {
        baseViewHolder.setText(R.id.tv_name, String.format("%.2f", value));
    }
}
