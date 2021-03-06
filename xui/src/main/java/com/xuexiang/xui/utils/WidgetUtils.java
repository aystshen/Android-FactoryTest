/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xui.R;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.adapter.recyclerview.DividerItemDecoration;
import com.xuexiang.xui.adapter.recyclerview.GridDividerItemDecoration;
import com.xuexiang.xui.adapter.recyclerview.XGridLayoutManager;
import com.xuexiang.xui.adapter.recyclerview.XLinearLayoutManager;
import com.xuexiang.xui.widget.dialog.LoadingDialog;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.progress.loading.IMessageLoader;
import com.xuexiang.xui.widget.progress.loading.LoadingViewLayout;

import java.util.List;

import static androidx.recyclerview.widget.OrientationHelper.VERTICAL;

/**
 * ???????????????
 *
 * @author xuexiang
 * @since 2018/11/26 ??????2:54
 */
public final class WidgetUtils {

    private WidgetUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * ???Activity????????????
     *
     * @param activity activity
     */
    public static void requestFullScreen(@NonNull Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //===============Spinner=============//

    /**
     * Spinner????????????????????????
     *
     * @param spinner ???????????????
     * @param items   ?????????
     */
    public static void initSpinnerStyle(@NonNull Spinner spinner, @NonNull String[] items) {
        initSpinnerStyle(spinner);
        initSpinnerItem(spinner, items);
    }

    /**
     * Spinner????????????
     *
     * @param spinner ???????????????
     */
    public static void initSpinnerStyle(@NonNull Spinner spinner) {
        // ????????????????????????
        spinner.setBackground(ResUtils.getDrawable(spinner.getContext(), R.drawable.xui_config_bg_spinner));
        ViewUtils.setPaddingEnd(spinner, ResUtils.getDimensionPixelSize(R.dimen.default_spinner_icon_padding_size));
        // ???????????????????????????
        spinner.setPopupBackgroundDrawable(ResUtils.getDrawable(spinner.getContext(), R.drawable.ms_drop_down_bg_radius));
        setSpinnerDropDownVerticalOffset(spinner);
    }

    /**
     * ?????????Spinner?????????????????????
     *
     * @param spinner ???????????????
     * @param items   ?????????
     */
    public static void initSpinnerItem(@NonNull Spinner spinner, @NonNull String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), R.layout.xui_layout_spinner_selected_item, R.id.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.xui_layout_spinner_drop_down_item);
        spinner.setAdapter(adapter);
    }

    /**
     * ????????????Spinner???????????????
     *
     * @param spinner ???????????????
     */
    public static void setSpinnerDropDownVerticalOffset(@NonNull Spinner spinner) {
        int itemHeight = ThemeUtils.resolveDimension(spinner.getContext(), R.attr.ms_item_height_size);
        int dropdownOffset = ThemeUtils.resolveDimension(spinner.getContext(), R.attr.ms_dropdown_offset);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            spinner.setDropDownVerticalOffset(0);
        } else {
            spinner.setDropDownVerticalOffset(itemHeight + dropdownOffset);
        }
    }

    //===============TabLayout=============//

    /**
     * ???TabLayout?????????????????????????????????
     *
     * @param tabLayout ?????????
     * @param text      ??????????????????
     * @param resId     ????????????
     */
    public static TabLayout.Tab addTabWithoutRipple(@NonNull TabLayout tabLayout, @Nullable CharSequence text, @DrawableRes int resId) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(text);
        tab.setIcon(resId);
        tab.view.setBackgroundColor(Color.TRANSPARENT);
        tabLayout.addTab(tab);
        return tab;
    }

    /**
     * ???TabLayout?????????????????????????????????
     *
     * @param tabLayout ?????????
     * @param text      ??????????????????
     * @param icon      ????????????
     */
    public static TabLayout.Tab addTabWithoutRipple(@NonNull TabLayout tabLayout, @Nullable CharSequence text, @Nullable Drawable icon) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(text);
        tab.setIcon(icon);
        tab.view.setBackgroundColor(Color.TRANSPARENT);
        tabLayout.addTab(tab);
        return tab;
    }

    /**
     * ??????TabLayout??????????????????
     *
     * @param tabLayout ?????????
     */
    public static void setTabLayoutTextFont(TabLayout tabLayout) {
        setTabLayoutTextFont(tabLayout, XUI.getDefaultTypeface());
    }

    /**
     * ??????TabLayout??????????????????
     *
     * @param tabLayout ?????????
     * @param typeface  ??????
     */
    public static void setTabLayoutTextFont(TabLayout tabLayout, Typeface typeface) {
        if (tabLayout == null || typeface == null) {
            return;
        }
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int i = 0; i < tabsCount; i++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            int tabCount = vgTab.getChildCount();
            for (int j = 0; j < tabCount; j++) {
                View tabViewChild = vgTab.getChildAt(j);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

    //===============recyclerView=============//

    /**
     * ?????????Grid??????RecyclerView
     *
     * @param recyclerView
     * @param spanCount    ???????????????
     */
    public static void initGridRecyclerView(@NonNull RecyclerView recyclerView, int spanCount) {
        recyclerView.setLayoutManager(new XGridLayoutManager(recyclerView.getContext(), spanCount));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(recyclerView.getContext(), spanCount));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * ?????????Grid??????RecyclerView
     *
     * @param recyclerView
     * @param spanCount    ???????????????
     * @param dividerWidth ??????????????????
     */
    public static void initGridRecyclerView(@NonNull RecyclerView recyclerView, int spanCount, int dividerWidth) {
        recyclerView.setLayoutManager(new XGridLayoutManager(recyclerView.getContext(), spanCount));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(recyclerView.getContext(), spanCount, dividerWidth));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * ?????????Grid??????RecyclerView
     *
     * @param recyclerView
     * @param spanCount    ???????????????
     * @param dividerWidth ???????????????
     * @param dividerColor ??????????????????
     */
    public static void initGridRecyclerView(@NonNull RecyclerView recyclerView, int spanCount, int dividerWidth, int dividerColor) {
        recyclerView.setLayoutManager(new XGridLayoutManager(recyclerView.getContext(), spanCount));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(recyclerView.getContext(), spanCount, dividerWidth, dividerColor));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * ?????????Grid??????RecyclerView
     *
     * @param recyclerView
     * @param canScroll    ??????????????????
     * @param spanCount    ???????????????
     * @param dividerWidth ???????????????
     * @param dividerColor ??????????????????
     */
    public static void initGridRecyclerView(@NonNull RecyclerView recyclerView, boolean canScroll, int spanCount, int dividerWidth, int dividerColor) {
        recyclerView.setLayoutManager(new XGridLayoutManager(recyclerView.getContext(), spanCount).setScrollEnabled(canScroll));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(recyclerView.getContext(), spanCount, dividerWidth, dividerColor));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * ?????????RecyclerView
     *
     * @param recyclerView
     */
    public static void initRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new XLinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * ?????????RecyclerView
     *
     * @param recyclerView
     * @param dividerHeight ??????????????????
     */
    public static void initRecyclerView(@NonNull RecyclerView recyclerView, int dividerHeight) {
        recyclerView.setLayoutManager(new XLinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), VERTICAL, dividerHeight));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * ?????????RecyclerView
     *
     * @param recyclerView
     * @param dividerHeight ??????????????????
     * @param dividerColor  ??????????????????
     */
    public static void initRecyclerView(@NonNull RecyclerView recyclerView, int dividerHeight, int dividerColor) {
        recyclerView.setLayoutManager(new XLinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), VERTICAL, dividerHeight, dividerColor));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * ?????????RecyclerView
     *
     * @param recyclerView
     * @param canScroll     ??????????????????
     * @param dividerHeight ??????????????????
     * @param dividerColor  ??????????????????
     */
    public static void initRecyclerView(@NonNull RecyclerView recyclerView, boolean canScroll, int dividerHeight, int dividerColor) {
        recyclerView.setLayoutManager(new XLinearLayoutManager(recyclerView.getContext()).setScrollEnabled(canScroll));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), VERTICAL, dividerHeight, dividerColor));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * ??????????????????????????????
     *
     * @param payloads ????????????
     * @return ????????????????????????
     */
    public static Bundle getChangePayload(List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return null;
        }

        for (Object payload : payloads) {
            if (payload instanceof Bundle) {
                return (Bundle) payload;
            }
        }
        return null;
    }

    //===============Loading=============//

    /**
     * ??????loading?????????
     *
     * @param context ?????????
     * @return loading?????????
     */
    public static LoadingDialog getLoadingDialog(@NonNull Context context) {
        return new LoadingDialog(context);
    }

    /**
     * ??????loading?????????
     *
     * @param context ?????????
     * @param message ????????????
     * @return loading?????????
     */
    public static LoadingDialog getLoadingDialog(@NonNull Context context, @NonNull String message) {
        return new LoadingDialog(context, message);
    }

    /**
     * ??????loading????????????????????????
     *
     * @param loadingDialog loading?????????
     * @param message       ????????????
     * @return loading?????????
     */
    public static LoadingDialog updateLoadingMessage(LoadingDialog loadingDialog, @NonNull Context context, @NonNull String message) {
        if (loadingDialog == null) {
            loadingDialog = getLoadingDialog(context);
        }
        loadingDialog.updateMessage(message);
        loadingDialog.show();
        return loadingDialog;
    }


    /**
     * ??????IMessageLoader
     *
     * @param isDialog ???????????????
     * @param context  ?????????
     * @return ???????????????
     */
    public static IMessageLoader getMessageLoader(boolean isDialog, @NonNull Context context) {
        return isDialog ? new LoadingDialog(context) : new LoadingViewLayout(context);
    }


    /**
     * ??????MiniLoadingDialog?????????
     *
     * @param context ?????????
     * @return MiniLoadingDialog?????????
     */
    public static MiniLoadingDialog getMiniLoadingDialog(@NonNull Context context) {
        return new MiniLoadingDialog(context);
    }

    /**
     * ??????MiniLoadingDialog?????????
     *
     * @param context ?????????
     * @param message ????????????
     * @return MiniLoadingDialog?????????
     */
    public static MiniLoadingDialog getMiniLoadingDialog(@NonNull Context context, @NonNull String message) {
        return new MiniLoadingDialog(context, message);
    }


    /**
     * ?????????????????????????????????(?????????????????????
     *
     * @param dialog ????????????
     */
    public static void transparentBottomSheetDialogBackground(BottomSheetDialog dialog) {
        if (dialog != null && dialog.getWindow() != null) {
            FrameLayout frameLayout = dialog.getWindow().findViewById(R.id.design_bottom_sheet);
            if (frameLayout != null) {
                frameLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param activity ??????
     */
    public static void clearActivityBackground(Activity activity) {
        if (activity == null) {
            return;
        }
        clearWindowBackground(activity.getWindow());
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param window ??????
     */
    public static void clearWindowBackground(Window window) {
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    /**
     * ??????????????????????????????????????????????????????
     *
     * @param view ??????
     */
    public static void clearViewBackground(View view) {
        if (view == null) {
            return;
        }
        view.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param view ??????
     */
    public static void clearAllViewBackground(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                clearAllViewBackground(viewGroup.getChildAt(i));
            }
        }
        view.setBackgroundColor(Color.TRANSPARENT);
    }

}
