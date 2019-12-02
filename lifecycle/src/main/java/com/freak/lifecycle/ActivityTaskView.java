package com.freak.lifecycle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.freak.lifecycle.model.ActivityTree;
import com.freak.lifecycle.model.LifecycleInfo;
import com.freak.lifecycle.model.ViewPool;
import com.freak.lifecycle.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Freak on 2019/12/1.
 */
public class ActivityTaskView extends LinearLayout implements Runnable {
    private static final String TAG = ActivityTaskView.class.getSimpleName();
    private ViewGroup mLinearLayout;
    private View mTinyView;
    private View mTaskView;
    private View mEmptyView;
    /**
     * 等待fragment销毁
     */
    private HashSet<String> mPendingRemove;
    private int mStatusHeight;
    private int mScreenWidth;
    private float mInnerX;
    private float mInnerY;
    /**
     * 按下时间
     */
    private long downTime;
    private ActivityTree mActivityTree = new ActivityTree();

    public ActivityTaskView(Context context) {
        this(context, null);
    }

    public ActivityTaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityTaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_activity_task, this);
        mStatusHeight = Utils.getStatusBarHeight(context);
        mScreenWidth = Utils.getScreenWidth(context);
        mLinearLayout = findViewById(R.id.container);
        mTinyView = findViewById(R.id.tiny_view);
        mTaskView = findViewById(R.id.main_view);
        mEmptyView = findViewById(R.id.view_empty);
        mPendingRemove = new HashSet<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                mInnerX = event.getX();
                mInnerY = event.getY();
                postDelayed(this, 300);
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                params.x = (int) (x - mInnerX);
                params.y = (int) (y - mInnerY);
                updateLayout(params);
                if (Math.abs(event.getX() - mInnerX) > 20 || Math.abs(event.getY() - mInnerX) > 20) {
                    removeCallbacks(this);
                }
                break;
            case MotionEvent.ACTION_UP:
                removeCallbacks(this);
                if (System.currentTimeMillis() - downTime < 100
                        && Math.abs(event.getX() - mInnerX) < 20
                        && Math.abs(event.getY() - mInnerX) < 20) {
                    doClick();
                }
                moveToBorder();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void moveToBorder() {
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) getLayoutParams();
        Log.d(TAG, "x " + p.x + " " + ((mScreenWidth - getWidth()) / 2));

        if (p.x <= (mScreenWidth - getWidth()) / 2) { // move left
            p.x = 0;
        } else { // move right
            p.x = mScreenWidth;
        }
        updateLayout(p);
    }

    private void doClick() {
        Log.e(TAG,"点击");
        boolean visible = mTaskView.getVisibility() == VISIBLE;
        mTaskView.setVisibility(visible ? GONE : VISIBLE);
        mTinyView.setVisibility(!visible ? GONE : VISIBLE);
    }

    private void doLongClick() {
//        Intent intent = new Intent(getContext(), MainActivity.class);
//        getContext().startActivity(intent);
    }

    private void updateLayout(WindowManager.LayoutParams params) {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.updateViewLayout(this, params);
        }
    }

    public void add(LifecycleInfo lifecycleInfo) {
        mActivityTree.add(lifecycleInfo.getTask(), lifecycleInfo.getActivity(), lifecycleInfo.getLificycle());
        notifyData();
    }

    public void remove(LifecycleInfo lifecycleInfo) {
        if (ViewPool.getInstance().getFragmentTaskView(lifecycleInfo.getActivity()) != null) {
            mPendingRemove.add(lifecycleInfo.getActivity());
            update(lifecycleInfo);
        } else {
            mActivityTree.remove(lifecycleInfo.getTask(), lifecycleInfo.getActivity());
            notifyData();
        }
    }

    private void update(LifecycleInfo lifecycleInfo) {
        mActivityTree.updateLifecycle(lifecycleInfo.getActivity(), lifecycleInfo.getLificycle());
        ViewPool.getInstance().notifyLifecycleChange(lifecycleInfo);
    }

    public void addFragmentTaskView(LifecycleInfo lifecycleInfo) {
        FragmentTaskView view = ViewPool.getInstance().getFragmentTaskView(lifecycleInfo.getActivity());
        if (view == null) {
            view = ViewPool.getInstance().addFragmentTaskView(getContext(), lifecycleInfo.getActivity());
            notifyData();
        }
        view.add(lifecycleInfo);
    }

    public void removeFragmentTaskView(LifecycleInfo lifecycleInfo) {
        FragmentTaskView fragmentTaskView = ViewPool.getInstance().getFragmentTaskView(lifecycleInfo.getActivity());
        if (fragmentTaskView != null) {
            fragmentTaskView.remove(lifecycleInfo);
            if (fragmentTaskView.getChildCount() == 0 && mPendingRemove.contains(lifecycleInfo.getActivity())) {
                mPendingRemove.remove(lifecycleInfo.getActivity());
                remove(lifecycleInfo);
            }
        }
    }

    public void updateFragmentTaskView(LifecycleInfo lifecycleInfo) {
        FragmentTaskView view = ViewPool.getInstance().getFragmentTaskView(lifecycleInfo.getActivity());
        if (view != null) {
            view.update(lifecycleInfo);
        }
    }

    private void notifyData() {
        ViewPool.getInstance().recycle(mLinearLayout);
        mLinearLayout.removeAllViews();
        for (Map.Entry<String, ArrayList<String>> entry : mActivityTree.entrySet()) {
            TaskLayout taskLayout = new TaskLayout(getContext());
            taskLayout.setTitle(entry.getKey());
            for (String activity : entry.getValue()) {
                LifecycleTextView textView = ViewPool.getInstance().getActivityTextView(getContext());
                textView.setInfoText(activity, mActivityTree.getLifecycle(activity));
                taskLayout.addFirst(textView);

                FragmentTaskView view = ViewPool.getInstance().getFragmentTaskView(activity);
                if (view != null) {
                    taskLayout.addSecond(view);
                }
            }
            mLinearLayout.addView(taskLayout, 0);
        }
        mEmptyView.setVisibility(mLinearLayout.getChildCount() == 0 ? VISIBLE : GONE);
    }

    public void clear() {
        ViewPool.getInstance().clearFragmentTaskView();
        mActivityTree = new ActivityTree();
        notifyData();
    }

    @Override
    public void run() {
//        doLongClick();
    }
}
