package com.recalot.demos.wallpaper.controller;

import com.recalot.common.communication.Item;

import java.util.Comparator;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class ItemCompare implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {
        return o2.getValue("timeStamp").compareTo(o1.getValue("timeStamp"));
    }
}
