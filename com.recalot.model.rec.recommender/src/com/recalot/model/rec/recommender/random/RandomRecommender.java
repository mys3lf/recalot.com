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

package com.recalot.model.rec.recommender.random;

import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author matthaeus.schmedding
 */
public class RandomRecommender extends Recommender {

    protected   List<RecommendedItem> recommendedItems;

    @Override
    public void train() throws BaseException {
        recommendedItems = new ArrayList<>();

        for (Item item : getDataSet().getItems()) {
            recommendedItems.add(new RecommendedItem(item, 0));
        }
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException{

        List<RecommendedItem> items = new ArrayList<>(recommendedItems);

        Collections.shuffle(items);

        return new RecommendationResult(getId(), items);
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException{
        return 0.0;
    }
}
