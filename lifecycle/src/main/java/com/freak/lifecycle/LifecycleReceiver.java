package com.freak.lifecycle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Freak on 2019/12/1.
 */
public class LifecycleReceiver extends BroadcastReceiver {
    private static final String TAG = LifecycleReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String lifecycle = intent.getStringExtra("lifecycle");
        String task = intent.getStringExtra("task");
        String activity = intent.getStringExtra("activity");
        ArrayList<String> fragments = intent.getStringArrayListExtra("fragments");
        String s = fragments == null ? "" : Arrays.toString(fragments.toArray());
        Log.d(TAG, lifecycle + " " + task + " " + activity + " " + s);

        LifecycleFloatWindow.add(lifecycle, task, activity, fragments);
    }
}
