/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xui.widget.textview.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

/**
 * 支持垂直居中的ImageSpan
 *
 * @author xuexiang
 * @since 2019/4/2 下午9:33
 */
public class AlignMiddleImageSpan extends ImageSpan {

    /**
     * 不要和父类重复
     */
    public static final int ALIGN_MIDDLE = -100;

    /**
     * 规定这个Span占几个字的宽度
     */
    private float mFontWidthMultiple = -1f;

    /**
     * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
     */
    private boolean mAvoidSuperChangeFontMetrics = false;

    @SuppressWarnings("FieldCanBeLocal")
    private int mWidth;

    /**
     * @param d                 作为 span 的 Drawable
     * @param verticalAlignment 垂直对齐方式, 如果要垂直居中, 则使用 {@link #ALIGN_MIDDLE}
     */
    public AlignMiddleImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    /**
     * @param d                 作为 span 的 Drawable
     * @param verticalAlignment 垂直对齐方式, 如果要垂直居中, 则使用 {@link #ALIGN_MIDDLE}
     * @param fontWidthMultiple 设置这个Span占几个中文字的宽度, 当该值 > 0 时, span 的宽度为该值*一个中文字的宽度; 当该值 <= 0 时, span 的宽度由 {@link #mAvoidSuperChangeFontMetrics} 决定
     */
    public AlignMiddleImageSpan(Drawable d, int verticalAlignment, float fontWidthMultiple) {
        this(d, verticalAlignment);
        if (fontWidthMultiple >= 0) {
            mFontWidthMultiple = fontWidthMultiple;
        }
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (mAvoidSuperChangeFontMetrics) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            mWidth = rect.right;
        } else {
            mWidth = super.getSize(paint, text, start, end, fm);
        }
        if (mFontWidthMultiple > 0) {
            mWidth = (int) (paint.measureText("子") * mFontWidthMultiple);
        }
        return mWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, @NonNull Paint paint) {
        if (mVerticalAlignment == ALIGN_MIDDLE) {
            Drawable d = getDrawable();
            canvas.save();

//            // 注意如果这样实现会有问题：TextView 有 lineSpacing 时，这里 bottom 偏大，导致偏下
//            int transY = bottom - d.getBounds().bottom; // 底对齐
//            transY -= (paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top) / 2 - d.getBounds().bottom / 2; // 居中对齐
//            canvas.translate(x, transY);
//            d.draw(canvas);
//            canvas.restore();

            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int fontTop = y + fontMetricsInt.top;
            int fontMetricsHeight = fontMetricsInt.bottom - fontMetricsInt.top;
            int iconHeight = d.getBounds().bottom - d.getBounds().top;
            int iconTop = fontTop + (fontMetricsHeight - iconHeight) / 2;
            canvas.translate(x, iconTop);
            d.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    /**
     * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
     */
    public void setAvoidSuperChangeFontMetrics(boolean avoidSuperChangeFontMetrics) {
        mAvoidSuperChangeFontMetrics = avoidSuperChangeFontMetrics;
    }
}
