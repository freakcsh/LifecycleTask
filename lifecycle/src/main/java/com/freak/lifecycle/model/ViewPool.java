package com.freak.lifecycle.model;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.freak.lifecycle.FragmentTaskView;
import com.freak.lifecycle.LifecycleTextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by Freak on 2019/12/1.
 */
public class ViewPool extends Observable {
    private LinkedList<LifecycleTextView> pool = new LinkedList<>();
    private HashMap<String, FragmentTaskView> mMap = new HashMap<>();
    private static ViewPool viewPool;

    public static ViewPool getInstance() {
        if (viewPool == null) {
            synchronized (ViewPool.class) {
                if (viewPool == null) {
                    viewPool = new ViewPool();
                }
            }
        }
        return viewPool;
    }

    public void recycle(ViewGroup viewGroup) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof LifecycleTextView) {
                    removeParent(view);
                    view.setTag(null);
                    pool.add((LifecycleTextView) view);
                } else if (view instanceof FragmentTaskView) {
                    // don't recycle
                } else if (view instanceof ViewGroup) {
                    recycle((ViewGroup) view);
                }
            }
        }
    }

    public LifecycleTextView getActivityTextView(Context context) {
        LifecycleTextView view;
        notifyObservers();
        if (pool.isEmpty()) {
            view = new LifecycleTextView(context);
            addObserver(view);
        } else {
            view = pool.remove();
        }
        return view;
    }

    public void removeParent(View view) {
        if (view != null && view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public void notifyLifecycleChange(LifecycleInfo info) {
        setChanged();
        notifyObservers(info);
    }

    public FragmentTaskView getFragmentTaskView(String activity) {
        return mMap.get(activity);
    }

    public FragmentTaskView addFragmentTaskView(Context context, String activity) {
        FragmentTaskView view = new FragmentTaskView(context);
        mMap.put(activity, view);
        return view;
    }

    public void removeFragmentTaskView(String activity) {
        mMap.remove(activity);
    }

    public void clearFragmentTaskView() {
        mMap.clear();
    }
}
