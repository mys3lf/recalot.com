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

package com.recalot.model.rec.recommender.social;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configurable;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.*;

/**
 *  A simple most popular social recommender.
 *  Recommends the most popular items of the users relations (could be friends or common group members)
 *
 * Created by Matthaeus.schmedding, Daniel Gafert on 02.06.2015.
 */
@Configuration(key = "DISTANCE", type = ConfigurationItem.ConfigurationItemType.Integer, requirement = ConfigurationItem.ConfigurationItemRequirementType.Required)
@Configuration(key = "SQUAREMAX", type = ConfigurationItem.ConfigurationItemType.Double, requirement = ConfigurationItem.ConfigurationItemRequirementType.Required)
@Configuration(key = "MAXCOUNTER", type = ConfigurationItem.ConfigurationItemType.Integer, requirement = ConfigurationItem.ConfigurationItemRequirementType.Required)
public class SocialMostPopularRecommender extends Recommender {
    //protected RecommendationResult result;

    private List<String> mostPopular = new LinkedList<>();
    // UserId  / SET FRIENDSITEMS
    private Map<String, Set<String>> userIdFriendsItems = new HashMap<>();
    private int DISTANCE = 6;
    private double SQUAREMAX = 4;
    private int MAXCOUNTER = 3;

    private int counterMethodCalled= 0;

    static class CountValue {
        private int count;
        private double value;

        public CountValue(double value, int count) {
            this.value = value;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public double getValue() {
            return value;
        }
    }

    public SocialMostPopularRecommender(){
        this.setKey("social-mp");
    }

    @Override
    public void train() throws BaseException {
        // Begin MostPopular
        // ItemID, Usercount
        Map<String, Integer> count = new LinkedHashMap<>();

        for (Interaction interaction : getDataSet().getInteractions()) {
            Helper.incrementMapValue(count, interaction.getItemId());
        }

        // Sort by count descending
        count = Helper.sortByValueDescending(count);

        for (String key : count.keySet()) {
            mostPopular.add(key);
        }
        // End MostPopular

        // Begin SocialMostPopular
        for(User userAkt : getDataSet().getUsers()) {
            String userId =userAkt.getId();
            Set<String> usersAlreadyTaken = new HashSet<>();
            counterMethodCalled = 0;

            // Add active User to Set
            usersAlreadyTaken.add(userId);
            // ItemId, CountValue
            Map<String, CountValue> itemIdCountValue = new HashMap<>();
            try {
                Relation[] relations = getDataSet().getRelationsFor(userId);
                for(Relation relationAct : relations) {
                    getItemsForRelation(relationAct, 1, itemIdCountValue, usersAlreadyTaken);
                }
            } catch (BaseException e) {
                e.printStackTrace();
            }

            // ItemId, Square Rating Friends
            Map<String, Double> itemIdSquareRatingFriends = new LinkedHashMap<>();
            for(String aktItemId : itemIdCountValue.keySet()) {
                CountValue actCountValue = itemIdCountValue.get(aktItemId);
                double square = actCountValue.getValue() / actCountValue.getCount();
                //System.out.println(square);
                if(square >= SQUAREMAX) {
                    //System.out.println(square +"= " + aktItemId + " = " + actCountValue);
                    itemIdSquareRatingFriends.put(aktItemId, square);
                }
            }
            // Sort Square-Rating descending
            itemIdSquareRatingFriends = Helper.sortByValueDescending(itemIdSquareRatingFriends);
            Set<String> itemIds = new HashSet<>();
            for(String aktItem : itemIdSquareRatingFriends.keySet()) {
                itemIds.add(aktItem);
            }
            if(itemIds.size() > 0) {
                userIdFriendsItems.put(userId, itemIds);
            }
        }
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return 0.0;
    }


    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {

        HashMap<String, Boolean> omitItems = new HashMap<>();
        try {
            Interaction[] userInteractions = getDataSet().getInteractions(userId);
            //do not already viewed items
            for(Interaction i : userInteractions) {
                omitItems.put(i.getItemId(), true);
            }
        } catch (BaseException e) {
            e.printStackTrace();
        }

        int counter = 0;
        // Add calculated Items to result
        List<RecommendedItem> recommendedItems = new ArrayList<>();
        if(userIdFriendsItems.get(userId) != null) {
            Set<String> items = userIdFriendsItems.get(userId);
            for(String aktItemId : items) {
                if(counter < MAXCOUNTER) {
                    if(!omitItems.containsKey(aktItemId)) {
                        recommendedItems.add(new RecommendedItem(aktItemId, 0.0));
                        omitItems.put(aktItemId, true);
                        counter++;
                    }
                } else {
                    break;
                }
            }
        }

        // Add most popular Items
        for(String aktItemId : mostPopular) {
            if(!omitItems.containsKey(aktItemId)) {
                recommendedItems.add(new RecommendedItem(aktItemId, 0.0));
            }
        }

        return new RecommendationResult(getId(), recommendedItems);
    }

    private void getItemsForRelation(Relation relationAct, int counter, Map<String, CountValue> countTemp, Set<String> usersAlreadyTaken) {
        counterMethodCalled++;
        String userAct = relationAct.getToId();
        if (counter <= DISTANCE) {
            if (!usersAlreadyTaken.contains(userAct)) {
                // User not processed
                usersAlreadyTaken.add(userAct);

                //get interaction of the friend and count occurrence
                Interaction[] interactions = new Interaction[0];
                try {
                    interactions = getDataSet().getInteractions(userAct);

                    if (interactions != null) {
                        for (Interaction i : interactions) {
                            String itemId = i.getItemId();
                            Double actRating = Double.parseDouble(i.getValue());
                            if(countTemp.get(itemId) == null) {
                                CountValue actCountValue = new CountValue(actRating, 1);
                                countTemp.put(itemId, actCountValue);
                            } else {
                                CountValue actCountValue = countTemp.get(itemId);
                                CountValue actCountValueHigh = new CountValue(actCountValue.getValue() + actRating, actCountValue.getCount()+1);
                                countTemp.put(itemId, actCountValueHigh);
                            }
                        }
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }

            }
            counter++;
            if (counter <= DISTANCE) {
                Relation[] relations = new Relation[0];
                try {
                    relations = getDataSet().getRelationsFor(userAct);

                    for (Relation relationAct2 : relations) {
                        getItemsForRelation(relationAct2, counter, countTemp, usersAlreadyTaken);
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getMAXCOUNTER() {
        return MAXCOUNTER;
    }

    public void setMAXCOUNTER(int MAXCOUNTER) {
        this.MAXCOUNTER = MAXCOUNTER;
    }

    public double getSQUAREMAX() {
        return SQUAREMAX;
    }

    public void setSQUAREMAX(double SQUAREMAX) {
        this.SQUAREMAX = SQUAREMAX;
    }

    public int getDISTANCE() {
        return DISTANCE;
    }

    public void setDISTANCE(int DISTANCE) {
        this.DISTANCE = DISTANCE;
    }
}
