package com.xlg.weidget;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.xlg.beisaierlearn.R;

import java.util.Random;

/**
 * Created by xulinggang on 15/6/21.
 */
public class HeartFlowLayout extends RelativeLayout {
    private Context mContext;
    private Random mRandom = new Random();
    private static final int[] mDrawables = new int[]{
            R.mipmap.heart1,
            R.mipmap.heart2,
            R.mipmap.heart3,
            R.mipmap.heart4,
    };
    public HeartFlowLayout(Context context) {
        super(context);
        mContext = context;
    }

    public HeartFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    private int mWide;
    private int mHeight;
    private int mMiddle;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWide = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mMiddle = mWide / 2;
    }

    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((mWide - 100));//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        pointF.y = mRandom.nextInt((mHeight - 100))/scale;
        return pointF;
    }

    public void addHeart(){
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mDrawables[mRandom.nextInt(4)]);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM,TRUE);
        params.addRule(CENTER_HORIZONTAL,TRUE);
        imageView.setLayoutParams(params);
        addView(imageView);

        PointF pointF1 = new PointF(mMiddle - 50,mHeight - 200);
        PointF pointF2 = new PointF(mMiddle + 100,mHeight - 320);
        //new PointF((mWidth-dWidth)/2,mHeight-dHeight),new PointF(random.nextInt(getWidth()),0));//随机
        PointF pointF0 = new PointF(0,mHeight);
        PointF pointF3 = new PointF( 300,0);

        ValueAnimator animator = ValueAnimator.ofObject(new BezierEvalutor(pointF1,pointF2),pointF0,pointF3);
        animator.setDuration(3000);
        animator.setTarget(imageView);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();


    }

    class BezierEvalutor implements TypeEvaluator<PointF>{
        //B(t) = P0 * (1-t)^3 + 3 * P1 * t(1-t)^2 + 3 * P2 * t^2(1-t) + P3 * t^3

        //中间两个控制点
        private PointF pointF1;
        private PointF pointF2;

        public BezierEvalutor(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }


        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float timeLeft = 1.0f - fraction;
            PointF pointF = new PointF();

            //代入公式
            pointF.x = startValue.x * timeLeft * timeLeft * timeLeft +
                       3 * pointF1.x * fraction * timeLeft * timeLeft +
                       3 * pointF2.x * fraction * fraction * timeLeft +
                       endValue.x + fraction * fraction * fraction;

            pointF.y = startValue.y * timeLeft * timeLeft * timeLeft +
                    3 * pointF1.y * fraction * timeLeft * timeLeft +
                    3 * pointF2.y * fraction * fraction * timeLeft +
                    endValue.y + fraction * fraction * fraction;

            return pointF;
        }
    }



}
