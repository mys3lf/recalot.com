package com.recalot.demos.wallpaper.model;

import java.util.ArrayList;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Paging<T> {
    private int count;
    private int pageSize;
    private int page;
    private T[] items;

    public Paging() {
        pageSize = 10;
        count = 0;
        page = 1;
    }

    public T[] getItems() {
        return items;
    }

    public void setItems(T[] items) {
        this.items = items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
