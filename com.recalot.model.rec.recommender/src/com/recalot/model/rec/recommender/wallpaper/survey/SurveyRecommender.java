package com.recalot.model.rec.recommender.wallpaper.survey;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;
import flexjson.JSONDeserializer;

import java.util.*;

/**
 * @author matthaeus.schmedding
 */
public class SurveyRecommender extends Recommender {

    private HashMap<String, LinkedHashMap<String, Integer>> categoryMP;


    @Override
    public void train() throws BaseException {
        categoryMP = new HashMap<>();

        Map<String, Integer> temp = new LinkedHashMap<>();

        for (Interaction interaction : getDataSet().getInteractions()) {
            if (interaction.getType().toLowerCase().equals("rating")) {
                Helper.incrementMapValue(temp, interaction.getItemId(), Integer.parseInt(interaction.getValue()));
            }
        }

        for (Item item : getDataSet().getItems()) {
            String content = item.getValue("content");
            if (content != null && !content.isEmpty()) {
                try {
                    HashMap itemContent = new JSONDeserializer<HashMap>().deserialize(content);
                    String rating = (String) itemContent.get("Rating");
                    List<String> categories = (ArrayList<String>) itemContent.get("Categories");

                    String ratingCount = (String) itemContent.get("RatingCount");

                    if (rating != null && !rating.isEmpty() && ratingCount != null && !ratingCount.isEmpty()) {
                        Double r = Double.parseDouble(rating);
                        Integer c = Integer.parseInt(ratingCount.replace("(", "").replace(" vote)", "").replace(" votes)", ""));

                        if (categories != null && categories.size() > 0) {
                            String cat = categories.get(0);

                            LinkedHashMap<String, Integer> count = categoryMP.get(cat);
                            if (count == null) {
                                count = new LinkedHashMap<>();
                            }

                            Helper.incrementMapValue(count, item.getId(), (int) Math.round(r * c));

                            if (temp.containsKey(item.getId())) {
                                Helper.incrementMapValue(count, item.getId(), temp.get(item.getId()));
                            }

                            categoryMP.put(cat, count);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (String key : categoryMP.keySet()) {
            Map<String, Integer> count = categoryMP.get(key);

            count = Helper.sortByValueDescending(count);
            categoryMP.put(key, (LinkedHashMap<String, Integer>) count);
        }
    }


    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {

        Random r = new Random();

        Interaction[] interactions = null;
        try {
            interactions = getDataSet().getInteractions(userId);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        List<String> result = new ArrayList<>();
        if (interactions != null) {
            List<Interaction> inter = Arrays.asList(interactions);

            for (String key : categoryMP.keySet()) {
                Map<String, Integer> catMP = categoryMP.get(key);
                List<String> items = new ArrayList<>(catMP.keySet());
                boolean found = false;
                int max = 10;
                int n = 0;

                while (!found && n < max) {
                    n++;

                    int next = r.nextInt(100) % 10;

                    if (next < items.size()) {
                        String itemId = items.get(next);
                        if (!inter.stream().anyMatch(i -> i.getItemId().equals(itemId))) {
                            found = true;
                            result.add(itemId);
                        }
                    }
                }
            }
        }

        List<RecommendedItem> items = new ArrayList<>();

        for (String key : result) {
            try {
                items.add(new RecommendedItem(getDataSet().getItem(key), 0.0));
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }

        int max = items.size();

        if (param.containsKey("count")) {
            max = Integer.parseInt(param.get("count"));
        }

        while (items.size() > max) {
            int next = r.nextInt(items.size() * 10) % items.size();

            items.remove(next);
        }

        return new RecommendationResult(getId(), items);
    }


    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return 0.0;
    }

}
