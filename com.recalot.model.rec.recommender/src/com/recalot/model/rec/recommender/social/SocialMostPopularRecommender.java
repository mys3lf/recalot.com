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
 * Created by Matthaeus.schmedding on 02.06.2015.
 */
@Configuration(key = "relationshipType", options = {"Group", "Friend", "Trust"}, type = ConfigurationItem.ConfigurationItemType.Options)
public class SocialMostPopularRecommender extends Recommender {

    private String relationshipType;
    private Map<String, Integer> mostPopular;

    public SocialMostPopularRecommender(){
        this.setKey("social-mp");
    }
    @Override
    public void train() throws BaseException {
        //most popular fall back for users without relationships
        mostPopular = new LinkedHashMap<>();

        for (Interaction interaction : getDataSet().getInteractions()) {
            Helper.incrementMapValue(mostPopular, interaction.getItemId());
        }

        mostPopular = Helper.sortByValueDescending(mostPopular);
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return 0.0;
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {
        Map<String, Integer> count = new LinkedHashMap<>();
        try {
            Relation[] relations = getDataSet().getRelationsFor(userId);
            for(Relation r : relations) {
                if(r.getType().toLowerCase().equals(relationshipType.toLowerCase())) {
                    switch (relationshipType.toLowerCase()) {
                        case "group":
                            //get all user of the group expect own one
                            Relation[] groupRelations = getDataSet().getRelationsFor(r.getToId());

                            if(groupRelations != null) {
                                for(Relation gr : groupRelations) {
                                    if(!gr.getToId().equals(userId)) {

                                        //get interaction of the group user and count occurrence
                                        Interaction[] interactions = getDataSet().getInteractions(gr.getToId());
                                        if(interactions != null) {
                                            for(Interaction i : interactions) {
                                                Helper.incrementMapValue(count, i.getItemId());
                                            }
                                        }
                                    }
                                }
                            }

                            break;

                        case "friend":
                        case "trust":

                            //get interaction of the friend and count occurrence
                            Interaction[] interactions = getDataSet().getInteractions(r.getToId());
                            if(interactions != null) {
                                for(Interaction i : interactions) {
                                    Helper.incrementMapValue(count, i.getItemId());
                                }
                            }

                            break;
                    }
                }
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        count = Helper.sortByValueDescending(count);

        ArrayList<String> result = new ArrayList<>(count.keySet());

        HashMap<String, Boolean> omitItems = new HashMap<>();
        try {
            Interaction[] userInteractions = getDataSet().getInteractions(userId);
            //do not already viewed items
            for(Interaction i : userInteractions) {
                omitItems.put(i.getItemId(), true);
            }
            //do not add item twice
            for(String itemId : result) {
                omitItems.put(itemId, true);
            }
        } catch (BaseException e) {
            e.printStackTrace();
        }

        result.addAll(mostPopular.keySet());

        List<RecommendedItem> recommendedItems = new ArrayList<>();

        for (String itemId: result) {
            if(!omitItems.containsKey(itemId)) {
                recommendedItems.add(new RecommendedItem(itemId, 0.0));
            }
        }

        return new RecommendationResult(getId(), recommendedItems);
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}
