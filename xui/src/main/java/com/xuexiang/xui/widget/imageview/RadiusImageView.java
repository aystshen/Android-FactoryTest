package com.xuexiang.xui.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xuexiang.xui.R;

/**
 * 提供为图片添加圆角、边框、剪裁到圆形或其他形状等功能。
 *
 * @author xuexiang
 * @since 2018/12/1 下午11:59
 */
public class RadiusImageView extends AppCompatImageView {
    private static final int DEFAULT_BORDER_COLOR = Color.GRAY;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMEN = 2;

    private boolean mIsSelected = false;
    private boolean mIsOval = false;
    private boolean mIsCircle = false;

    private int mBorderWidth;
    private int mBorderColor;

    private int mSelectedBorderWidth;
    private int mSelectedBorderColor;
    private int mSelectedMaskColor;
    private boolean mIsTouchSelectModeEnabled = true;

    private int mCornerRadius;

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private ColorFilter mColorFilter;
    private ColorFilter mSelectedColorFilter;
    private BitmapShader mBitmapShader;
    private boolean mNeedResetShader = false;

    private RectF mRectF = new RectF();

    private Bitmap mBitmap;

    private Matrix mMatrix;
    private int mWidth;
    private int mHeight;

    public RadiusImageView(Context context) {
        this(context, null, R.attr.RadiusImageViewStyle);
    }

    public RadiusImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.RadiusImageViewStyle);
    }

    public RadiusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mMatrix = new Matrix();

        setScaleType(ScaleType.CENTER_CROP);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RadiusImageView, defStyleAttr, 0);

        mBorderWidth = array.getDimensionPixelSize(R.styleable.RadiusImageView_riv_border_width, 0);
        mBorderColor = array.getColor(R.styleable.RadiusImageView_riv_border_color, DEFAULT_BORDER_COLOR);
        mSelectedBorderWidth = array.getDimensionPixelSize(
                R.styleable.RadiusImageView_riv_selected_border_width, mBorderWidth);
        mSelectedBorderColor = array.getColor(R.styleable.RadiusImageView_riv_selected_border_color, mBorderColor);
        mSelectedMaskColor = array.getColor(R.styleable.RadiusImageView_riv_selected_mask_color, Color.TRANSPARENT);
        if (mSelectedMaskColor != Color.TRANSPARENT) {
            mSelectedColorFilter = new PorterDuffColorFilter(mSelectedMaskColor, PorterDuff.Mode.DARKEN);
        }

        mIsTouchSelectModeEnabled = array.getBoolean(R.styleable.RadiusImageView_riv_is_touch_select_mode_enabled, true);
        mIsCircle = array.getBoolean(R.styleable.RadiusImageView_riv_is_circle, false);
        if (!mIsCircle) {
            mIsOval = array.getBoolean(R.styleable.RadiusImageView_riv_is_oval, false);
        }
        if (!mIsOval) {
            mCornerRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_riv_corner_radius, 0);
        }
        array.recycle();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP) {
            throw new IllegalArgumentException(String.format("不支持ScaleType %s", scaleType));
        }
        super.setScaleType(scaleType);
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("不支持adjustViewBounds");
        }
    }

    public void setBorderWidth(int borderWidth) {
        if (mBorderWidth != borderWidth) {
            mBorderWidth = borderWidth;
            invalidate();
        }
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (mBorderColor != borderColor) {
            mBorderColor = borderColor;
            invalidate();
        }
    }

    public void setCornerRadius(int cornerRadius) {
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;
            if (!mIsCircle && !mIsOval) {
                invalidate();
            }
        }
    }

    public void setSelectedBorderColor(@ColorInt int selectedBorderColor) {
        if (mSelectedBorderColor != selectedBorderColor) {
            mSelectedBorderColor = selectedBorderColor;
            if (mIsSelected) {
                invalidate();
            }
        }

    }

    public void setSelectedBorderWidth(int selectedBorderWidth) {
        if (mSelectedBorderWidth != selectedBorderWidth) {
            mSelectedBorderWidth = selectedBorderWidth;
            if (mIsSelected) {
                invalidate();
            }
        }
    }

    public void setSelectedMaskColor(@ColorInt int selectedMaskColor) {
        if (mSelectedMaskColor != selectedMaskColor) {
            mSelectedMaskColor = selectedMaskColor;
            if (mSelectedMaskColor != Color.TRANSPARENT) {
                mSelectedColorFilter = new PorterDuffColorFilter(mSelectedMaskColor, PorterDuff.Mode.DARKEN);
            } else {
                mSelectedColorFilter = null;
            }
            if (mIsSelected) {
                invalidate();
            }
        }
        mSelectedMaskColor = selectedMaskColor;
    }


    public void setCircle(boolean isCircle) {
        if (mIsCircle != isCircle) {
            mIsCircle = isCircle;
            requestLayout();
            invalidate();
        }
    }

    public void setOval(boolean isOval) {
        boolean forceUpdate = false;
        if (isOval) {
            if (mIsCircle) {
                // 必须先取消圆形
                mIsCircle = false;
                forceUpdate = true;
            }

        }
        if (mIsOval != isOval || forceUpdate) {
            mIsOval = isOval;
            requestLayout();
            invalidate();
        }
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public int getCornerRadius() {
        return mCornerRadius;
    }

    public int getSelectedBorderColor() {
        return mSelectedBorderColor;
    }

    public int getSelectedBorderWidth() {
        return mSelectedBorderWidth;
    }

    public int getSelectedMaskColor() {
        return mSelectedMaskColor;
    }


    public boolean isCircle() {
        return mIsCircle;
    }

    public boolean isOval() {
        return !mIsCircle && mIsOval;
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mIsSelected != isSelected) {
            mIsSelected = isSelected;
            invalidate();
        }
    }

    public void setTouchSelectModeEnabled(boolean touchSelectModeEnabled) {
        mIsTouchSelectModeEnabled = touchSelectModeEnabled;
    }

    public boolean isTouchSelectModeEnabled() {
        return mIsTouchSelectModeEnabled;
    }

    public void setSelectedColorFilter(ColorFilter cf) {
        if (mSelectedColorFilter == cf) {
            return;
        }
        mSelectedColorFilter = cf;
        if (mIsSelected) {
            invalidate();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mColorFilter == cf) {
            return;
        }
        mColorFilter = cf;
        if (!mIsSelected) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (mIsCircle) {
            int size = Math.min(width, height);
            setMeasuredDimension(size, size);
        } else {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (mBitmap == null) {
                return;
            }
            boolean widthWrapContent = widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED;
            boolean heightWrapContent = heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED;
            float bmWidth = mBitmap.getWidth(), bmHeight = mBitmap.getHeight();
            float scaleX = width / bmWidth, scaleY = height / bmHeight;
            if (widthWrapContent && heightWrapContent) {
                // 保证长宽比
                if (scaleX >= 1 && scaleY >= 1) {
                    setMeasuredDimension((int) bmWidth, (int) bmHeight);
                    return;
                }

                if (scaleX >= 1) {
                    setMeasuredDimension((int) (bmHeight * scaleY), height);
                    return;
                }

                if (scaleY >= 1) {
                    setMeasuredDimension(width, (int) (bmHeight * scaleX));
                    return;
                }

                if (scaleX < scaleY) {
                    setMeasuredDimension(width, (int) (bmHeight * scaleX));
                } else {
                    setMeasuredDimension((int) (bmWidth * scaleY), height);
                }
            } else if (widthWrapContent) {
                setMeasuredDimension((int) (bmWidth * scaleY), height);
            } else if (heightWrapContent) {
                setMeasuredDimension(width, (int) (bmHeight * scaleX));
            }
        }
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setupBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setupBitmap();
    }

    private Bitmap getBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null) {
                return null;
            }
            float bmWidth = bitmap.getWidth(), bmHeight = bitmap.getHeight();
            if (bmWidth == 0 || bmHeight == 0) {
                return null;
            }
            // ensure minWidth and minHeight
            float minScaleX = getMinimumWidth() / bmWidth, minScaleY = getMinimumHeight() / bmHeight;
            if (minScaleX > 1 || minScaleY > 1) {
                float scale = Math.max(minScaleX, minScaleY);
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                return Bitmap.createBitmap(bitmap, 0, 0, (int) bmWidth, (int) bmHeight, matrix, false);
            } else {
                return bitmap;
            }
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMEN, COLOR_DRAWABLE_DIMEN, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void setupBitmap() {
        Bitmap bm = getBitmap();
        if (bm == mBitmap) {
            return;
        }
        mBitmap = bm;
        if (mBitmap == null) {
            mBitmapShader = null;
            invalidate();
            return;
        }
        mNeedResetShader = true;
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);
        }
        mBitmapPaint.setShader(mBitmapShader);
        requestLayout();
        invalidate();
    }

    private void updateBitmapShader() {
        mMatrix.reset();
        mNeedResetShader = false;
        if (mBitmapShader == null || mBitmap == null) {
            return;
        }
        final float bmWidth = mBitmap.getWidth();
        final float bmHeight = mBitmap.getHeight();
        final float scaleX = mWidth / bmWidth;
        final float scaleY = mHeight / bmHeight;
        final float scale = Math.max(scaleX, scaleY);
        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate(-(scale * bmWidth - mWidth) / 2, -(scale * bmHeight - mHeight) / 2);
        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setShader(mBitmapShader);
    }

    private void drawBitmap(Canvas canvas, int borderWidth) {
        final float halfBorderWidth = borderWidth * 1.0f / 2;
        mBitmapPaint.setColorFilter(mIsSelected ? mSelectedColorFilter : mColorFilter);

        if (mIsCircle) {
            int center = getWidth() / 2;
            canvas.drawCircle(center, center, center - halfBorderWidth, mBitmapPaint);
        } else {
            mRectF.left = halfBorderWidth;
            //noinspection SuspiciousNameCombination
            mRectF.top = halfBorderWidth;
            mRectF.right = getWidth() - halfBorderWidth;
            mRectF.bottom = getHeight() - halfBorderWidth;
            if (mIsOval) {
                canvas.drawOval(mRectF, mBitmapPaint);
            } else {
                canvas.drawRoundRect(mRectF, mCornerRadius, mCornerRadius, mBitmapPaint);
            }
        }
    }

    private void drawBorder(Canvas canvas, int borderWidth) {
        if (borderWidth <= 0) {
            return;
        }
        final float halfBorderWidth = borderWidth * 1.0f / 2;
        mBorderPaint.setColor(mIsSelected ? mSelectedBorderColor : mBorderColor);
        mBorderPaint.setStrokeWidth(borderWidth);
        if (mIsCircle) {
            int radius = getWidth() / 2;
            canvas.drawCircle(radius, radius, radius - halfBorderWidth, mBorderPaint);
        } else {
            mRectF.left = halfBorderWidth;
            //noinspection SuspiciousNameCombination
            mRectF.top = halfBorderWidth;
            mRectF.right = getWidth() - halfBorderWidth;
            mRectF.bottom = getHeight() - halfBorderWidth;
            if (mIsOval) {
                canvas.drawOval(mRectF, mBorderPaint);
            } else {
                canvas.drawRoundRect(mRectF, mCornerRadius, mCornerRadius, mBorderPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth(), height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        int borderWidth = mIsSelected ? mSelectedBorderWidth : mBorderWidth;

        if (mBitmap == null || mBitmapShader == null) {
            drawBorder(canvas, borderWidth);
            return;
        }

        if (mWidth != width || mHeight != height || mNeedResetShader) {
            mWidth = width;
            mHeight = height;
            updateBitmapShader();
        }
        drawBitmap(canvas, borderWidth);
        drawBorder(canvas, borderWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isClickable()) {
            this.setSelected(false);
            return super.onTouchEvent(event);
        }

        if (!mIsTouchSelectModeEnabled) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setSelected(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_SCROLL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                this.setSelected(false);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
