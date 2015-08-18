package com.recalot.demos.wallpaper.model;

import java.util.ArrayList;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Category {
    private final ArrayList<String> items;
    private String preview;
    private int count;
    private String name;

    public Category(String name, String preview) {
        this.preview = preview;
        count = 0;
        this.name = name;
        this.items = new ArrayList<String>();
    }

    public void addItem(String itemId) {
        if (!items.contains(itemId)) {
            items.add(itemId);
            count = items.size();
        }
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getItems() {
        return items;
    }
}
