package com.xuexiang.xui.widget.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.widget.AppCompatEditText;

import com.xuexiang.xui.R;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.ResUtils;

/**
 * 带删除按钮的输入框
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:06
 */
public class ClearEditText extends AppCompatEditText implements OnFocusChangeListener, TextWatcher {

    /**
     * 增大点击区域
     */
    private int mExtraClickArea;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ClearEditTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs, defStyle);
    }

    public ClearEditText setExtraClickAreaSize(int extraClickArea) {
        mExtraClickArea = extraClickArea;
        return this;
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        mExtraClickArea = DensityUtils.dp2px(20);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText, defStyleAttr, 0);
        mClearDrawable = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.ClearEditText_cet_clearIcon);
        int iconSize = typedArray.getDimensionPixelSize(R.styleable.ClearEditText_cet_clearIconSize, 0);
        typedArray.recycle();

        if (mClearDrawable == null) {
            //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
            mClearDrawable = getCompoundDrawablesRelative()[2];
            if (mClearDrawable == null) {
                mClearDrawable = ResUtils.getVectorDrawable(context, R.drawable.xui_ic_default_clear_btn);
            }
        }
        if (iconSize != 0) {
            mClearDrawable.setBounds(0, 0, iconSize, iconSize);
        } else {
            mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        }
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawablesRelative()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = isTouchable(event);
                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private boolean isTouchable(MotionEvent event) {
        if (isRtl()) {
            return event.getX() > getPaddingLeft() - mExtraClickArea && event.getX() < getPaddingLeft() + mClearDrawable.getIntrinsicWidth() + mExtraClickArea;
        } else {
            return event.getX() > getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth() - mExtraClickArea && event.getX() < getWidth() - getPaddingRight() + mExtraClickArea;
        }
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            int length = getText() != null ? getText().length() : 0;
            setClearIconVisible(length > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable end = visible ? mClearDrawable : null;
        setCompoundDrawablesRelative(getCompoundDrawablesRelative()[0], getCompoundDrawablesRelative()[1], end, getCompoundDrawablesRelative()[3]);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        setClearIconVisible(s.length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        setClearIconVisible(false);
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    private boolean isRtl() {
        return getLayoutDirection() == LAYOUT_DIRECTION_RTL;
    }

}
