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

package com.recalot.common.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class InnerIds {

    private static final HashMap<String, HashMap<String, Integer>> ids;
    private static final HashMap<String, HashMap<Integer, String>> revert;

    static {
        ids = new HashMap<>();
        revert = new HashMap<>();
    }

    /**
     * @param rawId raw id as String
     * @param type  id type
     * @return inner id as int
     */
    public static int getId(String rawId, String type)  {
        if (!ids.containsKey(type) || !ids.get(type).containsKey(rawId)) return -1;
        return ids.get(type).get(rawId);
    }

    /**
     * @param innerId inner user id as int
     * @param type    id type
     * @return raw id as string
     */
    public static String getId(int innerId, String type) {
        if (!revert.containsKey(type) || !revert.get(type).containsKey(innerId)) return null;
        return revert.get(type).get(innerId);
    }

    /**
     * @param rawId rawId user id as String
     * @param type  id type
     * @return available inner id as int
     */
    public static int getNextId(String rawId, String type) {
        if (!ids.containsKey(type)) {
            ids.put(type, new HashMap<>());
        }

        if (!revert.containsKey(type)) {
            revert.put(type, new HashMap<>());
        }

        if (ids.get(type).containsKey(rawId)) {
            return ids.get(type).get(rawId);
        }

        int innerId = ids.get(type).size();

        ids.get(type).put(rawId, innerId);
        revert.get(type).put(innerId, rawId);

        return innerId;
    }

}
