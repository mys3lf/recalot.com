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

package com.recalot.model.rec.recommender.reddit;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.rec.recommender.mostpopular.MostPopularRecommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class ContextAwareMostPopular extends MostPopularRecommender {

    private String contextType;
    private HashMap<String, HashMap<String, Boolean>> coocurence;

    @Override
    public void train() throws BaseException {
        super.train();

        coocurence = new HashMap<>();

        String last = null;
        for (Interaction interaction : getDataSet().getInteractions()) {

            String current = interaction.getItemId().toLowerCase();
            if (last != null) {
                if (!coocurence.containsKey(last)) {
                    coocurence.put(last, new HashMap<>());
                }

                if (!coocurence.get(last).containsKey(current)) {
                    coocurence.get(last).put(current, true);
                }
            }

            last = current;
        }
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException {

        UserContext userInputContext = (UserContext) context.getInstance("user-input");
        UserContext userLastItemContext = (UserContext) context.getInstance("user-last-visited");

        List<RecommendedItem> items = new ArrayList<>();

        if (result != null) {
            items = result.getItems();
        }

        if (items == null) items = new ArrayList<>();

        switch (contextType) {
            case "letter":
                items = ApplyLetterContext(items, userId, userInputContext);
                break;
            case "last":
                items = ApplyLastWordsContext(items, userId, userLastItemContext);
                break;
            case "both":
                items = ApplyBothContext(items, userId, userInputContext, userLastItemContext);
                break;
        }


        items = Helper.applySubList(items, param, 10);

        return new RecommendationResult(getId(), items);
    }

    private List<RecommendedItem> ApplyBothContext(List<RecommendedItem> items, String userId, UserContext context, UserContext lastItemContext) throws BaseException {
        Object c = context.getContext(getDataSourceId(), userId);
        Object c2 = lastItemContext.getContext(getDataSourceId(), userId);


        if (c != null && c instanceof String && c2 != null && c2 instanceof ArrayList) {
            String letter = (String) c;

            List<RecommendedItem> resultBothMatches = new ArrayList<>();
            List<RecommendedItem> result1Match = new ArrayList<>();
            List<RecommendedItem> resultNoMatch = new ArrayList<>();

            ArrayList lastItems = (ArrayList) c2;

            String lastItemId = ((String) lastItems.get(lastItems.size() - 1)).toLowerCase();


            for (RecommendedItem item : items) {
                //id equals the word
                String itemId = item.getItemId().toLowerCase();

                int match = 0;
                if (item.getItemId().toLowerCase().startsWith(letter)) {
                    match++;
                }

                if (coocurence.get(lastItemId).containsKey(itemId)) {
                    match++;
                }

                switch (match) {
                    case 2:
                        resultBothMatches.add(item);
                        break;
                    case 1:
                        result1Match.add(item);
                        break;
                    case 0:
                        resultNoMatch.add(item);
                        break;
                }
            }

            List<RecommendedItem> result = new ArrayList<>();

            result.addAll(resultBothMatches);
            result.addAll(result1Match);
            result.addAll(resultNoMatch);

            return result;
        }

        return items;
    }

    private List<RecommendedItem> ApplyLastWordsContext(List<RecommendedItem> items, String userId, UserContext context) throws BaseException {
        Object c = context.getContext(getDataSourceId(), userId);
        if (c != null && c instanceof ArrayList) {
            ArrayList lastItems = (ArrayList) c;
            List<RecommendedItem> result = new ArrayList<>();

            String lastItemId = ((String) lastItems.get(lastItems.size() - 1)).toLowerCase();

            if (coocurence.containsKey(lastItemId)) {
                for (RecommendedItem item : items) {
                    //id equals the word
                    String itemId = item.getItemId().toLowerCase();

                    if (coocurence.get(lastItemId).containsKey(itemId)) {
                        result.add(item);
                    }
                }
            }
            return result;
        }

        return items;
    }

    private List<RecommendedItem> ApplyLetterContext(List<RecommendedItem> items, String userId, UserContext context) throws BaseException {
        Object c = context.getContext(getDataSourceId(), userId);
        if (c != null && c instanceof String) {
            String letter = (String) c;
            List<RecommendedItem> result = new ArrayList<>();

            for (RecommendedItem item : items) {
                //id equals the word
                if (item.getItemId().toLowerCase().startsWith(letter)) {
                    result.add(item);
                }
            }

            return result;
        }

        return items;
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException {
        return super.predict(userId, itemId, context, param);
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }
}
