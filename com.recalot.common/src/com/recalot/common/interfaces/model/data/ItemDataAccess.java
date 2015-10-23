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

package com.recalot.common.interfaces.model.data;

import com.recalot.common.communication.Item;
import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;

import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface ItemDataAccess {
    public Item[] getItems() throws BaseException;
    public int getItemsCount();

    public Item getItem(String itemId) throws BaseException;
    public Item tryGetItem(String itemId) throws BaseException;
    public Item updateItem(String itemId, Map<String, String> content) throws BaseException;
    public Item createItem(Map<String, String> content) throws BaseException;
}
