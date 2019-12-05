package com.freak.lifecycletask;

public class DialogActivity extends BaseActivity {

    @Override
    protected void init() {
        setContentView(R.layout.activity_base);
        addCheckBoxes();
    }
}
