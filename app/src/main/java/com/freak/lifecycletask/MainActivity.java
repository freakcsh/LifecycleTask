package com.freak.lifecycletask;

public class MainActivity extends BaseActivity {

    protected void init() {
        setContentView(R.layout.activity_base);
        setTitle(getClass().getSimpleName());
        addCheckBoxes();
    }
}
