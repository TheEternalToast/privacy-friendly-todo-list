package org.secuso.privacyfriendlytodolist.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class SwipeListener implements View.OnTouchListener {
    private static final double SLOWNESS = 0.75;

    private final double swipeThreshold;
    private final int clickThreshold;
    private final boolean dragX;
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
            double swipeThreshold, int clickThreshold,
            boolean dragX, boolean dragY,
            View leftView, View rightView, View topView, View bottomView
    ) {
        this.swipeThreshold = swipeThreshold;
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
                112.5,
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
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isClick(event)) {
                    if (dragX) move(view,
                            slow((double) event.getX() - startX, true),
                            true);
                    if (dragY) move(view,
                            slow((double) event.getY() - startY, false),
                            false);
                }
                return true;
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
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (dragX) move(view, 0, true);
                if (dragY) move(view, 0, false);
                return true;
        }
        return false;
    }

    public Integer getViewLeft() {
        return viewLeft;
    }

    public Integer getViewRight() {
        return viewRight;
    }

    private boolean isClick(MotionEvent event) {
        float endX = event.getX();
        float endY = event.getY();
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return differenceX <= clickThreshold && differenceY <= clickThreshold;
    }

    private void move(View view, double distance, boolean horizontal) {
        distance *= 2;
        if (horizontal) {
            if (dragX) {
                if (leftView != null) {
                    if (distance > 0)
                        leftView.getLayoutParams().width = (int) distance;
                    else leftView.getLayoutParams().width = 0;
                    leftView.setRight(viewLeft + (int) distance);
                    leftView.requestLayout();
                }
                if (rightView != null) {
                    if (distance < 0)
                        rightView.getLayoutParams().width = (int) -distance;
                    else rightView.getLayoutParams().width = 0;
                    rightView.setLeft(viewRight + (int) distance);
                    rightView.requestLayout();
                }

                view.setLeft(viewLeft + (int) distance);
                view.setRight(viewRight + (int) distance);
            }
        } else {
            if (dragY) {
                if (topView != null) {
                    if (distance > 0)
                        topView.getLayoutParams().width = (int) distance;
                    else topView.getLayoutParams().width = 0;
                    topView.requestLayout();
                    topView.setBottom(viewTop + (int) distance);
                }
                if (bottomView != null) {
                    if (distance < 0)
                        bottomView.getLayoutParams().width = (int) -distance;
                    else bottomView.getLayoutParams().width = 0;
                    bottomView.requestLayout();
                    bottomView.setTop(viewBottom + (int) distance);
                }

                view.setTop(viewTop + (int) distance);
                view.setBottom(viewBottom + (int) distance);
            }
        }
    }

    private double slow(double distance, boolean horizontal) {
        double threshold = horizontal ? swipeThreshold : swipeThreshold;
        if (distance < -threshold - 1) {
            distance = -threshold - Math.pow(-distance - threshold, SLOWNESS);
        } else if (distance > threshold + 1) {
            distance = threshold + Math.pow(distance - threshold, SLOWNESS);
        }
        return distance;
    }

    private double snap(float distance, boolean horizontal) {
        double threshold = horizontal ? swipeThreshold : swipeThreshold;
        if (distance < -threshold / 2) return -threshold;
        if (distance <= threshold / 2) return 0;
        return threshold;
    }

    private void triggerSwipeAction(View view, double distX, double distY) {
        if (distY >= swipeThreshold) {
            onSwipeDown(view);
            if (distX >= swipeThreshold) {
                onSwipeRight(view);
                onSwipeDownRight(view);
            } else if (distX <= -swipeThreshold) {
                onSwipeLeft(view);
                onSwipeDownLeft(view);
            }
        } else if (distY <= -swipeThreshold) {
            onSwipeUp(view);
            if (distX >= swipeThreshold) {
                onSwipeRight(view);
                onSwipeUpRight(view);
            } else if (distX <= -swipeThreshold) {
                onSwipeLeft(view);
                onSwipeUpLeft(view);
            }
        } else if (distX >= swipeThreshold)
            onSwipeRight(view);
        else if (distX <= -swipeThreshold)
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