// Copyright (C) 2015 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

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
        this.items = new ArrayList<>();
    }

    public void addItem(String itemId) {
        if (!items.contains(itemId)) {
            items.add(itemId);
            count = items.size();
        }
    }

    public void addItem(int index, String itemId) {
        if (!items.contains(itemId)) {
            items.add(index, itemId);
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
