package com.freak.lifecycle.model;

import java.util.List;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleInfo {
    private String lifecycle;
    private String task;
    private String activity;
    private List<String> fragment;

    public LifecycleInfo(String lifecycle, String task, String activity, List<String> fragment) {
        this.lifecycle = lifecycle;
        this.task = task;
        this.activity = activity;
        this.fragment = fragment;
    }

    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public List<String> getFragment() {
        return fragment;
    }

    public void setFragment(List<String> fragment) {
        this.fragment = fragment;
    }

    @Override
    public String toString() {
        return "LifecycleInfo{" +
                "lifecycle='" + lifecycle + '\'' +
                ", task='" + task + '\'' +
                ", activity='" + activity + '\'' +
                ", fragment=" + fragment +
                '}';
    }
}
