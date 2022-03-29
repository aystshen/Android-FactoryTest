package com.ayst.factorytest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ayst.factorytest.R;

import java.util.ArrayList;

public class TouchTestView extends View {
    private static final String TAG = "TouchTestView";

    private Paint mNormalPaint;
    private Paint mPassPaint;
    private Paint mLinePaint;
    private ArrayList<TestRect> mRects;
    private ArrayList<ArrayList<PT>> mLines;
    private ArrayList<PT> mCurrentLine;
    private CallBack mCallBack;

    private int mLineWidth;
    private float mLastPointX;
    private float mLastPointY;
    private float mPointX;
    private float mPointY;
    private float mRecordStep = 4;
    private int mNormalColor;
    private int mPassColor;
    private int mLineColor;
    private boolean mTestPass;

    public TouchTestView(Context context) {
        this(context, null);
    }

    public TouchTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLineWidth = 1;
        mTestPass = false;
        mNormalColor = context.getResources().getColor(R.color.black);
        mPassColor = context.getResources().getColor(R.color.green);
        mLineColor = context.getResources().getColor(R.color.black);
        mNormalPaint = new Paint();
        mNormalPaint.setColor(mNormalColor);
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setStyle(Paint.Style.STROKE);
        mPassPaint = new Paint();
        mPassPaint.setColor(mPassColor);
        mPassPaint.setAntiAlias(true);
        mPassPaint.setStyle(Paint.Style.FILL);
        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mRects = new ArrayList<>();
        mLines = new ArrayList<>();
        mCurrentLine = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            createRect();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        TestRect rect = null;
        Paint paint = null;
        for (int i = 0; i < mRects.size(); i++) {
            rect = mRects.get(i);
            if (rect.isPass) {
                paint = mPassPaint;
            } else {
                paint = mNormalPaint;
            }
            canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
        }

        float lastX = 0;
        float lastY = 0;
        PT pt;
        ArrayList<PT> line;
        for (int i = 0; i < mLines.size(); i++) {
            line = mLines.get(i);
            for (int j = 0; j < line.size(); j++) {
                pt = line.get(j);
                if (line.size() == 1) {
                    canvas.drawPoint(pt.mX, pt.mY, mLinePaint);
                } else {
                    if (j > 0) {
                        canvas.drawLine(lastX, lastY, pt.mX, pt.mY, mLinePaint);
                    }
                    lastX = pt.mX;
                    lastY = pt.mY;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mTestPass) {
                    mLastPointX = mPointX = event.getX();
                    mLastPointY = mPointY = event.getY();
                    mCurrentLine = new ArrayList<PT>();
                    mCurrentLine.add(new PT(mPointX, mPointY));
                    mLines.add(mCurrentLine);
                    testRectPass(mPointX, mPointY);
                    isPass();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mTestPass) {
                    mPointX = event.getX();
                    mPointY = event.getY();
                    if (Math.abs(mPointX - mLastPointX) >= mRecordStep
                            || Math.abs(mPointY - mLastPointY) >= mRecordStep) {
                        mCurrentLine.add(new PT(mPointX, mPointY));
                        mLastPointX = mPointX;
                        mLastPointY = mPointY;
                    }
                    testRectPass(mPointX, mPointY);
                    isPass();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                invalidate();
                break;
        }
        return true;
    }

    private void testRectPass(float x, float y) {
        for (TestRect tr : mRects) {
            if (!tr.isPass) {
                if (tr.contain(x, y)) {
                    tr.isPass = true;
                }
            }
        }
    }

    private void isPass() {
        int passCount = 0;
        for (TestRect tr : mRects) {
            if (tr.isPass) {
                passCount++;
            } else {
                break;
            }
        }

        if (passCount == mRects.size()) {
            mTestPass = true;
            Log.i(TAG, "isPass, completed");
            if (mCallBack != null) {
                mCallBack.onTestCompleted();
            }
        }
    }

    private void createRect() {
        int width = getWidth();
        int height = getHeight();
        float rectWidth = (float) Math.max(width, height) / 30;

        Log.i(TAG, "createRect, width: " + width + ", height: " + height + ", rectWidth=" + rectWidth);

        mRects = new ArrayList<>();
        // 区分屏幕方向，尽量排满整个屏幕
        if (width > height) {
            for (int i = 0; i < width / rectWidth; i++) {
                TestRect rect1 = new TestRect();
                rect1.top = i * (rectWidth * height / width);
                rect1.left = i * rectWidth;
                rect1.right = rect1.left + rectWidth;
                rect1.bottom = rect1.top + rectWidth;
                mRects.add(rect1);

                TestRect rect2 = new TestRect();
                rect2.top = rect1.top;
                rect2.left = width - rect1.left - rectWidth;
                rect2.right = rect2.left + rectWidth;
                rect2.bottom = rect2.top + rectWidth;
                mRects.add(rect2);
            }
        } else {
            for (int i = 0; i < height / rectWidth; i++) {
                TestRect rect1 = new TestRect();
                rect1.top = i * rectWidth;
                rect1.left = i * (rectWidth * width / height);
                rect1.right = rect1.left + rectWidth;
                rect1.bottom = rect1.top + rectWidth;
                mRects.add(rect1);

                TestRect rect2 = new TestRect();
                rect2.top = rect1.top;
                rect2.left = width - rect1.left - rectWidth;
                rect2.right = rect2.left + rectWidth;
                rect2.bottom = rect2.top + rectWidth;
                mRects.add(rect2);
            }
        }

        if (mTestPass) {
            for (TestRect tr : mRects) {
                tr.isPass = true;
            }
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void onTestCompleted();
    }

    class TestRect {
        float top;
        float left;
        float right;
        float bottom;
        boolean isPass;

        public boolean contain(float x, float y) {
            if (x >= left && x <= right && y >= top && y <= bottom) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "[" + top + ", " + left + ", " + right + ", " + bottom + ", " + isPass + "]";
        }
    }

    class PT {
        public float mX;
        public float mY;

        public PT(float x, float y) {
            this.mX = x;
            this.mY = y;
        }
    }
}
