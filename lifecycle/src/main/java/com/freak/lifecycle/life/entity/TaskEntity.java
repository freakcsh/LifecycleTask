package com.freak.lifecycle.life.entity;

/**
 * Created by Freak on 2019/12/1.
 */
public class TaskEntity {
    private int taskId;
    private int activityId;
    private String activityName;
    private boolean isOnCreate = false;

    public TaskEntity(int taskId, int activityId, String activityName) {
        this.taskId = taskId;
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public boolean isOnCreate() {
        return isOnCreate;
    }

    public void setOnCreate(boolean onCreate) {
        isOnCreate = onCreate;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TaskEntity && taskId == ((TaskEntity) obj).getActivityId();
    }
}
