package com.freak.lifecycle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Freak on 2019/12/1.
 */
public class TaskLayout extends FrameLayout {
    private LinearLayout mLinearLayout;
    private TextView mTextView;

    public TaskLayout(@NonNull Context context) {
        this(context, null);
    }

    public TaskLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        inflate(context, R.layout.layout_task, this);
        mLinearLayout = findViewById(R.id.container);
        mTextView = findViewById(R.id.tv_title);
    }

    public void setTitle(String title) {
        mTextView.setText("[" + title + "]");
    }
    public void addFirst(View view){
        mLinearLayout.addView(view,0);
    }
    public void addSecond(View view) {
        mLinearLayout.addView(view, 1);
    }
}
