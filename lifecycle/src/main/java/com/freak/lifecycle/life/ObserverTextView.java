package com.freak.lifecycle.life;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatTextView;

import com.freak.lifecycle.life.entity.ColorEntity;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Freak on 2019/12/1.
 */

public class ObserverTextView extends AppCompatTextView implements Observer {

    public ObserverTextView(Context context) {
        this(context, null);
    }

    public ObserverTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObserverTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextSize(10);
        setTextColor(Color.WHITE);
        setMaxLines(1);
        setPadding(10, 10, 10, 10);
        setBackgroundColor(Color.parseColor("#99000000"));
    }

    @Override
    public void update(Observable o, Object arg) {
        ColorEntity colorEntity = (ColorEntity) arg;
        if (colorEntity.getActivityId() == (int) getTag()) {
            setTextColor(colorEntity.getColor());
        }
    }

}