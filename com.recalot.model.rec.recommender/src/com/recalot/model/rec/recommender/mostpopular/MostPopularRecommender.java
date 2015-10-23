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

package com.recalot.model.rec.recommender.mostpopular;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;


import java.util.*;

/**
 * @author matthaeus.schmedding
 */
public class MostPopularRecommender extends Recommender {

    private RecommendationResult result;

    @Override
    public void train() throws BaseException {

        Map<String, Integer> count = new LinkedHashMap<>();

        for (Interaction interaction : getDataSet().getInteractions()) {
            Helper.incrementMapValue(count, interaction.getItemId());
        }

        List<RecommendedItem> recommendedItems = new ArrayList<>();

        count = Helper.sortByValueDescending(count);

        double sum = Helper.sum(count);

        for (String key : count.keySet()) {
            recommendedItems.add(new RecommendedItem(key, 1.0 * count.get(key) / sum));
        }

        List<RecommendedItem> remainingItems = new ArrayList<>();

        for(Item item : getDataSet().getItems()) {
            if(!recommendedItems.stream().anyMatch(i -> i.getItemId().equals(item.getId()))){
                remainingItems.add(new RecommendedItem(item.getId(), 0.0));
            }
        }

        recommendedItems.addAll(remainingItems);

        this.result = new RecommendationResult(getId(), recommendedItems);
    }


    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {
        List<RecommendedItem> items = null;
        if(result != null) {
            items = result.getItems();
        }

        if(items == null) items = new ArrayList<>();

        return new RecommendationResult(getId(), items);
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return 0.0;
    }
}
