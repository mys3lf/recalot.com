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

package com.recalot.model.rec.recommender.knn;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.*;

/**
 * Created by Matthaeus.schmedding on 02.06.2015.
 */

@Configuration(key = "minOverlap", type = ConfigurationItem.ConfigurationItemType.Integer, value = "3", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional)
@Configuration(key = "maxNeighbors", type = ConfigurationItem.ConfigurationItemType.Integer, value = "10", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional)
@Configuration(key = "minSimilarity", type = ConfigurationItem.ConfigurationItemType.Integer, value = "0.0", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional)
public class UserBasedCosineNearestNeighborsRecommender extends Recommender {
    private Integer minOverlap = 1;
    private Integer maxNeighbors = 10;
    private Double minSimilarity = 0.0;
    private HashMap<String, List<Integer>> userVectors;
    private HashMap<String, Integer> itemPosInVector;
    private LinkedHashMap<String, Map<String, Double>> similarityies;

    public UserBasedCosineNearestNeighborsRecommender() {
        this.setKey("cosine-user-knn");
    }

    @Override
    public void train() throws BaseException {
        this.userVectors = new HashMap<>();

        ArrayList<Integer> emptyVector = new ArrayList<>();
        itemPosInVector = new HashMap<>();

        int i = 0;
        for (Item item : getDataSet().getItems()) {
            emptyVector.add(0);
            itemPosInVector.put(item.getId(), i++);
        }

        for (User user : getDataSet().getUsers()) {
            userVectors.put(user.getId(), new ArrayList<>(emptyVector));
        }

        for (Interaction interaction : getDataSet().getInteractions()) {
            String userId = interaction.getUserId();
            String itemId = interaction.getItemId();

            if (userVectors.containsKey(userId) && itemPosInVector.containsKey(itemId)) {
                int pos = itemPosInVector.get(itemId);

                if (interaction.getValue() != null && !interaction.getValue().isEmpty()) {
                    int value = Integer.parseInt(interaction.getValue());
                    userVectors.get(userId).set(pos, value);
                } else {
                    userVectors.get(userId).set(pos, 1);
                }
            }
        }

        similarityies = new LinkedHashMap<>();

        for (User user1 : getDataSet().getUsers()) {
            Map<String, Double> similarity = new LinkedHashMap<>();

            for (User user2 : getDataSet().getUsers()) {
                if (!user1.getId().equals(user2.getId())) {
                    if (userVectors.containsKey(user1.getId()) && userVectors.containsKey(user2.getId())) {

                        List<Integer> v1 = userVectors.get(user1.getId());
                        List<Integer> v2 = userVectors.get(user2.getId());

                        if (Helper.getOverlapping(v1, v2) > minOverlap) {
                            similarity.put(user2.getId(), Helper.computeCosSimilarity(v1, v2));
                        }
                    }
                }
            }

            similarityies.put(user1.getId(), Helper.sortByValueDescending(similarity));
        }
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return 0.0;
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {
        List<RecommendedItem> result = new ArrayList<>();

        double maxValue = 0;

        if (similarityies != null && similarityies.containsKey(userId)) {
            Map<String, Double> sim = similarityies.get(userId);

            int i = 0;

            Map<String, Double> sumSim = new LinkedHashMap<>();
            Map<String, Double> ratingsItems = new LinkedHashMap<>();
            for (String key : sim.keySet()) {

                double simUser = sim.get(key);

                try {
                    Interaction[] interactions = getDataSet().getInteractions(key);
                    if (interactions != null) {
                        for (Interaction interaction : interactions) {
                            double value = Double.parseDouble(interaction.getValue());

                            if (value > maxValue) maxValue = value;

                            Helper.incrementMapValue(ratingsItems, interaction.getItemId(), value * simUser);
                            Helper.incrementMapValue(sumSim, interaction.getItemId(), simUser);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (i++ >= maxNeighbors) {
                    break;
                }
            }

            ratingsItems = Helper.sortByValueDescending(ratingsItems);
            int count = Integer.MAX_VALUE;
            if (param != null && param.containsKey("count")) {
                count = Integer.parseInt(param.get("count"));
            }

            i = 0;
            for (String itemId : ratingsItems.keySet()) {
                result.add(new RecommendedItem(itemId, ratingsItems.get(itemId) / sumSim.get(itemId) / maxValue));
                if (i++ >= count) break;
            }
        }

        return new RecommendationResult(getId(), result);
    }

    public Integer getMinOverlap() {
        return minOverlap;
    }

    public void setMinOverlap(Integer minOverlap) {
        this.minOverlap = minOverlap;
    }

    public Integer getMaxNeighbors() {
        return maxNeighbors;
    }

    public void setMaxNeighbors(Integer maxNeighbors) {
        this.maxNeighbors = maxNeighbors;
    }

    public Double getMinSimilarity() {
        return minSimilarity;
    }

    public void setMinSimilarity(Double minSimilarity) {
        this.minSimilarity = minSimilarity;
    }
}
