package com.xlg.weidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class DrawDottedCurve extends View {

    Path[] path = new Path[8];
    Path[] drawingPath = new Path[8];
    PathMeasure[] measure = new PathMeasure[8];
    Path[] segmentPath = new Path[8];
    float[] length = new float[8];
    float[] start = new float[8];
    float[] percent = new float[8];
    Paint paint = new Paint();
    float x1;
    float y1;
    float x3;
    float y3;
    long k = 0;
    Canvas canvas;
    Random r = new Random();

//    public DrawDottedCurve(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }

    public DrawDottedCurve(Context context, int a, int b, int c, int d) {
        super(context);
        x1 = a;
        y1 = b;
        x3 = c;
        y3 = d;

        paint.setAlpha(255);
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] { 2, 4 }, 50));

        for (int i = 0; i < 8; i++) {
            k = r.nextInt(100 - 30) + 30;
            path[i] = new Path();
            path[i].moveTo(x1 + k, y1 + k); //
            path[i].quadTo((x3 + k - x1 + k) / 2, (y3 + k - y1 + k) / 2,
                    x3 + k, y3 + k); // Calculate Bezier Curves
        }

        final long DRAW_TIME = 10000;
        CountDownTimer timer = new CountDownTimer(DRAW_TIME, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("Timer", "Inizio");
                for (int i = 0; i < 8; i++) {
                    start[i] = 0;
                    measure[i] = new PathMeasure();
                    measure[i].setPath(path[i], false);

                    percent[i] = ((float) (DRAW_TIME - millisUntilFinished))
                            / (float) DRAW_TIME;

                    segmentPath[i] = new Path();
                    drawingPath[i] = new Path();
                    length[i] = measure[i].getLength() * percent[i];
                    measure[i].getSegment(start[i], length[i], segmentPath[i],
                            true);
                    start[i] = length[i];
                    drawingPath[i].addPath(segmentPath[i]);


                }
                invalidate();
            }
            @Override
            public void onFinish() {
                for (int i = 0; i < 8; i++) {
                    measure[i].getSegment(start[i], measure[i].getLength(),
                            segmentPath[i], true);
                    drawingPath[i].addPath(segmentPath[i]);


                }

                invalidate();
                Log.d("Timer", "Fine");
            }

        };
        timer.start();
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 8; i++) {
            canvas.drawPath(drawingPath[i], paint);
            invalidate();

        }

    }
}

