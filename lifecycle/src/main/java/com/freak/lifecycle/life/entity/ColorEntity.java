package com.freak.lifecycle.life.entity;

/**
 * Created by Freak on 2019/12/1.
 */
public class ColorEntity {
    private int color;
    private int activityId;

    public ColorEntity(int color, int activityId) {
        this.color = color;
        this.activityId = activityId;
    }

    public int getColor() {
        return color;
    }

    public int getActivityId() {
        return activityId;
    }
}
