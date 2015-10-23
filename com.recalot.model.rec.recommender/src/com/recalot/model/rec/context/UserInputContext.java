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

package com.recalot.model.rec.context;

import com.recalot.common.Helper;
import com.recalot.common.communication.Item;
import com.recalot.common.context.UserContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class UserInputContext implements UserContext {

    private Map<String, Map<String, String>> userContext = new HashMap<>();

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
                Object input = params.get("input");
                if(input != null && input instanceof String){
                    userContext.get(sourceId).put(userId, (String)input);
                }
            } else if(type.equals(Helper.Keys.Context.Item) && context instanceof Item) {
                Item item = (Item)context;

                //just take the first letter
                userContext.get(sourceId).put(userId, item.getId().substring(0, 1));
            }
        }
    }


    @Override
    public String getKey() {
        return "user-input";
    }

    @Override
    public String getDescription() {
        return "Returns the current input of a user.";
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Object getContext() {
        return null;
    }
}
