package com.xlg.beisaierlearn;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.xlg.beisaierlearn.R;
import com.xlg.weidget.BeiSaiErView;
import com.xlg.weidget.HeartFlowLayout;


public class MainActivity extends Activity {
    private BeiSaiErView mBeiSaiErView;
    private Button mButton;
    private TextView mHello;
    private HeartFlowLayout mHeartFlowLayout;

    private Button mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        mBeiSaiErView = (BeiSaiErView) findViewById(R.id.beisai_view);
        mButton = (Button) findViewById(R.id.btn_click);
        mButtonNext = (Button) findViewById(R.id.btn_next);

        mHello = (TextView) findViewById(R.id.hello_world);

        LinearInterpolator interpolator = new LinearInterpolator();
        final ObjectAnimator animator = ObjectAnimator.ofFloat(mHello,"translationY",0,1920);
        animator.setInterpolator(interpolator);
        animator.setDuration(3000);

        mHeartFlowLayout = (HeartFlowLayout) findViewById(R.id.heart_flow);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeiSaiErView.startRun();
                animator.start();
            }

        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
