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

import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.*;

/**
 * @author matthaeus.schmedding
 */

@Configuration(key = "recommendOnlyItemsTheUserAlreadyViewed", type = ConfigurationItem.ConfigurationItemType.Boolean, value = "true", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional)
public class RandomRecommender extends Recommender {

    protected   List<RecommendedItem> recommendedItems;
    protected HashMap<String, HashMap<String, Item>> uItems;
    protected boolean recommendOnlyItemsTheUserAlreadyViewed = false;


    @Override
    public void train() throws BaseException {
        recommendedItems = new ArrayList<>();

        for (Item item : getDataSet().getItems()) {
            if (item.getId().length() <= 2) continue;
            recommendedItems.add(new RecommendedItem(item, 0));
        }


        uItems = new HashMap<>();

        for (Interaction interaction : getDataSet().getInteractions()) {
            String userId = interaction.getUserId();
            if (!uItems.containsKey(userId)) uItems.put(userId, new HashMap<>());

            String itemId = interaction.getItemId();

            if (!uItems.get(userId).containsKey(itemId)) {
                uItems.get(userId).put(itemId, getDataSet().getItem(itemId));
            }
        }
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException{

        List<RecommendedItem> temp = new ArrayList<>(recommendedItems);
        List<RecommendedItem> items;
        Collections.shuffle(temp);

        if (recommendOnlyItemsTheUserAlreadyViewed) {
            items = new ArrayList<>();
            for (RecommendedItem item : temp) {
                if(uItems.get(userId).containsKey(item.getItemId())) {
                    items.add(item);
                }
            }
        } else {
            items = temp;
        }


        return new RecommendationResult(getId(), items);
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException{
        return 0.0;
    }

    public boolean isRecommendOnlyItemsTheUserAlreadyViewed() {
        return recommendOnlyItemsTheUserAlreadyViewed;
    }

    public void setRecommendOnlyItemsTheUserAlreadyViewed(boolean recommendOnlyItemsTheUserAlreadyViewed) {
        this.recommendOnlyItemsTheUserAlreadyViewed = recommendOnlyItemsTheUserAlreadyViewed;
    }
}
