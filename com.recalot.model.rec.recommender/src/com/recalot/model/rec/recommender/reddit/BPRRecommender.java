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
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.rec.recommender.bprmf.BPRMFRecommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */

@Configuration(key = "recommendOnlyItemsTheUserAlreadyViewed", type = ConfigurationItem.ConfigurationItemType.Boolean, value = "true", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional)
public class BPRRecommender extends BPRMFRecommender {

    protected HashMap<String, HashMap<String, Item>> uItems;
    protected ArrayList<Item> filteredItems;

    protected boolean recommendOnlyItemsTheUserAlreadyViewed = false;

    @Override
    public void train() throws BaseException {
        super.train();

        uItems = new HashMap<>();
        filteredItems = new ArrayList<>();

        for(Interaction interaction: getDataSet().getInteractions()){
            String userId = interaction.getUserId();
            if(!uItems.containsKey(userId)) uItems.put(userId, new HashMap<>());

            String itemId = interaction.getItemId();

            if(!uItems.get(userId).containsKey(itemId)) {
                uItems.get(userId).put(itemId, getDataSet().getItem(itemId));
            }
        }

        for(Item item: getDataSet().getItems()){
            if(item.getId().length() > 2) {
                filteredItems.add(item);
            }
        }
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException {

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

        items = Helper.applySubList(items, param, 10);

        return new RecommendationResult(getId(), items);
    }


    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException {
        return super.predict(userId, itemId, context, param);
    }

    public boolean isRecommendOnlyItemsTheUserAlreadyViewed() {
        return recommendOnlyItemsTheUserAlreadyViewed;
    }

    public void setRecommendOnlyItemsTheUserAlreadyViewed(boolean recommendOnlyItemsTheUserAlreadyViewed) {
        this.recommendOnlyItemsTheUserAlreadyViewed = recommendOnlyItemsTheUserAlreadyViewed;
    }
}
