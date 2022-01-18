package com.xuexiang.xui.widget.dialog.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.xuexiang.xui.R;
import com.xuexiang.xui.logs.UILog;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * BottomSheet 在 {@link Dialog} 的基础上重新定制了 {@link #show()} 和 {@link #hide()} 时的动画效果, 使 {@link Dialog} 在界面底部升起和降下。
 * <p>
 * 提供了以下两种面板样式:
 * <ul>
 * <li>列表样式, 使用 {@link BottomSheet.BottomListSheetBuilder} 生成。</li>
 * <li>宫格类型, 使用 {@link BottomSheet.BottomGridSheetBuilder} 生成。</li>
 * </ul>
 * </p>
 */
public class BottomSheet extends Dialog {
    // 动画时长
    private final static int ANIMATION_DURATION = 200;
    // 持有 ContentView，为了做动画
    private View mContentView;
    private boolean mIsAnimating = false;

    private OnBottomSheetShowListener mOnBottomSheetShowListener;

    public BottomSheet(Context context) {
        super(context, R.style.BottomSheet);
    }

    public void setOnBottomSheetShowListener(OnBottomSheetShowListener onBottomSheetShowListener) {
        mOnBottomSheetShowListener = onBottomSheetShowListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        // 在底部，宽度撑满
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;

        int screenWidth = Utils.getScreenWidth(getContext());
        int screenHeight = Utils.getScreenHeight(getContext());
        params.width = Math.min(screenWidth, screenHeight);
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void setContentView(int layoutResId) {
        mContentView = LayoutInflater.from(getContext()).inflate(layoutResId, null);
        super.setContentView(mContentView);
    }

    @Override
    public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
        mContentView = view;
        super.setContentView(view, params);
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    public void setContentView(@NonNull View view) {
        mContentView = view;
        super.setContentView(view);
    }

    /**
     * BottomSheet升起动画
     */
    private void animateUp() {
        if (mContentView == null) {
            return;
        }
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATION_DURATION);
        set.setFillAfter(true);
        mContentView.startAnimation(set);
    }

    /**
     * BottomSheet降下动画
     */
    private void animateDown() {
        if (mContentView == null) {
            return;
        }
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
        );
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATION_DURATION);
        set.setFillAfter(true);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
                /**
                 * Bugfix： Attempting to destroy the window while drawing!
                 */
                mContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        // java.lang.IllegalArgumentException: View=com.android.internal.policy.PhoneWindow$DecorView{22dbf5b V.E...... R......D 0,0-1080,1083} not attached to window manager
                        // 在dismiss的时候可能已经detach了，简单try-catch一下
                        try {
                            BottomSheet.super.dismiss();
                        } catch (Exception e) {
                            UILog.w("dismiss error\n" + Log.getStackTraceString(e));
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mContentView.startAnimation(set);
    }

    @Override
    public void show() {
        super.show();
        animateUp();
        if (mOnBottomSheetShowListener != null) {
            mOnBottomSheetShowListener.onShow();
        }
    }

    @Override
    public void dismiss() {
        if (mIsAnimating) {
            return;
        }
        animateDown();
    }

    public interface OnBottomSheetShowListener {
        void onShow();
    }

    /**
     * 生成列表类型的 {@link BottomSheet} 对话框。
     */
    public static class BottomListSheetBuilder {

        private Context mContext;

        private BottomSheet mDialog;
        private List<BottomSheetListItemData> mItems;
        private BaseAdapter mAdapter;
        private List<View> mHeaderViews;
        private ListView mContainerView;
        private boolean mNeedRightMark; //是否需要rightMark,标识当前项
        private int mCheckedIndex;
        private String mTitle;
        private boolean mIsCenter;
        private TextView mTitleTv;
        private OnSheetItemClickListener mOnSheetItemClickListener;
        private OnDismissListener mOnBottomDialogDismissListener;

        public BottomListSheetBuilder(Context context) {
            this(context, false);
        }

        /**
         * @param needRightMark 是否需要在被选中的 Item 右侧显示一个勾(使用 {@link #setCheckedIndex(int)} 设置选中的 Item)
         */
        public BottomListSheetBuilder(Context context, boolean needRightMark) {
            mContext = context;
            mItems = new ArrayList<>();
            mHeaderViews = new ArrayList<>();
            mNeedRightMark = needRightMark;
            mIsCenter = false;
        }

        /**
         * 设置要被选中的 Item 的下标。
         * <p>
         * 注意:仅当 {@link #mNeedRightMark} 为 true 时才有效。
         */
        public BottomListSheetBuilder setCheckedIndex(int checkedIndex) {
            mCheckedIndex = checkedIndex;
            return this;
        }

        /**
         * @param textAndTag Item 的文字内容，同时会把内容设置为 tag。
         */
        public BottomListSheetBuilder addItem(String textAndTag) {
            mItems.add(new BottomSheetListItemData(textAndTag, textAndTag));
            return this;
        }

        /**
         * @param image      icon Item 的 icon。
         * @param textAndTag Item 的文字内容，同时会把内容设置为 tag。
         */
        public BottomListSheetBuilder addItem(Drawable image, String textAndTag) {
            mItems.add(new BottomSheetListItemData(image, textAndTag, textAndTag));
            return this;
        }

        /**
         * @param text Item 的文字内容。
         * @param tag  item 的 tag。
         */
        public BottomListSheetBuilder addItem(String text, String tag) {
            mItems.add(new BottomSheetListItemData(text, tag));
            return this;
        }

        /**
         * @param imageRes Item 的图标 Resource。
         * @param text     Item 的文字内容。
         * @param tag      Item 的 tag。
         */
        public BottomListSheetBuilder addItem(int imageRes, String text, String tag) {
            Drawable drawable = imageRes != 0 ? ContextCompat.getDrawable(mContext, imageRes) : null;
            mItems.add(new BottomSheetListItemData(drawable, text, tag));
            return this;
        }

        /**
         * @param imageRes    Item 的图标 Resource。
         * @param text        Item 的文字内容。
         * @param tag         Item 的 tag。
         * @param hasRedPoint 是否显示红点。
         */
        public BottomListSheetBuilder addItem(int imageRes, String text, String tag, boolean hasRedPoint) {
            Drawable drawable = imageRes != 0 ? ContextCompat.getDrawable(mContext, imageRes) : null;
            mItems.add(new BottomSheetListItemData(drawable, text, tag, hasRedPoint));
            return this;
        }

        /**
         * @param imageRes    Item 的图标 Resource。
         * @param text        Item 的文字内容。
         * @param tag         Item 的 tag。
         * @param hasRedPoint 是否显示红点。
         * @param disabled    是否显示禁用态。
         */
        public BottomListSheetBuilder addItem(int imageRes, String text, String tag, boolean hasRedPoint, boolean disabled) {
            Drawable drawable = imageRes != 0 ? ContextCompat.getDrawable(mContext, imageRes) : null;
            mItems.add(new BottomSheetListItemData(drawable, text, tag, hasRedPoint, disabled));
            return this;
        }

        public BottomListSheetBuilder setOnSheetItemClickListener(OnSheetItemClickListener onSheetItemClickListener) {
            mOnSheetItemClickListener = onSheetItemClickListener;
            return this;
        }

        public BottomListSheetBuilder setOnBottomDialogDismissListener(OnDismissListener listener) {
            mOnBottomDialogDismissListener = listener;
            return this;
        }

        public BottomListSheetBuilder addHeaderView(View view) {
            if (view != null) {
                mHeaderViews.add(view);
            }
            return this;
        }

        public BottomListSheetBuilder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public BottomListSheetBuilder setTitle(int resId) {
            mTitle = mContext.getResources().getString(resId);
            return this;
        }

        /**
         * 设置文字是否居中对齐
         * @param isCenter
         * @return
         */
        public BottomListSheetBuilder setIsCenter(boolean isCenter) {
            mIsCenter = isCenter;
            return this;
        }

        public BottomSheet build() {
            mDialog = new BottomSheet(mContext);
            View contentView = buildViews();
            mDialog.setContentView(contentView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (mOnBottomDialogDismissListener != null) {
                mDialog.setOnDismissListener(mOnBottomDialogDismissListener);
            }
            return mDialog;
        }

        private View buildViews() {
            View wrapperView = View.inflate(mContext, getContentViewLayoutId(), null);
            mTitleTv = wrapperView.findViewById(R.id.title);
            mContainerView = wrapperView.findViewById(R.id.listview);
            if (mTitle != null && mTitle.length() != 0) {
                mTitleTv.setVisibility(View.VISIBLE);
                mTitleTv.setText(mTitle);
            } else {
                mTitleTv.setVisibility(View.GONE);
            }
            if (mIsCenter) {
                mTitleTv.setGravity(Gravity.CENTER);
            }
            if (mHeaderViews.size() > 0) {
                for (View headerView : mHeaderViews) {
                    mContainerView.addHeaderView(headerView);
                }
            }
            if (needToScroll()) {
                mContainerView.getLayoutParams().height = getListMaxHeight();
                mDialog.setOnBottomSheetShowListener(new OnBottomSheetShowListener() {
                    @Override
                    public void onShow() {
                        mContainerView.setSelection(mCheckedIndex);
                    }
                });
            }

            mAdapter = new ListAdapter(mIsCenter);
            mContainerView.setAdapter(mAdapter);
            return wrapperView;
        }

        private boolean needToScroll() {
            int itemHeight = ThemeUtils.resolveDimension(mContext, R.attr.xui_bottom_sheet_list_item_height);
            int totalHeight = mItems.size() * itemHeight;
            if (mHeaderViews.size() > 0) {
                for (View view : mHeaderViews) {
                    if (view.getMeasuredHeight() == 0) {
                        view.measure(0, 0);
                    }
                    totalHeight += view.getMeasuredHeight();
                }
            }
            if (mTitleTv != null && !Utils.isNullOrEmpty(mTitle)) {
                totalHeight += ThemeUtils.resolveDimension(mContext, R.attr.xui_bottom_sheet_title_height);
            }
            return totalHeight > getListMaxHeight();
        }

        /**
         * 注意:这里只考虑List的高度,如果有title或者headerView,不计入考虑中
         */
        protected int getListMaxHeight() {
            return (int) (Utils.getScreenHeight(mContext) * 0.5);
        }

        public void notifyDataSetChanged() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            if (needToScroll()) {
                mContainerView.getLayoutParams().height = getListMaxHeight();
                mContainerView.setSelection(mCheckedIndex);
            }
        }

        protected int getContentViewLayoutId() {
            return R.layout.xui_bottom_sheet_list;
        }

        public interface OnSheetItemClickListener {
            void onClick(BottomSheet dialog, View itemView, int position, String tag);
        }

        private static class BottomSheetListItemData {

            Drawable image = null;
            String text;
            String tag = "";
            boolean hasRedPoint = false;
            boolean isDisabled = false;

            public BottomSheetListItemData(String text, String tag) {
                this.text = text;
                this.tag = tag;
            }

            public BottomSheetListItemData(Drawable image, String text, String tag) {
                this.image = image;
                this.text = text;
                this.tag = tag;
            }

            public BottomSheetListItemData(Drawable image, String text, String tag, boolean hasRedPoint) {
                this.image = image;
                this.text = text;
                this.tag = tag;
                this.hasRedPoint = hasRedPoint;
            }

            public BottomSheetListItemData(Drawable image, String text, String tag, boolean hasRedPoint, boolean isDisabled) {
                this.image = image;
                this.text = text;
                this.tag = tag;
                this.hasRedPoint = hasRedPoint;
                this.isDisabled = isDisabled;
            }
        }

        private static class ViewHolder {
            ImageView imageView;
            TextView textView;
            View markView;
            View redPoint;
        }

        private class ListAdapter extends BaseAdapter {

            private boolean mIsCenter;

            public ListAdapter(boolean isCenter) {
                mIsCenter = isCenter;
            }

            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public BottomSheetListItemData getItem(int position) {
                return mItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final BottomSheetListItemData data = getItem(position);
                final ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    convertView = inflater.inflate(R.layout.xui_bottom_sheet_list_item, parent, false);
                    holder = new ViewHolder();
                    holder.imageView = convertView.findViewById(R.id.bottom_dialog_list_item_img);
                    holder.textView = convertView.findViewById(R.id.bottom_dialog_list_item_title);
                    if (mIsCenter) {
                        holder.textView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                        holder.textView.setGravity(Gravity.CENTER);
                    }
                    holder.markView = convertView.findViewById(R.id.bottom_dialog_list_item_mark_view_stub);
                    holder.redPoint = convertView.findViewById(R.id.bottom_dialog_list_item_point);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (data.image != null) {
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.imageView.setImageDrawable(data.image);
                } else {
                    holder.imageView.setVisibility(View.GONE);
                }

                holder.textView.setText(data.text);
                if (data.hasRedPoint) {
                    holder.redPoint.setVisibility(View.VISIBLE);
                } else {
                    holder.redPoint.setVisibility(View.GONE);
                }

                if (data.isDisabled) {
                    holder.textView.setEnabled(false);
                    convertView.setEnabled(false);
                } else {
                    holder.textView.setEnabled(true);
                    convertView.setEnabled(true);
                }

                if (mNeedRightMark) {
                    if (holder.markView instanceof ViewStub) {
                        holder.markView = ((ViewStub) holder.markView).inflate();
                    }
                    if (mCheckedIndex == position) {
                        holder.markView.setVisibility(View.VISIBLE);
                    } else {
                        holder.markView.setVisibility(View.GONE);
                    }
                } else {
                    holder.markView.setVisibility(View.GONE);
                }

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.hasRedPoint) {
                            data.hasRedPoint = false;
                            holder.redPoint.setVisibility(View.GONE);
                        }
                        if (mNeedRightMark) {
                            setCheckedIndex(position);
                            notifyDataSetChanged();
                        }
                        if (mOnSheetItemClickListener != null) {
                            mOnSheetItemClickListener.onClick(mDialog, v, position, data.tag);
                        }
                    }
                });
                return convertView;
            }
        }

    }

    /**
     * 生成宫格类型的 {@link BottomSheet} 对话框。
     */
    public static class BottomGridSheetBuilder {

        /**
         * item 出现在第一行
         */
        public static final int FIRST_LINE = 0;
        /**
         * item 出现在第二行
         */
        public static final int SECOND_LINE = 1;
        private Context mContext;
        private BottomSheet mDialog;
        private SparseArray<View> mFirstLineViews;
        private SparseArray<View> mSecondLineViews;
        private int mMiniItemWidth = -1;
        private OnSheetItemClickListener mOnSheetItemClickListener;
        private Typeface mItemTextTypeFace = null;
        private Typeface mBottomButtonTypeFace = null;
        private boolean mIsShowButton = true;
        private CharSequence mButtonText = null;
        private View.OnClickListener mButtonClickListener = null;

        public BottomGridSheetBuilder(Context context) {
            mContext = context;
            mFirstLineViews = new SparseArray<>();
            mSecondLineViews = new SparseArray<>();
        }

        public BottomGridSheetBuilder addItem(int imageRes, CharSequence textAndTag, @Style int style) {
            return addItem(imageRes, textAndTag, textAndTag, style, 0);
        }

        public BottomGridSheetBuilder addItem(int imageRes, CharSequence text, Object tag, @Style int style) {
            return addItem(imageRes, text, tag, style, 0);
        }

        public BottomGridSheetBuilder setIsShowButton(boolean isShowButton) {
            mIsShowButton = isShowButton;
            return this;
        }

        public BottomGridSheetBuilder setButtonText(CharSequence buttonText) {
            mButtonText = buttonText;
            return this;
        }

        public BottomGridSheetBuilder setButtonClickListener(View.OnClickListener buttonClickListener) {
            mButtonClickListener = buttonClickListener;
            return this;
        }

        public BottomGridSheetBuilder setItemTextTypeFace(Typeface itemTextTypeFace) {
            mItemTextTypeFace = itemTextTypeFace;
            return this;
        }

        public BottomGridSheetBuilder setBottomButtonTypeFace(Typeface bottomButtonTypeFace) {
            mBottomButtonTypeFace = bottomButtonTypeFace;
            return this;
        }

        public BottomGridSheetBuilder addItem(int imageRes, CharSequence text, Object tag, @Style int style, int subscriptRes) {
            BottomSheetItemView itemView = createItemView(AppCompatResources.getDrawable(mContext, imageRes), text, tag, subscriptRes);
            return addItem(itemView, style);
        }

        public BottomGridSheetBuilder addItem(View view, @Style int style) {
            switch (style) {
                case FIRST_LINE:
                    mFirstLineViews.append(mFirstLineViews.size(), view);
                    break;
                case SECOND_LINE:
                    mSecondLineViews.append(mSecondLineViews.size(), view);
                    break;
            }
            return this;
        }

        public BottomSheetItemView createItemView(Drawable drawable, CharSequence text, Object tag, int subscriptRes) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            BottomSheetItemView itemView = (BottomSheetItemView) inflater.inflate(getItemViewLayoutId(), null, false);
            TextView titleTV = itemView.findViewById(R.id.grid_item_title);
            if (mItemTextTypeFace != null) {
                titleTV.setTypeface(mItemTextTypeFace);
            }
            titleTV.setText(text);

            itemView.setTag(tag);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSheetItemClickListener != null) {
                        mOnSheetItemClickListener.onClick(mDialog, (BottomSheetItemView) v);
                    }
                }
            });
            AppCompatImageView imageView = itemView.findViewById(R.id.grid_item_image);
            imageView.setImageDrawable(drawable);

            if (subscriptRes != 0) {
                ViewStub stub = itemView.findViewById(R.id.grid_item_subscript);
                View inflated = stub.inflate();
                ((ImageView) inflated).setImageResource(subscriptRes);
            }
            return itemView;
        }

        public void setItemVisibility(Object tag, int visibility) {
            View foundView = null;
            for (int i = 0; i < mFirstLineViews.size(); i++) {
                View view = mFirstLineViews.get(i);
                if (view != null && view.getTag().equals(tag)) {
                    foundView = view;
                }
            }
            for (int i = 0; i < mSecondLineViews.size(); i++) {
                View view = mSecondLineViews.get(i);
                if (view != null && view.getTag().equals(tag)) {
                    foundView = view;
                }
            }
            if (foundView != null) {
                foundView.setVisibility(visibility);
            }
        }

        public BottomGridSheetBuilder setOnSheetItemClickListener(OnSheetItemClickListener onSheetItemClickListener) {
            mOnSheetItemClickListener = onSheetItemClickListener;
            return this;
        }

        public BottomSheet build() {
            mDialog = new BottomSheet(mContext);
            View contentView = buildViews();
            mDialog.setContentView(contentView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return mDialog;
        }

        private View buildViews() {
            LinearLayout baseLinearLayout;
            baseLinearLayout = (LinearLayout) View.inflate(mContext, getContentViewLayoutId(), null);
            LinearLayout firstLine = baseLinearLayout.findViewById(R.id.bottom_sheet_first_linear_layout);
            LinearLayout secondLine = baseLinearLayout.findViewById(R.id.bottom_sheet_second_linear_layout);
            ViewGroup mBottomButtonContainer = baseLinearLayout.findViewById(R.id.bottom_sheet_button_container);
            TextView mBottomButton = baseLinearLayout.findViewById(R.id.bottom_sheet_close_button);

            int maxItemCountEachLine = Math.max(mFirstLineViews.size(), mSecondLineViews.size());
            int screenWidth = Utils.getScreenWidth(mContext);
            int screenHeight = Utils.getScreenHeight(mContext);
            int width = Math.min(screenWidth, screenHeight);
            int itemWidth = calculateItemWidth(width, maxItemCountEachLine, firstLine.getPaddingLeft(), firstLine.getPaddingRight());

            addViewsInSection(mFirstLineViews, firstLine, itemWidth);
            addViewsInSection(mSecondLineViews, secondLine, itemWidth);

            boolean hasFirstLine = mFirstLineViews.size() > 0;
            boolean hasSecondLine = mSecondLineViews.size() > 0;
            if (!hasFirstLine) {
                firstLine.setVisibility(View.GONE);
            }
            if (!hasSecondLine) {
                if (hasFirstLine) {
                    firstLine.setPadding(
                            firstLine.getPaddingLeft(),
                            firstLine.getPaddingTop(),
                            firstLine.getPaddingRight(),
                            0);
                }
                secondLine.setVisibility(View.GONE);
            }

            // button 在用户自定义了contentView的情况下可能不存在
            if (mBottomButtonContainer != null) {
                if (mIsShowButton) {
                    mBottomButtonContainer.setVisibility(View.VISIBLE);
                    baseLinearLayout.setPadding(baseLinearLayout.getPaddingLeft(),
                            baseLinearLayout.getPaddingTop(),
                            baseLinearLayout.getPaddingRight(),
                            0);
                } else {
                    mBottomButtonContainer.setVisibility(View.GONE);
                }
                if (mBottomButtonTypeFace != null) {
                    mBottomButton.setTypeface(mBottomButtonTypeFace);
                }
                if (mButtonText != null) {
                    mBottomButton.setText(mButtonText);
                }

                if (mButtonClickListener != null) {
                    mBottomButton.setOnClickListener(mButtonClickListener);
                } else {
                    mBottomButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                }
            }

            return baseLinearLayout;
        }

        protected int getContentViewLayoutId() {
            return R.layout.xui_bottom_sheet_grid;
        }

        protected int getItemViewLayoutId() {
            return R.layout.xui_bottom_sheet_grid_item;
        }

        /**
         * 拿个数最多的一行，去决策item的平铺/拉伸策略
         *
         * @return item 宽度
         */
        private int calculateItemWidth(int width, int maxItemCountInEachLine, int paddingLeft, int paddingRight) {
            if (mMiniItemWidth == -1) {
                mMiniItemWidth = ThemeUtils.resolveDimension(mContext, R.attr.xui_bottom_sheet_grid_item_mini_width);
            }

            final int parentSpacing = width - paddingLeft - paddingRight;
            int itemWidth = mMiniItemWidth;
            // 看是否需要把 Item 拉伸平分 parentSpacing
            if (maxItemCountInEachLine >= 3
                    && parentSpacing - maxItemCountInEachLine * itemWidth > 0
                    && parentSpacing - maxItemCountInEachLine * itemWidth < itemWidth) {
                int count = parentSpacing / itemWidth;
                itemWidth = parentSpacing / count;
            }
            // 看是否需要露出半个在屏幕边缘
            if (itemWidth * maxItemCountInEachLine > parentSpacing) {
                int count = (width - paddingLeft) / itemWidth;
                itemWidth = (int) ((width - paddingLeft) / (count + .5f));
            }
            return itemWidth;
        }

        private void addViewsInSection(SparseArray<View> items, LinearLayout parent, int itemWidth) {

            for (int i = 0; i < items.size(); i++) {
                View itemView = items.get(i);
                setItemWidth(itemView, itemWidth);
                parent.addView(itemView);
            }
        }

        private void setItemWidth(View itemView, int itemWidth) {
            LinearLayout.LayoutParams itemLp;
            if (itemView.getLayoutParams() != null) {
                itemLp = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                itemLp.width = itemWidth;
            } else {
                itemLp = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(itemLp);
            }
            itemLp.gravity = Gravity.TOP;
        }

        public interface OnSheetItemClickListener {
            void onClick(BottomSheet dialog, BottomSheetItemView itemView);
        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({FIRST_LINE, SECOND_LINE})
        public @interface Style {
        }
    }
}
