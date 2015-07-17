package com.recalot.model.rec.recommender.mostpopular;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Context;
import com.recalot.common.interfaces.model.rec.Recommender;


import java.util.*;

/**
 * @author matthaeus.schmedding
 */
public class MostPopularRecommender extends Recommender {

    private RecommendationResult result;
    private int topN = 10;


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
    public RecommendationResult recommend(String userId, Context context, Map<String, String> param) {
        List<RecommendedItem> items = null;
        if(result != null) {
            items = result.getItems();
        }

        if(items == null) items = new ArrayList<>();


        items =  Helper.applySubList(items, param, topN);
        return new RecommendationResult(getId(), items);
    }

    @Override
    public Double predict(String userId, String itemId, Context context, Map<String, String> param) {
        return 0.0;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(Integer topN) {
        this.topN = topN;
    }
}
