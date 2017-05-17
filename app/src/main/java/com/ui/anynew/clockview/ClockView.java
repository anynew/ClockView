package com.ui.anynew.clockview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by anynew on 2017/1/4.
 */

public class ClockView extends View {
    /**
     * 圆盘的颜色
     */
    private Paint mCirclePaint;
    /**
     * 大刻度画笔
     */
    private Paint mScalePaint;
    /**
     * 小刻度画笔
     */
    private Paint mLscalePaint;
    /**
     * 秒针画笔
     */
    private Paint mSecPtrPaint;
    /**
     * 分针画笔
     */
    private Paint mMinPtrPaint;
    /**
     * 时针画笔
     */
    private Paint mHorPtrPaint;

    /**
     *  文本画笔
     */
    private Paint txPaint;

    /**
     * logo文本
     */
    private Paint logoPaint;
    /**
    * logo文本距离
    */
    private float logoTextDistance;
    /**
     * 表盘颜色
     */
    private int mCircleColor;
    /**
     * 表盘宽度
     */
    private int mCircleWidth;
    /**
     * 小刻度宽度
     */
    private int mLScaleWidth;
    /**
     * 刻度颜色
     */
    private int mScaleColor;
    /**
     * 刻度宽度
     */
    private int mScaleWidth;
    /**
     * 自身view的宽度和高度
     */
    private int width, height;
    /**
     * 时分秒针颜色
     */
    private int mSecPtrColor, mMinPtrColor, mHorPtrColor;

    private int defaultSize = 150; //默认大小

    private int radius; //半径
    /**
     * 时、分、秒针的长度
     */
    private float sacleHLength, sacleMLength, sacleSLength;

    private float progress_sec=1,progress_min=1,progress_hor=1;
    /**
     * 数字文本与表盘间距
     */
    private float txDistance = 70;
    /**
     * 坐标变化数组
     */
    private float[] cirLocationSec = new float[2];
    private float[] cirLocationMin = new float[2];
    private float[] cirLocationHor = new float[2];
    /**
     * 时分秒三维数组
     */
    private float[] time;

    private float[] degree;

    private float[] d2t;

    private String[] str = {"12","3","6","9"};
    private Paint mImgPaint;
    private String logo;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        disableHardwareRendering(this);
        BlurMaskFilter bmf = new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID);

        d2t = time2degree();

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, 0);

        mCircleColor = ta.getColor(R.styleable.ClockView_CircleColor, Color.CYAN);
        mCircleWidth = ta.getDimensionPixelSize(R.styleable.ClockView_CircleWidth, 10);
        mScaleColor = ta.getColor(R.styleable.ClockView_ScaleColor, Color.WHITE);
        mScaleWidth = ta.getDimensionPixelSize(R.styleable.ClockView_ScaleWidth, 15);
        sacleSLength = ta.getDimensionPixelSize(R.styleable.ClockView_ScaleLength, 10);
        logo = ta.getString(R.styleable.ClockView_LogoText);
        logoTextDistance = ta.getDimensionPixelSize(R.styleable.ClockView_LogoTextDistance,dp_px(10));

        mSecPtrColor = ta.getColor(R.styleable.ClockView_SecPtrColor, Color.parseColor("#CC7832"));
        mMinPtrColor = ta.getColor(R.styleable.ClockView_MinPtrColor, Color.parseColor("#2CB044"));
        mHorPtrColor = ta.getColor(R.styleable.ClockView_HourPtrColor, Color.parseColor("#FFFFFF"));
        ta.recycle();

        mCirclePaint = new Paint();
        mScalePaint = new Paint();
        mSecPtrPaint = new Paint();
        mMinPtrPaint = new Paint();
        mHorPtrPaint = new Paint();
        mLscalePaint = new Paint();
        logoPaint = new Paint();

        mImgPaint = new Paint();
        mImgPaint.setAlpha(200);

        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleWidth);

        mScalePaint.setAntiAlias(true);
        mScalePaint.setColor(mScaleColor);
        mScalePaint.setStyle(Paint.Style.STROKE);
        mScalePaint.setStrokeWidth(mScaleWidth);

        mLscalePaint.setAntiAlias(true);
        mLscalePaint.setColor(mScaleColor);
        mLscalePaint.setStyle(Paint.Style.STROKE);
        mLscalePaint.setStrokeWidth(2);

        mSecPtrPaint.setAntiAlias(true);
        mSecPtrPaint.setColor(mSecPtrColor);
        mSecPtrPaint.setStyle(Paint.Style.STROKE);
        mSecPtrPaint.setStrokeWidth(7);
        mSecPtrPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecPtrPaint.setMaskFilter(bmf);

        mMinPtrPaint.setAntiAlias(true);
        mMinPtrPaint.setColor(mMinPtrColor);
        mMinPtrPaint.setStyle(Paint.Style.STROKE);
        mMinPtrPaint.setStrokeWidth(9);
        mMinPtrPaint.setStrokeCap(Paint.Cap.ROUND);
        mMinPtrPaint.setMaskFilter(bmf);

        mHorPtrPaint.setAntiAlias(true);
        mHorPtrPaint.setStrokeCap(Paint.Cap.ROUND);
        mHorPtrPaint.setColor(mHorPtrColor);
        mHorPtrPaint.setStyle(Paint.Style.STROKE);
        mHorPtrPaint.setStrokeWidth(13);
        mHorPtrPaint.setMaskFilter(bmf);

        txPaint = new Paint();
        txPaint.setAntiAlias(true);
        txPaint.setColor(Color.WHITE);
        txPaint.setTextSize(30);

        logoPaint = new Paint();
        logoPaint.setAntiAlias(true);
        logoPaint.setColor(Color.GRAY);
        logoPaint.setTextSize(40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(widthMeasureSpec);
        if (wMode == MeasureSpec.EXACTLY) {
            width = wSize;
        } else {
            width = Math.min(wSize, defaultSize);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            height = hSize;
        } else {
            height = Math.min(hSize, defaultSize);
        }
        radius = width / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        drawBackgroundImage(canvas);

        float minDistance = 80;
        float scaleDis = 4/2;
        //确定表盘区域
        final RectF rectF =  new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        final RectF rectFmin = new RectF(getPaddingLeft()+ minDistance, getPaddingTop()+minDistance, getWidth() - getPaddingRight()-minDistance, getHeight() - getPaddingBottom()-minDistance);
        final RectF rectFhour = new RectF(getPaddingLeft()+ minDistance*scaleDis, getPaddingTop()+minDistance*scaleDis, getWidth() - getPaddingRight()-minDistance*scaleDis, getHeight() - getPaddingBottom()-minDistance*scaleDis);
//        canvas.drawArc(rectF, 0, 360, false, mCirclePaint);
        canvas.save();
        //大刻度绘制 分成12等份
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(radius, getPaddingTop(), radius, getPaddingTop() + sacleSLength, mScalePaint);
            canvas.rotate(30, radius, radius);
        }
        canvas.restore();
        canvas.save();
        //绘制小刻度 60等份
        for (int i = 0; i < 60; i++) {
            canvas.drawLine(radius, getPaddingTop(), radius, getPaddingTop() + sacleSLength - 10, mLscalePaint);
            canvas.rotate(6, radius, radius);

        }
        canvas.restore();

        float logoLength = logoPaint.measureText(logo);
        canvas.drawText(logo,radius - logoLength / 2,getPaddingTop() + txDistance + logoTextDistance,logoPaint);

        canvas.save();
        //PathMeasure
        final Path pathSec = new Path();
        pathSec.addArc(rectF, 0, progress_sec);
        PathMeasure pathMeasureSec = new PathMeasure(pathSec, false);
        pathMeasureSec.getPosTan(pathMeasureSec.getLength(), cirLocationSec, null);
        canvas.rotate(d2t[0], radius, radius);
        canvas.drawLine(radius, radius, cirLocationSec[0], cirLocationSec[1], mSecPtrPaint);
        canvas.restore();

        canvas.save();
        final Path pathMin = new Path();
        pathMin.addArc(rectFmin, 0, progress_min);
        PathMeasure pathMeasureMin = new PathMeasure(pathMin, false);
        pathMeasureMin.getPosTan(pathMeasureMin.getLength(), cirLocationMin, null);
        canvas.rotate(d2t[1], radius, radius);
        canvas.drawLine(radius, radius, cirLocationMin[0], cirLocationMin[1], mMinPtrPaint);
        canvas.restore();

        canvas.save();
        final Path pathHor = new Path();
        pathHor.addArc(rectFhour, 0, progress_hor);
        PathMeasure pathMeasureHor = new PathMeasure(pathHor, false);
        pathMeasureHor.getPosTan(pathMeasureHor.getLength(), cirLocationHor, null);
        canvas.rotate(d2t[2], radius, radius);
        canvas.drawLine(radius, radius, cirLocationHor[0], cirLocationHor[1], mHorPtrPaint);
        canvas.restore();

        canvas.save();

        drawNumber(canvas);

    }



    /**
     * 画表盘背景图
     * @param canvas
     */
    private void drawBackgroundImage(Canvas canvas) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.mickey_mouse);
        Matrix matrix = new Matrix();
        matrix.postScale(0.6f, 0.6f);
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
        canvas.drawBitmap(dstbmp,radius*2/3,radius*2/3,mImgPaint);
    }

    /**
     * 绘制指针数字
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
     /*   for (int i = 0; i < str.length; i++) {
            float textLen = txPaint.measureText(str[i]);
            canvas.drawText(str[i],radius-textLen/2,getPaddingTop()+50,txPaint);
            canvas.rotate(90,radius,radius);
        }*/
        float textLen_12 = txPaint.measureText(str[0]);
        float textLen_3 = txPaint.measureText(str[1]);
        float textLen_6 = txPaint.measureText(str[2]);
        float textLen_9 = txPaint.measureText(str[3]);
        canvas.drawText(str[0],radius - textLen_12 / 2,getPaddingTop() + txDistance,txPaint);
        canvas.drawText(str[1],width - txDistance - getPaddingLeft(),radius + textLen_3 / 2 ,txPaint );
        canvas.drawText(str[2],radius - textLen_6 / 2, height - getPaddingTop() - txDistance*2/3,txPaint);
        canvas.drawText(str[3],getPaddingLeft()+ txDistance*2/3 ,radius + textLen_9 / 2 ,txPaint );

        Log.e("drawNumber", "getPaddingTop =  "+getPaddingTop() + " txDistance = "+txDistance + " height = "+ height + " radius = "+radius);
    }

    public void startAnim() {

        ValueAnimator mSecondAnim = ValueAnimator.ofFloat(progress_sec, 360);
        mSecondAnim.setInterpolator(new LinearInterpolator());
        mSecondAnim.setRepeatCount(Integer.MAX_VALUE);
        mSecondAnim.setDuration(60 * 1000);
        mSecondAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress_sec = (float) animation.getAnimatedValue();
                postInvalidate();
//                 Log.e("progress", "onAnimationUpdate: " + progress);
            }
        });
        mSecondAnim.start();

        ValueAnimator mMinAnim = ValueAnimator.ofFloat(progress_min, 360);
        mMinAnim.setInterpolator(new LinearInterpolator());
        mMinAnim.setRepeatCount(Integer.MAX_VALUE);
        mMinAnim.setDuration( 60 * 60 * 1000);
        mMinAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress_min = (float) animation.getAnimatedValue();
                postInvalidate();
//                Log.e("progress", "onAnimationUpdate: " + progress_min);
            }
        });
        mMinAnim.start();

        ValueAnimator mHorAnim = ValueAnimator.ofFloat(progress_hor, 360);
        mHorAnim.setInterpolator(new LinearInterpolator());
        mHorAnim.setRepeatCount(Integer.MAX_VALUE);
        mHorAnim.setDuration(12 * 60 * 60 * 1000);
        mHorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress_hor = (float) animation.getAnimatedValue();
                postInvalidate();
//                Log.e("progress", "onAnimationUpdate: " + progress_hor);
            }
        });
        mHorAnim.start();
    }

    /**
     * 获取时分秒的数组
     * @return
     */
    public float[] curTime() {
        time = new float[3];
        Calendar c = Calendar.getInstance();
        time[0] = c.get(Calendar.SECOND);
        time[1] = c.get(Calendar.MINUTE);
        time[2] = c.get(Calendar.HOUR);
        Log.e("Time", "getCurrentSeconds: " + Arrays.toString(time));
        return time;
    }

    /**
     *
     * @return 度数与时间的转换
     */
    public float[] time2degree() {
        degree = new float[3];
        float[] curTime = curTime();
        degree[0] = (curTime[0] / 15) * 90 - 90;  //秒的转换
        degree[1] = (curTime[1] / 15 ) * 90 - 90;  //分的转换
        degree[2] = ((curTime[2] * 60 + curTime[1] + curTime[0]/60 )/180) * 90 -90;  //时的转换
        Log.e("time2degree", "time2degree: "+ Arrays.toString(degree));
        return degree;
    }

    /**
     *  关闭硬件加速
     * @param v
     */
    public static void disableHardwareRendering(View v) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * dp转px
     * @param values
     * @return
     */
    public int dp_px(int values)
    {

        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }
}