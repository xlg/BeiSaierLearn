package com.xlg.weidget;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.xlg.beisaierlearn.R;
import com.xlg.util.Util;

/**
 * Created by xulinggang on 15/6/16.
 * 主要是模仿苹果的神奇效果，界面往底部缩
 * 感觉2种效果都差了点，只能当做是学习贝塞尔曲线绘制，以及drawBitmapMesh 等api得学习了
 */
public class BeiSaiErView extends View {
    private static final String TAG = BeiSaiErView.class.getSimpleName();
    private Paint mPaint;
    private Context mContext;
    private Path mPathLeft;
    private Path mPathRight;

    private PointF mLeftStart = new PointF();
    private PointF mLeftControl1 = new PointF();
    private PointF mLeftControl2 = new PointF();
    private PointF mLeftEnd = new PointF();

    private PointF mRightStart = new PointF();
    private PointF mRightControl1 = new PointF();
    private PointF mRightControl2 = new PointF();
    private PointF mRightEnd = new PointF();

    private int mViewWide;
    private int mViewHeight;
    private int mMiddle;

    private Point mPointF;
    private Paint mPaintPoint;

    private ValueAnimator mAnimator;

    private Bitmap mBitmap;

    /**显示效果，可选*/
    private Effect mEffect = Effect.EFFECT_ONE;

    public enum Effect {
        EFFECT_ONE, EFFECT_TWO
    }

    public BeiSaiErView(Context context) {
        super(context);
        mContext = context;
        init();

    }

    public BeiSaiErView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //
    private static final int WIDTH = 15, HEIGHT = 22;// 分割数
    private static final int COUNT = (WIDTH + 1) * (HEIGHT + 1);// 交点数
    private float[] matrixOriganal = new float[COUNT * 2];// 基准点坐标数组
    private Paint origPaint, movePaint, pointPaint, graphicPaint;// 基准点、变换点和线段的绘制Paint

    private void init() {
        mViewWide = getResources().getDisplayMetrics().widthPixels;
        mViewHeight = getResources().getDisplayMetrics().heightPixels;
        mMiddle = mViewWide / 2;

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bt);
        // 初始化坐标数组
        int index = 0;
        for (int y = 0; y <= HEIGHT; y++) {
            float fy = mViewHeight * y / HEIGHT;

            for (int x = 0; x <= WIDTH; x++) {
                float fx = mViewWide * x / WIDTH;
                setXY(matrixOriganal, index, fx, fy);
                index += 1;
            }
        }
        // 实例画笔并设置颜色
        origPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        origPaint.setColor(0x660000FF);

        graphicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        graphicPaint.setStyle(Paint.Style.STROKE);
        graphicPaint.setColor(0x660000FF);
        graphicPaint.setStrokeWidth(8);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setColor(Color.YELLOW);
        pointPaint.setStrokeWidth(6);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4);

        mPaintPoint = new Paint();
        mPaintPoint.setAntiAlias(true);
        mPaintPoint.setStyle(Paint.Style.STROKE);
        mPaintPoint.setColor(Color.YELLOW);
        mPaintPoint.setStrokeWidth(8);

        mLeftStart.x = 0;
        mLeftStart.y = 0;
        mLeftControl1.x = 100;
        mLeftControl1.y = 700;
        mLeftControl2.x = 300;
        mLeftControl2.y = 100;
        mLeftEnd.x = mMiddle - 50;
        mLeftEnd.y = mViewHeight;
//        mLeftEnd.x = 281.1968f;
//        mLeftEnd.y = 736.6974f ;

        mRightStart.x = mViewWide;
        mRightStart.y = 0;
        mRightControl1.x = mViewWide - 100;
        mRightControl1.y = 700;
        mRightControl2.x = mViewWide - 300;
        mRightControl2.y = 100;
        mRightEnd.x = mMiddle + 50;
        mRightEnd.y = mViewHeight;

        mPathLeft = new Path();
        mPathLeft.moveTo(mLeftStart.x, mLeftStart.y);
        mPathLeft.cubicTo(mLeftControl1.x, mLeftControl1.y, mLeftControl2.x, mLeftControl2.y, mLeftEnd.x, mLeftEnd.y);

        mPathRight = new Path();
        mPathRight.moveTo(mRightStart.x, mRightStart.y);
        mPathRight.cubicTo(mRightControl1.x, mRightControl1.y, mRightControl2.x, mRightControl2.y, mRightEnd.x, mRightEnd.y);

        initAnimator();
        initInPathPoints();
    }

    /**
     * 绘制参考元素
     *
     * @param canvas 画布
     */
    private void drawGuide(Canvas canvas) {
        for (int i = 0; i < COUNT * 2; i += 2) {
            float x = matrixOriganal[i + 0];
            float y = matrixOriganal[i + 1];
            canvas.drawCircle(x, y, 4, origPaint);
        }
    }

    /**
     * 设置坐标数组
     *
     * @param array 坐标数组
     * @param index 标识值
     * @param x     x坐标
     * @param y     y坐标
     */
    private void setXY(float[] array, int index, float x, float y) {
        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;
    }


    /***
     * 初始化动画，曲线效果
     */
    private void initAnimator() {
        //两个控制点
//        PointF pointF1 = new PointF(mLeftControl1.x);
//        PointF pointF2 = new PointF(300, 100);
        BeiSaiErEvaluator beiSaiErEvaluator = new BeiSaiErEvaluator(mLeftControl1, mLeftControl2);

        //起点与终点
//        PointF start = new PointF(0, 0);
//        PointF end = new PointF(mMiddle, mViewHeight);
        mAnimator = ValueAnimator.ofObject(beiSaiErEvaluator, mLeftStart, mLeftEnd);
        mAnimator.setDuration(3000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPointF = (Point) animation.getAnimatedValue();
                Log.i(TAG, "mPointF x => " + mPointF.x + ", y => " + mPointF.y + ", t = >" + mPointF.t);
                calculateMesh(mPointF);
                invalidate();
            }
        });
    }

    //计算左右边每段上的点
    Point[] posLeft;
    Point[] posRight;
    float leftLength;
    float rightLength;

    private Path cutPath = new Path();
    PathMeasure leftPathMeasure;
    PathMeasure rightPathMeasure;

    /**
     * 将mesh数组左边界，定位在path上
     */
    private void initInPathPoints() {
        leftPathMeasure = new PathMeasure(mPathLeft, false);
        rightPathMeasure = new PathMeasure(mPathRight, false);
        leftLength = leftPathMeasure.getLength();
        float leftDivider = leftLength / HEIGHT; //每段长度


        posLeft = new Point[HEIGHT + 1];
        posRight = new Point[HEIGHT + 1];

        float[] f = new float[2];
        for (int i = 0; i < posLeft.length; i++) {
            Point pointF = new Point();
            leftPathMeasure.getPosTan(leftDivider * i, f, null);
            pointF.x = f[0];
            pointF.y = f[1];

            CubeParam cubeParam = new CubeParam();
            cubeParam.calculateParams(mLeftStart, mLeftControl1, mLeftControl2, mLeftEnd, pointF);
            pointF.t = (float) Util.calculate3X(cubeParam.a, cubeParam.b, cubeParam.c, cubeParam.d);
            posLeft[i] = pointF;

        }

        rightLength = leftPathMeasure.getLength();
        float rightDivider = rightLength / HEIGHT; //每段长度
        for (int i = 0; i < posRight.length; i++) {
            Point pointFR = new Point();
            rightPathMeasure.getPosTan(rightDivider * i, f, null);
            pointFR.x = f[0];
            pointFR.y = f[1];
            CubeParam cubeParamR = new CubeParam();
            cubeParamR.calculateParams(mRightStart, mRightControl1, mRightControl2, mRightEnd, pointFR);
            pointFR.t = (float) Util.calculate3X(cubeParamR.a, cubeParamR.b, cubeParamR.c, cubeParamR.d);
            posRight[i] = pointFR;
        }
    }

    /**
     * 开始动画
     */
    public void startRun() {
        if (movePath != null) {
            movePath.reset();
        }
        mAnimator.start();
    }


    Path movePath = new Path();
    PathMeasure pathMeasure = new PathMeasure();

    /**
     * 重新计算mesh
     */
    public void calculateMesh(Point movePoint) {
        float leftDivider = 0;
        float moveLength = 0;
        if (mEffect == Effect.EFFECT_TWO) {
            float height = movePoint.y;
            //TODO 其实这里应用的是贝塞尔上两点间的曲线长度，而暂时不知道如何求(微积分？)，所以用height来替用下
            leftPathMeasure.getSegment(0, height, movePath, false);
            pathMeasure.setPath(movePath, false);
            moveLength = pathMeasure.getLength();
            leftDivider = (leftLength - moveLength) / HEIGHT; //每段长度
        } else {
            leftDivider = leftLength / HEIGHT; //每段长度
        }


        float[] f = new float[2];
        for (int i = 0; i < posLeft.length; i++) {
            Point pointF = new Point();
            if (mEffect == Effect.EFFECT_ONE) {
                leftPathMeasure.getPosTan(leftDivider * i, f, null);
            } else {
                leftPathMeasure.getPosTan(moveLength + leftDivider * i, f, null);
            }
            pointF.x = f[0];
            pointF.y = f[1];

            if (mEffect == Effect.EFFECT_ONE) {
                CubeParam cubeParam = new CubeParam();
                cubeParam.calculateParams(mLeftStart, mLeftControl1, mLeftControl2, mLeftEnd, pointF);
                pointF.t = (float) Util.calculate3X(cubeParam.a, cubeParam.b, cubeParam.c, cubeParam.d);
            }
            posLeft[i] = pointF;

        }
        for (int i = 0; i < posRight.length; i++) {
            Point pointFR = new Point();
            rightPathMeasure.getPosTan(moveLength + leftDivider * i, f, null);
            pointFR.x = f[0];
            pointFR.y = f[1];
            if (mEffect == Effect.EFFECT_ONE) {
                CubeParam cubeParamR = new CubeParam();
                cubeParamR.calculateParams(mRightStart, mRightControl1, mRightControl2, mRightEnd, pointFR);
                pointFR.t = (float) Util.calculate3X(cubeParamR.a, cubeParamR.b, cubeParamR.c, cubeParamR.d);

            }
            posRight[i] = pointFR;
        }

        if (mEffect == Effect.EFFECT_ONE) {
            //首先将2条path上的点横坐标移动，根据t
            for (int i = 0; i < posLeft.length; i++) {
                posLeft[i].x = beiSaiEr3((posLeft[i].t + movePoint.t), mLeftStart.x, mLeftControl1.x, mLeftControl2.x, mLeftEnd.x);
                posLeft[i].y = beiSaiEr3((posLeft[i].t + movePoint.t), mLeftStart.y, mLeftControl1.y, mLeftControl2.y, mLeftEnd.y);
            }
            for (int i = 0; i < posRight.length; i++) {
                posRight[i].x = beiSaiEr3((posRight[i].t + movePoint.t), mRightStart.x, mRightControl1.x, mRightControl2.x, mRightEnd.x);
                posRight[i].y = beiSaiEr3((posRight[i].t + movePoint.t), mRightStart.y, mRightControl1.y, mRightControl2.y, mRightEnd.y);
            }

        }

        int index = 0;
        for (int i = 0; i <= HEIGHT; i++) {
            float fy = posLeft[i].y;

            float distance = posRight[i].x - posLeft[i].x;

            float pad = distance / WIDTH;
            float margin = posLeft[i].x;
            for (int x = 0; x <= WIDTH; x++) {
                float fx = margin + pad * x;
                setXY(matrixOriganal, index, fx, fy);
                index += 1;
            }
        }
    }

    /**
     * 求3阶贝塞尔值
     *
     * @param t        (0 - 1)
     * @param start    起始点
     * @param control1 控制点1
     * @param control2 控制点2
     * @param end      结束点
     * @return
     */
    private float beiSaiEr3(float t, float start, float control1, float control2, float end) {
        float result = 0;
        if (t >= 1f) {
            t = 1f;
        }
        float timeLeft = 1 - t;
        result = start * timeLeft * timeLeft * timeLeft +
                3 * control1 * t * timeLeft * timeLeft +
                3 * control2 * t * t * timeLeft +
                end * t * t * t;

        return result;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, matrixOriganal, 0, null, 0, null);
        drawGuide(canvas);
//
        canvas.drawPath(mPathLeft, mPaint);
        canvas.drawPath(mPathRight, mPaint);
        canvas.drawPoint(0, 700, mPaint);
//
        if (mPointF != null) {
            canvas.drawLine(mPointF.x, mPointF.y, mViewWide - mPointF.x, mPointF.y, mPaintPoint);
        }
//
        for (int i = 0; i < posLeft.length; i++) {
            canvas.drawPoint(posLeft[i].x, posLeft[i].y, pointPaint);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 属性动画差值器
     */
    class BeiSaiErEvaluator implements TypeEvaluator<PointF> {
        private PointF pointF1; //控制点1
        private PointF pointF2; //控制点2

        public BeiSaiErEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }


        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float timeLeft = 1 - fraction;
            Point pointF = new Point();
            pointF.t = fraction;

            //代入公式
            pointF.x = startValue.x * timeLeft * timeLeft * timeLeft +
                    3 * pointF1.x * fraction * timeLeft * timeLeft +
                    3 * pointF2.x * fraction * fraction * timeLeft +
                    endValue.x * fraction * fraction * fraction;

            pointF.y = startValue.y * timeLeft * timeLeft * timeLeft +
                    3 * pointF1.y * fraction * timeLeft * timeLeft +
                    3 * pointF2.y * fraction * fraction * timeLeft +
                    endValue.y * fraction * fraction * fraction;
            return pointF;
        }
    }

    class Point extends PointF {
        /**
         * 该点在贝塞尔曲线上的时间
         */
        public float t;
    }

    /**
     * 一元3次方程，各参数计算
     */
    class CubeParam {
        public double a;
        public double b;
        public double c;
        public double d;

        //a = -X0 + 3X1 - 3X2 + X3
        //b = 3X0 - 6X1 + 3X2
        //c = -3X0 + 3X1
        //d = X0 - Bx(已知的坐标)

        /**
         * @param f0 起始点
         * @param f1 控制点1
         * @param f2 控制点2
         * @param f3 结束点
         * @param fp 曲线上的点
         */
        public void calculateParams(PointF f0, PointF f1, PointF f2, PointF f3, PointF fp) {
            a = -1 * f0.x + 3 * f1.x - 3 * f2.x + f3.x;
            b = 3 * f0.x - 6 * f1.x + 3 * f2.x;
            c = -3 * f0.x + 3 * f1.x;
            d = f0.x - fp.x;
        }
    }


}
