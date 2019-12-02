package com.freak.lifecycle.model;

import java.util.List;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleInfo {
    private String lificycle;
    private String task;
    private String activity;
    private List<String> fragment;

    public LifecycleInfo(String lificycle, String task, String activity, List<String> fragment) {
        this.lificycle = lificycle;
        this.task = task;
        this.activity = activity;
        this.fragment = fragment;
    }

    public String getLificycle() {
        return lificycle;
    }

    public void setLificycle(String lificycle) {
        this.lificycle = lificycle;
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
}
