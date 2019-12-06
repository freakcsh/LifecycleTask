package com.freak.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.freak.lifecycle.model.LifecycleInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.freak.lifecycle.util.Utils.getSimpleName;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleOverlayWindow {
    public static final String TAG = LifecycleOverlayWindow.class.getSimpleName();
    private static ActivityTaskView activityTaskView;
    private static long interval = 100;
    private static QueueHandler queueHandler;

    public static void start(Application context) {
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
    /**
     * 在application中的onCreate方法中初始化
     *
     * @param app   application
     * @param debug 是否是 BuildConfig.DEBUG 模式，设置为false则不显示悬浮窗
     */
    public static void init(Application app, boolean debug) {
        if (!debug) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(app)) {
            Intent intent = new Intent(app, RequestOverlayActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            app.startActivity(intent);
        } else {
            start(app);
        }
//        start(app);
    }
    private static void addViewToWindow(Application app, View view) {
        WindowManager windowManager = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
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
        params.y = app.getResources().getDisplayMetrics().heightPixels - 500;
        if (windowManager != null) {
            windowManager.addView(view, params);
        }
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }
    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof FragmentActivity) {
                Log.e(TAG,"FragmentActivity");
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
            }
            Log.e(TAG,"onActivityCreated");
            handleActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.e(TAG,"onActivityStarted");
            handleActivity(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.e(TAG,"onActivityResumed");
            handleActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.e(TAG,"onActivityPaused");
            handleActivity(activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.e(TAG,"onActivityStopped");
            handleActivity(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.e(TAG,"onActivitySaveInstanceState");
            handleActivity(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.e(TAG,"onActivityDestroyed");
            handleActivity(activity);
        }
    };
    private static FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {

        @Override
        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            handleFragment(f, context);
        }

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            handleFragment(f, context);
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
            handleFragment(f);
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            handleFragment(f);
        }
    };

    private static void handleActivity(Activity activity) {
        if(activity == null) {
            Log.e("ActivityTaskHelper", "handleActivity null");
            return;
        }
        sendLifecycle(activity, null);
    }
    private static void handleFragment(Fragment fragment) {
        if (fragment == null || fragment.getActivity() == null) {
            Log.e(TAG, "handleFragment null");
            return;
        }
        sendLifecycle(fragment.getActivity(), fragment);
    }

    static void handleFragment(Fragment fragment, Context context) {
        if (fragment == null || !(context instanceof Activity)) {
            Log.e("ActivityTaskHelper", "handleFragment null");
            return;
        }
        sendLifecycle((Activity) context, fragment);
    }

    private static void sendLifecycle(Activity activity, Fragment fragment) {
        String lifecycle = Thread.currentThread().getStackTrace()[5].getMethodName();
        String task = activity.getPackageName() + "@0x" + Integer.toHexString(activity.getTaskId());
        String activityString = getSimpleName(activity);
        ArrayList<String> fragments = null;
        if (fragment != null) {
            Log.e(TAG,"fragment 不为null");
            fragments = getAllFragments(fragment);
        }
        String s = fragments == null ? "" : Arrays.toString(fragments.toArray());
        Log.d(TAG, "lifecycle  "+lifecycle + "task  " + task + "activity " + activity + "s " + s);

        LifecycleOverlayWindow.add(lifecycle, task, activityString, fragments);
    }

    private static ArrayList<String> getAllFragments(Fragment fragment) {
        ArrayList<String> res = new ArrayList<>();
        while (fragment != null) {
            res.add(getSimpleName(fragment));
            fragment = fragment.getParentFragment();
        }
        return res;
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
                        Log.e(TAG,"Lifecycle handleMessage "+lifecycleInfo.getLifecycle());
                        if (lifecycleInfo.getLifecycle().contains("PreAttached")) {
                            activityTaskView.addFragmentTaskView(lifecycleInfo);
                            Log.e("TAG","添加fragment");
                        } else if (lifecycleInfo.getLifecycle().contains("Detached")) {
                            Log.e("TAG","删除fragment");
                            activityTaskView.removeFragmentTaskView(lifecycleInfo);
                        } else {
                            Log.e("TAG","更新fragment");
                            Log.e("TAG","lifecycleInfo "+lifecycleInfo.toString());
                            activityTaskView.updateFragmentTaskView(lifecycleInfo);
                        }
                    } else {
                        if (lifecycleInfo.getLifecycle().contains("Create")) {
                            Log.e("TAG","添加activity");
                            activityTaskView.add(lifecycleInfo);
                        } else if (lifecycleInfo.getLifecycle().contains("Destroy")) {
                            Log.e("TAG","移除activity");
                            activityTaskView.remove(lifecycleInfo);
                        } else {
                            Log.e("TAG","更新activity");
                            Log.e("TAG","lifecycleInfo "+lifecycleInfo.toString());
                            activityTaskView.update(lifecycleInfo);
                        }
                    }
                }
            }
        }
    }
}
