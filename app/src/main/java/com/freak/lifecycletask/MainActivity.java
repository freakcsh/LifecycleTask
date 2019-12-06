package com.freak.lifecycletask;

import com.freak.lifecycle.LifecycleOverlayWindow;

public class MainActivity extends BaseActivity {

    protected void init() {
        setContentView(R.layout.activity_base);
        setTitle(getClass().getSimpleName());
        addCheckBoxes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LifecycleOverlayWindow.clear();
    }
}
