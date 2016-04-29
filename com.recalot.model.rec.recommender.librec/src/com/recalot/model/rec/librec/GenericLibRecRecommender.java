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
package com.recalot.model.rec.librec;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.configuration.Configurations;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;
import librec.data.SparseMatrix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class GenericLibRecRecommender extends Recommender {

    private librec.intf.Recommender recommender;
    private DataDAO dataDao;
    private java.lang.String className;
    private ArrayList<ConfigurationItem> config = new ArrayList<>();

    @Override
    public void train() throws BaseException {

        this.dataDao = new DataDAO();
        SparseMatrix[] matrices;
        try {

            this.dataDao.setDataSet(getDataSet());

            this.recommender = (librec.intf.Recommender) Class.forName(className).newInstance();

            applyConfiguration();
            this.recommender.setDao(dataDao);

            // init the model
            this.recommender.initModel();

            // build the model
            this.recommender.buildModel();

            // post-processing after building a model, e.g., release intermediate memory to avoid memory leak
            this.recommender.postModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationItem[] getConfigurationItems(String className) {

        Map<String, ConfigurationItem> items = new HashMap<>();

        try {
            Class recommender = Class.forName(className);

            while (recommender != null) {


                if (recommender.isAnnotationPresent(Configuration.class)) {
                    com.recalot.common.configuration.Configuration config = (Configuration) recommender.getAnnotation(Configuration.class);

                    if (config != null && !items.containsKey(config.key())) {
                        items.put(config.key(), new ConfigurationItem(config.key(), config.type(), config.value(), config.requirement(), config.description(), new ArrayList<>(Arrays.asList(config.options()))));
                    }
                }

                if (recommender.isAnnotationPresent(Configurations.class)) {

                    com.recalot.common.configuration.Configuration annotations[] = ((Configurations) recommender.getAnnotation(Configurations.class)).value();

                    for (com.recalot.common.configuration.Configuration t : annotations) {
                        if (!items.containsKey(t.key())) {
                            items.put(t.key(), new ConfigurationItem(t.key(), t.type(), t.value(), t.requirement(), t.description(), new ArrayList<>(Arrays.asList(t.options()))));
                        }
                    }
                }

                recommender = recommender.getSuperclass();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }

        return items.values().toArray(new ConfigurationItem[items.size()]);
    }

    public void setConfigurationValue(ConfigurationItem item) {
        config.add(item);
    }

    public void applyConfiguration() {
        for (ConfigurationItem item : config) {
            try {
                switch (item.getType()) {
                    case Integer: {

                        Field f = getField(recommender.getClass(), item.getKey(), Integer.class);
                        if (f == null) f = getField(recommender.getClass(), item.getKey(), int.class);

                        if (f != null) {
                            f.set(recommender, Integer.parseInt(item.getValue()));
                        }

                        break;
                    }
                    case Double: {
                        Field f = getField(recommender.getClass(), item.getKey(), Double.class);
                        if (f == null) f = getField(recommender.getClass(), item.getKey(), double.class);

                        if (f != null) {
                            f.set(recommender, Double.parseDouble(item.getValue()));
                        }

                        break;
                    }
                    case Boolean: {
                        Field f = getField(recommender.getClass(), item.getKey(), Boolean.class);
                        if (f == null) f = getField(recommender.getClass(), item.getKey(), boolean.class);

                        if (f != null) {
                            f.set(recommender, Boolean.parseBoolean(item.getValue()));
                        }

                        break;
                    }
                    case Options:
                    case String: {
                        Field f = getField(recommender.getClass(), item.getKey(), String.class);

                        if (f != null) {
                            f.set(recommender, item.getValue());
                        }

                        break;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected static Field getField(Class c, String fieldName, Class cl) {
        Field f = null;
        Class tempClass = c;
        boolean isParent = false;
        while (tempClass != null && f == null) {
            try {
                isParent = false;
                f = tempClass.getDeclaredField(fieldName);

                if (!f.getType().equals(cl)) {
                    f = null;
                }

                if (f != null) {
                    f.setAccessible(true);

                    if (!f.isAccessible()) {
                        f = null;
                    }
                }
            } catch (NoSuchFieldException e) {
                tempClass = tempClass.getSuperclass();
                isParent = true;
            }

            if (f == null && tempClass != null && !isParent) {
                tempClass = tempClass.getSuperclass();
            }
        }

        return f;
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException {
        List<RecommendedItem> items = new ArrayList<>();
        try {
            List<String> rec = recommendItems(userId, true);

            for (String key : rec) {
                items.add(new RecommendedItem(key, 0.0));
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        return new RecommendationResult(getId(), items);
    }


    public List<String> recommendItems(String userId, boolean omitVisited) throws BaseException {


        // If there are no ratings for the user in the test set,
        // there is no point of making a recommendation.
        Interaction[] interactions = getDataSet().getInteractions(userId);
        // If we have no ratings...

         /*
        if (interactions == null || interactions.length == 0) {
            return Collections.emptyList();
        }
*/

        List<Interaction> interactionList = Arrays.asList(interactions);

        //put visited item into a map. It is faster this way
        Map<String, Boolean> visited = new HashMap<>();
        if (omitVisited) {
            for (Interaction interaction : interactionList) {
                if (!visited.containsKey(interaction.getItemId())) {
                    visited.put(interaction.getItemId(), true);
                }
            }
        }

        // Calculate rating predictions for all items we know
        Map<String, Double> predictions = new HashMap<>();
        double pred;
        // Go through all the items
        for (Item item : getDataSet().getItems()) {

            // check if we have seen the item already
            if (!visited.containsKey(item.getId())) {
                // make a prediction and remember it in case the recommender
                // could make one


                pred = getRank(userId, item.getId());
                if (!Double.isNaN(pred)) {
                    predictions.put(item.getId(), pred);
                }
            }
        }
        // Calculate rating predictions for all items we know
        predictions = Helper.sortByValueDescending(predictions);

        return new ArrayList<>(predictions.keySet());
    }

    private double getRank(String userId, String itemId) {
        try {
            int item = dataDao.getItemId(itemId);
            int user = dataDao.getUserId(userId);

            return recommender.ranking(user, item);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException {

        int item = dataDao.getItemId(itemId);
        int user = dataDao.getUserId(userId);

        try {
            return recommender.predict(user, item, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
