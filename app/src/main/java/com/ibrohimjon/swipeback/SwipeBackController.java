package com.ibrohimjon.swipeback;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Ibrohimjon on 11.04.2020.
 */
public class SwipeBackController {
    private static final String TAG = SwipeBackController.class.getSimpleName();
    public static final int ANIMATION_DURATION = 300;
    public static final int DEFAULT_TOUCH_THRESHOLD = 60;

    private int mScreenWidth;
    private int mTouchSlop;

    private boolean isMoving = false;
    private float mInitX;
    private float mInitY;

    private ViewGroup decorView;
    private ViewGroup contentView;
    private ViewGroup userView;

    private ArgbEvaluator evaluator;
    private ValueAnimator mAnimator;
    private VelocityTracker mVelTracker;

    public SwipeBackController(final Activity activity) {
        mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
        evaluator = new ArgbEvaluator();

        decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        userView = (ViewGroup) contentView.getChildAt(0);

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = (Integer) valueAnimator.getAnimatedValue();
                if (x >= mScreenWidth) {
                    activity.finish();
                }

                handleView(x);
                handleBackgroundColor(x);
            }
        });
    }

    public void handleView(int x) {
        userView.setTranslationX(x);
    }

    private void handleBackgroundColor(float x) {
        int colorValue = (int) evaluator.evaluate(x / mScreenWidth,
                Color.parseColor("#dd000000"), Color.parseColor("#00000000"));
        contentView.setBackgroundColor(colorValue);
        Log.d(TAG, "x is " + x);
    }

    public boolean processEvent(MotionEvent event) {
        getVelocityTracker(event);

        if (mAnimator.isRunning()) {
            return true;
        }

        int pointId = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitX = event.getRawX();
                mInitY = event.getRawY();
                pointId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isMoving) {
                    float dx = Math.abs(event.getRawX() - mInitX);
                    float dy = Math.abs(event.getRawY() - mInitY);
                    if (dx > mTouchSlop && dx > dy && mInitX < DEFAULT_TOUCH_THRESHOLD) {
                        isMoving = true;
                    }
                }
                if (isMoving) {
                    handleView((int) event.getRawX());
                    handleBackgroundColor(event.getRawX());
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int distance = (int) (event.getRawX() - mInitX);

                mVelTracker.computeCurrentVelocity(1000);

                float velocityX = mVelTracker.getXVelocity(pointId);

                Log.d(TAG, "mVelocityX is " + velocityX);
                if (isMoving && Math.abs(userView.getTranslationX()) >= 0) {
                    if (velocityX > 1000f || distance >= mScreenWidth / 4) {
                        mAnimator.setIntValues((int) event.getRawX(), mScreenWidth);
                    } else {
                        mAnimator.setIntValues((int) event.getRawX(), 0);
                    }
                    mAnimator.start();
                    isMoving = false;
                }

                mInitX = 0;
                mInitY = 0;

                recycleVelocityTracker();
                break;
        }
        return true;
    }

    private VelocityTracker getVelocityTracker(MotionEvent event) {
        if (mVelTracker == null) {
            mVelTracker = VelocityTracker.obtain();
        }
        mVelTracker.addMovement(event);
        return mVelTracker;
    }

    private void recycleVelocityTracker() {
        if (mVelTracker != null) {
            mVelTracker.clear();
            mVelTracker.recycle();
            mVelTracker = null;
        }
    }
}
