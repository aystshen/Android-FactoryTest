package com.xuexiang.xui.widget.button.roundbutton;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.annotation.Nullable;

import android.util.AttributeSet;

import com.xuexiang.xui.R;
import com.xuexiang.xui.utils.ResUtils;

/**
 * 可以方便地生成圆角矩形/圆形 {@link android.graphics.drawable.Drawable}。
 * <p>
 * <ul>
 * <li>使用 {@link #setBgData(ColorStateList)} 设置背景色。</li>
 * <li>使用 {@link #setStrokeData(int, ColorStateList)} 设置描边大小、描边颜色。</li>
 * <li>使用 {@link #setIsRadiusAdjustBounds(boolean)} 设置圆角大小是否自动适应为 {@link android.view.View} 的高度的一半, 默认为 true。</li>
 * </ul>
 */
public class RoundDrawable extends GradientDrawable {

    /**
     * 圆角大小是否自适应为 View 的高度的一般
     */
    private boolean mRadiusAdjustBounds = true;
    private ColorStateList mFillColors;
    private int mStrokeWidth = 0;
    private ColorStateList mStrokeColors;

    /**
     * 设置按钮的背景色(只支持纯色,不支持 Bitmap 或 Drawable)
     */
    public void setBgData(@Nullable ColorStateList colors) {
        if (hasNativeStateListAPI()) {
            super.setColor(colors);
        } else {
            mFillColors = colors;
            final int currentColor;
            if (colors == null) {
                currentColor = Color.TRANSPARENT;
            } else {
                currentColor = colors.getColorForState(getState(), 0);
            }
            setColor(currentColor);
        }
    }

    /**
     * 设置按钮的描边粗细和颜色
     */
    public void setStrokeData(int width, @Nullable ColorStateList colors) {
        if (hasNativeStateListAPI()) {
            super.setStroke(width, colors);
        } else {
            mStrokeWidth = width;
            mStrokeColors = colors;
            final int currentColor;
            if (colors == null) {
                currentColor = Color.TRANSPARENT;
            } else {
                currentColor = colors.getColorForState(getState(), 0);
            }
            setStroke(width, currentColor);
        }
    }

    private boolean hasNativeStateListAPI() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 设置圆角大小是否自动适应为 View 的高度的一半
     */
    public void setIsRadiusAdjustBounds(boolean isRadiusAdjustBounds) {
        mRadiusAdjustBounds = isRadiusAdjustBounds;
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        boolean superRet = super.onStateChange(stateSet);
        if (mFillColors != null) {
            int color = mFillColors.getColorForState(stateSet, 0);
            setColor(color);
            superRet = true;
        }
        if (mStrokeColors != null) {
            int color = mStrokeColors.getColorForState(stateSet, 0);
            setStroke(mStrokeWidth, color);
            superRet = true;
        }
        return superRet;
    }

    @Override
    public boolean isStateful() {
        return (mFillColors != null && mFillColors.isStateful())
                || (mStrokeColors != null && mStrokeColors.isStateful())
                || super.isStateful();
    }

    @Override
    protected void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        if (mRadiusAdjustBounds) {
            // 修改圆角为短边的一半
            setCornerRadius(Math.min(r.width(), r.height()) / 2F);
        }
    }

    /**
     * 根据 AttributeSet
     *
     * @param context
     * @param attrs
     * @return
     */
    public static RoundDrawable fromAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundButton);
        ColorStateList colorBg = ResUtils.getColorStateListAttrRes(context, typedArray, R.styleable.RoundButton_rb_backgroundColor);
        ColorStateList colorBorder = ResUtils.getColorStateListAttrRes(context, typedArray, R.styleable.RoundButton_rb_borderColor);
        int borderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundButton_rb_borderWidth, ResUtils.getDimensionPixelSize(R.dimen.default_rb_border_width));
        boolean isRadiusAdjustBounds = typedArray.getBoolean(R.styleable.RoundButton_rb_isRadiusAdjustBounds, false);
        int mRadius = typedArray.getDimensionPixelSize(R.styleable.RoundButton_rb_radius, ResUtils.getDimensionPixelSize(R.dimen.default_rb_radius));
        int mRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.RoundButton_rb_radiusTopLeft, 0);
        int mRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.RoundButton_rb_radiusTopRight, 0);
        int mRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.RoundButton_rb_radiusBottomLeft, 0);
        int mRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.RoundButton_rb_radiusBottomRight, 0);
        typedArray.recycle();

        RoundDrawable bg = new RoundDrawable();
        bg.setBgData(colorBg);
        bg.setStrokeData(borderWidth, colorBorder);
        bg.setIsRadiusAdjustBounds(isRadiusAdjustBounds);
        if (mRadiusTopLeft > 0 || mRadiusTopRight > 0 || mRadiusBottomLeft > 0 || mRadiusBottomRight > 0) {
            float[] radii = new float[]{
                    mRadiusTopLeft, mRadiusTopLeft,
                    mRadiusTopRight, mRadiusTopRight,
                    mRadiusBottomRight, mRadiusBottomRight,
                    mRadiusBottomLeft, mRadiusBottomLeft
            };
            bg.setCornerRadii(radii);
        } else {
            bg.setCornerRadius(mRadius);
        }
        return bg;
    }

}
