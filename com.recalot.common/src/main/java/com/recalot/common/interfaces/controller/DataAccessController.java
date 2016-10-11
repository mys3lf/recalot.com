// Copyright (C) 2016 Matthäus Schmedding
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

package com.recalot.common.interfaces.controller;

/**
 * @author Matthaeus.schmedding
 */
public interface DataAccessController extends Controller {

    public enum DataAccessRequestAction implements RequestAction {
        GetData(0),
        GetUser (1),
        GetUsers(2),
        UpdateUser(3),
        CreateUser(4),
        GetItems(5),
        GetItem(6),
        UpdateItem(7),
        CreateItem(8),
        DeleteItem(22),
        GetInteractions(9),
        GetInteraction(10),
        GetSources(11),
        CreateSource(12),
        UpdateSource(13),
        GetSource(14),
        DeleteSource(15),
        AddInteraction(16),
        GetDataSourceBuilder(17),
        GetRelation(18),
        GetRelations(19),
        CreateRelation(20),
        UpdateRelation(21);

        private final int value;

        @Override
        public int getValue() {
            return this.value;
        }

        DataAccessRequestAction(int value) {
            this.value = value;
        }
    }
}
