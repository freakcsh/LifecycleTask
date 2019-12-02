package com.freak.lifecycle;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.freak.lifecycle.model.LifecycleInfo;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleTextView extends AppCompatTextView implements Observer {
    private static AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(8, true);

    public LifecycleTextView(Context context) {
        this(context, null);
    }

    public LifecycleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LifecycleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMaxLines(1);
        setTextSize(10);
    }

    public void setInfoText(String string, String lifecycle) {
        String hash = string.substring(string.indexOf("@"));
        setTag(hash);
        string = string.replace(hash, "");
        addLifecycle(string, lifecycle);
    }

    private void addLifecycle(String string, String lifecycle) {
//        lifecycle = lifecycle.replace("onFragment", "");
//        lifecycle = lifecycle.replace("onActivity", "");
//        lifecycle = lifecycle.replace("SaveInstanceState", "SIS");
        setTextColor(lifecycle.contains("onResume") ? Color.BLACK : Color.WHITE);
        SpannableString span = new SpannableString(string + lifecycle);
        span.setSpan(absoluteSizeSpan, string.length(), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(span);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (getTag() == null) {
            return;
        }
        if (arg instanceof LifecycleInfo) {
            LifecycleInfo info = (LifecycleInfo) arg;
            String string = info.getFragment() != null ? info.getFragment().get(0) : info.getActivity();
            String hash = string.substring(string.indexOf("@"));
            if (TextUtils.equals((CharSequence) getTag(), hash)) {
                string = getText().toString();
                string = string.substring(0, string.lastIndexOf(" ") + 1);
                addLifecycle(string, info.getLificycle());
            }
        }
    }
}
