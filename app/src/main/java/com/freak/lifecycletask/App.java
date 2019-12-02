package com.freak.lifecycletask;

import android.app.Application;


import com.freak.lifecycle.LifecycleFloatWindow;
import com.freak.lifecycle.life.LifecycleOverlayWindow;


/**
 * Created by Freak on 2019/12/1.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LifecycleOverlayWindow.init(this, BuildConfig.DEBUG);
//        LifecycleFloatWindow.start(this);
    }
}
