package com.freak.lifecycle;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.freak.lifecycle.model.LifecycleInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleFloatWindow {
    private static final String TAG = LifecycleFloatWindow.class.getSimpleName();
    private static ActivityTaskView activityTaskView;
    private static long interval = 100;
    private static QueueHandler queueHandler;

    public static void start(Context context) {
        if (activityTaskView == null) {
            activityTaskView = new ActivityTaskView(context);
            addViewToWindow(context, activityTaskView);
        }
    }

    public static void clear() {
        if (activityTaskView != null) {
            activityTaskView.clear();
        }
    }

    private static void addViewToWindow(Context context, View view) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
//            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = context.getResources().getDisplayMetrics().heightPixels - 500;
        if (windowManager != null) {
            windowManager.addView(view, params);
        }
    }

    public static void add(String lifecycle, String task, String activity, List<String> fragments) {
        if (queueHandler == null) {
            queueHandler = new QueueHandler();
        }
        LifecycleInfo lifecycleInfo = new LifecycleInfo(lifecycle, task, activity, fragments);
        queueHandler.send(lifecycleInfo);
    }

    private static class QueueHandler extends Handler {
        private Queue<LifecycleInfo> mQueue;
        private long lastTime;

        public QueueHandler() {
            super(Looper.getMainLooper());
            lastTime = 0;
            mQueue = new LinkedList<>();
        }

        void send(LifecycleInfo lifecycleInfo) {
            mQueue.add(lifecycleInfo);
            sendEmptyMessage(0);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (System.currentTimeMillis() - lastTime < interval) {
                sendEmptyMessageDelayed(0, interval / 5);
            } else {
                lastTime = System.currentTimeMillis();
                //检索并删除此队列的开头，如果此队列为空，则返回
                LifecycleInfo lifecycleInfo = mQueue.poll();
                if (lifecycleInfo != null && activityTaskView != null) {
                    if (lifecycleInfo.getFragment() != null) {
                        if (lifecycleInfo.getLificycle().contains("PreAttach")) {
                            activityTaskView.addFragmentTaskView(lifecycleInfo);
                        } else if (lifecycleInfo.getLificycle().contains("Detach")) {
                            activityTaskView.removeFragmentTaskView(lifecycleInfo);
                        } else {
                            activityTaskView.updateFragmentTaskView(lifecycleInfo);
                        }
                    } else {
                        if (lifecycleInfo.getLificycle().contains("Create")) {
                            activityTaskView.add(lifecycleInfo);
                        } else if (lifecycleInfo.getLificycle().contains("Destroy")) {
                            activityTaskView.remove(lifecycleInfo);
                        } else {
                            activityTaskView.updateFragmentTaskView(lifecycleInfo);
                        }
                    }
                }
            }
        }
    }
}
