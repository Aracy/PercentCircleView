package com.aracy.ui.percentcircleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aracy.ui.PercentCircleView;
import com.sun.bl.percentcircleview.R;

public class MainActivity extends AppCompatActivity {
    
    public static final String TAG = "MainActivity";

    private PercentCircleView mPercentCircleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Log.i(TAG, "onCreate: ");
    }

    private void initView(){

        mPercentCircleView = (PercentCircleView) findViewById(R.id.pcv);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    public void setPercent(View view){

        if(mPercentCircleView == null){
            return;
        }

        mPercentCircleView.setPercent(54);

    }

}

