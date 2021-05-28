package org.secuso.privacyfriendlytodolist.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import org.secuso.privacyfriendlytodolist.R;
import org.secuso.privacyfriendlytodolist.model.Helper;

public class OnSwipeListener implements View.OnTouchListener {
    protected static final double SLOWNESS = 0.75;

    protected final double swipeThreshold;
    protected final int clickThreshold;
    protected final int longClickThreshold;
    protected final boolean dragX;
    protected final boolean dragY;

    protected float startX;
    protected float startY;
    protected float distX;
    protected float distY;
    protected long clickStartTime;
    protected boolean exceededClickThreshold;

    protected View centerView;
    protected View leftView;
    protected View rightView;
    protected View topView;
    protected View bottomView;
    protected Integer initialViewLeft = null;
    protected Integer initialViewRight = null;
    protected Integer initialViewTop = null;
    protected Integer initialViewBottom = null;


    public OnSwipeListener(double swipeThreshold, int clickThreshold, int longClickThreshold, boolean dragX, boolean dragY) {
        this.swipeThreshold = swipeThreshold;
        this.clickThreshold = clickThreshold;
        this.longClickThreshold = longClickThreshold;
        this.dragX = dragX;
        this.dragY = dragY;
    }

    public OnSwipeListener(Context context) {
        this(112.5,
                ViewConfiguration.get(context).getScaledTouchSlop(),
                ViewConfiguration.getLongPressTimeout(),
                true, false);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        distX = event.getX() - startX;
        distY = event.getY() - startY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onDownEvent(view, event);
            case MotionEvent.ACTION_MOVE:
                return onMoveEvent(view, event);
            case MotionEvent.ACTION_OUTSIDE:
                return onOutsideEvent(view, event);
            case MotionEvent.ACTION_UP:
                return onUpEvent(view, event);
            case MotionEvent.ACTION_CANCEL:
                return onCancel(view, event);
        }
        return false;
    }

    protected boolean onDownEvent(View view, MotionEvent event) {
        setViewStats(view);
        startX = event.getX();
        startY = event.getY();
        exceededClickThreshold = false;
        clickStartTime = Helper.getCurrentTimestamp();
        return true;
    }

    protected boolean onMoveEvent(View view, MotionEvent event) {
        if (!isClick(event)) {
            if (dragX) move(slow(distX), true);
            if (dragY) move(slow(distY), false);
        }
        return true;
    }

    protected boolean onOutsideEvent(View view, MotionEvent event) {
        return onUpEvent(view, event);
    }

    protected boolean onUpEvent(View view, MotionEvent event) {
        if (isClick(event)) {
            if (clickIsLong()) view.performLongClick();
            else view.performClick();
        } else {
            if (dragX) move(snap(distX), true);
            if (dragY) move(snap(distY), false);
            triggerSwipeAction(view);
        }
        centerView = null;
        return true;
    }

    protected boolean onCancel(View view, MotionEvent event) {
        if (dragX) move(0, true);
        if (dragY) move(0, false);
        onTinySwipe(view);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    protected void setViewStats(View view) {
        Integer centerViewID = null;
        Integer leftViewID = null;
        Integer rightViewID = null;
        Integer topViewID = null;
        Integer bottomViewID = null;
        switch (view.getId()) {
            case R.id.rl_exlv_task_root:
            case R.id.bt_exlv_edit_task:
            case R.id.bt_exlv_delete_task:
                centerViewID = R.id.rl_exlv_task_group;
                leftViewID = R.id.bt_exlv_edit_task;
                rightViewID = R.id.bt_exlv_delete_task;
                break;
            case R.id.rl_subtask_row_root:
            case R.id.bt_exlv_edit_subtask:
            case R.id.bt_exlv_delete_subtask:
                centerViewID = R.id.rl_subtask_row;
                leftViewID = R.id.bt_exlv_edit_subtask;
                rightViewID = R.id.bt_exlv_delete_subtask;
                break;
        }

        if (centerViewID != null) {
            centerView = view.findViewById(centerViewID);
            initialViewLeft = centerView.getLeft();
            initialViewRight = centerView.getRight();
            initialViewTop = centerView.getTop();
            initialViewBottom = centerView.getBottom();
        } else {
            initialViewLeft = view.getLeft();
            initialViewRight = view.getRight();
            initialViewTop = view.getTop();
            initialViewBottom = view.getBottom();
        }
        if (leftViewID != null)
            leftView = view.findViewById(leftViewID);
        if (rightViewID != null)
            rightView = view.findViewById(rightViewID);
        if (topViewID != null)
            topView = view.findViewById(topViewID);
        if (bottomViewID != null)
            bottomView = view.findViewById(bottomViewID);
    }

    protected boolean isClick(MotionEvent event) {
        float endX = event.getX();
        float endY = event.getY();
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > clickThreshold || differenceY > clickThreshold)
            exceededClickThreshold = true;
        return !exceededClickThreshold;
    }

    protected boolean clickIsLong() {
        return Helper.getCurrentTimestamp() - clickStartTime > longClickThreshold;
    }

    public View getLeftView() {
        return leftView;
    }

    public View getRightView() {
        return rightView;
    }

    protected void move(double distance, boolean horizontal) {
        distance *= 2;
        if (horizontal) {
            if (dragX) {
                if (leftView != null) {
                    if (distance > 0)
                        leftView.getLayoutParams().width = (int) distance;
                    else leftView.getLayoutParams().width = 0;
                    leftView.setRight(initialViewLeft + (int) distance);
                    leftView.requestLayout();
                }
                if (centerView != null) {
                    centerView.offsetLeftAndRight(initialViewLeft + (int) distance);
                }
                if (rightView != null) {
                    if (distance < 0)
                        rightView.getLayoutParams().width = (int) -distance;
                    else rightView.getLayoutParams().width = 0;
                    rightView.setLeft(initialViewRight + (int) distance);
                    rightView.requestLayout();
                }
            }
        } else {
            if (dragY) {
                if (topView != null) {
                    if (distance > 0)
                        topView.getLayoutParams().width = (int) distance;
                    else topView.getLayoutParams().width = 0;
                    topView.requestLayout();
                    topView.setBottom(initialViewTop + (int) distance);
                }
                if (centerView != null) {
                    centerView.offsetTopAndBottom(initialViewTop + (int) distance);
                }
                if (bottomView != null) {
                    if (distance < 0)
                        bottomView.getLayoutParams().width = (int) -distance;
                    else bottomView.getLayoutParams().width = 0;
                    bottomView.requestLayout();
                    bottomView.setTop(initialViewBottom + (int) distance);
                }
            }
        }
    }

    protected double slow(float distance) {
        if (distance < -swipeThreshold - 1) {
            distance = (float) (-swipeThreshold - Math.pow(-distance - swipeThreshold, SLOWNESS));
        } else if (distance > swipeThreshold + 1) {
            distance = (float) (swipeThreshold + Math.pow(distance - swipeThreshold, SLOWNESS));
        }
        return distance;
    }

    protected double snap(float distance) {
        if (distance < -swipeThreshold / 2) return -swipeThreshold;
        if (distance <= swipeThreshold / 2) return 0;
        return swipeThreshold;
    }

    protected void triggerSwipeAction(View view) {
        if (distY >= swipeThreshold) {
            if (distX >= swipeThreshold) {
                onSwipeRightDown(view);
                onSwipeRight(view);
            }
            else if (distX <= -swipeThreshold) {
                onSwipeLeftDown(view);
                onSwipeLeft(view);
            }
            onSwipeDown(view);
        } else if (distY <= -swipeThreshold) {
            if (distX >= swipeThreshold) {
                onSwipeRightUp(view);
                onSwipeRight(view);
            }
            else if (distX <= -swipeThreshold) {
                onSwipeLeftUp(view);
                onSwipeLeft(view);
            }
            onSwipeUp(view);
        } else if (distX >= swipeThreshold)
            onSwipeRight(view);
        else if (distX <= -swipeThreshold)
            onSwipeLeft(view);
        else onTinySwipe(view);
    }

    public void onTinySwipe(View view) {

    }

    public void onSwipeLeft(View view) {

    }

    public void onSwipeRight(View view) {

    }

    public void onSwipeUp(View view) {

    }

    public void onSwipeDown(View view) {

    }

    public void onSwipeLeftUp(View view) {

    }

    public void onSwipeLeftDown(View view) {

    }

    public void onSwipeRightUp(View view) {

    }

    public void onSwipeRightDown(View view) {

    }
}