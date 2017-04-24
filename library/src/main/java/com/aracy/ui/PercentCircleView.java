package com.aracy.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.view.View;

import com.aracy.ui.utils.ViewUtil;

/***
 * 圆形百分比控件
 *
 * @author aracy
 * @version [1.0, 2015-12-28]
 */
public class PercentCircleView extends View {

    public static final String TAG = "PercentCircleView";

    private final static int DEFAULT_REACH_COLOR = Color.parseColor("#FC423A");// 达到的百分比默认颜色

    private final static int DEFAULT_UNREACH_COLOR = Color.parseColor("#CCCECF");// 未达到的百分比默认颜色

    private final static int DEFAULT_PERCENT_TEXT_COLOR = Color.parseColor("#636466");// 百分比字体的默认颜色

    // 变量
    private int mMeasureWidth = 0; // 测距宽度

    private int mMeasureHeight = 0; // 测距高度

    private int mReachColor; // 达到的百分比颜色

    private int mUnReachColor; // 没有达到的百分比颜色

    private int mPercentTextColor;// 百分比数据的颜色

    private float mCircleWidth;// 圆形框的宽度

    private float mPercent;// 百分比

    private float mCurrentPercent;

    private Paint mReachPaint, mUnReachPaint, mPercentPaint;// 画笔

    private RectF oval = new RectF();// 圆形绘制区域

    public PercentCircleView(Context context) {
        super(context);
        init();
    }

    public PercentCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentCircleView, defStyleAttr, 0);

        mReachColor = a.getColor(R.styleable.PercentCircleView_reach_color, DEFAULT_REACH_COLOR);
        mUnReachColor = a.getColor(R.styleable.PercentCircleView_unreach_color, DEFAULT_UNREACH_COLOR);
        mPercentTextColor = a.getColor(R.styleable.PercentCircleView_percent_text_color, DEFAULT_PERCENT_TEXT_COLOR);
        mCircleWidth = a.getDimensionPixelSize(R.styleable.PercentCircleView_circle_width, (int) dip2px(context, 1));
        mPercent = a.getFloat(R.styleable.PercentCircleView_percent, -1);

        a.recycle();
        init();
    }

    private void init() {
        // 达到的画笔的初始化
        mReachPaint = new Paint();
        mReachPaint.setColor(mReachColor); // 设置画笔颜色
        mReachPaint.setStrokeWidth(mCircleWidth); // 线宽
        mReachPaint.setStyle(Style.STROKE);
        mReachPaint.setAntiAlias(true);
        mReachPaint.setFilterBitmap(true);
        mReachPaint.setDither(true);

        // 未达到画笔的初始化
        mUnReachPaint = new Paint();
        mUnReachPaint.setColor(mUnReachColor); // 设置画笔颜色
        mUnReachPaint.setStrokeWidth(mCircleWidth); // 线宽
        mUnReachPaint.setStyle(Style.STROKE);
        mUnReachPaint.setAntiAlias(true);
        mUnReachPaint.setFilterBitmap(true);
        mUnReachPaint.setDither(true);

        // 字体画笔的初始化
        mPercentPaint = new Paint();
        mPercentPaint.setColor(mPercentTextColor); // 设置画笔颜色
        mPercentPaint.setStrokeWidth(mCircleWidth); // 线宽
        mPercentPaint.setAntiAlias(true);
        mPercentPaint.setFilterBitmap(true);
        mPercentPaint.setDither(true);

        // 设置字体（robot字体：细）
        Typeface font = Typeface.create("sans-serif-light", Typeface.NORMAL);
        mPercentPaint.setTypeface(font);

        mCurrentPercent = 0;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = ViewCompat.getMinimumWidth(this);
        int minHeight = ViewCompat.getMinimumHeight(this);
        mMeasureWidth = ViewUtil.chooseSize(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), minWidth);
        mMeasureHeight = ViewUtil.chooseSize(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), minHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 去宽度和高度重小的一个计算半径
        float radius = Math.min(mMeasureWidth, mMeasureHeight) / 2;

        // 圆形的绘制区域
        oval.left = mMeasureWidth / 2 - radius + mCircleWidth / 2;
        oval.right = mMeasureWidth - oval.left - mCircleWidth / 2;
        oval.top = mMeasureHeight / 2 - radius + mCircleWidth / 2;
        oval.bottom = mMeasureHeight - oval.top - mCircleWidth / 2;

        // 绘制百分比没有达到的颜色，整个圆
        canvas.drawArc(oval, 270f, 360f, false, mUnReachPaint);

        // 如果百分比大于0，绘制百分比到达区域
        if (mPercent >= 0) {
            canvas.drawArc(oval, 270f, 360f * (mCurrentPercent / 100), false, mReachPaint);
        }

        // 根据圆的半径计算字体的大小
        int textWidth = (int) Math.sqrt(radius * radius / 2);
        mPercentPaint.setTextSize((float) (textWidth));

        // 计算字体Y轴的BaseLine
        FontMetrics fontMetrics = mPercentPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseY = mMeasureHeight - (mMeasureHeight - fontHeight) / 2 - fontMetrics.bottom;

        // X轴的中心距离
        int centerX = mMeasureWidth / 2;

        if (mPercent >= 0) {
            // 去除小数点以后的
            int percentFloor = (int) Math.floor(mCurrentPercent);
            // 测量百分比字体宽度
            float percentWidth = mPercentPaint.measureText(percentFloor + "");
            // 测量并绘制百分比号
            float x = centerX - percentWidth / 2;
            canvas.drawText(percentFloor + "", x, textBaseY, mPercentPaint);

            mPercentPaint.setTextSize((float) (textWidth * 0.4));
            canvas.drawText("%", x + percentWidth, textBaseY, mPercentPaint);
            // 绘制百分数
        } else {
            // 如果百分比小于0表示没有设置数据，为初始化状态
            float stringWidth = mPercentPaint.measureText("Init");
            float x = (float) (centerX - stringWidth * 0.5);
            canvas.drawText("Init", x, textBaseY, mPercentPaint);
        }

        mCurrentPercent += 1;
        if (mCurrentPercent <= mPercent)
            postInvalidateDelayed(2);
        else
            mCurrentPercent -= 1;

        super.onDraw(canvas);
    }


    /**
     * 设备独立像素到物理像素转换
     *
     * @param context  上线文
     * @param dipValue 设备独立像素
     * @return float 物理像素
     */
    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    /****
     * 设置百分比圆形的颜色
     *
     * @param reachColor 百分比圆形的颜色
     * @see [类、类#方法、类#成员]
     */
    public void setReachColor(int reachColor) {
        this.mReachColor = reachColor;
        mReachPaint.setColor(mReachColor);
        invalidate();
    }

    /***
     * 设置百分比圆形的颜色
     *
     * @param reachRes 百分比圆形的颜色资源
     * @see [类、类#方法、类#成员]
     */
    public void setReachColorRes(int reachRes) {
        this.mReachColor = ContextCompat.getColor(getContext(), reachRes);
        mReachPaint.setColor(mReachColor);
        invalidate();
    }


    /**
     * 设置百分比没有达到的圆形颜色
     *
     * @param unReachColor 颜色
     * @see [类、类#方法、类#成员]
     */
    public void setUnReachColor(int unReachColor) {
        this.mUnReachColor = unReachColor;
        mUnReachPaint.setColor(mUnReachColor);
        invalidate();
    }

    /***
     * 设置百分比没有达到的圆形颜色
     *
     * @param unReachRes 颜色资源
     * @see [类、类#方法、类#成员]
     */
    public void setUnReachColorRes(int unReachRes) {
        this.mUnReachColor = ContextCompat.getColor(getContext(), unReachRes);
        mUnReachPaint.setColor(mUnReachColor);
        invalidate();
    }


    /***
     * 设置圆形的宽度
     *
     * @param circleWidth 圆形宽度（单位px）
     * @see [类、类#方法、类#成员]
     */
    public void setCircleWidth(float circleWidth) {
        this.mCircleWidth = circleWidth;
        mReachPaint.setStrokeWidth(mCircleWidth);
        mUnReachPaint.setStrokeWidth(mCircleWidth);
        invalidate();
    }

    /***
     * 获取百分比的字体颜色
     *
     * @return 字体颜色
     * @see [类、类#方法、类#成员]
     */
    public int getPercentTextColor() {
        return mPercentTextColor;
    }

    /***
     * 设置百分比的字体颜色
     *
     * @param percentTextColor 字体颜色
     * @see [类、类#方法、类#成员]
     */
    public void setPercentTextColor(int percentTextColor) {
        this.mPercentTextColor = percentTextColor;
        mPercentPaint.setColor(mPercentTextColor);
        invalidate();
    }

    /***
     * 设置百分比的字体颜色
     *
     * @param percentTextColorRes
     * @see [类、类#方法、类#成员]
     */
    public void setPercentTextColorRes(int percentTextColorRes) {
        this.mPercentTextColor = ContextCompat.getColor(getContext(), percentTextColorRes);
        mPercentPaint.setColor(mPercentTextColor);
        invalidate();
    }

    /**
     * 获取已经设置的百分比
     *
     * @return 小于0则为初始化状态
     * @see [setPercent]
     */
    public float getPercent() {
        return mPercent;
    }

    /**
     * 设置百分比
     *
     * @param percent 小于0则为初始化状态
     * @see [getPercent]
     */
    public void setPercent(float percent) {
        this.mPercent = percent;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        PercentSaveState saveState = new PercentSaveState(superState);
        saveState.mPercent = mPercent;
        saveState.mCircleWidth = mCircleWidth;
        saveState.mPercentTextColor = mPercentTextColor;
        saveState.mUnReachColor = mUnReachColor;
        saveState.mReachColor = mReachColor;
        return saveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof PercentSaveState)) {
            super.onRestoreInstanceState(state);
        }
        PercentSaveState saveState = (PercentSaveState) state;
        super.onRestoreInstanceState(saveState.getSuperState());
        mCircleWidth = saveState.mCircleWidth;
        mPercent = saveState.mPercent;
        mReachColor = saveState.mReachColor;
        mUnReachColor = saveState.mUnReachColor;
        mPercentTextColor = saveState.mPercentTextColor;
        invalidate();
    }

    private static class PercentSaveState extends BaseSavedState {

        private int mReachColor; // 达到的百分比颜色

        private int mUnReachColor; // 没有达到的百分比颜色

        private int mPercentTextColor;// 百分比数据的颜色

        private float mCircleWidth;// 圆形框的宽度

        private float mPercent;// 百分比

        PercentSaveState(Parcel source) {
            super(source);
            mReachColor = source.readInt();
            mUnReachColor = source.readInt();
            mPercentTextColor = source.readInt();
            mCircleWidth = source.readFloat();
            mPercent = source.readFloat();
        }


         PercentSaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mReachColor);
            out.writeInt(mUnReachColor);
            out.writeInt(mPercentTextColor);
            out.writeFloat(mCircleWidth);
            out.writeFloat(mPercent);
        }

        public static final Parcelable.Creator<PercentSaveState> CREATOR = new Parcelable.Creator<PercentSaveState>() {

            @Override
            public PercentSaveState createFromParcel(Parcel source) {
                return new PercentSaveState(source);
            }

            @Override
            public PercentSaveState[] newArray(int size) {
                return new PercentSaveState[size];
            }


        };

    }

}
