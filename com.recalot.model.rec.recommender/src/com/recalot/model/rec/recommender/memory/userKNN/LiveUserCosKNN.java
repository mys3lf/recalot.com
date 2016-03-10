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
package com.recalot.model.rec.recommender.memory.userKNN;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.*;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
@Configuration(key = "maxUsers", type = ConfigurationItem.ConfigurationItemType.Integer, value = "1000", description = "The maximum amount of examined users.")
public class LiveUserCosKNN extends Recommender {

    private int maxUsers;

    @Override
    public void train() throws BaseException {

    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException {
        ArrayList<Integer> emptyVector = new ArrayList<>();
        HashMap<Integer, Integer> itemPosInVector = new HashMap<>();
        HashMap<Integer, Integer> vectorPosPosForItem = new HashMap<>();
        HashMap<Integer, Boolean> consumed = new HashMap<>();

        int i = 0;
        for (Item item : getDataSet().getItems()) {
            emptyVector.add(0);
            vectorPosPosForItem.put(i, InnerIds.getId(item.getId()));
            itemPosInVector.put(InnerIds.getId(item.getId()), i++);
        }
        HashMap<Integer, ArrayList<Integer>> userVectors = new HashMap<>();

        //choose 1000 random user
        if (maxUsers < getDataSet().getUsersCount()) {
            Random r = new Random();

            while (userVectors.size() < maxUsers) {
                int next = r.nextInt(getDataSet().getUsersCount() * 10) % getDataSet().getUsersCount();
                User user = getDataSet().getUsers()[next];
                if (!userVectors.containsKey(InnerIds.getId(user.getId()))) {
                    userVectors.put(InnerIds.getId(user.getId()), new ArrayList<>(emptyVector));
                }
            }

            //insert current user
            userVectors.put(InnerIds.getId(userId), new ArrayList<>(emptyVector));
        } else { // take all
            for (User user : getDataSet().getUsers()) {
                userVectors.put(InnerIds.getId(user.getId()), new ArrayList<>(emptyVector));
            }
        }

        for (Interaction interaction : getDataSet().getInteractions()) {
            int iUserId = InnerIds.getId(interaction.getUserId());
            int iItemId = InnerIds.getId(interaction.getItemId());

            if (userVectors.containsKey(iUserId) && itemPosInVector.containsKey(iItemId)) {
                int pos = itemPosInVector.get(iItemId);

                if (interaction.getValue() != null && !interaction.getValue().isEmpty()) {
                    int value = Integer.parseInt(interaction.getValue());
                    userVectors.get(iUserId).set(pos, value);
                } else {
                    userVectors.get(iUserId).set(pos, 1);
                }
            }
        }

        Map<Integer, Double> similarity = new LinkedHashMap<>();
        int innerUserId = InnerIds.getId(userId);
        for (Integer user2 : userVectors.keySet()) {
            if (innerUserId != user2) {

                if (userVectors.containsKey(innerUserId) && userVectors.containsKey(user2)) {

                    List<Integer> v1 = userVectors.get(innerUserId);
                    List<Integer> v2 = userVectors.get(user2);

                    similarity.put(user2, Helper.computeCosSimilarity(v1, v2));
                }
            }
        }

        Map<Integer, Double> itemSim = new LinkedHashMap<>();
        Map<Integer, Double> simSum = new LinkedHashMap<>();

        for (Integer userId2 : similarity.keySet()) {
            if (userVectors.containsKey(userId2)) {
                for(Integer itemPos = 0; itemPos < userVectors.get(userId2).size(); itemPos++) {
                    Helper.incrementMapValue(simSum, itemPos, similarity.get(userId2));
                    Helper.incrementMapValue(itemSim, itemPos, similarity.get(userId2) * userVectors.get(userId2).get(itemPos));
                }
            }
        }

        for(Integer itemId : simSum.keySet()) {
            if(simSum.get(itemId) > 0 && itemSim.containsKey(itemId)) {
                itemSim.put(itemId, itemSim.get(itemId) / simSum.get(itemId));
            }
        }

        itemSim = Helper.sortByValueDescending(itemSim);

        List<RecommendedItem> recommendedItems = new ArrayList<>();

        for(Integer item : itemSim.keySet()) {
            //just take items that were not consumed by user already
            if(userVectors.get(InnerIds.getId(userId)).get(item) == 0) {
                int innerId =  vectorPosPosForItem.get(item);
                String itemId = InnerIds.getId(innerId);

                if(getDataSet().hasItem(itemId)) {
                    recommendedItems.add(new RecommendedItem(getDataSet().getItem(itemId), itemSim.get(item)));
                }
            }
        }

        return new RecommendationResult(getId(), Helper.applySubList(recommendedItems, param, 50));
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException {
        return 0.0;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }
}