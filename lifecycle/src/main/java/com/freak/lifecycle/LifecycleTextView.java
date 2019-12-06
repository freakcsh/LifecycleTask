package com.freak.lifecycle;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.freak.lifecycle.model.LifecycleInfo;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleTextView extends AppCompatTextView implements Observer {
    private static AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(10, true);
    private static final int[] COLORS = {
            0xffffffff,//onActivityCreated
            0x33ff0000,//onActivityStarted
            0xffff0000,//onActivityResumed

            0xff000000,//onActivityPaused
            0x33000000,//onActivityStopped
            0xffffffff//onActivityDestroyed
    };
    public LifecycleTextView(Context context) {
        this(context, null);
    }

    public LifecycleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LifecycleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setMaxLines(1);
        setTextSize(10);
        setLayoutParams(params);
    }

    public void setInfoText(String string, String lifecycle) {
        String hash = string.substring(string.indexOf("@"));
        setTag(hash);
        string = string.replace("Activity", "Activity…");
        string = string.replace("Fragment", "Fragment…");
        string = string.replace(hash, " ");
        addLifecycle(string, lifecycle);
    }

    private void addLifecycle(String string, String lifecycle) {
        Log.e(LifecycleOverlayWindow.TAG,"addLifecycle "+lifecycle);
        if (!TextUtils.isEmpty(lifecycle)){
            lifecycle = lifecycle.replace("onFragment", "");
            lifecycle = lifecycle.replace("onActivity", "");
            lifecycle = lifecycle.replace("SaveInstanceState", "SIS");
            if (lifecycle.contains("Created")){
                setTextColor(COLORS[0]);
            }else if (lifecycle.contains("Started")){
                setTextColor(COLORS[1]);
            }else if (lifecycle.contains("Resumed")){
                setTextColor(COLORS[2]);
            }else if (lifecycle.contains("Paused")){
                setTextColor(COLORS[3]);
            }else if (lifecycle.contains("Stopped")){
                setTextColor(COLORS[4]);
            }else if (lifecycle.contains("Destroyed")){
                setTextColor(COLORS[5]);
            }else if (lifecycle.contains("SIS")){
                setTextColor(COLORS[5]);
            }
            SpannableString span = new SpannableString(string + lifecycle);
            span.setSpan(absoluteSizeSpan, string.length(), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(span);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (getTag() == null) {
            return;
        }
        if (arg instanceof LifecycleInfo) {
            LifecycleInfo info = (LifecycleInfo) arg;
            Log.e("TAG","信息  "+info.toString());
            String string = info.getFragment() != null ? info.getFragment().get(0) : info.getActivity();
            String hash = string.substring(string.indexOf("@"));
            if (TextUtils.equals((CharSequence) getTag(), hash)) {
                string = getText().toString();
                string = string.substring(0, string.lastIndexOf(" ") + 1);
                addLifecycle(string, info.getLifecycle());
            }
        }
    }
}
