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

package com.recalot.model.rec.context;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class ParamsContext implements UserContext {

    private Map<String, Map<String, Object>> userContext = new HashMap<>();

    @Override
    public Object getContext(String sourceId, String userId) {
        return userContext.get(sourceId) != null ? userContext.get(sourceId).get(userId) : null;
    }

    @Override
    public void processContext(String sourceId, String userId, Object context) {
        processContext(sourceId, userId, context, null);
    }

    @Override
    public void processContext(String sourceId, String userId, Object context, String type) {
        if (context != null && type != null) {
            if (!userContext.containsKey(sourceId)) {
                userContext.put(sourceId, new HashMap<>());
            }

            if (type.equals(Helper.Keys.Context.Params) && context instanceof HashMap) {
                HashMap params = (HashMap) context;
                userContext.get(sourceId).put(userId, params);
            }
        }
    }


    @Override
    public String getKey() {
        return "user-params";
    }

    @Override
    public String getDescription() {
        return "Returns the last visited items.";
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Object getContext() {
        return null;
    }
}
