package org.secuso.privacyfriendlytodolist.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class SwipeListener implements View.OnTouchListener {
    private static final double SLOWNESS = 0.75;

    private final double swipeThresholdX;
    private final double swipeThresholdY;
    private final int clickThreshold;
    private boolean dragX;
    private boolean dragY;
    private float startX;
    private float startY;

    private Integer viewLeft = null;
    private Integer viewRight = null;
    private Integer viewTop = null;
    private Integer viewBottom = null;

    private final View leftView;
    private final View rightView;
    private final View topView;
    private final View bottomView;

    public SwipeListener(
            double swipeThresholdX, double swipeThresholdY, int clickThreshold,
            boolean dragX, boolean dragY,
            View leftView, View rightView, View topView, View bottomView
    ) {
        this.swipeThresholdX = swipeThresholdX;
        this.swipeThresholdY = swipeThresholdY;
        this.clickThreshold = clickThreshold;
        this.dragX = dragX;
        this.dragY = dragY;
        this.leftView = leftView;
        this.rightView = rightView;
        this.topView = topView;
        this.bottomView = bottomView;
    }

    public SwipeListener(
            Context context,
            View leftView, View rightView, View topView, View bottomView) {
        this(
                100.0, 100.0,
                ViewConfiguration.get(context).getScaledTouchSlop(),
                true, false,
                leftView, rightView, topView, bottomView
        );
    }

    public SwipeListener(Context context) {
        this(context, null, null, null, null);
    }

    private void initViewStats(View v) {
        if (viewLeft == null) viewLeft = v.getLeft();
        if (viewRight == null) viewRight = v.getRight();
        if (viewTop == null) viewTop = v.getTop();
        if (viewBottom == null) viewBottom = v.getBottom();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initViewStats(view);
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isClick(event)) {
                    if (dragX) move(view,
                            slow((double) event.getX() - startX, true),
                            true);
                    if (dragY) move(view,
                            slow((double) event.getY() - startY, false),
                            false);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                if (isClick(event)) {
                    view.performClick();
                } else {
                    double distX = snap(event.getX() - startX, true);
                    double distY = snap(event.getY() - startY, false);
                    if (dragX) move(view, distX, true);
                    if (dragY) move(view, distY, false);
                    triggerSwipeAction(view, distX, distY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (dragX) move(view, 0, true);
                if (dragY) move(view, 0, false);
        }
        return false;
    }

    private boolean isClick(MotionEvent event) {
        float endX = event.getX();
        float endY = event.getY();
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return differenceX <= clickThreshold && differenceY <= clickThreshold;
    }

    private void move(View view, double distance, boolean horizontal) {
        if (horizontal) {
            if (dragX) {
                view.setLeft(viewLeft + (int) distance);
                view.setRight(viewRight + (int) distance);
                if (rightView != null) rightView.setLeft(viewRight + (int) distance);
                if (leftView != null) leftView.setRight(viewLeft + (int) distance);
            }
        } else {
            if (dragY) {
                view.setTop(viewTop + (int) distance);
                view.setBottom(viewBottom + (int) distance);
                if (topView != null) topView.setBottom(viewTop + (int) distance);
                if (bottomView != null) bottomView.setTop(viewBottom + (int) distance);
            }
        }
    }

    private double slow(double distance, boolean horizontal) {
        double threshold = horizontal ? swipeThresholdX : swipeThresholdY;
        if (distance < -threshold - 1) {
            distance = -threshold - Math.pow(-distance - threshold, SLOWNESS);
        } else if (distance > threshold + 1) {
            distance = threshold + Math.pow(distance - threshold, SLOWNESS);
        }
        return distance;
    }

    private double snap(float distance, boolean horizontal) {
        double threshold = horizontal ? swipeThresholdX : swipeThresholdY;
        if (distance < -threshold / 2) return -threshold;
        if (distance <= threshold / 2) return 0;
        return threshold;
    }

    private void triggerSwipeAction(View view, double distX, double distY) {
        if (distY >= swipeThresholdY) {
            onSwipeDown(view);
            if (distX >= swipeThresholdX) {
                onSwipeRight(view);
                onSwipeDownRight(view);
            } else if (distX <= -swipeThresholdX) {
                onSwipeLeft(view);
                onSwipeDownLeft(view);
            }
        } else if (distY <= -swipeThresholdY) {
            onSwipeUp(view);
            if (distX >= swipeThresholdX) {
                onSwipeRight(view);
                onSwipeUpRight(view);
            } else if (distX <= -swipeThresholdX) {
                onSwipeLeft(view);
                onSwipeUpLeft(view);
            }
        } else if (distX >= swipeThresholdX)
            onSwipeRight(view);
        else if (distX <= -swipeThresholdX)
            onSwipeLeft(view);
    }

    public void onSwipeUp(View view) {

    }

    public void onSwipeDown(View view) {

    }

    public void onSwipeLeft(View view) {

    }

    public void onSwipeRight(View view) {

    }

    public void onSwipeUpLeft(View view) {

    }

    public void onSwipeDownLeft(View view) {

    }

    public void onSwipeUpRight(View view) {

    }

    public void onSwipeDownRight(View view) {

    }
}