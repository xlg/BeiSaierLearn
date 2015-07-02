package com.xlg.weidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.transition.PathMotion;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.PathInterpolator;

/**
 * Created by xulinggang on 15/6/28.
 */
public class PathMeasureView extends View {
    private static final String TAG = "PathMeasureView";
    private int mViewWide;
    private int mViewHeight;
    private float radius;
    public PathMeasureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        drawPathCircle(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWide = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        radius = 200;
        Log.i(TAG, "wide = " + mViewWide + ", mViewHeight = " + mViewHeight);
    }

    private void drawPathCircle(Canvas canvas) {
        PointF center = new PointF(mViewWide / 2, mViewHeight / 2);
        RectF area = new RectF(center.x - radius,center.y - radius, center.x + radius, center.y + radius);
        Path orbit = new Path();
        orbit.addArc(area, 180, 90);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawPath(orbit, paint);

        Paint paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPoint.setStrokeWidth(6);
        paintPoint.setStyle(Paint.Style.STROKE);
        paintPoint.setColor(Color.WHITE);
        canvas.drawPoint(center.x, center.y, paintPoint);

//        PathInterpolator pathInterpolator = new PathInterpolator()
//        PathEffect
        PathMeasure pathMeasure = new PathMeasure(orbit,false);
        float length = pathMeasure.getLength();
        Log.i(TAG,"length => " + length);
        float divider = length / 4;
        float[] position = new float[2];
        for (int i = 0; i <= 4; i++) {
            pathMeasure.getPosTan(divider * i,position , null);
            canvas.drawPoint(position[0],position[1],paintPoint);
        }



    }
}
