package com.recalot.model.rec.recommender.wallpaper.mostpopular;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Context;
import com.recalot.common.interfaces.model.rec.Recommender;
import flexjson.JSONDeserializer;

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
            if(interaction.getType().toLowerCase().equals("rating")) {
                Helper.incrementMapValue(count, interaction.getItemId(), Integer.parseInt(interaction.getValue()));
            }
        }

        for(Item item : getDataSet().getItems()) {
            String content = item.getValue("content");
            if(content != null && !content.isEmpty()){
                try{
                    HashMap itemContent = new JSONDeserializer<HashMap>().deserialize(content);
                    String rating = (String) itemContent.get("Rating");
                    String ratingCount = (String)itemContent.get("RatingCount");

                    if (rating != null && !rating.isEmpty() && ratingCount != null && !ratingCount.isEmpty()) {
                        Double r = Double.parseDouble(rating);
                        Integer c = Integer.parseInt(ratingCount.replace("(", "").replace(" vote)", "").replace(" votes)", ""));
                        Helper.incrementMapValue(count, item.getId(), (int)Math.round(r * c));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
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

        return new RecommendationResult(getId(), items);
    }


    @Override
    public Double predict(String userId, String itemId, Context context, Map<String, String> param) {
        return 0.0;
    }

}
