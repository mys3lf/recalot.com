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

package com.recalot.model.rec.recommender.reddit;

import com.recalot.common.Helper;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.BaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//TODO
/**
 * @author Matthäus Schmedding (info@recalot.com)
 */

@Configuration(key = "contextType", type = ConfigurationItem.ConfigurationItemType.Options, value = "letter", options = {"letter", "last", "both"}, requirement = ConfigurationItem.ConfigurationItemRequirementType.Required)
public class ContextAwareBPRRecommender extends BPRRecommender {

    private String contextType;
    private ContextHelper contextHelper;

    @Override
    public void train() throws BaseException {
        super.train();

        contextHelper = new ContextHelper(getDataSourceId());
        contextHelper.train(getDataSet());
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException {

        UserContext userInputContext = (UserContext) context.getInstance("user-input");
        UserContext userLastItemContext = (UserContext) context.getInstance("user-last-visited");

        List<RecommendedItem> items = new ArrayList<>();
        try {
            List<String> rec;
            if(recommendOnlyItemsTheUserAlreadyViewed) {
                rec = recommendItemsByRatingPrediction(userId, uItems.get(userId).values().toArray(new Item[uItems.get(userId).values().size()]), true);
            } else {
                rec = recommendItemsByRatingPrediction(userId, filteredItems.toArray(new Item[filteredItems.size()]), true);
            }

            for (String key : rec) {
                items.add(new RecommendedItem(key, 0.0));
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        switch (contextType) {
            case "letter":
                items = contextHelper.applyLetterContext(items, userId, userInputContext);
                break;
            case "last":
                items = contextHelper.applyLastWordsContext(items, userId, userLastItemContext);
                break;
            case "both":
                items = contextHelper.applyBothContext(items, userId, userInputContext, userLastItemContext);
                break;
        }

        items = Helper.applySubList(items, param, 10);

        return new RecommendationResult(getId(), items);
    }


    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException {
        return super.predict(userId, itemId, context, param);
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }
}
