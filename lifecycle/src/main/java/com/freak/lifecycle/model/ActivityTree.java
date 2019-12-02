package com.freak.lifecycle.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Freak on 2019/12/1.
 */
public class ActivityTree extends LinkedHashMap<String, ArrayList<String>> {
    private HashMap<String, String> lifeMap = new HashMap<>();

    public void add(String key, String value, String lifecycle) {
        ArrayList<String> values = get(key);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        put(key, values);
        lifeMap.put(value, lifecycle);
    }

    public void remove(String key, String value) {
        ArrayList<String> values = get(key);
        if (values == null) {
            return;
        }
        values.remove(key);
        if (values.isEmpty()) {
            remove(key);
        }
        lifeMap.remove(value);
    }

    public String getLifecycle(String name) {
        return lifeMap.get(name);
    }

    public void updateLifecycle(String key, String value) {
        lifeMap.put(key, value);
    }
}
