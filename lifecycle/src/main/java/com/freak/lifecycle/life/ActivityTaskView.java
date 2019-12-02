package com.freak.lifecycle.life;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.freak.lifecycle.life.entity.TaskEntity;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Freak on 2019/12/1.
 */

public class ActivityTaskView extends LinearLayout {

    public static final String TAG = ActivityTaskView.class.getSimpleName();

    private TreeMap<Integer, LinearLayout> mLayoutMap;

    private HashMap<Integer, ObserverTextView> mObserverTextViewMap;

    private LifecycleOverlayWindow.ActivityLifecycleObservable mObservable;

    private int mStatusHeight;

    public ActivityTaskView(Context context) {
        this(context, null);
    }

    public ActivityTaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("UseSparseArrays")
    public ActivityTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.parseColor("#99000000"));
        mLayoutMap = new TreeMap<>();
        mObserverTextViewMap = new HashMap<>();

        mStatusHeight = getStatusBarHeight();
    }

    public void setObservable(LifecycleOverlayWindow.ActivityLifecycleObservable observable) {
        mObservable = observable;

        HashMap.SimpleEntry<Character, Integer> entry = new AbstractMap.SimpleEntry<>('a', 1);
    }

    public void add(TaskEntity taskEntity) {
        int activityId = taskEntity.getActivityId();
        int taskId = taskEntity.getTaskId();
        ObserverTextView textView = createObserverTextView(activityId, taskEntity.getActivityName());
        mObservable.addObserver(textView);
        mObserverTextViewMap.put(activityId, textView);
        LinearLayout layout = mLayoutMap.get(taskId);
        if (layout == null) {
            layout = createLinearLayout();
            mLayoutMap.put(taskId, layout);
            addView(layout);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
            params.leftMargin = 2;
            layout.setLayoutParams(params);

            Log.i(TAG, "addLayout " + taskId);
        }
        layout.addView(textView, 0);
        LinearLayout.LayoutParams params = (LayoutParams) textView.getLayoutParams();
        params.bottomMargin = 1;
        textView.setLayoutParams(params);
        Log.i(TAG, "addObserverTextView " + taskId);
    }

    public void remove(TaskEntity taskEntity) {
        int taskId = taskEntity.getTaskId();
        LinearLayout layout = mLayoutMap.get(taskId);
        if (layout == null) {
            Log.e(TAG, "LinearLayout not found");
            return;
        }
        ObserverTextView textView = mObserverTextViewMap.remove(taskEntity.getActivityId());
        if (textView == null) {
            Log.e(TAG, "ObserverTextView not found");
            return;
        }
        mObservable.deleteObserver(textView);
        layout.removeView(textView);
        Log.i(TAG, "removeObserverTextView " + taskId);
        if (layout.getChildCount() == 0) {
            mLayoutMap.remove(taskId);
            removeView(layout);
            Log.i(TAG, "removeLinearLayout " + taskId);
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#99000000"));
        return layout;
    }

    private ObserverTextView createObserverTextView(int activityId, String text) {
        ObserverTextView textView = new ObserverTextView(getContext());
        textView.setText(text);
        textView.setTag(activityId);
        return textView;
    }

    float mInnerX;
    float mInnerY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInnerX = event.getX();
                mInnerY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                params.x = (int) (x - mInnerX);
                params.y = (int) (y - mInnerY - mStatusHeight);
                WindowManager windowManager = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));
                assert windowManager != null;
                windowManager.updateViewLayout(this, params);
                break;
        }
        return true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
