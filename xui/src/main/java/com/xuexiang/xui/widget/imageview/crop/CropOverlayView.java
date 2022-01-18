/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xui.widget.imageview.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;

/**
 * 裁剪窗口
 *
 * @author xuexiang
 * @since 2019-10-15 11:22
 */
public class CropOverlayView extends View {

    // Private Constants ///////////////////////////////////////////////////////

    private static final int SNAP_RADIUS_DP = 6;
    private static final float DEFAULT_SHOW_GUIDELINES_LIMIT = 100;

    // Gets default values from PaintUtil, sets a bunch of values such that the
    // corners will draw correctly
    private static final float DEFAULT_CORNER_THICKNESS_DP = PaintUtil
            .getCornerThickness();
    private static final float DEFAULT_LINE_THICKNESS_DP = PaintUtil
            .getLineThickness();
    private static final float DEFAULT_CORNER_OFFSET_DP = (DEFAULT_CORNER_THICKNESS_DP / 2)
            - (DEFAULT_LINE_THICKNESS_DP / 2);
    private static final float DEFAULT_CORNER_EXTENSION_DP = DEFAULT_CORNER_THICKNESS_DP
            / 2 + DEFAULT_CORNER_OFFSET_DP;
    private static final float DEFAULT_CORNER_LENGTH_DP = 20;

    // mGuidelines enumerations
    private static final int GUIDELINES_OFF = 0;
    private static final int GUIDELINES_ON_TOUCH = 1;
    private static final int GUIDELINES_ON = 2;

    // Member Variables ////////////////////////////////////////////////////////

    // The Paint used to draw the white rectangle around the crop area.
    private Paint mBorderPaint;

    // The Paint used to draw the guidelines within the crop area when pressed.
    private Paint mGuidelinePaint;

    // The Paint used to draw the corners of the Border

    /**
     * 剪切框四角
     */
    private Paint mCornerPaint;

    // The Paint used to darken the surrounding areas outside the crop area.
    /**
     * 背景蒙版
     */
    private Paint mBackgroundPaint;

    // The bounding box around the Bitmap that we are cropping.
    private Rect mBitmapRect;

    // The radius of the touch zone (in pixels) around a given Handle.
    private float mHandleRadius;

    // An edge of the crop window will snap to the corresponding edge of a
    // specified bounding box when the crop window edge is less than or equal to
    // this distance (in pixels) away from the bounding box edge.
    private float mSnapRadius;

    // Holds the x and y offset between the exact touch location and the exact
    // handle location that is activated. There may be an offset because we
    // allow for some leeway (specified by mHandleRadius) in activating a
    // handle. However, we want to maintain these offset values while the handle
    // is being dragged so that the handle doesn't jump.
    private Pair<Float, Float> mTouchOffset;

    // The Handle that is currently pressed; null if no Handle is pressed.
    private Handle mPressedHandle;

    // Flag indicating if the crop area should always be a certain aspect ratio
    // (indicated by mTargetAspectRatio).
    private boolean mFixAspectRatio = CropImageView.DEFAULT_FIXED_ASPECT_RATIO;

    // Floats to save the current aspect ratio of the image
    private int mAspectRatioX = CropImageView.DEFAULT_ASPECT_RATIO_X;
    private int mAspectRatioY = CropImageView.DEFAULT_ASPECT_RATIO_Y;

    // The aspect ratio that the crop area should maintain; this variable is
    // only used when mMaintainAspectRatio is true.
    private float mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

    // Instance variables for customizable attributes
    private int mGuidelines;

    // Whether the Crop View has been initialized for the first time
    private boolean initializedCropWindow = false;

    // Instance variables for the corner values
    private float mCornerExtension;
    private float mCornerOffset;
    private float mCornerLength;

    // Constructors ////////////////////////////////////////////////////////////

    public CropOverlayView(Context context) {
        super(context);
        init(context);
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // View Methods ////////////////////////////////////////////////////////////

    public Bitmap mBitmap = null;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Initialize the crop window here because we need the size of the view
        // to have been determined.
        initCropWindow(mBitmapRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // Draw translucent background for the cropped area.
        drawBackground(canvas, mBitmapRect);

        if (showGuidelines()) {
            // Determines whether guidelines should be drawn or not
            if (mGuidelines == GUIDELINES_ON) {
                drawRuleOfThirdsGuidelines(canvas);
                /* Added by jarlen */
                drawCornerText(canvas);
            } else if (mGuidelines == GUIDELINES_ON_TOUCH) {
                // Draw only when resizing
                if (mPressedHandle != null) {
                    drawRuleOfThirdsGuidelines(canvas);
                    /* Added by jarlen */
                    drawCornerText(canvas);
                }

            } else if (mGuidelines == GUIDELINES_OFF) {
                // Do nothing
            }
        }

        // Draws the main crop window border.
        canvas.drawRect(Edge.LEFT.getCoordinate(), Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(), Edge.BOTTOM.getCoordinate(),
                mBorderPaint);

        // mBorderPaint.

        drawCorners(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // If this View is not enabled, don't allow for touch interactions.
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }

    // Public Methods //////////////////////////////////////////////////////////

    /**
     * jarlen Begin
     **/
    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;

    private Bitmap mCornerBitmap = null;
    private int mCornerWidth = 0;
    private int mCornerHeight = 0;

    public void setBitmapSize(int width, int height) {
        this.mBitmapWidth = width;
        this.mBitmapHeight = height;
    }

    public void setCropOverlayCornerBitmap(Bitmap bitmap) {
        mCornerBitmap = bitmap;
        if (mCornerBitmap != null) {
            mCornerWidth = mCornerBitmap.getWidth();
            mCornerHeight = mCornerBitmap.getHeight();
        }
    }

    /** End **/

    /**
     * Informs the CropOverlayView of the image's position relative to the
     * ImageView. This is necessary to call in order to draw the crop window.
     *
     * @param bitmapRect the image's bounding box
     */
    public void setBitmapRect(Rect bitmapRect) {
        mBitmapRect = bitmapRect;
        initCropWindow(mBitmapRect);
    }

    /**
     * Resets the crop overlay view.
     * the Bitmap to set
     *
     * @param
     */
    public void resetCropOverlayView() {
        if (initializedCropWindow) {
            initCropWindow(mBitmapRect);
            invalidate();
        }
    }

    /**
     * Sets the guidelines for the CropOverlayView to be either on, off, or to
     * show when resizing the application.
     *
     * @param guidelines Integer that signals whether the guidelines should be on, off,
     *                   or only showing when resizing.
     */
    public void setGuidelines(int guidelines) {
        if (guidelines < 0 || guidelines > 2) {
            throw new IllegalArgumentException(
                    "Guideline value must be set between 0 and 2. See documentation.");
        } else {
            mGuidelines = guidelines;
            resetCropOverlayView();
        }
    }

    /**
     * 设置边框线的颜色
     *
     * @param color
     */
    public void setBorderColor(@ColorInt int color) {
        mBorderPaint.setColor(color);
        resetCropOverlayView();
    }

    /**
     * 设置边框线的粗细
     *
     * @param px
     */
    public void setBorderWidth(@Dimension float px) {
        mBorderPaint.setStrokeWidth(px);
        resetCropOverlayView();
    }

    /**
     * 设置边框线的样式
     *
     * @param px
     */
    public void setBorderStyle(@ColorInt int color, @Dimension float px) {
        mBorderPaint.setColor(color);
        mBorderPaint.setStrokeWidth(px);
        resetCropOverlayView();
    }

    /**
     * 设置网格的颜色
     *
     * @param color
     */
    public void setGuidelineColor(@ColorInt int color) {
        mGuidelinePaint.setColor(color);
        resetCropOverlayView();
    }

    /**
     * 设置网格的粗细
     *
     * @param px
     */
    public void setGuidelineWidth(@Dimension float px) {
        mGuidelinePaint.setStrokeWidth(px);
        resetCropOverlayView();
    }

    /**
     * 设置网格的样式
     *
     * @param px
     */
    public void setGuidelineStyle(@ColorInt int color, @Dimension float px) {
        mGuidelinePaint.setColor(color);
        mGuidelinePaint.setStrokeWidth(px);
        resetCropOverlayView();
    }

    /**
     * 设置4个边角的颜色
     *
     * @param color
     */
    public void setCornerColor(@ColorInt int color) {
        mCornerPaint.setColor(color);
        resetCropOverlayView();
    }

    /**
     * 设置4个边角的粗细
     *
     * @param px
     */
    public void setCornerWidth(@Dimension float px) {
        mCornerPaint.setStrokeWidth(px);
        resetCropOverlayView();
    }

    /**
     * 设置4个边角的粗细
     *
     * @param px
     */
    public void setCornerStyle(@ColorInt int color, @Dimension float px) {
        mCornerPaint.setColor(color);
        mCornerPaint.setStrokeWidth(px);
        resetCropOverlayView();
    }

    /**
     * 设置边框和网格的颜色
     *
     * @param color
     */
    public void setBorderAndGuidelineColor(@ColorInt int color) {
        mBorderPaint.setColor(color);
        mGuidelinePaint.setColor(color);
        resetCropOverlayView();
    }

    /**
     * 设置边框和网格的样式
     *
     * @param color
     */
    public void setBorderAndGuidelineStyle(@ColorInt int color, @Dimension float px) {
        mBorderPaint.setColor(color);
        mBorderPaint.setStrokeWidth(px);
        mGuidelinePaint.setColor(color);
        mGuidelinePaint.setStrokeWidth(px);
        resetCropOverlayView();
    }


    /**
     * Sets whether the aspect ratio is fixed or not; true fixes the aspect
     * ratio, while false allows it to be changed.
     *
     * @param fixAspectRatio Boolean that signals whether the aspect ratio should be
     *                       maintained.
     */
    public void setFixedAspectRatio(boolean fixAspectRatio) {
        mFixAspectRatio = fixAspectRatio;
        resetCropOverlayView();
    }

    /**
     * Sets the X value of the aspect ratio; is defaulted to 1.
     *
     * @param aspectRatioX int that specifies the new X value of the aspect ratio
     */
    public void setAspectRatioX(int aspectRatioX) {
        if (aspectRatioX <= 0) {
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        } else {
            mAspectRatioX = aspectRatioX;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
            resetCropOverlayView();
        }
    }

    /**
     * Sets the Y value of the aspect ratio; is defaulted to 1.
     *
     * @param aspectRatioY int that specifies the new Y value of the aspect ratio
     */
    public void setAspectRatioY(int aspectRatioY) {
        if (aspectRatioY <= 0) {
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        } else {
            mAspectRatioY = aspectRatioY;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
            resetCropOverlayView();
        }
    }

    /**
     * Sets all initial values, but does not call initCropWindow to reset the
     * views. Used once at the very start to initialize the attributes.
     *
     * @param guidelines     Integer that signals whether the guidelines should be on, off,
     *                       or only showing when resizing.
     * @param fixAspectRatio Boolean that signals whether the aspect ratio should be
     *                       maintained.
     * @param aspectRatioX   float that specifies the new X value of the aspect ratio
     * @param aspectRatioY   float that specifies the new Y value of the aspect ratio
     */
    public void setInitialAttributeValues(int guidelines,
                                          boolean fixAspectRatio, int aspectRatioX, int aspectRatioY) {
        if (guidelines < 0 || guidelines > 2) {
            throw new IllegalArgumentException(
                    "Guideline value must be set between 0 and 2. See documentation.");
        } else {
            mGuidelines = guidelines;
        }

        mFixAspectRatio = fixAspectRatio;

        if (aspectRatioX <= 0) {
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        } else {
            mAspectRatioX = aspectRatioX;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
        }

        if (aspectRatioY <= 0) {
            throw new IllegalArgumentException(
                    "Cannot set aspect ratio value to a number less than or equal to 0.");
        } else {
            mAspectRatioY = aspectRatioY;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
        }

    }

    // Private Methods /////////////////////////////////////////////////////////

    private void init(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        mHandleRadius = HandleUtil.getTargetRadius(context);

        mSnapRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SNAP_RADIUS_DP, displayMetrics);
        mBorderPaint = PaintUtil.newBorderPaint(context);
        mGuidelinePaint = PaintUtil.newGuidelinePaint(context);
        mBackgroundPaint = PaintUtil.newBackgroundPaint(context);
        mCornerPaint = PaintUtil.newCornerPaint(context);

        // Sets the values for the corner sizes
        mCornerOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_OFFSET_DP, displayMetrics);
        mCornerExtension = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CORNER_EXTENSION_DP,
                displayMetrics);
        mCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_LENGTH_DP, displayMetrics);

        // Sets guidelines to default until specified otherwise
        mGuidelines = CropImageView.DEFAULT_GUIDELINES;

    }

    /**
     * Set the initial crop window size and position. This is dependent on the
     * size and position of the image being cropped.
     *
     * @param bitmapRect the bounding box around the image being cropped
     */
    private void initCropWindow(Rect bitmapRect) {

        // Tells the attribute functions the crop window has already been
        // initialized
        if (!initializedCropWindow) {
            initializedCropWindow = true;
        }

        if (mFixAspectRatio) {

            // If the image aspect ratio is wider than the crop aspect ratio,
            // then the image height is the determining initial length. Else,
            // vice-versa.
            if (AspectRatioUtil.calculateAspectRatio(bitmapRect) > mTargetAspectRatio) {
                Edge.TOP.setCoordinate(bitmapRect.top);
                Edge.BOTTOM.setCoordinate(bitmapRect.bottom);

                final float centerX = getWidth() / 2f;

                // Limits the aspect ratio to no less than 40 wide or 40 tall
                final float cropWidth = Math
                        .max(Edge.MIN_CROP_LENGTH_PX, AspectRatioUtil
                                .calculateWidth(Edge.TOP.getCoordinate(),
                                        Edge.BOTTOM.getCoordinate(),
                                        mTargetAspectRatio));

                // Create new TargetAspectRatio if the original one does not fit
                // the screen
                if (cropWidth == Edge.MIN_CROP_LENGTH_PX) {
                    mTargetAspectRatio = (Edge.MIN_CROP_LENGTH_PX)
                            / (Edge.BOTTOM.getCoordinate() - Edge.TOP
                            .getCoordinate());
                }

                final float halfCropWidth = cropWidth / 2f;
                Edge.LEFT.setCoordinate(centerX - halfCropWidth);
                Edge.RIGHT.setCoordinate(centerX + halfCropWidth);

            } else {

                Edge.LEFT.setCoordinate(bitmapRect.left);
                Edge.RIGHT.setCoordinate(bitmapRect.right);

                final float centerY = getHeight() / 2f;

                // Limits the aspect ratio to no less than 40 wide or 40 tall
                final float cropHeight = Math
                        .max(Edge.MIN_CROP_LENGTH_PX, AspectRatioUtil
                                .calculateHeight(Edge.LEFT.getCoordinate(),
                                        Edge.RIGHT.getCoordinate(),
                                        mTargetAspectRatio));

                // Create new TargetAspectRatio if the original one does not fit
                // the screen
                if (cropHeight == Edge.MIN_CROP_LENGTH_PX) {
                    float edgeWidth = Edge.RIGHT.getCoordinate() - Edge.LEFT
                            .getCoordinate();
                    if (edgeWidth > 0) {
                        mTargetAspectRatio = edgeWidth / Edge.MIN_CROP_LENGTH_PX;
                    }
                }

                final float halfCropHeight = cropHeight / 2f;
                Edge.TOP.setCoordinate(centerY - halfCropHeight);
                Edge.BOTTOM.setCoordinate(centerY + halfCropHeight);
            }

        } else { // ... do not fix aspect ratio...

            // Initialize crop window to have 10% padding w/ respect to image.
            final float horizontalPadding = 0.1f * bitmapRect.width();
            final float verticalPadding = 0.1f * bitmapRect.height();

            Edge.LEFT.setCoordinate(bitmapRect.left + horizontalPadding);
            Edge.TOP.setCoordinate(bitmapRect.top + verticalPadding);
            Edge.RIGHT.setCoordinate(bitmapRect.right - horizontalPadding);
            Edge.BOTTOM.setCoordinate(bitmapRect.bottom - verticalPadding);
        }
    }

    /**
     * Indicates whether the crop window is small enough that the guidelines
     * should be shown. Public because this function is also used to determine
     * if the center handle should be focused.
     *
     * @return boolean Whether the guidelines should be shown or not
     */
    public static boolean showGuidelines() {
        return (!(Math.abs(Edge.LEFT.getCoordinate() - Edge.RIGHT.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT))
                && (!(Math.abs(Edge.TOP.getCoordinate()
                - Edge.BOTTOM.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT));
    }

    private void drawRuleOfThirdsGuidelines(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        // Draw vertical guidelines.
        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        // Draw horizontal guidelines.
        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

    private void drawBackground(Canvas canvas, Rect bitmapRect) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

		/*-
		  -------------------------------------
		  |                top                |
		  -------------------------------------
		  |      |                    |       |
		  |      |                    |       |
		  | left |                    | right |
		  |      |                    |       |
		  |      |                    |       |
		  -------------------------------------
		  |              bottom               |
		  -------------------------------------
		 */

        // Draw "top", "bottom", "left", then "right" quadrants.
        canvas.drawRect(bitmapRect.left, bitmapRect.top, bitmapRect.right, top,
                mBackgroundPaint);
        canvas.drawRect(bitmapRect.left, bottom, bitmapRect.right,
                bitmapRect.bottom, mBackgroundPaint);
        canvas.drawRect(bitmapRect.left, top, left, bottom, mBackgroundPaint);
        canvas.drawRect(right, top, bitmapRect.right, bottom, mBackgroundPaint);
    }

    private void drawCornerText(Canvas canvas) {

        float displayedImageWidth = mBitmapRect.width();
        float displayedImageHeight = mBitmapRect.height();

        float scaleFactorWidth = mBitmapWidth / displayedImageWidth;
        float scaleFactorHeight = mBitmapHeight / displayedImageHeight;

        int actualCropWidth = (int) (Edge.getWidth() * scaleFactorWidth);
        int actualCropHeight = (int) (Edge.getHeight() * scaleFactorHeight);

//        mBorderPaint.setARGB(255, 255, 255, 255);
        mBorderPaint.setStrokeWidth(0);
        mBorderPaint.setTextAlign(Align.CENTER);
        mBorderPaint.setTextSize(25);
        canvas.drawText(
                actualCropWidth + "x" + actualCropHeight,
                Edge.LEFT.getCoordinate() / 2 + +Edge.RIGHT.getCoordinate() / 2,
                Edge.TOP.getCoordinate() / 2 + Edge.BOTTOM.getCoordinate() / 2,
                mBorderPaint);

    }

    private void drawCorners(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        // Draws the corner lines

        // Top left

        if (mCornerBitmap != null) {
            // Top left
            canvas.drawBitmap(mCornerBitmap, left - mCornerWidth / 2F, top
                    - mCornerHeight / 2F, null);

            // Top right

            canvas.drawBitmap(mCornerBitmap, right - mCornerWidth / 2F, top
                    - mCornerHeight / 2F, null);

            // Bottom left

            canvas.drawBitmap(mCornerBitmap, left - mCornerWidth / 2F, bottom
                    - mCornerHeight / 2F, null);

            // Bottom right

            canvas.drawBitmap(mCornerBitmap, right - mCornerWidth / 2F, bottom
                    - mCornerHeight / 2F, null);

        } else {
            // Top left
            canvas.drawLine(left - mCornerOffset, top - mCornerExtension, left
                    - mCornerOffset, top + mCornerLength, mCornerPaint);
            canvas.drawLine(left, top - mCornerOffset, left + mCornerLength,
                    top - mCornerOffset, mCornerPaint);

            // Top right
            canvas.drawLine(right + mCornerOffset, top - mCornerExtension,
                    right + mCornerOffset, top + mCornerLength, mCornerPaint);
            canvas.drawLine(right, top - mCornerOffset, right - mCornerLength,
                    top - mCornerOffset, mCornerPaint);

            // Bottom left
            canvas.drawLine(left - mCornerOffset, bottom + mCornerExtension,
                    left - mCornerOffset, bottom - mCornerLength, mCornerPaint);
            canvas.drawLine(left, bottom + mCornerOffset, left + mCornerLength,
                    bottom + mCornerOffset, mCornerPaint);

            // Bottom right
            canvas.drawLine(right + mCornerOffset, bottom + mCornerExtension,
                    right + mCornerOffset, bottom - mCornerLength, mCornerPaint);
            canvas.drawLine(right, bottom + mCornerOffset, right
                    - mCornerLength, bottom + mCornerOffset, mCornerPaint);
        }

    }

    /**
     * Handles a {@link MotionEvent#ACTION_DOWN} event.
     *
     * @param x the x-coordinate of the down action
     * @param y the y-coordinate of the down action
     */
    private void onActionDown(float x, float y) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        mPressedHandle = HandleUtil.getPressedHandle(x, y, left, top, right,
                bottom, mHandleRadius);

        if (mPressedHandle == null) {
            return;
        }

        // Calculate the offset of the touch point from the precise location
        // of the handle. Save these values in a member variable since we want
        // to maintain this offset as we drag the handle.
        mTouchOffset = HandleUtil.getOffset(mPressedHandle, x, y, left, top,
                right, bottom);

        invalidate();
    }

    /**
     * Handles a {@link MotionEvent#ACTION_UP} or
     * {@link MotionEvent#ACTION_CANCEL} event.
     */
    private void onActionUp() {
        if (mPressedHandle == null) {
            return;
        }
        mPressedHandle = null;
        invalidate();
    }

    /**
     * Handles a {@link MotionEvent#ACTION_MOVE} event.
     *
     * @param x the x-coordinate of the move event
     * @param y the y-coordinate of the move event
     */
    private void onActionMove(float x, float y) {

        if (mPressedHandle == null) {
            return;
        }

        // Adjust the coordinates for the finger position's offset (i.e. the
        // distance from the initial touch to the precise handle location).
        // We want to maintain the initial touch's distance to the pressed
        // handle so that the crop window size does not "jump".
        x += mTouchOffset.first;
        y += mTouchOffset.second;

        // Calculate the new crop window size/position.
        if (mFixAspectRatio) {
            mPressedHandle.updateCropWindow(x, y, mTargetAspectRatio,
                    mBitmapRect, mSnapRadius);
        } else {
            mPressedHandle.updateCropWindow(x, y, mBitmapRect, mSnapRadius);
        }
        invalidate();
    }
}
