#android自定义view之时钟的实现
首先看下自定义view的效果图

![Markdown](http://p1.bqimg.com/1949/d1b2d10d37e60edf.gif)

先说下主要实现原理，使用pathMeasure来测量path，创建一个用来记录一组不断变化坐标的数组，然后结合属性动画（ValueAnimator）就可以画出。下面直接贴上代码

##实现步骤
1. 定义画笔的属性及一些常量

	    
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
	     * 时分秒数组
	     */
	    private float[] time;
	
	    private float[] degree;
	
	    private float[] d2t;
	
	    private String[] str = {"12","3","6","9"};

	    private Paint mImgPaint;
		//表盘logo的文本
	    private String logo;
2. 自定义属性

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
		
3. 重写onMeasure()方法


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

4. 重写onDraw()方法

	  	final RectF rectF =  new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
这是确定了表盘的区域

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


上面代码就是就是利用canvas的rotate()方法进行旋转并进行等分画出刻度

		float logoLength = logoPaint.measureText(logo);
        canvas.drawText(logo,radius - logoLength / 2,getPaddingTop() + txDistance + logoTextDistance,logoPaint);

logo文本的绘制，先测量文本的宽度，然后利用drawText画出文本，算起始位置的时候用radius减去文本长度的一半是为了能在确保始终在12点位置的中间显示，注意drawText方法的参数的含义。


绘制指针数字，如果用注释的代码显示数组也按照弧度进行显示，这样就不正确了	

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
path路径的计算，这里以秒针为例计算，其他见文末源码

        final Path pathSec = new Path();
        pathSec.addArc(rectF, 0, progress_sec);
        PathMeasure pathMeasureSec = new PathMeasure(pathSec, false);
        pathMeasureSec.getPosTan(pathMeasureSec.getLength(), cirLocationSec, null);
        canvas.rotate(d2t[0], radius, radius);
        canvas.drawLine(radius, radius, cirLocationSec[0], cirLocationSec[1], mSecPtrPaint);
        canvas.restore();

		//属性动画
	   ValueAnimator mSecondAnim = ValueAnimator.ofFloat(progress_sec, 360);
	        mSecondAnim.setInterpolator(new LinearInterpolator());
	        mSecondAnim.setRepeatCount(Integer.MAX_VALUE);
	        mSecondAnim.setDuration(60 * 1000);
	        mSecondAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	            @Override
	            public void onAnimationUpdate(ValueAnimator animation) {
	                progress_sec = (float) animation.getAnimatedValue();
	                postInvalidate();
	//                Log.e("progress", "onAnimationUpdate: " + progress);
	            }
	        });
	        mSecondAnim.start();

一个圆周是360°，此时我让属性动画运转60s正好是一分钟的时间，这样间接的实现了秒针的移动，插值器这里自然用线性插值器，并且设置无线循环，同理分钟实现原理相同，这里要说明 的是秒针和分针的实现都是根据真实时间转换而来并且是相互独立的，但是时针的计算就必须要根据分针和秒针的综合来算的，比如从系统取得时间是9点59分10秒，这时候时针已经相当接近10点的位置了，如果这时候再设置9点的位置就不合适了。下面是时间转换的代码：

		/**
	  	 * @return 度数与时间的转换
	  	 * 
	  	 **/
	    public float[] time2degree() {
	        degree = new float[3];
	        float[] curTime = curTime();
	        degree[0] = (curTime[0] / 15) * 90 - 90;  //秒的转换
	        degree[1] = (curTime[1] / 15 ) * 90 - 90;  //分的转换
	        degree[2] = ((curTime[2] * 60 + curTime[1] + curTime[0]/60 )/180) * 90 -90;  //时的转换
	        Log.e("time2degree", "time2degree: "+ Arrays.toString(degree));
	        return degree;
	    }

获取时间的代码

		/**
		 *
	  	 * @return 获取时分秒的数组
	  	 * 
	  	 **/
	    public float[] curTime() {
	        time = new float[3];
	        Calendar c = Calendar.getInstance();
	        time[0] = c.get(Calendar.SECOND);
	        time[1] = c.get(Calendar.MINUTE);
	        time[2] = c.get(Calendar.HOUR);
	        Log.e("Time", "getCurrentSeconds: " + Arrays.toString(time));
	        return time;
	    }
#[源码链接](https://github.com/anynew/ClockView) 


