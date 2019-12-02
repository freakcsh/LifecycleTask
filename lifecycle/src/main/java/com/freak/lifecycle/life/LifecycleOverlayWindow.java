package com.freak.lifecycle.life;

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


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.freak.lifecycle.life.entity.ColorEntity;
import com.freak.lifecycle.life.entity.TaskEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.freak.lifecycle.util.Utils.getSimpleName;

/**
 * Created by Freak on 2019/12/1.
 * 生命周期悬浮窗
 */
public class LifecycleOverlayWindow {

    private static final String TAG = LifecycleOverlayWindow.class.getSimpleName();

    private static final long DELAY = 300;

    private static final int[] COLORS = {
            0xffffffff,//onCreate
            0x33ff0000,//onStart
            0xffff0000,//onResume

            0xff000000,//onPause
            0x33000000,//onStop
            0xffffffff//onDestroy
    };

    private static ActivityTaskView lifecycleTaskView;
    private static ActivityLifecycleObservable activityLifecycleObservable;

    /**
     * 是否处于前台
     * <p>
     * 是: Activity A onPause -> Activity B onResume -> Activity A onStop
     * <p>
     * 不是: Activity A onPause -> Activity A onStop
     */
    private static boolean isFront;

    private static QueueHandler queueHandler;

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
            addWindow(app);
        }
    }

    static void addWindow(Application app) {
        activityLifecycleObservable = new ActivityLifecycleObservable();
        lifecycleTaskView = new ActivityTaskView(app);
        lifecycleTaskView.setObservable(activityLifecycleObservable);

        queueHandler = new QueueHandler();

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
        params.y = app.getResources().getDisplayMetrics().heightPixels - 800;
        if (windowManager == null) {
            return;
        }
        windowManager.addView(lifecycleTaskView, params);

        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    static class QueueHandler extends Handler {

        private Queue<Object> queue;
        private long lastTime;

        public QueueHandler() {
            super(Looper.getMainLooper());
            lastTime = 0;
            queue = new LinkedList<>();
        }

        public void add(Object o) {
            queue.add(o);
            sendEmptyMessage(0);
        }

        @Override
        public void handleMessage(Message msg) {
            if (System.currentTimeMillis() - lastTime < DELAY) {
                sendEmptyMessageDelayed(0, DELAY / 5);
            } else {
                Object obj = queue.poll();
                if (obj instanceof TaskEntity) {
                    TaskEntity taskEntity = (TaskEntity) obj;
                    if (taskEntity.isOnCreate()) {
                        lifecycleTaskView.add(taskEntity);
                        activityLifecycleObservable.lifecycleChange(new ColorEntity(COLORS[0], taskEntity.getActivityId()));
                    } else {
                        lifecycleTaskView.remove(taskEntity);
                    }
                    lastTime = System.currentTimeMillis();
                } else if (obj instanceof ColorEntity) {
                    activityLifecycleObservable.lifecycleChange((ColorEntity) obj);
                    lastTime = System.currentTimeMillis();
                }
            }
        }
    }

    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof FragmentActivity) {
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
            }
            Log.w(TAG, activity.getClass().getName() + "@" + activity.hashCode() + " " + activity.getTaskId() + " " + " onActivityCreated");

            TaskEntity taskEntity = new TaskEntity(activity.getTaskId(), activity.hashCode(), activity.getClass().getSimpleName());
            taskEntity.setOnCreate(true);
            queueHandler.add(taskEntity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName() + " onActivityStarted");
            queueHandler.add(new ColorEntity(COLORS[1], activity.hashCode()));
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName() + " onActivityResumed");
            queueHandler.add(new ColorEntity(COLORS[2], activity.hashCode()));
            lifecycleTaskView.setVisibility(VISIBLE);
            isFront = true;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName() + " onActivityPaused");
            queueHandler.add(new ColorEntity(COLORS[3], activity.hashCode()));
            isFront = false;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName() + " onActivityStopped");
            queueHandler.add(new ColorEntity(COLORS[4], activity.hashCode()));
            lifecycleTaskView.setVisibility(isFront ? VISIBLE : GONE);

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG, activity.getClass().getSimpleName() + " onActivitySaveInstanceState");

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.w(TAG, activity.getClass().getSimpleName() + " onActivityDestroyed");
            queueHandler.add(new ColorEntity(COLORS[5], activity.hashCode()));
            queueHandler.add(new TaskEntity(activity.getTaskId(), activity.hashCode(), activity.getClass().getSimpleName()));
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

    {

    }

    private static void handleFragment(Fragment fragment) {
        if (fragment == null || fragment.getActivity() == null) {
            Log.e(TAG, "handleFragment null");
            return;
        }
        sendBroadcast(fragment.getActivity(), fragment);
    }

    static void handleFragment(Fragment fragment, Context context) {
        if (fragment == null || !(context instanceof Activity)) {
            Log.e("ActivityTaskHelper", "handleFragment null");
            return;
        }
        sendBroadcast((Activity) context, fragment);
    }
    private static void sendBroadcast(Activity activity, Fragment fragment) {
        String lifecycle = Thread.currentThread().getStackTrace()[5].getMethodName();
        String packageName = "com.freak.lifecycle";
        Intent intent = new Intent(packageName + ".ACTION_UPDATE_LIFECYCLE");
        intent.setPackage(packageName);
        intent.putExtra("lifecycle", lifecycle);
        intent.putExtra("task", activity.getPackageName() + "@0x" + Integer.toHexString(activity.getTaskId()));
        intent.putExtra("activity", getSimpleName(activity));
        if(fragment != null) {
            intent.putStringArrayListExtra("fragments", getAllFragments(fragment));
        }
        activity.sendBroadcast(intent);
    }
    private static ArrayList<String> getAllFragments(Fragment fragment){
        ArrayList<String> res = new ArrayList<>();
        while(fragment != null){
            res.add(getSimpleName(fragment));
            fragment = fragment.getParentFragment();
        }
        return res;
    }
    static class ActivityLifecycleObservable extends Observable {

        /**
         * 活动生命周期更改时，通知观察者
         *
         * @param info ColorEntity
         */
        void lifecycleChange(ColorEntity info) {
            setChanged();
            notifyObservers(info);
        }
    }
}
