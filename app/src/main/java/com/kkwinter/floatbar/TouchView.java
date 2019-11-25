package com.kkwinter.floatbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.kkwinter.floatbar.utils.ContextUtil;
import com.kkwinter.floatbar.utils.Preference;

@SuppressLint("ViewConstructor")
public class TouchView extends android.support.v7.widget.AppCompatButton {

    private double stateHeight;
    private float startX = 0, startY = 0;
    private float startRawX = 0, startRawY = 0;
    private int iconViewX = 0, iconViewY = 0;
    private int width, height;

    public final static int ICON_WIDTH = Utils.dp2px(42);

    public final static int ICON_HEIGHT = Utils.dp2px(42);

    private final static int ICON_WIDTH_DOWN = Utils.dp2px(72);

    private final static int ICON_HEIGHT_DOWN = Utils.dp2px(72);

    private final static int MOVE_PADDING = Utils.dp2px(10);

    private final static int ICON_PADDING = Utils.dp2px(3);

    private boolean isDown = false;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    private ValueAnimator alphaAnim;

    private ValueAnimator transitionAnim;

    private Paint mPaint;


    public TouchView(Context context, WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        super(context);
        this.mWindowManager = windowManager;
        this.mLayoutParams = layoutParams;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        stateHeight = Utils.getStatusBarHeight(context);
        iconViewX = ICON_PADDING;
        iconViewY = ICON_PADDING;
        mPaint = new Paint();

        startAlphaAnim(5000);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(ICON_WIDTH, ICON_HEIGHT);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        float rawX = event.getRawX();
        float rawY = (float) (event.getRawY() - stateHeight);
        int sumX = (int) (rawX - startRawX);
        int sumY = (int) (event.getRawY() - startRawY);
        mLayoutParams.width = getMeasuredWidth();
        mLayoutParams.height = getMeasuredHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
                startX = event.getX();
                startY = event.getY();
                startRawX = event.getRawX();
                startRawY = event.getRawY();
                isDown = true;
                mLayoutParams.alpha = 1f;
                clearAnim();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (sumX > -MOVE_PADDING && sumX < MOVE_PADDING && sumY > -MOVE_PADDING && sumY < MOVE_PADDING) {
                    performClick();
                } else {
                    float endRawX = rawX - startX;
                    float endRawY = rawY - startY;
                    if (endRawX > width / 2) {
                        startTransitionAnim(width - ICON_WIDTH - ICON_PADDING, (int) endRawY);
                    } else {
                        startTransitionAnim(ICON_PADDING, (int) endRawY);
                    }
                }
                startX = 0;
                startY = 0;
                startRawX = 0;
                startRawY = 0;
                isDown = false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                YeLog.i("TouchView-onTouchEvent:sumX=" + sumX + ";sumY=" + sumY);
                if (sumX < -MOVE_PADDING || sumX > MOVE_PADDING || sumY < -MOVE_PADDING || sumY > MOVE_PADDING) {
                    updateIconViewPosition(rawX - startX, rawY - startY);
                }
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        YeLog.i("TouchView-onDraw:sumX=onDraw height=" + getMeasuredHeight() + ",width=" + getMeasuredWidth());
        String outerRingColor;
        String innerRingColor;
        if (isDown) {
            outerRingColor = "#A9A9A9";
            innerRingColor = "#F5F5F5";
        } else {
            outerRingColor = "#A9A9A9";
            innerRingColor = "#808080";
        }
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setColor(Color.parseColor(outerRingColor));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2f, mPaint);
        mPaint.setColor(Color.parseColor(innerRingColor));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2.5f, mPaint);

//        mLayoutParams.width = getMeasuredWidth();
//        mLayoutParams.height = getMeasuredHeight();
//        mWindowManager.updateViewLayout(this, mLayoutParams);
    }

    private void updateIconViewPosition(float x, float y) {
        if (!isDisplay()) return;
        iconViewX = (int) x;
        iconViewY = (int) y;
        mLayoutParams.width = getMeasuredWidth();
        mLayoutParams.height = getMeasuredHeight();
        mLayoutParams.x = (int) x;
        mLayoutParams.y = (int) y;
        mWindowManager.updateViewLayout(this, mLayoutParams);
    }

    private void startTransitionAnim(final int x, int y) {
        if (!isDisplay()) return;
        iconViewX = x;
        iconViewY = y;
        if (transitionAnim != null && transitionAnim.isRunning()) {
            transitionAnim.cancel();
        }
        transitionAnim = ValueAnimator.ofInt(mLayoutParams.x, x);
        transitionAnim.setDuration(Math.abs(x - mLayoutParams.x));

        transitionAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                YeLog.i("TouchView-transitionAnim-onAnimationUpdate:Transition animation.getAnimatedValue=" + animation.getAnimatedValue());
                mLayoutParams.x = (int) animation.getAnimatedValue();
                mWindowManager.updateViewLayout(TouchView.this, mLayoutParams);
            }
        });
        transitionAnim.addListener(new AnimatorListenerAdapter() {

            boolean isCancel = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isCancel = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                YeLog.i("TouchView-transitionAnim-onAnimationEnd:Transition onAnimationEnd  isCancel=" + isCancel);
                if (!isCancel) {
                    startAlphaAnim();
                }

            }
        });
        transitionAnim.setInterpolator(new AccelerateInterpolator(2f));
        transitionAnim.start();
    }

    private void stopTransitionAnim() {
        if (transitionAnim != null && (transitionAnim.isRunning() || transitionAnim.isStarted())) {
            transitionAnim.cancel();
        }
    }

    public void startAlphaAnim(long startDelay) {
        if (!isDisplay()) return;
        if (alphaAnim != null && (alphaAnim.isRunning() || alphaAnim.isStarted())) {
            alphaAnim.cancel();
        }
        alphaAnim = ValueAnimator.ofFloat(1f, 0.4f);
        alphaAnim.setStartDelay(startDelay);
        alphaAnim.setDuration(1500);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                YeLog.i("TouchView-startAlphaAnim-onAnimationUpdate:animation.getAnimatedValue=" + animation.getAnimatedValue());
                mLayoutParams.alpha = (float) animation.getAnimatedValue();
                mWindowManager.updateViewLayout(TouchView.this, mLayoutParams);
            }
        });
        alphaAnim.start();
    }

    public void startAlphaAnim() {
        startAlphaAnim(3000);
    }

    public void stopAlphaAnim() {
        if (alphaAnim != null && (alphaAnim.isRunning() || alphaAnim.isStarted())) {
            alphaAnim.cancel();
        }
    }

    public void clearAnim() {
        stopTransitionAnim();
        stopAlphaAnim();
    }

    private boolean isDisplay() {
        return Preference.getBoolean(ContextUtil.getAppContext(), Preference.KEY_DISPLAY);
    }


    public int getIconViewX() {
        return iconViewX;
    }

    public int getIconViewY() {
        return iconViewY;
    }
}
