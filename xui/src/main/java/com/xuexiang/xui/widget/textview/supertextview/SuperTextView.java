package com.xuexiang.xui.widget.textview.supertextview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.xuexiang.xui.R;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.PasswordEditText;

import io.github.inflationx.calligraphy3.HasTypeface;

/**
 * 打造万能的布局满足市面常见的样式
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:05
 */
public class SuperTextView extends RelativeLayout implements HasTypeface {

    private Context mContext;

    private BaseTextView mLeftView, mCenterView, mRightView;
    private LayoutParams mLeftBaseViewParams, mCenterBaseViewParams, mRightBaseViewParams;

    private ImageView mLeftIconIV, mRightIconIV;
    private LayoutParams mLeftImgParams, mRightImgParams;
    /**
     * 左边图标资源
     */
    private Drawable mLeftIconRes;
    /**
     * 左边图标的宽
     */
    private int mLeftIconWidth;
    /**
     * 左边图标的高
     */
    private int mLeftIconHeight;
    /**
     * 左边图标的左边距
     */
    private int mLeftIconMarginLeft;
    private ColorStateList mLeftIconTint;
    private int mLeftIconPadding;
    /**
     * 右边图标资源
     */
    private Drawable mRightIconRes;
    /**
     * 右边图标的宽
     */
    private int mRightIconWidth;
    /**
     * 右边图标的高
     */
    private int mRightIconHeight;
    /**
     * 右边图标的右边距
     */
    private int mRightIconMarginRight;
    private ColorStateList mRightIconTint;
    private int mRightIconPadding;
    /**
     * 文字默认颜色
     */
    private int mDefaultTextColor;
    /**
     * 默认字体大小
     */
    private int mDefaultTextSize;
    /**
     * 默认文字的最大字符数
     */
    private int mDefaultMaxEms;
    /**
     * 默认间距
     */
    private int mDefaultMargin;

    private String mLeftTextString;
    private String mLeftTopTextString;
    private String mLeftBottomTextString;

    private String mRightTextString;
    private String mRightTopTextString;
    private String mRightBottomTextString;

    private String mCenterTextString;
    private String mCenterTopTextString;
    private String mCenterBottomTextString;

    private int mLeftTextColor;
    private int mLeftTopTextColor;
    private int mLeftBottomTextColor;

    private int mCenterTextColor;
    private int mCenterTopTextColor;
    private int mCenterBottomTextColor;

    private int mRightTextColor;
    private int mRightTopTextColor;
    private int mRightBottomTextColor;

    private int mLeftTextSize;
    private int mLeftTopTextSize;
    private int mLeftBottomTextSize;

    private int mRightTextSize;
    private int mRightTopTextSize;
    private int mRightBottomTextSize;

    private int mCenterTextSize;
    private int mCenterTopTextSize;
    private int mCenterBottomTextSize;

    private int mLeftTopLines;
    private int mLeftLines;
    private int mLeftBottomLines;

    private int mCenterTopLines;
    private int mCenterLines;
    private int mCenterBottomLines;

    private int mRightTopLines;
    private int mRightLines;
    private int mRightBottomLines;

    private int mLeftTopMaxEms;
    private int mLeftMaxEms;
    private int mLeftBottomMaxEms;

    private int mCenterTopMaxEms;
    private int mCenterMaxEms;
    private int mCenterBottomMaxEms;

    private int mRightTopMaxEms;
    private int mRightMaxEms;
    private int mRightBottomMaxEms;

    private boolean mLeftTopTextBold;
    private boolean mLeftTextBold;
    private boolean mLeftBottomTextBold;

    private boolean mCenterTopTextBold;
    private boolean mCenterTextBold;
    private boolean mCenterBottomTextBold;

    private boolean mRightTopTextBold;
    private boolean mRightTextBold;
    private boolean mRightBottomTextBold;

    private Drawable mLeftTextBackground;
    private Drawable mCenterTextBackground;
    private Drawable mRightTextBackground;

    private Drawable mLeftTvDrawableLeft;
    private Drawable mLeftTvDrawableRight;

    private Drawable mCenterTvDrawableLeft;
    private Drawable mCenterTvDrawableRight;

    private Drawable mRightTvDrawableLeft;
    private Drawable mRightTvDrawableRight;

    private int mLeftTvDrawableWidth;
    private int mLeftTvDrawableHeight;

    private int mCenterTvDrawableWidth;
    private int mCenterTvDrawableHeight;

    private int mRightTvDrawableWidth;
    private int mRightTvDrawableHeight;

    private int mTextViewDrawablePadding;

    private static final int GRAVITY_LEFT_CENTER = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT_CENTER = 2;
    private static final int DEFAULT_GRAVITY = 1;

    private int mLeftGravity;
    private int mCenterGravity;
    private int mRightGravity;

    private int mLeftViewWidth;

    private View mTopDividerLineView, mBottomDividerLineView;

    private LayoutParams mTopDividerLineParams, mBottomDividerLineParams;
    private int mTopDividerLineMarginLR;
    private int mTopDividerLineMarginLeft;
    private int mTopDividerLineMarginRight;

    private int mBottomDividerLineMarginLR;
    private int mBottomDividerLineMarginLeft;
    private int mBottomDividerLineMarginRight;

    private int mDividerLineType;
    private int mDividerLineColor;
    private int mDividerLineHeight;

    /**
     * 分割线的类型
     */
    private static final int NONE = 0;
    private static final int TOP = 1;
    private static final int BOTTOM = 2;
    private static final int BOTH = 3;
    private static final int DEFAULT_DIVIDER = BOTTOM;

    private int mLeftViewMarginLeft;
    private int mLeftViewMarginRight;

    private int mCenterViewMarginLeft;
    private int mCenterViewMarginRight;

    private int mRightViewMarginLeft;
    private int mRightViewMarginRight;


    private boolean mUseRipple;
    private Drawable mBackgroundDrawable;

    private OnSuperTextViewClickListener mSuperTextViewClickListener;

    private OnLeftTopTvClickListener mLeftTopTvClickListener;
    private OnLeftTvClickListener mLeftTvClickListener;
    private OnLeftBottomTvClickListener mLeftBottomTvClickListener;

    private OnCenterTopTvClickListener mCenterTopTvClickListener;
    private OnCenterTvClickListener mCenterTvClickListener;
    private OnCenterBottomTvClickListener mCenterBottomTvClickListener;

    private OnRightTopTvClickListener mRightTopTvClickListener;
    private OnRightTvClickListener mRightTvClickListener;
    private OnRightBottomTvClickListener mRightBottomTvClickListener;

    private CompoundButton.OnCheckedChangeListener mSwitchCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mCheckBoxCheckedChangeListener;

    private OnLeftImageViewClickListener mLeftImageViewClickListener;
    private OnRightImageViewClickListener mRightImageViewClickListener;

    private boolean mEnableEdit = false;
    /**
     * 输入框
     */
    private EditText mCenterEditText;
    private int mEditTextWidth = LayoutParams.MATCH_PARENT;
    private static final int TYPE_NONE = 0;
    private static final int TYPE_CLEAR = 1;
    private static final int TYPE_PASSWORD = 2;
    /**
     * 编辑输入框类型
     */
    private int mEditTextButtonType = TYPE_CLEAR;
    /**
     * 输入框的背景
     */
    private Drawable mEditBackground;
    private String mEditTextHint;
    private String mEditTextString;
    private int mEditTextInputType;
    /**
     * 密码输入框文字的样式是否是“*”
     */
    private boolean mIsAsteriskStyle;

    private static final int TYPE_CHECKBOX = 0;
    private static final int TYPE_SWITCH = 1;

    private int mRightViewType;
    /**
     * 右边checkbox
     */
    private CheckBox mRightCheckBox;
    /**
     * checkBox的背景
     */
    private Drawable mRightCheckBoxBg;
    /**
     * 右边checkBox的右边距
     */
    private int mRightCheckBoxMarginRight;
    /**
     * 是否默认选中
     */
    private boolean mIsChecked;
    /**
     * 中间空间的高度
     */
    private int mCenterSpaceHeight;

    /**
     * 右边switch
     */
    private Switch mRightSwitch;
    private int mRightSwitchMarginRight;
    private boolean mSwitchIsChecked;

    /**
     * Switch开关关闭的文字提示
     */
    private String mSwitchTextOff;
    /**
     * Switch开关打开的文字提示
     */
    private String mSwitchTextOn;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mThumbTextPadding;
    /**
     * Switch开关的滑块样式
     */
    private Drawable mSwitchThumbResource;
    /**
     * Switch开关的底层样式
     */
    private Drawable mSwitchTrackResource;

    //====================一下是shape相关属性==================//

    private int mDefaultShapeColor;

    private int mSelectorPressedColor;
    private int mSelectorNormalColor;

    private int mSolidColor;

    private float mCornersRadius;
    private float mCornersTopLeftRadius;
    private float mCornersTopRightRadius;
    private float mCornersBottomLeftRadius;
    private float mCornersBottomRightRadius;

    private int mStrokeWidth;
    private int mStrokeColor;

    private float mStrokeDashWidth;
    private float mStrokeDashGap;

    private boolean mUseShape;

    private GradientDrawable mGradientDrawable;

    public SuperTextView(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public SuperTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;

        mDefaultTextColor = ThemeUtils.resolveColor(context, R.attr.stv_color_common_text, ResUtils.getColor(R.color.stv_color_common_text));
        mDefaultTextSize = ThemeUtils.resolveDimension(context, R.attr.stv_text_size, ResUtils.getDimensionPixelSize(R.dimen.default_stv_text_size));
        mDefaultMaxEms = ThemeUtils.resolveInt(context, R.attr.stv_max_ems, 20);
        mDefaultMargin = ThemeUtils.resolveDimension(context, R.attr.stv_margin, ResUtils.getDimensionPixelSize(R.dimen.default_stv_margin));
        mDefaultShapeColor = ThemeUtils.resolveColor(context, R.attr.stv_color_shape, ResUtils.getColor(R.color.xui_config_color_white));

        getAttr(attrs);
        initView();
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SuperTextView);

        /////////////////////////////////////////////////
        mLeftTextString = typedArray.getString(R.styleable.SuperTextView_sLeftTextString);
        mLeftTopTextString = typedArray.getString(R.styleable.SuperTextView_sLeftTopTextString);
        mLeftBottomTextString = typedArray.getString(R.styleable.SuperTextView_sLeftBottomTextString);

        mCenterTextString = typedArray.getString(R.styleable.SuperTextView_sCenterTextString);
        mCenterTopTextString = typedArray.getString(R.styleable.SuperTextView_sCenterTopTextString);
        mCenterBottomTextString = typedArray.getString(R.styleable.SuperTextView_sCenterBottomTextString);

        mRightTextString = typedArray.getString(R.styleable.SuperTextView_sRightTextString);
        mRightTopTextString = typedArray.getString(R.styleable.SuperTextView_sRightTopTextString);
        mRightBottomTextString = typedArray.getString(R.styleable.SuperTextView_sRightBottomTextString);

        //////////////////////////////////////////////////

        mLeftTextColor = typedArray.getColor(R.styleable.SuperTextView_sLeftTextColor, mDefaultTextColor);
        mLeftTopTextColor = typedArray.getColor(R.styleable.SuperTextView_sLeftTopTextColor, mDefaultTextColor);
        mLeftBottomTextColor = typedArray.getColor(R.styleable.SuperTextView_sLeftBottomTextColor, mDefaultTextColor);

        mCenterTextColor = typedArray.getColor(R.styleable.SuperTextView_sCenterTextColor, mDefaultTextColor);
        mCenterTopTextColor = typedArray.getColor(R.styleable.SuperTextView_sCenterTopTextColor, mDefaultTextColor);
        mCenterBottomTextColor = typedArray.getColor(R.styleable.SuperTextView_sCenterBottomTextColor, mDefaultTextColor);

        mRightTextColor = typedArray.getColor(R.styleable.SuperTextView_sRightTextColor, mDefaultTextColor);
        mRightTopTextColor = typedArray.getColor(R.styleable.SuperTextView_sRightTopTextColor, mDefaultTextColor);
        mRightBottomTextColor = typedArray.getColor(R.styleable.SuperTextView_sRightBottomTextColor, mDefaultTextColor);

        //////////////////////////////////////////////////


        mLeftTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftTextSize, mDefaultTextSize);
        mLeftTopTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftTopTextSize, mDefaultTextSize);
        mLeftBottomTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftBottomTextSize, mDefaultTextSize);

        mCenterTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterTextSize, mDefaultTextSize);
        mCenterTopTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterTopTextSize, mDefaultTextSize);
        mCenterBottomTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterBottomTextSize, mDefaultTextSize);

        mRightTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightTextSize, mDefaultTextSize);
        mRightTopTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightTopTextSize, mDefaultTextSize);
        mRightBottomTextSize = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightBottomTextSize, mDefaultTextSize);

        //////////////////////////////////////////////////
        mLeftTopLines = typedArray.getInt(R.styleable.SuperTextView_sLeftTopLines, 1);
        mLeftLines = typedArray.getInt(R.styleable.SuperTextView_sLeftLines, 1);
        mLeftBottomLines = typedArray.getInt(R.styleable.SuperTextView_sLeftBottomLines, 1);

        mCenterTopLines = typedArray.getInt(R.styleable.SuperTextView_sCenterTopLines, 1);
        mCenterLines = typedArray.getInt(R.styleable.SuperTextView_sCenterLines, 1);
        mCenterBottomLines = typedArray.getInt(R.styleable.SuperTextView_sCenterBottomLines, 1);

        mRightTopLines = typedArray.getInt(R.styleable.SuperTextView_sRightTopLines, 1);
        mRightLines = typedArray.getInt(R.styleable.SuperTextView_sRightLines, 1);
        mRightBottomLines = typedArray.getInt(R.styleable.SuperTextView_sRightBottomLines, 1);

        //////////////////////////////////////////////////

        mLeftTopMaxEms = typedArray.getInt(R.styleable.SuperTextView_sLeftTopMaxEms, mDefaultMaxEms);
        mLeftMaxEms = typedArray.getInt(R.styleable.SuperTextView_sLeftMaxEms, mDefaultMaxEms);
        mLeftBottomMaxEms = typedArray.getInt(R.styleable.SuperTextView_sLeftBottomMaxEms, mDefaultMaxEms);

        mCenterTopMaxEms = typedArray.getInt(R.styleable.SuperTextView_sCenterTopMaxEms, mDefaultMaxEms);
        mCenterMaxEms = typedArray.getInt(R.styleable.SuperTextView_sCenterMaxEms, mDefaultMaxEms);
        mCenterBottomMaxEms = typedArray.getInt(R.styleable.SuperTextView_sCenterBottomMaxEms, mDefaultMaxEms);

        mRightTopMaxEms = typedArray.getInt(R.styleable.SuperTextView_sRightTopMaxEms, mDefaultMaxEms);
        mRightMaxEms = typedArray.getInt(R.styleable.SuperTextView_sRightMaxEms, mDefaultMaxEms);
        mRightBottomMaxEms = typedArray.getInt(R.styleable.SuperTextView_sRightBottomMaxEms, mDefaultMaxEms);

        ////////////////////////////////////////////////

        mLeftGravity = typedArray.getInt(R.styleable.SuperTextView_sLeftViewGravity, DEFAULT_GRAVITY);
        mCenterGravity = typedArray.getInt(R.styleable.SuperTextView_sCenterViewGravity, DEFAULT_GRAVITY);
        mRightGravity = typedArray.getInt(R.styleable.SuperTextView_sRightViewGravity, DEFAULT_GRAVITY);

        ////////////////////////////////////////////////

        mLeftTvDrawableLeft = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sLeftTvDrawableLeft);
        mLeftTvDrawableRight = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sLeftTvDrawableRight);
        mCenterTvDrawableLeft = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sCenterTvDrawableLeft);
        mCenterTvDrawableRight = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sCenterTvDrawableRight);
        mRightTvDrawableLeft = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sRightTvDrawableLeft);
        mRightTvDrawableRight = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sRightTvDrawableRight);

        mTextViewDrawablePadding = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sTextViewDrawablePadding, mDefaultMargin);
        ////////////////////////////////////////////////

        mLeftTvDrawableWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftTvDrawableWidth, -1);
        mLeftTvDrawableHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftTvDrawableHeight, -1);

        mCenterTvDrawableWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterTvDrawableWidth, -1);
        mCenterTvDrawableHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterTvDrawableHeight, -1);

        mRightTvDrawableWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightTvDrawableWidth, -1);
        mRightTvDrawableHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightTvDrawableHeight, -1);

        mLeftViewWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftViewWidth, 0);
        ///////////////////////////////////////////////
        mTopDividerLineMarginLR = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sTopDividerLineMarginLR, 0);
        mTopDividerLineMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sTopDividerLineMarginLeft, 0);
        mTopDividerLineMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sTopDividerLineMarginRight, 0);

        mBottomDividerLineMarginLR = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sBottomDividerLineMarginLR, 0);
        mBottomDividerLineMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sBottomDividerLineMarginLeft, 0);
        mBottomDividerLineMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sBottomDividerLineMarginRight, 0);
        ///////////////////////////////////////////////
        mDividerLineType = typedArray.getInt(R.styleable.SuperTextView_sDividerLineType, DEFAULT_DIVIDER);
        mDividerLineColor = typedArray.getColor(R.styleable.SuperTextView_sDividerLineColor, ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_separator_light));

        mDividerLineHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sDividerLineHeight, DensityUtils.dp2px(mContext, 0.5f));
        ////////////////////////////////////////////////
        mLeftViewMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftViewMarginLeft, mDefaultMargin);
        mLeftViewMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftViewMarginRight, mDefaultMargin);
        mCenterViewMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterViewMarginLeft, 0);
        mCenterViewMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterViewMarginRight, 0);
        mRightViewMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightViewMarginLeft, mDefaultMargin);
        mRightViewMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightViewMarginRight, mDefaultMargin);
        ///////////////////////////////////////////////
        mLeftIconRes = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sLeftIconRes);
        mLeftIconWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftIconWidth, 0);
        mLeftIconHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftIconHeight, 0);
        mLeftIconMarginLeft = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftIconMarginLeft, mDefaultMargin);
        mLeftIconTint = ResUtils.getColorStateListAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sLeftIconTint);
        mLeftIconPadding = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftIconPadding, 0);

        mRightIconRes = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sRightIconRes);
        mRightIconWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightIconWidth, 0);
        mRightIconHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightIconHeight, 0);
        mRightIconMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightIconMarginRight, mDefaultMargin);
        mRightIconTint = ResUtils.getColorStateListAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sLeftIconTint);
        mRightIconPadding = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sLeftIconPadding, 0);

        //////////////////////////////////////////////
        mLeftTopTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sLeftTopTextIsBold, false);
        mLeftTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sLeftTextIsBold, false);
        mLeftBottomTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sLeftBottomTextIsBold, false);

        mCenterTopTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sCenterTopTextIsBold, false);
        mCenterTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sCenterTextIsBold, false);
        mCenterBottomTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sCenterBottomTextIsBold, false);

        mRightTopTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sRightTopTextIsBold, false);
        mRightTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sRightTextIsBold, false);
        mRightBottomTextBold = typedArray.getBoolean(R.styleable.SuperTextView_sRightBottomTextIsBold, false);

        mLeftTextBackground = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sLeftTextBackground);
        mCenterTextBackground = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sCenterTextBackground);
        mRightTextBackground = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sRightTextBackground);

        ////////////////////////////////////////////////////

        mEnableEdit = typedArray.getBoolean(R.styleable.SuperTextView_sEnableEdit, mEnableEdit);
        mEditBackground = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sEditBackGround);

        mEditTextWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sEditTextWidth, mEditTextWidth);
        mEditTextString = typedArray.getString(R.styleable.SuperTextView_sEditTextString);
        mEditTextHint = typedArray.getString(R.styleable.SuperTextView_sEditTextHint);
        mEditTextInputType = typedArray.getInt(R.styleable.SuperTextView_android_inputType, -1);
        mEditTextButtonType = typedArray.getInt(R.styleable.SuperTextView_sEditTextButtonType, mEditTextButtonType);
        mIsAsteriskStyle = typedArray.getBoolean(R.styleable.SuperTextView_sEditTextIsAsteriskStyle, mIsAsteriskStyle);

        //////////////////////////////////////////////
        mUseRipple = typedArray.getBoolean(R.styleable.SuperTextView_sUseRipple, true);
        mBackgroundDrawable = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sBackgroundDrawableRes);
        ///////////////////////////////////////////////
        mRightViewType = typedArray.getInt(R.styleable.SuperTextView_sRightViewType, -1);
        ////////////////////////////////////////////////
        mIsChecked = typedArray.getBoolean(R.styleable.SuperTextView_sIsChecked, false);
        mRightCheckBoxMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightCheckBoxMarginRight, mDefaultMargin);
        mRightCheckBoxBg = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sRightCheckBoxRes);
        //////////////////////////////////////////////////
        mRightSwitchMarginRight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sRightSwitchMarginRight, mDefaultMargin);
        mSwitchIsChecked = typedArray.getBoolean(R.styleable.SuperTextView_sSwitchIsChecked, false);
        mSwitchTextOff = typedArray.getString(R.styleable.SuperTextView_sTextOff);
        mSwitchTextOn = typedArray.getString(R.styleable.SuperTextView_sTextOn);

        mSwitchMinWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sSwitchMinWidth, 0);
        mSwitchPadding = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sSwitchPadding, 0);
        mThumbTextPadding = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sThumbTextPadding, 0);

        mSwitchThumbResource = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sThumbResource);
        mSwitchTrackResource = ResUtils.getDrawableAttrRes(getContext(), typedArray, R.styleable.SuperTextView_sTrackResource);

        mCenterSpaceHeight = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sCenterSpaceHeight, DensityUtils.dp2px(mContext, 5));
        ////////////////////////////////////////////////////
        mSelectorPressedColor = typedArray.getColor(R.styleable.SuperTextView_sShapeSelectorPressedColor, mDefaultShapeColor);
        mSelectorNormalColor = typedArray.getColor(R.styleable.SuperTextView_sShapeSelectorNormalColor, mDefaultShapeColor);

        mSolidColor = typedArray.getColor(R.styleable.SuperTextView_sShapeSolidColor, mDefaultShapeColor);

        mCornersRadius = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeCornersRadius, 0);
        mCornersTopLeftRadius = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeCornersTopLeftRadius, 0);
        mCornersTopRightRadius = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeCornersTopRightRadius, 0);
        mCornersBottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeCornersBottomLeftRadius, 0);
        mCornersBottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeCornersBottomRightRadius, 0);

        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeStrokeWidth, 0);
        mStrokeDashWidth = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeStrokeDashWidth, 0);
        mStrokeDashGap = typedArray.getDimensionPixelSize(R.styleable.SuperTextView_sShapeStrokeDashGap, 0);

        mStrokeColor = typedArray.getColor(R.styleable.SuperTextView_sShapeStrokeColor, mDefaultShapeColor);

        mUseShape = typedArray.getBoolean(R.styleable.SuperTextView_sUseShape, false);

        typedArray.recycle();
    }

    /**
     * 初始化Params
     *
     * @param params params
     * @return params
     */
    private LayoutParams getParams(LayoutParams params) {
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        return params;
    }

    /**
     * 初始化View
     */
    private void initView() {

        initSuperTextView();

        initLeftIcon();

        initExtraView();

        initRightIcon();

        initLeftTextView();
        initCenterTextView();
        initRightTextView();

        initDividerLineView();

    }


    private void initSuperTextView() {
        if (mUseRipple) {
            this.setBackgroundResource(R.drawable.stv_btn_selector_white);
            this.setClickable(true);
        }

        if (mBackgroundDrawable != null) {
            this.setBackgroundDrawable(mBackgroundDrawable);
        }

        if (mUseShape) {
            if (Build.VERSION.SDK_INT < 16) {
                setBackgroundDrawable(getSelector());
            } else {
                setBackground(getSelector());
            }
        }
    }

    /**
     * 初始化左边图标
     */
    private void initLeftIcon() {
        if (mLeftIconIV == null) {
            mLeftIconIV = new AppCompatImageView(mContext);
        }
        mLeftImgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLeftImgParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
        mLeftImgParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        if (mLeftIconHeight != 0 && mLeftIconWidth != 0) {
            mLeftImgParams.width = mLeftIconWidth;
            mLeftImgParams.height = mLeftIconHeight;
        }
        mLeftIconIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mLeftIconIV.setId(R.id.sLeftImgId);
        mLeftIconIV.setLayoutParams(mLeftImgParams);
        mLeftIconIV.setPadding(mLeftIconPadding, mLeftIconPadding, mLeftIconPadding, mLeftIconPadding);
        if (mLeftIconRes != null) {
            mLeftImgParams.setMargins(mLeftIconMarginLeft, 0, 0, 0);
            mLeftImgParams.setMarginStart(mLeftIconMarginLeft);
            mLeftIconIV.setImageDrawable(mLeftIconRes);
        }
        if (mLeftIconTint != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mLeftIconIV.setImageTintList(mLeftIconTint);
            }
        }
        addView(mLeftIconIV);
    }

    /**
     * 初始化右边图标
     */
    private void initRightIcon() {
        if (mRightIconIV == null) {
            mRightIconIV = new AppCompatImageView(mContext);
        }
        mRightImgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mRightImgParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);

        switch (mRightViewType) {
            case TYPE_CHECKBOX:
                mRightImgParams.addRule(RelativeLayout.START_OF, R.id.sRightCheckBoxId);
                break;
            case TYPE_SWITCH:
                mRightImgParams.addRule(RelativeLayout.START_OF, R.id.sRightSwitchId);
                break;
            default:
                mRightImgParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);

        }

        if (mRightIconHeight != 0 && mRightIconWidth != 0) {
            mRightImgParams.width = mRightIconWidth;
            mRightImgParams.height = mRightIconHeight;
        }

        mRightIconIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mRightIconIV.setId(R.id.sRightImgId);
        mRightIconIV.setLayoutParams(mRightImgParams);
        mRightIconIV.setPadding(mRightIconPadding, mRightIconPadding, mRightIconPadding, mRightIconPadding);
        if (mRightIconRes != null) {
            mRightImgParams.setMargins(0, 0, mRightIconMarginRight, 0);
            mRightImgParams.setMarginEnd(mRightIconMarginRight);
            mRightIconIV.setImageDrawable(mRightIconRes);
        }
        if (mRightIconTint != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRightIconIV.setImageTintList(mRightIconTint);
            }
        }
        addView(mRightIconIV);
    }

    /**
     * 初始化LeftTextView
     */
    private void initLeftTextView() {
        if (mLeftView == null) {
            mLeftView = initBaseView(R.id.sLeftViewId);
        }
        mLeftBaseViewParams = getParams(mLeftBaseViewParams);
        mLeftBaseViewParams.addRule(RelativeLayout.END_OF, R.id.sLeftImgId);
        mLeftBaseViewParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        if (mLeftViewWidth != 0) {
            mLeftBaseViewParams.width = mLeftViewWidth;
        }
        mLeftBaseViewParams.setMargins(mLeftViewMarginLeft, 0, mLeftViewMarginRight, 0);

        mLeftView.setLayoutParams(mLeftBaseViewParams);
        mLeftView.setCenterSpaceHeight(mCenterSpaceHeight);
        setDefaultColor(mLeftView, mLeftTopTextColor, mLeftTextColor, mLeftBottomTextColor);
        setDefaultSize(mLeftView, mLeftTopTextSize, mLeftTextSize, mLeftBottomTextSize);
        setDefaultLines(mLeftView, mLeftTopLines, mLeftLines, mLeftBottomLines);
        setDefaultMaxEms(mLeftView, mLeftTopMaxEms, mLeftMaxEms, mLeftBottomMaxEms);
        setDefaultTextIsBold(mLeftView, mLeftTopTextBold, mLeftTextBold, mLeftBottomTextBold);
        setDefaultGravity(mLeftView, mLeftGravity);
        setDefaultDrawable(mLeftView.getCenterTextView(), mLeftTvDrawableLeft, mLeftTvDrawableRight, mTextViewDrawablePadding, mLeftTvDrawableWidth, mLeftTvDrawableHeight);
        setDefaultBackground(mLeftView.getCenterTextView(), mLeftTextBackground);
        setDefaultString(mLeftView, mLeftTopTextString, mLeftTextString, mLeftBottomTextString);

        addView(mLeftView);
    }


    /**
     * 初始化CenterTextView
     */
    private void initCenterTextView() {
        if (mEnableEdit) {
            if (mCenterEditText == null) {
                if (mEditTextButtonType == TYPE_NONE) {
                    mCenterEditText = new AppCompatEditText(mContext);
                } else if (mEditTextButtonType == TYPE_CLEAR) {
                    mCenterEditText = new ClearEditText(mContext);
                } else if (mEditTextButtonType == TYPE_PASSWORD) {
                    mCenterEditText = new PasswordEditText(mContext);
                    ((PasswordEditText) mCenterEditText).setIsAsteriskStyle(mIsAsteriskStyle);
                }
            }
            /**
             * 输入框布局参数
             */
            LayoutParams mCenterEditTextParams = new LayoutParams(mEditTextWidth, LayoutParams.WRAP_CONTENT);

            mCenterEditTextParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
            mCenterEditTextParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
            if (mCenterGravity != GRAVITY_CENTER) {
                mCenterEditTextParams.addRule(END_OF, R.id.sLeftViewId);
                mCenterEditTextParams.addRule(START_OF, R.id.sRightViewId);
            }
            mCenterEditTextParams.setMargins(mCenterViewMarginLeft, 0, mCenterViewMarginRight, 0);
            mCenterEditTextParams.setMarginStart(mCenterViewMarginLeft);
            mCenterEditTextParams.setMarginEnd(mCenterViewMarginRight);
            mCenterEditText.setId(R.id.sCenterEditTextId);
            mCenterEditText.setLayoutParams(mCenterEditTextParams);

            if (mEditBackground != null) {
                mCenterEditText.setBackground(mEditBackground);
            } else {
                mCenterEditText.setBackgroundColor(ResUtils.getColor(R.color.xui_config_color_transparent));
            }
            mCenterEditText.setTextColor(mCenterTextColor);
            mCenterEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCenterTextSize);
            mCenterEditText.setMaxLines(mCenterLines);
            mCenterEditText.setText(mEditTextString);
            mCenterEditText.setHint(mEditTextHint);
            if (mEditTextInputType != -1) {
                mCenterEditText.setInputType(mEditTextInputType);
            }
            addView(mCenterEditText);
        } else {
            if (mCenterView == null) {
                mCenterView = initBaseView(R.id.sCenterViewId);
            }
            mCenterBaseViewParams = getParams(mCenterBaseViewParams);
            mCenterBaseViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
            mCenterBaseViewParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);

            //默认情况下  中间的View整体剧中显示，设置左对齐或者右对齐的话使用下边属性
            if (mCenterGravity != GRAVITY_CENTER) {
                mCenterBaseViewParams.addRule(END_OF, R.id.sLeftViewId);
                mCenterBaseViewParams.addRule(START_OF, R.id.sRightViewId);
            }

            mCenterBaseViewParams.setMargins(mCenterViewMarginLeft, 0, mCenterViewMarginRight, 0);
            mCenterBaseViewParams.setMarginStart(mCenterViewMarginLeft);
            mCenterBaseViewParams.setMarginEnd(mCenterViewMarginRight);
            mCenterView.setLayoutParams(mCenterBaseViewParams);
            mCenterView.setCenterSpaceHeight(mCenterSpaceHeight);

            setDefaultColor(mCenterView, mCenterTopTextColor, mCenterTextColor, mCenterBottomTextColor);
            setDefaultSize(mCenterView, mCenterTopTextSize, mCenterTextSize, mCenterBottomTextSize);
            setDefaultLines(mCenterView, mCenterTopLines, mCenterLines, mCenterBottomLines);
            setDefaultMaxEms(mCenterView, mCenterTopMaxEms, mCenterMaxEms, mCenterBottomMaxEms);
            setDefaultTextIsBold(mCenterView, mCenterTopTextBold, mCenterTextBold, mCenterBottomTextBold);
            setDefaultGravity(mCenterView, mCenterGravity);
            setDefaultDrawable(mCenterView.getCenterTextView(), mCenterTvDrawableLeft, mCenterTvDrawableRight, mTextViewDrawablePadding, mCenterTvDrawableWidth, mCenterTvDrawableHeight);
            setDefaultBackground(mCenterView.getCenterTextView(), mCenterTextBackground);
            setDefaultString(mCenterView, mCenterTopTextString, mCenterTextString, mCenterBottomTextString);

            addView(mCenterView);
        }
    }

    /**
     * 初始化RightTextView
     */
    private void initRightTextView() {
        if (mRightView == null) {
            mRightView = initBaseView(R.id.sRightViewId);
        }
        mRightBaseViewParams = getParams(mRightBaseViewParams);
        mRightBaseViewParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);

        mRightBaseViewParams.addRule(RelativeLayout.START_OF, R.id.sRightImgId);
        mRightBaseViewParams.setMargins(mRightViewMarginLeft, 0, mRightViewMarginRight, 0);
        mRightBaseViewParams.setMarginStart(mRightViewMarginLeft);
        mRightBaseViewParams.setMarginEnd(mRightViewMarginRight);
        mRightView.setLayoutParams(mRightBaseViewParams);
        mRightView.setCenterSpaceHeight(mCenterSpaceHeight);

        setDefaultColor(mRightView, mRightTopTextColor, mRightTextColor, mRightBottomTextColor);
        setDefaultSize(mRightView, mRightTopTextSize, mRightTextSize, mRightBottomTextSize);
        setDefaultLines(mRightView, mRightTopLines, mRightLines, mRightBottomLines);
        setDefaultMaxEms(mRightView, mRightTopMaxEms, mRightMaxEms, mRightBottomMaxEms);
        setDefaultTextIsBold(mRightView, mRightTopTextBold, mRightTextBold, mRightBottomTextBold);
        setDefaultGravity(mRightView, mRightGravity);
        setDefaultDrawable(mRightView.getCenterTextView(), mRightTvDrawableLeft, mRightTvDrawableRight, mTextViewDrawablePadding, mRightTvDrawableWidth, mRightTvDrawableHeight);
        setDefaultBackground(mRightView.getCenterTextView(), mRightTextBackground);
        setDefaultString(mRightView, mRightTopTextString, mRightTextString, mRightBottomTextString);

        addView(mRightView);
    }

    /**
     * 初始化拓展控件
     */
    private void initExtraView() {
        switch (mRightViewType) {
            case TYPE_CHECKBOX:
                initRightCheckBox();
                break;
            case TYPE_SWITCH:
                initRightSwitch();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化RightCheckBox
     */
    private void initRightCheckBox() {
        if (mRightCheckBox == null) {
            mRightCheckBox = new CheckBox(mContext);
        }
        //右边checkbox
        LayoutParams mRightCheckBoxParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        mRightCheckBoxParams.addRule(ALIGN_PARENT_END, TRUE);
        mRightCheckBoxParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        mRightCheckBoxParams.setMargins(0, 0, mRightCheckBoxMarginRight, 0);
        mRightCheckBoxParams.setMarginEnd(mRightCheckBoxMarginRight);
        mRightCheckBox.setId(R.id.sRightCheckBoxId);
        mRightCheckBox.setLayoutParams(mRightCheckBoxParams);
        if (mRightCheckBoxBg != null) {
            mRightCheckBox.setGravity(CENTER_IN_PARENT);
            mRightCheckBox.setButtonDrawable(mRightCheckBoxBg);
        }
        mRightCheckBox.setChecked(mIsChecked);
        mRightCheckBox.setOnCheckedChangeListener(mCheckBoxCheckedChangeListener);
        addView(mRightCheckBox);
    }

    /**
     * 初始化RightSwitch
     */
    private void initRightSwitch() {
        if (mRightSwitch == null) {
            mRightSwitch = new Switch(mContext);
        }
        LayoutParams mRightSwitchParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        mRightSwitchParams.addRule(ALIGN_PARENT_END, TRUE);
        mRightSwitchParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
        mRightSwitchParams.setMargins(0, 0, mRightSwitchMarginRight, 0);
        mRightSwitchParams.setMarginEnd(mRightSwitchMarginRight);
        mRightSwitch.setId(R.id.sRightSwitchId);
        mRightSwitch.setLayoutParams(mRightSwitchParams);

        mRightSwitch.setChecked(mSwitchIsChecked);
        if (!TextUtils.isEmpty(mSwitchTextOff)) {
            mRightSwitch.setTextOff(mSwitchTextOff);
        }
        if (!TextUtils.isEmpty(mSwitchTextOn)) {
            mRightSwitch.setTextOn(mSwitchTextOn);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (mSwitchMinWidth != 0) {
                mRightSwitch.setSwitchMinWidth(mSwitchMinWidth);
            }
            if (mSwitchPadding != 0) {
                mRightSwitch.setSwitchPadding(mSwitchPadding);
            }
            if (mSwitchThumbResource != null) {
                mRightSwitch.setThumbDrawable(mSwitchThumbResource);
            }
            if (mSwitchThumbResource != null) {
                mRightSwitch.setTrackDrawable(mSwitchTrackResource);
            }
            if (mThumbTextPadding != 0) {
                mRightSwitch.setThumbTextPadding(mThumbTextPadding);
            }

        }
        mRightSwitch.setOnCheckedChangeListener(mSwitchCheckedChangeListener);

        addView(mRightSwitch);
    }

    /////////////////////////////////////默认属性设置----begin/////////////////////////////////

    /**
     * 初始化BaseTextView
     *
     * @param id id
     * @return baseTextView
     */
    private BaseTextView initBaseView(int id) {
        BaseTextView baseTextView = new BaseTextView(mContext);
        baseTextView.setId(id);
        return baseTextView;
    }

    /**
     * 设置默认值
     *
     * @param baseTextView     baseTextView
     * @param topTextString    topTextString
     * @param leftTextString   leftTextString
     * @param bottomTextString bottomTextString
     */
    private void setDefaultString(BaseTextView baseTextView, String topTextString, String leftTextString, String bottomTextString) {
        if (baseTextView != null) {
            baseTextView.setTopTextString(topTextString);
            baseTextView.setCenterTextString(leftTextString);
            baseTextView.setBottomTextString(bottomTextString);
        }
    }

    /**
     * 设置默认
     *
     * @param baseTextView    baseTextView
     * @param topTextColor    topTextColor
     * @param textColor       textColor
     * @param bottomTextColor bottomTextColor
     */
    private void setDefaultColor(BaseTextView baseTextView, int topTextColor, int textColor, int bottomTextColor) {
        if (baseTextView != null) {
            baseTextView.getTopTextView().setTextColor(topTextColor);
            baseTextView.getCenterTextView().setTextColor(textColor);
            baseTextView.getBottomTextView().setTextColor(bottomTextColor);
        }
    }

    /**
     * 设置默认字体大小
     *
     * @param baseTextView   baseTextView
     * @param leftTextSize   leftTextSize
     * @param topTextSize    topTextSize
     * @param bottomTextSize bottomTextSize
     */
    private void setDefaultSize(BaseTextView baseTextView, int topTextSize, int leftTextSize, int bottomTextSize) {
        if (baseTextView != null) {
            baseTextView.getTopTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, topTextSize);
            baseTextView.getCenterTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
            baseTextView.getBottomTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, bottomTextSize);
        }
    }

    /**
     * 设置默认maxEms
     *
     * @param baseTextView baseTextView
     * @param topMaxEms    topMaxEms
     * @param centerMaxEms centerMaxEms
     * @param bottomMaxEms bottomMaxEms
     */
    private void setDefaultMaxEms(BaseTextView baseTextView, int topMaxEms, int centerMaxEms, int bottomMaxEms) {
        if (baseTextView != null) {
            baseTextView.setMaxEms(topMaxEms, centerMaxEms, bottomMaxEms);
        }

    }

    /**
     * 设置默认lines
     *
     * @param baseTextView baseTextView
     * @param leftTopLines leftTopLines
     * @param leftLines    leftLines
     * @param bottomLines  bottomLines
     */
    private void setDefaultLines(BaseTextView baseTextView, int leftTopLines, int leftLines, int bottomLines) {
        if (baseTextView != null) {
            baseTextView.getTopTextView().setMaxLines(leftTopLines);
            baseTextView.getCenterTextView().setMaxLines(leftLines);
            baseTextView.getBottomTextView().setMaxLines(bottomLines);
        }

    }

    /**
     * 设置文字对其方式
     *
     * @param baseTextView baseTextView
     * @param gravity      对其方式
     */
    private void setDefaultGravity(BaseTextView baseTextView, int gravity) {
        if (baseTextView != null) {
            setGravity(baseTextView, gravity);
        }
    }

    /**
     * 文字对其方式
     *
     * @param baseTextView textView
     * @param gravity      对其方式
     */
    private void setGravity(BaseTextView baseTextView, int gravity) {
        switch (gravity) {
            case GRAVITY_LEFT_CENTER:
                baseTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                break;
            case GRAVITY_CENTER:
                baseTextView.setGravity(Gravity.CENTER);
                break;
            case GRAVITY_RIGHT_CENTER:
                baseTextView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                break;
            default:
                break;
        }
    }

    /**
     * 设置textView的drawable
     *
     * @param textView        对象
     * @param drawableLeft    左边图标
     * @param drawableRight   右边图标
     * @param drawablePadding 图标距离文字的间距
     */
    public void setDefaultDrawable(TextView textView, Drawable drawableLeft, Drawable drawableRight, int drawablePadding, int drawableWidth, int drawableHeight) {
        if (drawableLeft != null || drawableRight != null) {
            textView.setVisibility(VISIBLE);
        }
        //可以指定drawable的宽高
        if (drawableWidth != -1 && drawableHeight != -1) {
            if (drawableLeft != null) {
                drawableLeft.setBounds(0, 0, drawableWidth, drawableHeight);
            }
            if (drawableRight != null) {
                drawableRight.setBounds(0, 0, drawableWidth, drawableHeight);
            }
            textView.setCompoundDrawables(drawableLeft, null, drawableRight, null);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null);
        }
        textView.setCompoundDrawablePadding(drawablePadding);
    }

    /**
     * 设置textView的背景，用户传入drawable实现圆角之类的样式
     *
     * @param textView
     * @param background
     */
    private void setDefaultBackground(TextView textView, Drawable background) {
        if (background != null) {
            textView.setVisibility(VISIBLE);
            if (Build.VERSION.SDK_INT < 16) {
                textView.setBackgroundDrawable(background);
            } else {
                textView.setBackground(background);
            }
        }
    }

    /**
     * 初始化分割线
     */
    private void initDividerLineView() {
        if (!mUseShape) {
            switch (mDividerLineType) {
                case NONE:
                    break;
                case TOP:
                    setTopDividerLineView();
                    break;
                case BOTTOM:
                    setBottomDividerLineView();
                    break;
                case BOTH:
                    setTopDividerLineView();
                    setBottomDividerLineView();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 设置上边的分割线
     */
    private void setTopDividerLineView() {
        if (mTopDividerLineMarginLR != 0) {
            initTopDividerLineView(mTopDividerLineMarginLR, mTopDividerLineMarginLR);
        } else {
            initTopDividerLineView(mTopDividerLineMarginLeft, mTopDividerLineMarginRight);
        }
    }

    /**
     * 设置下边的分割线
     */
    private void setBottomDividerLineView() {
        if (mBottomDividerLineMarginLR != 0) {
            initBottomDividerLineView(mBottomDividerLineMarginLR, mBottomDividerLineMarginLR);
        } else {
            initBottomDividerLineView(mBottomDividerLineMarginLeft, mBottomDividerLineMarginRight);
        }
    }

    /**
     * 初始化上边分割线view
     *
     * @param marginStart 起始的间距
     * @param marginEnd   结束的间距
     */
    private void initTopDividerLineView(int marginStart, int marginEnd) {
        if (mTopDividerLineView == null) {
            if (mTopDividerLineParams == null) {
                mTopDividerLineParams = new LayoutParams(LayoutParams.MATCH_PARENT, mDividerLineHeight);
            }
            mTopDividerLineParams.addRule(ALIGN_PARENT_TOP, TRUE);
            mTopDividerLineParams.setMarginStart(marginStart);
            mTopDividerLineParams.setMarginEnd(marginEnd);
            mTopDividerLineView = new View(mContext);
            mTopDividerLineView.setLayoutParams(mTopDividerLineParams);
            mTopDividerLineView.setBackgroundColor(mDividerLineColor);
        }
        addView(mTopDividerLineView);
    }

    /**
     * 初始化底部分割线view
     *
     * @param marginStart 起始的间距
     * @param marginEnd   结束的间距
     */
    private void initBottomDividerLineView(int marginStart, int marginEnd) {
        if (mBottomDividerLineView == null) {
            if (mBottomDividerLineParams == null) {
                mBottomDividerLineParams = new LayoutParams(LayoutParams.MATCH_PARENT, mDividerLineHeight);
            }
            mBottomDividerLineParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
            mBottomDividerLineParams.setMarginStart(marginStart);
            mBottomDividerLineParams.setMarginEnd(marginEnd);
            mBottomDividerLineView = new View(mContext);
            mBottomDividerLineView.setLayoutParams(mBottomDividerLineParams);
            mBottomDividerLineView.setBackgroundColor(mDividerLineColor);
        }
        addView(mBottomDividerLineView);
    }


    /**
     * 左边点击事件
     *
     * @param baseTextView baseTextView
     */
    private void setDefaultLeftViewClickListener(final BaseTextView baseTextView) {
        if (baseTextView != null) {
            if (mLeftTopTvClickListener != null) {
                baseTextView.getTopTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLeftTopTvClickListener.onClick(baseTextView.getTopTextView());
                    }
                });
            }
            if (mLeftTvClickListener != null) {
                baseTextView.getCenterTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLeftTvClickListener.onClick(baseTextView.getCenterTextView());
                    }
                });
            }
            if (mLeftBottomTvClickListener != null) {
                baseTextView.getBottomTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLeftBottomTvClickListener.onClick(baseTextView.getBottomTextView());
                    }
                });
            }
        }

    }

    /**
     * 中间点击事件
     *
     * @param baseTextView baseTextView
     */
    private void setDefaultCenterViewClickListener(final BaseTextView baseTextView) {
        if (baseTextView != null) {
            if (mCenterTopTvClickListener != null) {
                baseTextView.getTopTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCenterTopTvClickListener.onClick(baseTextView.getTopTextView());
                    }
                });
            }
            if (mCenterTvClickListener != null) {
                baseTextView.getCenterTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCenterTvClickListener.onClick(baseTextView.getCenterTextView());
                    }
                });
            }
            if (mCenterBottomTvClickListener != null) {
                baseTextView.getBottomTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCenterBottomTvClickListener.onClick(baseTextView.getBottomTextView());
                    }
                });
            }
        }

    }


    /**
     * 右边点击事件
     *
     * @param baseTextView baseTextView
     */
    private void setDefaultRightViewClickListener(final BaseTextView baseTextView) {
        if (baseTextView != null) {
            if (mRightTopTvClickListener != null) {
                baseTextView.getTopTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRightTopTvClickListener.onClick(baseTextView.getTopTextView());
                    }
                });
            }
            if (mRightTvClickListener != null) {
                baseTextView.getCenterTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRightTvClickListener.onClick(baseTextView.getCenterTextView());
                    }
                });
            }
            if (mRightBottomTvClickListener != null) {
                baseTextView.getBottomTextView().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRightBottomTvClickListener.onClick(baseTextView.getBottomTextView());
                    }
                });
            }
        }

    }


    /**
     * 字体是否加粗
     *
     * @param baseTextView   baseTextView
     * @param topTextBold    上边字体加粗
     * @param centerTextBold 中间字体加粗
     * @param bottomTextBold 下边字体加粗
     */
    private void setDefaultTextIsBold(BaseTextView baseTextView, boolean topTextBold, boolean centerTextBold, boolean bottomTextBold) {
        if (baseTextView != null) {
            baseTextView.getTopTextView().getPaint().setFakeBoldText(topTextBold);
            baseTextView.getCenterTextView().getPaint().setFakeBoldText(centerTextBold);
            baseTextView.getBottomTextView().getPaint().setFakeBoldText(bottomTextBold);
        }
    }


    /////////////////////////////////////默认属性设置----end/////////////////////////////////


    /////////////////////////////////////对外暴露的方法---begin/////////////////////////////////

    /**
     * 设置左上字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setLeftTopString(CharSequence string) {
        if (mLeftView != null) {
            mLeftView.setTopTextString(string);
        }
        return this;
    }

    /**
     * 设置左中字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setLeftString(CharSequence string) {
        if (mLeftView != null) {
            mLeftView.setCenterTextString(string);
        }
        return this;
    }

    /**
     * 设置左下字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setLeftBottomString(CharSequence string) {
        if (mLeftView != null) {
            mLeftView.setBottomTextString(string);
        }
        return this;
    }


    /**
     * 设置中上字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setCenterTopString(CharSequence string) {
        if (mCenterView != null) {
            mCenterView.setTopTextString(string);
        }
        return this;
    }

    /**
     * 设置中间字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setCenterString(CharSequence string) {
        if (mCenterView != null) {
            mCenterView.setCenterTextString(string);
        }
        return this;
    }

    /**
     * 设置输入框字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setCenterEditString(CharSequence string) {
        if (mCenterEditText != null) {
            mCenterEditText.setText(string);
        }
        return this;
    }

    /**
     * 设置中下字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setCenterBottomString(CharSequence string) {
        if (mCenterView != null) {
            mCenterView.setBottomTextString(string);
        }
        return this;
    }

    /**
     * 设置右上字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setRightTopString(CharSequence string) {
        if (mRightView != null) {
            mRightView.setTopTextString(string);
        }
        return this;
    }

    /**
     * 设置右中字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setRightString(CharSequence string) {
        if (mRightView != null) {
            mRightView.setCenterTextString(string);
        }
        return this;
    }

    /**
     * 设置右下字符串
     *
     * @param string 字符串
     * @return 方便链式调用
     */
    public SuperTextView setRightBottomString(CharSequence string) {
        if (mRightView != null) {
            mRightView.setBottomTextString(string);
        }
        return this;
    }

    /**
     * 设置左上文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setLeftTopTextColor(int color) {
        if (mLeftView != null) {
            mLeftView.getTopTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置左中文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setLeftTextColor(int color) {
        if (mLeftView != null) {
            mLeftView.getCenterTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置左下文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setLeftBottomTextColor(int color) {
        if (mLeftView != null) {
            mLeftView.getBottomTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置中上文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setCenterTopTextColor(int color) {
        if (mCenterView != null) {
            mCenterView.getTopTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置中间文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setCenterTextColor(int color) {
        if (mCenterView != null) {
            mCenterView.getCenterTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置中下文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setCenterBottomTextColor(int color) {
        if (mCenterView != null) {
            mCenterView.getBottomTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置右上文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setRightTopTextColor(int color) {
        if (mRightView != null) {
            mRightView.getTopTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置右中文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setRightTextColor(int color) {
        if (mRightView != null) {
            mRightView.getCenterTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 设置右下文字颜色
     *
     * @param color 颜色值
     * @return SuperTextView
     */
    public SuperTextView setRightBottomTextColor(int color) {
        if (mRightView != null) {
            mRightView.getBottomTextView().setTextColor(color);
        }
        return this;
    }

    /**
     * 获取左上字符串
     *
     * @return 返回字符串
     */
    public String getLeftTopString() {
        return mLeftView != null ? mLeftView.getTopTextView().getText().toString().trim() : "";
    }

    /**
     * 获取左中字符串
     *
     * @return 返回字符串
     */
    public String getLeftString() {
        return mLeftView != null ? mLeftView.getCenterTextView().getText().toString().trim() : "";
    }

    /**
     * 获取左下字符串
     *
     * @return 返回字符串
     */
    public String getLeftBottomString() {
        return mLeftView != null ? mLeftView.getBottomTextView().getText().toString().trim() : "";
    }

    ////////////////////////////////////////////

    /**
     * 获取中上字符串
     *
     * @return 返回字符串
     */
    public String getCenterTopString() {
        return mCenterView != null ? mCenterView.getTopTextView().getText().toString().trim() : "";
    }

    /**
     * 获取中间字符串
     *
     * @return 返回字符串
     */

    public String getCenterString() {
        return mCenterView != null ? mCenterView.getCenterTextView().getText().toString().trim() : "";
    }

    /**
     * 获取中下字符串
     *
     * @return 返回字符串
     */
    public String getCenterBottomString() {
        return mCenterView != null ? mCenterView.getBottomTextView().getText().toString().trim() : "";
    }

    /**
     * 获取右上字符串
     *
     * @return 返回字符串
     */
    public String getRightTopString() {
        return mRightView != null ? mRightView.getTopTextView().getText().toString().trim() : "";
    }

    /**
     * 获取右中字符串
     *
     * @return 返回字符串
     */
    public String getRightString() {
        return mRightView != null ? mRightView.getCenterTextView().getText().toString().trim() : "";
    }

    /**
     * 获取右下字符串
     *
     * @return 返回字符串
     */
    public String getRightBottomString() {
        return mRightView != null ? mRightView.getBottomTextView().getText().toString().trim() : "";
    }

    /**
     * 获取左边ImageView
     *
     * @return ImageView
     */
    public ImageView getLeftIconIV() {
        mLeftImgParams.setMargins(mLeftIconMarginLeft, 0, 0, 0);
        mLeftImgParams.setMarginStart(mLeftIconMarginLeft);
        return mLeftIconIV;
    }

    /**
     * 获取右边ImageView
     *
     * @return ImageView
     */
    public ImageView getRightIconIV() {
        mRightImgParams.setMargins(0, 0, mRightIconMarginRight, 0);
        mLeftImgParams.setMarginEnd(mRightIconMarginRight);
        return mRightIconIV;
    }

    /**
     * 设置CheckBox是否选中
     *
     * @param isChecked 是否选中
     * @return 返回值
     */
    public SuperTextView setCheckBoxChecked(boolean isChecked) {
        setCheckBoxChecked(isChecked, true);
        return this;
    }

    /**
     * 设置CheckBox是否选中
     *
     * @param isChecked 是否选中
     * @param isSilent  是否不触发回调
     * @return 返回值
     */
    public SuperTextView setCheckBoxChecked(boolean isChecked, boolean isSilent) {
        mIsChecked = isChecked;
        if (mRightCheckBox != null) {
            if (isSilent) {
                mRightCheckBox.setOnCheckedChangeListener(null);
                mRightCheckBox.setChecked(isChecked);
                mRightCheckBox.setOnCheckedChangeListener(mCheckBoxCheckedChangeListener);
            } else {
                mRightCheckBox.setChecked(isChecked);
            }
        }
        return this;
    }

    /**
     * 设置checkbox的背景图
     *
     * @param drawable drawable对象
     * @return 返回对象
     */
    public SuperTextView setCheckBoxBackground(Drawable drawable) {
        mRightCheckBoxBg = drawable;
        if (mRightCheckBox != null) {
            mRightCheckBox.setBackgroundDrawable(drawable);
        }
        return this;
    }

    /**
     * 获取checkbox状态
     *
     * @return 返回选择框当前选中状态
     */
    public boolean getCheckBoxIsChecked() {
        boolean isChecked = false;
        if (mRightCheckBox != null) {
            isChecked = mRightCheckBox.isChecked();
        }
        return isChecked;
    }

    /**
     * 设置Switch是否选中
     *
     * @param isChecked Switch是否选中
     * @return 返回值
     */
    public SuperTextView setSwitchIsChecked(boolean isChecked) {
        setSwitchIsChecked(isChecked, true);
        return this;
    }

    /**
     * 设置Switch是否选中
     *
     * @param isChecked Switch是否选中
     * @param isSilent  是否不触发回调
     * @return 返回值
     */
    public SuperTextView setSwitchIsChecked(boolean isChecked, boolean isSilent) {
        mSwitchIsChecked = isChecked;
        if (mRightSwitch != null) {
            if (isSilent) {
                mRightSwitch.setOnCheckedChangeListener(null);
                mRightSwitch.setChecked(isChecked);
                mRightSwitch.setOnCheckedChangeListener(mSwitchCheckedChangeListener);
            } else {
                mRightSwitch.setChecked(isChecked);
            }
        }
        return this;
    }

    /**
     * 获取switch状态
     *
     * @return 返回switch当前选中状态
     */
    public boolean getSwitchIsChecked() {
        boolean isChecked = false;
        if (mRightSwitch != null) {
            isChecked = mRightSwitch.isChecked();
        }
        return isChecked;
    }

    /**
     * 设置左边tv的左侧图片
     *
     * @param drawableLeft 左边图片资源
     */
    public SuperTextView setLeftTvDrawableLeft(Drawable drawableLeft) {
        setDefaultDrawable(mLeftView.getCenterTextView(), drawableLeft, null, mTextViewDrawablePadding, mLeftTvDrawableWidth, mLeftTvDrawableHeight);
        return this;
    }

    /**
     * 设置左边tv的右侧图片
     *
     * @param drawableRight 右边图片资源
     */
    public SuperTextView setLeftTvDrawableRight(Drawable drawableRight) {
        setDefaultDrawable(mLeftView.getCenterTextView(), null, drawableRight, mTextViewDrawablePadding, mLeftTvDrawableWidth, mLeftTvDrawableHeight);
        return this;
    }


    /**
     * 设置中间tv的左侧图片
     *
     * @param drawableLeft 左边图片资源
     */
    public SuperTextView setCenterTvDrawableLeft(Drawable drawableLeft) {
        setDefaultDrawable(mCenterView.getCenterTextView(), drawableLeft, null, mTextViewDrawablePadding, mCenterTvDrawableWidth, mCenterTvDrawableHeight);
        return this;
    }


    /**
     * 设置中间tv的右侧图片
     *
     * @param drawableRight 右边图片资源
     */
    public SuperTextView setCenterTvDrawableRight(Drawable drawableRight) {
        setDefaultDrawable(mCenterView.getCenterTextView(), null, drawableRight, mTextViewDrawablePadding, mCenterTvDrawableWidth, mCenterTvDrawableHeight);
        return this;
    }


    /**
     * 设置右边tv的左侧图片
     *
     * @param drawableLeft 左边图片资源
     */
    public SuperTextView setRightTvDrawableLeft(Drawable drawableLeft) {
        setDefaultDrawable(mRightView.getCenterTextView(), drawableLeft, null, mTextViewDrawablePadding, mRightTvDrawableWidth, mRightTvDrawableHeight);
        return this;
    }

    /**
     * 设置右边tv的右侧图片
     *
     * @param drawableRight 右边图片资源
     */
    public SuperTextView setRightTvDrawableRight(Drawable drawableRight) {
        setDefaultDrawable(mRightView.getCenterTextView(), null, drawableRight, mTextViewDrawablePadding, mRightTvDrawableWidth, mRightTvDrawableHeight);
        return this;
    }

    /**
     * 设置左边图标
     *
     * @param leftIcon 左边图标
     * @return 返回对象
     */
    public SuperTextView setLeftIcon(Drawable leftIcon) {
        if (mLeftIconIV != null) {
            mLeftImgParams.setMargins(mLeftIconMarginLeft, 0, 0, 0);
            mLeftImgParams.setMarginStart(mLeftIconMarginLeft);
            mLeftIconIV.setImageDrawable(leftIcon);
        }
        return this;
    }

    /**
     * 设置左边图标
     *
     * @param resId 左边图标资源id
     * @return 返回对象
     */
    public SuperTextView setLeftIcon(int resId) {
        if (mLeftIconIV != null) {
            mLeftImgParams.setMargins(mLeftIconMarginLeft, 0, 0, 0);
            mLeftImgParams.setMarginStart(mLeftIconMarginLeft);
            mLeftIconIV.setImageResource(resId);
        }
        return this;
    }

    /**
     * 设置右边图标
     *
     * @param rightIcon 右边图标
     * @return 返回对象
     */
    public SuperTextView setRightIcon(Drawable rightIcon) {
        if (mRightIconIV != null) {
            mRightImgParams.setMargins(0, 0, mRightIconMarginRight, 0);
            mRightImgParams.setMarginEnd(mRightIconMarginRight);
            mRightIconIV.setImageDrawable(rightIcon);
        }
        return this;
    }

    /**
     * 设置右边图标资源Id
     *
     * @param resId 右边图标
     * @return 返回对象
     */
    public SuperTextView setRightIcon(int resId) {
        if (mRightIconIV != null) {
            mRightImgParams.setMargins(0, 0, mRightIconMarginRight, 0);
            mRightImgParams.setMarginEnd(mRightIconMarginRight);
            mRightIconIV.setImageResource(resId);
        }
        return this;
    }

    /**
     * 设置背景
     *
     * @param drawable 背景资源
     * @return 对象
     */
    public SuperTextView setSBackground(Drawable drawable) {
        if (drawable != null) {
            this.setBackgroundDrawable(drawable);
        }
        return this;
    }

    /**
     * 获取左上的TextView
     *
     * @return textView
     */
    public TextView getLeftTopTextView() {
        TextView textView = null;
        if (mLeftView != null) {
            textView = mLeftView.getTopTextView();
        }
        return textView;
    }

    /**
     * 获取左中的TextView
     *
     * @return textView
     */
    public TextView getLeftTextView() {
        TextView textView = null;
        if (mLeftView != null) {
            textView = mLeftView.getCenterTextView();
        }
        return textView;
    }

    /**
     * 获取左下的TextView
     *
     * @return textView
     */
    public TextView getLeftBottomTextView() {
        TextView textView = null;
        if (mLeftView != null) {
            textView = mLeftView.getBottomTextView();
        }
        return textView;
    }

    /**
     * 获取中上的TextView
     *
     * @return textView
     */
    public TextView getCenterTopTextView() {
        TextView textView = null;
        if (mCenterView != null) {
            textView = mCenterView.getTopTextView();
        }
        return textView;
    }

    /**
     * 获取中中的TextView
     *
     * @return textView
     */
    public TextView getCenterTextView() {
        TextView textView = null;
        if (mCenterView != null) {
            textView = mCenterView.getCenterTextView();
        }
        return textView;
    }

    /**
     * 获取输入框
     *
     * @return
     */
    @Nullable
    public EditText getCenterEditText() {
        return mCenterEditText;
    }

    /**
     * 获取输入框
     *
     * @return
     */
    public String getCenterEditValue() {
        if (mCenterEditText != null) {
            return mCenterEditText.getText().toString();
        }
        return "";
    }

    /**
     * 设置输入框的密码显示样式
     *
     * @param transformationMethod
     * @return
     */
    public SuperTextView setPasswordTransformationMethod(PasswordTransformationMethod transformationMethod) {
        if (mCenterEditText != null) {
            if (mCenterEditText instanceof PasswordEditText) {
                ((PasswordEditText) mCenterEditText).setPasswordTransformationMethod(transformationMethod);
            }
        }
        return this;
    }

    /**
     * 获取输入框内容是否为空
     *
     * @return
     */
    public boolean isEditEmpty() {
        if (mCenterEditText != null) {
            return TextUtils.isEmpty(mCenterEditText.getText().toString());
        }
        return true;
    }

    /**
     * 获取输入框内容是否不为空
     *
     * @return
     */
    public boolean isEditNotEmpty() {
        if (mCenterEditText != null) {
            return !TextUtils.isEmpty(mCenterEditText.getText().toString());
        }
        return false;
    }

    /**
     * 获取中下的TextView
     *
     * @return textView
     */
    public TextView getCenterBottomTextView() {
        TextView textView = null;
        if (mCenterView != null) {
            textView = mCenterView.getBottomTextView();
        }
        return textView;
    }

    /**
     * 获取右上的TextView
     *
     * @return textView
     */
    public TextView getRightTopTextView() {
        TextView textView = null;
        if (mRightView != null) {
            textView = mRightView.getTopTextView();
        }
        return textView;
    }

    /**
     * 获取右中的TextView
     *
     * @return textView
     */
    public TextView getRightTextView() {
        TextView textView = null;
        if (mRightView != null) {
            textView = mRightView.getCenterTextView();
        }
        return textView;
    }

    /**
     * 获取右下的TextView
     *
     * @return textView
     */
    public TextView getRightBottomTextView() {
        TextView textView = null;
        if (mRightView != null) {
            textView = mRightView.getBottomTextView();
        }
        return textView;
    }

    /**
     * 设置左边textView文字对齐方式
     *
     * @param gravity 对齐方式
     * @return SuperTextView
     */
    public SuperTextView setLeftTextGravity(int gravity) {
        setTextGravity(mLeftView, gravity);
        return this;
    }

    /**
     * 设置中间textView文字对齐方式
     *
     * @param gravity 对齐方式
     * @return SuperTextView
     */
    public SuperTextView setCenterTextGravity(int gravity) {
        setTextGravity(mCenterView, gravity);
        return this;
    }

    /**
     * 设置右边textView文字对齐方式
     *
     * @param gravity 对齐方式
     * @return SuperTextView
     */
    public SuperTextView setRightTextGravity(int gravity) {
        setTextGravity(mRightView, gravity);
        return this;
    }

    /**
     * 文字对齐方式
     *
     * @param baseTextView view
     * @param gravity      对齐方式
     */
    private void setTextGravity(BaseTextView baseTextView, int gravity) {
        if (baseTextView != null) {
            baseTextView.getCenterTextView().setGravity(gravity);
        }
    }

    /**
     * 设置上边分割线显示状态
     *
     * @param visibility visibility
     * @return superTextView
     */
    public SuperTextView setTopDividerLineVisibility(int visibility) {
        if (mTopDividerLineView == null) {
            setTopDividerLineView();
        }
        mTopDividerLineView.setVisibility(visibility);
        return this;
    }

    /**
     * 设置下边分割线显示状态
     *
     * @param visibility visibility
     * @return superTextView
     */
    public SuperTextView setBottomDividerLineVisibility(int visibility) {
        if (mBottomDividerLineView == null) {
            setBottomDividerLineView();
        }
        mBottomDividerLineView.setVisibility(visibility);
        return this;
    }

    /////////////////////////////////////对外暴露的方法---end/////////////////////////////////


    /**
     * 点击事件
     *
     * @param onSuperTextViewClickListener ClickListener
     * @return SuperTextView
     */
    public SuperTextView setOnSuperTextViewClickListener(OnSuperTextViewClickListener onSuperTextViewClickListener) {
        this.mSuperTextViewClickListener = onSuperTextViewClickListener;
        if (mSuperTextViewClickListener != null) {
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSuperTextViewClickListener.onClick(SuperTextView.this);
                }
            });
        }
        return this;
    }

    public SuperTextView setLeftTopTvClickListener(OnLeftTopTvClickListener leftTopTvClickListener) {
        this.mLeftTopTvClickListener = leftTopTvClickListener;
        setDefaultLeftViewClickListener(mLeftView);
        return this;
    }

    public SuperTextView setLeftTvClickListener(OnLeftTvClickListener leftTvClickListener) {
        this.mLeftTvClickListener = leftTvClickListener;
        setDefaultLeftViewClickListener(mLeftView);
        return this;
    }

    public SuperTextView setLeftBottomTvClickListener(OnLeftBottomTvClickListener leftBottomTvClickListener) {
        this.mLeftBottomTvClickListener = leftBottomTvClickListener;
        setDefaultLeftViewClickListener(mLeftView);
        return this;
    }

    public SuperTextView setCenterTopTvClickListener(OnCenterTopTvClickListener centerTopTvClickListener) {
        this.mCenterTopTvClickListener = centerTopTvClickListener;
        setDefaultCenterViewClickListener(mCenterView);
        return this;
    }

    /**
     * 点击监听
     *
     * @param clickListener
     * @return
     */
    public SuperTextView setCenterEditTextClickListener(OnClickListener clickListener) {
        if (mCenterEditText != null) {
            mCenterEditText.setOnClickListener(clickListener);
        }
        return this;
    }

    /**
     * 聚焦变化监听
     *
     * @param focusChangeListener
     * @return
     */
    public SuperTextView setCenterEditTextFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        if (mCenterEditText != null && mEditTextButtonType == TYPE_NONE) {
            mCenterEditText.setOnFocusChangeListener(focusChangeListener);
        }
        return this;
    }

    public SuperTextView setCenterTvClickListener(OnCenterTvClickListener centerTvClickListener) {
        this.mCenterTvClickListener = centerTvClickListener;
        setDefaultCenterViewClickListener(mCenterView);
        return this;
    }

    public SuperTextView setCenterBottomTvClickListener(OnCenterBottomTvClickListener centerBottomTvClickListener) {
        this.mCenterBottomTvClickListener = centerBottomTvClickListener;
        setDefaultCenterViewClickListener(mCenterView);
        return this;
    }

    public SuperTextView setRightTopTvClickListener(OnRightTopTvClickListener rightTopTvClickListener) {
        this.mRightTopTvClickListener = rightTopTvClickListener;
        setDefaultRightViewClickListener(mRightView);
        return this;
    }

    public SuperTextView setRightTvClickListener(OnRightTvClickListener rightTvClickListener) {
        this.mRightTvClickListener = rightTvClickListener;
        setDefaultRightViewClickListener(mRightView);
        return this;
    }

    public SuperTextView setRightBottomTvClickListener(OnRightBottomTvClickListener rightBottomTvClickListener) {
        this.mRightBottomTvClickListener = rightBottomTvClickListener;
        setDefaultRightViewClickListener(mRightView);
        return this;
    }

    public SuperTextView setLeftImageViewClickListener(OnLeftImageViewClickListener listener) {
        mLeftImageViewClickListener = listener;

        if (mLeftIconIV != null) {
            mLeftIconIV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLeftImageViewClickListener.onClick(mLeftIconIV);
                }
            });
        }
        return this;
    }

    public SuperTextView setRightImageViewClickListener(final OnRightImageViewClickListener listener) {
        this.mRightImageViewClickListener = listener;
        if (mRightIconIV != null) {
            mRightIconIV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRightImageViewClickListener.onClick(mRightIconIV);
                }
            });
        }
        return this;
    }

    public SuperTextView setSwitchCheckedChangeListener(CompoundButton.OnCheckedChangeListener switchCheckedChangeListener) {
        mSwitchCheckedChangeListener = switchCheckedChangeListener;
        if (mRightSwitch != null) {
            mRightSwitch.setOnCheckedChangeListener(mSwitchCheckedChangeListener);
        }
        return this;
    }

    public SuperTextView setCheckBoxCheckedChangeListener(CompoundButton.OnCheckedChangeListener checkBoxCheckedChangeListener) {
        mCheckBoxCheckedChangeListener = checkBoxCheckedChangeListener;
        if (mRightCheckBox != null) {
            mRightCheckBox.setOnCheckedChangeListener(mCheckBoxCheckedChangeListener);
        }
        return this;
    }

    @Override
    public void setTypeface(Typeface typeface) {
        if (mLeftView != null) {
            mLeftView.setTypeface(typeface);
        }
        if (mCenterView != null) {
            mCenterView.setTypeface(typeface);
        }
        if (mCenterEditText != null) {
            mCenterEditText.setTypeface(typeface);
        }
        if (mRightView != null) {
            mRightView.setTypeface(typeface);
        }
        if (mCenterView != null) {
            mCenterView.setTypeface(typeface);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    public interface OnSuperTextViewClickListener {
        void onClick(SuperTextView superTextView);
    }

    public interface OnLeftTopTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnLeftTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnLeftBottomTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnCenterTopTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnCenterTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnCenterBottomTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnRightTopTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnRightTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnRightBottomTvClickListener {
        void onClick(TextView textView);
    }

    public interface OnLeftImageViewClickListener {
        void onClick(ImageView imageView);
    }

    public interface OnRightImageViewClickListener {
        void onClick(ImageView imageView);
    }

    //=================以下是shape相关属性方法================//

    /**
     * 获取设置之后的Selector
     *
     * @return stateListDrawable
     */
    public StateListDrawable getSelector() {

        StateListDrawable stateListDrawable = new StateListDrawable();

        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, getDrawable(android.R.attr.state_pressed));
        stateListDrawable.addState(new int[]{}, getDrawable(android.R.attr.state_enabled));

        return stateListDrawable;
    }

    public GradientDrawable getDrawable(int state) {
        mGradientDrawable = new GradientDrawable();
        mGradientDrawable.setShape(GradientDrawable.RECTANGLE);
        switch (state) {
            case android.R.attr.state_pressed:
                mGradientDrawable.setColor(mSelectorPressedColor);
                break;
            case android.R.attr.state_enabled:
                mGradientDrawable.setColor(mSelectorNormalColor);
                break;
            default:
                mGradientDrawable.setColor(mSolidColor);
        }
        setBorder();
        setRadius();

        return mGradientDrawable;
    }


    /**
     * 设置边框  宽度  颜色  虚线  间隙
     */
    private void setBorder() {
        mGradientDrawable.setStroke(mStrokeWidth, mStrokeColor, mStrokeDashWidth, mStrokeDashGap);
    }

    /**
     * 只有类型是矩形的时候设置圆角半径才有效
     */
    private void setRadius() {
        if (mCornersRadius != 0) {
            //设置圆角的半径
            mGradientDrawable.setCornerRadius(mCornersRadius);
        } else {
            //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
            mGradientDrawable.setCornerRadii(
                    new float[]
                            {
                                    mCornersTopLeftRadius, mCornersTopLeftRadius,
                                    mCornersTopRightRadius, mCornersTopRightRadius,
                                    mCornersBottomRightRadius, mCornersBottomRightRadius,
                                    mCornersBottomLeftRadius, mCornersBottomLeftRadius
                            }
            );
        }

    }

    /**
     * 设置按下的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public SuperTextView setShapeSelectorPressedColor(int color) {
        mSelectorPressedColor = color;
        return this;
    }

    /**
     * 设置正常的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public SuperTextView setShapeSelectorNormalColor(int color) {
        mSelectorNormalColor = color;
        return this;
    }

    /**
     * 设置填充的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public SuperTextView setShapeSolidColor(int color) {
        mSolidColor = color;
        return this;
    }

    /**
     * 设置边框宽度
     *
     * @param strokeWidth 边框宽度值
     * @return 对象
     */
    public SuperTextView setShapeStrokeWidth(int strokeWidth) {
        mStrokeWidth = DensityUtils.dp2px(mContext, strokeWidth);
        return this;
    }

    /**
     * 设置边框颜色
     *
     * @param strokeColor 边框颜色
     * @return 对象
     */
    public SuperTextView setShapeStrokeColor(int strokeColor) {
        this.mStrokeColor = strokeColor;
        return this;
    }

    /**
     * 设置边框虚线宽度
     *
     * @param strokeDashWidth 边框虚线宽度
     * @return 对象
     */
    public SuperTextView setShapeSrokeDashWidth(float strokeDashWidth) {
        this.mStrokeDashWidth = DensityUtils.dp2px(mContext, strokeDashWidth);
        return this;
    }

    /**
     * 设置边框虚线间隙
     *
     * @param strokeDashGap 边框虚线间隙值
     * @return 对象
     */
    public SuperTextView setShapeStrokeDashGap(float strokeDashGap) {
        this.mStrokeDashGap = DensityUtils.dp2px(mContext, strokeDashGap);
        return this;
    }

    /**
     * 设置圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public SuperTextView setShapeCornersRadius(float radius) {
        this.mCornersRadius = DensityUtils.dp2px(mContext, radius);
        return this;
    }

    /**
     * 设置左上圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public SuperTextView setShapeCornersTopLeftRadius(float radius) {
        this.mCornersTopLeftRadius = DensityUtils.dp2px(mContext, radius);
        return this;
    }

    /**
     * 设置右上圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public SuperTextView setShapeCornersTopRightRadius(float radius) {
        this.mCornersTopRightRadius = DensityUtils.dp2px(mContext, radius);
        return this;
    }

    /**
     * 设置左下圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public SuperTextView setShapeCornersBottomLeftRadius(float radius) {
        this.mCornersBottomLeftRadius = DensityUtils.dp2px(mContext, radius);
        return this;
    }

    /**
     * 设置右下圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public SuperTextView setShapeCornersBottomRightRadius(float radius) {
        this.mCornersBottomRightRadius = DensityUtils.dp2px(mContext, radius);
        return this;
    }

    /**
     * 所有与shape相关的属性设置之后调用此方法才生效
     *
     * @return 对象
     */
    public SuperTextView useShape() {
        if (Build.VERSION.SDK_INT < 16) {
            setBackgroundDrawable(getSelector());
        } else {
            setBackground(getSelector());
        }
        return this;
    }

}
