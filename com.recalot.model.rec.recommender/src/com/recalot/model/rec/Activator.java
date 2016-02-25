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


package com.recalot.model.rec;

import com.recalot.common.builder.Initiator;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.configuration.Configurations;
import com.recalot.common.context.Context;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.model.rec.context.LastVisitedContext;
import com.recalot.model.rec.context.ParamsContext;
import com.recalot.model.rec.context.UserInputContext;
import com.recalot.model.rec.recommender.mostpopular.MostPopularRecommender;
import com.recalot.model.rec.recommender.social.SocialMostPopularRecommender;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.net.URLClassLoader;
import java.util.*;


/**
 * Register the recommender builder services. This class is called automatically when the bundle is activated.
 *
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, Initiator {

    private List<RecommenderBuilder> recommenders;
    private List<Context> contexts;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        registerRecommenders(context);
        registerContext(context);
    }

    private void registerContext(BundleContext context) {
        contexts = new ArrayList<>();

        contexts.add(new LastVisitedContext());
        contexts.add(new ParamsContext());
        contexts.add(new UserInputContext());

        for (Context c : contexts) {
            context.registerService(Context.class.getName(), c, null);
        }
    }

    private void registerRecommenders(BundleContext context) {
        recommenders = new ArrayList<>();


        HashMap<String, String> map = new HashMap<>();

        map.put("mp", MostPopularRecommender.class.getName());
        map.put("social-mp", SocialMostPopularRecommender.class.getName());
        map.put("wallpaper-survey", com.recalot.model.rec.recommender.wallpaper.mostpopular.MostPopularRecommender.class.getName());
        map.put("cosine-user-knn",  com.recalot.model.rec.recommender.knn.UserBasedCosineNearestNeighborsRecommender.class.getName());
        map.put("slopeone", com.recalot.model.rec.recommender.slopeone.SlopeOneRecommender.class.getName());
        map.put("bprmf",com.recalot.model.rec.recommender.bprmf.BPRMFRecommender.class.getName());

        map.put("random",com.recalot.model.rec.recommender.random.RandomRecommender.class.getName());
        map.put("item-average-rating",com.recalot.model.rec.recommender.experiments.GlobalItemAverageRatingRecommender.class.getName());
        map.put("user-average-rating",com.recalot.model.rec.recommender.experiments.GlobalUserAverageRatingRecommender.class.getName());
        map.put("global-average-rating", com.recalot.model.rec.recommender.experiments.GlobalAverageRatingRecommender.class.getName());

        map.put("reddit-bprmf",com.recalot.model.rec.recommender.reddit.MostPopularRecommender.class.getName());
        map.put("reddit-context-bprmf",com.recalot.model.rec.recommender.reddit.ContextAwareBPRRecommender.class.getName());
        map.put("reddit-context-mp", com.recalot.model.rec.recommender.reddit.ContextAwareMostPopular.class.getName());
        map.put("reddit-context-random",  com.recalot.model.rec.recommender.reddit.ContextAwareRandomRecommender.class.getName());
        map.put("reddit-random", com.recalot.model.rec.recommender.reddit.RandomRecommender.class.getName());
        map.put("reddit-mp", com.recalot.model.rec.recommender.reddit.MostPopularRecommender.class.getName());

        for(String key: map.keySet()) {
            try {
                RecommenderBuilder builder = new RecommenderBuilder(this, map.get(key), key, "");

                ConfigurationItem[] items = getConfigurationItems(map.get(key));
                for (ConfigurationItem item : items) {
                    builder.setConfiguration(item);
                }

                recommenders.add(builder);
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }

        for (RecommenderBuilder c : recommenders) {
            context.registerService(RecommenderBuilder.class.getName(), c, null);
        }
    }



    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    public void stop(BundleContext context) throws Exception {
        if (recommenders != null) {
            for (RecommenderBuilder c : recommenders) {
                c.close();
            }

            recommenders = null;
        }
        if (contexts != null) {
            for (Context c : contexts) {
                c.close();
            }

            contexts = null;
        }
    }

    @Override
    public Object createInstance(String className) {
        try {
            Class c = Class.forName(className);
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}