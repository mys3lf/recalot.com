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

package com.recalot.common.interfaces.model.data;

import com.recalot.common.communication.Relation;
import com.recalot.common.exceptions.BaseException;

import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface RelationDataAccess {
    public Relation[] getRelations() throws BaseException;
    public Relation[] getRelations(String fromId, String toId) throws BaseException;
    public Relation[] getRelationsFor(String fromId) throws BaseException;
    public int getRelationCount();
    public Relation getRelation(String relationId) throws BaseException;
    public Relation updateRelation(String relationId, String fromId, String toId, String type, Map<String, String> content) throws BaseException;
    public Relation createRelation(String fromId, String toId, String type, Map<String, String> content) throws BaseException;
}
