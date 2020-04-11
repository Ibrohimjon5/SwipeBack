package com.ibrohimjon.swipeback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class Second extends AppCompatActivity {

    private SwipeBackController swipeBackController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        swipeBackController = new SwipeBackController(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(swipeBackController.processEvent(event)){
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
