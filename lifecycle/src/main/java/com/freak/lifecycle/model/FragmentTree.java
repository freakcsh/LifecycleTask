package com.freak.lifecycle.model;


import android.util.Log;

import com.freak.lifecycle.LifecycleOverlayWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Freak on 2019/12/1.
 */
public class FragmentTree extends LinkedHashMap<String, ArrayList<String>> {
    private static final String tab1 = "" + '\u2502';// |
    private static final String tab2 = "" + '\u2514' + '\u2500';// |_
    private static final String tab3 = "" + '\u251c' + '\u2500';// |-
    private Node mNode;
    private HashMap<String, String> lifeMap = new HashMap<>();

    public FragmentTree() {
        mNode = new Node("");
    }

    private static class Node {
        private String name;
        private HashMap<String, Node> children;

        public Node(String name) {
            this.name = name;
            this.children = new HashMap<>();
        }
    }

    public void add(List<String> list, String lifecycle) {
        lifeMap.put(list.get(0), lifecycle);
        Node node = mNode;
        while (!list.isEmpty()) {
            String string = list.remove(list.size() - 1);
            if (node == null) {
                return;
            }
            if (!node.children.containsKey(string)) {
                Node newNode = new Node(string);
                node.children.put(string, newNode);
            }
            node = node.children.get(string);
        }
    }

    public void remove(List<String> list) {
        if (list.isEmpty()) {
            return;
        }
        lifeMap.remove(list.get(0));
        Node node = mNode;
        while (list.size() > 1) {
            String string = list.remove(list.size() - 1);
            if (node == null) {
                return;
            }
            if (node.children.containsKey(string)) {
                node = node.children.get(string);
            } else {
                return;
            }
        }
        if (node == null) {
            return;
        }
        String last = list.get(list.size() - 1);
        node.children.remove(last);
    }

    /**
     * 转换成list
     */
    public List<String> convertToList() {
        List<String> list = new ArrayList<>();
        convert(list, mNode, "", true);
        return list;
    }

    private void convert(List<String> list, Node node, String pre, boolean end) {
        if (node != mNode) {
            String string = pre + (end ? tab2 : tab3) + node.name;
            Log.e(LifecycleOverlayWindow.TAG,"convert string "+string);
            list.add(string);
        }
        int i = 0;
        for (Entry<String, Node> entry : node.children.entrySet()) {
            i++;
            boolean subEnd = i == node.children.size();
            String subPre = pre + (node == mNode ? "" : (end ? "      " : tab1 + "    "));
            convert(list, entry.getValue(), subPre, subEnd);
        }
    }

    public String getLifecycle(String name) {
        return lifeMap.get(name);
    }

    public void updateLifecycle(String key, String value) {
        lifeMap.put(key, value);
    }

}
