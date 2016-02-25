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

package com.recalot.model.experiments.metrics;


import com.recalot.common.Helper;
import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.Initiator;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.configuration.Configurations;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.MetricInformation;
import com.recalot.model.experiments.metrics.list.*;
import com.recalot.model.experiments.metrics.rating.MAE;
import com.recalot.model.experiments.metrics.rating.MSE;
import com.recalot.model.experiments.metrics.rating.RMSE;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.*;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, Initiator {


    private List<MetricBuilder> metrics;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {


        metrics = new ArrayList<>();

        HashMap<String, String> map = new HashMap<>();

        map.put("fscore", FScore.class.getName());
        map.put("precision", Precision.class.getName());
        map.put("recall", Recall.class.getName());
        map.put("mrr", MRR.class.getName());
        map.put("gini", Gini.class.getName());
        map.put("coverage", Coverage.class.getName());
        map.put("mae", MAE.class.getName());
        map.put("mse", MSE.class.getName());
        map.put("rmse", RMSE.class.getName());


        for (String key : map.keySet()) {
            try {
                MetricBuilder builder = new MetricBuilder(this, map.get(key), key, "");

                ConfigurationItem[] items = getConfigurationItems(map.get(key));
                for (ConfigurationItem item : items) {
                    builder.setConfiguration(item);
                }

                metrics.add(builder);
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }

        for (MetricBuilder c : metrics) {
            context.registerService(MetricBuilder.class.getName(), c, null);
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
        if (metrics != null) {
            metrics = null;
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