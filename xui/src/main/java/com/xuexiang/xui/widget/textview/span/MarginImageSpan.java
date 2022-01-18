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
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * 支持设置图片左右间距的 ImageSpan
 *
 * @author xuexiang
 * @since 2019/4/2 下午9:34
 */
public class MarginImageSpan extends AlignMiddleImageSpan {

    private int mSpanMarginLeft = 0;
    private int mSpanMarginRight = 0;
    private int mOffsetY = 0;

    public MarginImageSpan(Drawable d, int verticalAlignment, int marginLeft, int marginRight) {
        super(d, verticalAlignment);
        mSpanMarginLeft = marginLeft;
        mSpanMarginRight = marginRight;
    }

    public MarginImageSpan(Drawable d, int verticalAlignment, int marginLeft, int marginRight, int offsetY) {
        this(d, verticalAlignment, marginLeft, marginRight);
        mOffsetY = offsetY;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (mSpanMarginLeft != 0 || mSpanMarginRight != 0) {
            super.getSize(paint, text, start, end, fm);
            Drawable d = getDrawable();
            return d.getIntrinsicWidth() + mSpanMarginLeft + mSpanMarginRight;
        } else {
            return super.getSize(paint, text, start, end, fm);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.save();
        canvas.translate(0, mOffsetY);
        // marginRight不用专门处理，只靠getSize()中改变即可
        super.draw(canvas, text, start, end, x + mSpanMarginLeft, top, y, bottom, paint);
        canvas.restore();
    }
}
