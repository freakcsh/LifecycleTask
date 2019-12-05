package com.freak.lifecycle;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.freak.lifecycle.model.FragmentTree;
import com.freak.lifecycle.model.LifecycleInfo;
import com.freak.lifecycle.model.ViewPool;

import java.util.List;

/**
 * Created by Freak on 2019/12/1.
 */
public class FragmentTaskView extends LinearLayout {

    private FragmentTree mFragmentTree = new FragmentTree();

    public FragmentTaskView(Context context) {
        this(context, null);
    }

    public FragmentTaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FragmentTaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public void add(LifecycleInfo lifecycleInfo) {
        mFragmentTree.add(lifecycleInfo.getFragment(), lifecycleInfo.getLifecycle());
        notifyData();
    }

    public void remove(LifecycleInfo lifecycleInfo) {
        mFragmentTree.remove(lifecycleInfo.getFragment());
        notifyData();
        if (getChildCount() == 0) {
            ViewPool.getInstance().removeFragmentTaskView(lifecycleInfo.getActivity());
        }
    }

    public void update(LifecycleInfo lifecycleInfo) {
        mFragmentTree.updateLifecycle(lifecycleInfo.getFragment().get(0), lifecycleInfo.getLifecycle());
        ViewPool.getInstance().notifyLifecycleChange(lifecycleInfo);
    }

    private void notifyData() {
        ViewPool.getInstance().recycle(this);
        removeAllViews();
        if (mFragmentTree != null) {
            List<String> strings = mFragmentTree.convertToList();
            for (String string : strings) {
                LifecycleTextView lifecycleTextView = ViewPool.getInstance().getActivityTextView(getContext());
                String[] arr = string.split("-");
                String name = arr[arr.length - 1];
                lifecycleTextView.setInfoText(string, mFragmentTree.getLifecycle(name));
                addView(lifecycleTextView);
            }
        }
    }
}
