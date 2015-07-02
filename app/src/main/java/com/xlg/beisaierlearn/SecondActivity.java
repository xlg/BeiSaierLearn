package com.xlg.beisaierlearn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.xlg.weidget.DrawDottedCurve;

/**
 * Created by xulinggang on 15/6/26.
 */
public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        DrawDottedCurve curve = new DrawDottedCurve(this,0,200,400,400);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2,-2);
        curve.setLayoutParams(params);
        RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
        root.addView(curve);
    }
}
