package com.freak.lifecycle;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Freak on 2019/12/1.
 */
public class TaskLayout extends LinearLayout {
    public static LinearLayout mLinearLayout;
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
        // 设置总体布局属性
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinearLayout = createLinearLayout();
        mLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        mLinearLayout.setOrientation(VERTICAL);
        this.addView(mLinearLayout);
        mTextView = new TextView(context);
        mTextView.setTextColor(Color.parseColor("#00FF00"));
        mTextView.setTextSize(10);
        mTextView.setIncludeFontPadding(false);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setLayoutParams(params);
//        mTextView.setPadding(15, 5, 15, 5);
        this.addView(mTextView,0);
    }

    public  LinearLayout createLinearLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#000000"));
        layout.setId(R.id.create_linear_layout);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return layout;
    }


    public void setTitle(String title) {
        mTextView.setText("[" + title + "]");
    }

    public void addFirst(View view) {
        this.addView(view, 1);
    }

    public void addSecond(View view) {
        this.addView(view, 2);
    }
}
