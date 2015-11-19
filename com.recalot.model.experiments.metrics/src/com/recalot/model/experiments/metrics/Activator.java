// Copyright (C) 2015 Matthäus Schmedding
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
import com.recalot.common.builder.Initiator;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.MetricInformation;
import com.recalot.model.experiments.metrics.list.*;
import com.recalot.model.experiments.metrics.rating.MAE;
import com.recalot.model.experiments.metrics.rating.MSE;
import com.recalot.model.experiments.metrics.rating.RMSE;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;


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
        try {

            MetricBuilder fscoreBuilder = new MetricBuilder(this, FScore.class.getName(), "fscore", "F-Score");
            fscoreBuilder.setConfiguration(new ConfigurationItem(Helper.Keys.TopN, ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            fscoreBuilder.setConfiguration(new ConfigurationItem("beta", ConfigurationItem.ConfigurationItemType.Double , "1.0", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            MetricBuilder precisionBuilder = new MetricBuilder(this, Precision.class.getName(), "precision", "Precision");
            precisionBuilder.setConfiguration(new ConfigurationItem(Helper.Keys.TopN, ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));
          //  precisionBuilder.setConfiguration(new ConfigurationItem("beta", ConfigurationItem.ConfigurationItemType.Double , "1.0", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            MetricBuilder recallBuilder = new MetricBuilder(this, Recall.class.getName(), "recall", "Recall");
            recallBuilder.setConfiguration(new ConfigurationItem(Helper.Keys.TopN, ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            MetricBuilder mrrBuilder = new MetricBuilder(this, MRR.class.getName(), "mrr", "MRR");
            mrrBuilder.setConfiguration(new ConfigurationItem(Helper.Keys.TopN, ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));
         //   recallBuilder.setConfiguration(new ConfigurationItem("beta", ConfigurationItem.ConfigurationItemType.Double , "1.0", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            MetricBuilder giniBuilder = new MetricBuilder(this, Gini.class.getName(), "gini", "Gini");
            giniBuilder.setConfiguration(new ConfigurationItem(Helper.Keys.TopN, ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));
        //    giniBuilder.setConfiguration(new ConfigurationItem("beta", ConfigurationItem.ConfigurationItemType.Double , "1.0", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            MetricBuilder coverageBuilder = new MetricBuilder(this, Coverage.class.getName(), "coverage", "Coverage");
            coverageBuilder.setConfiguration(new ConfigurationItem(Helper.Keys.TopN, ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));
       //     coverageBuilder.setConfiguration(new ConfigurationItem("beta", ConfigurationItem.ConfigurationItemType.Double , "1.0", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            MetricBuilder maeBuilder = new MetricBuilder(this, MAE.class.getName(), "mae", "Mean Absolute Error");
            MetricBuilder mseBuilder = new MetricBuilder(this, MSE.class.getName(), "mse", "Mean Squared Error");
            MetricBuilder rmseBuilder = new MetricBuilder(this, RMSE.class.getName(), "rmse", "Root Mean Squared Error");

            metrics.add(fscoreBuilder);
            metrics.add(precisionBuilder);
            metrics.add(recallBuilder);
            metrics.add(mrrBuilder);
            metrics.add(giniBuilder);
            metrics.add(coverageBuilder);

            metrics.add(maeBuilder);
            metrics.add(mseBuilder);
            metrics.add(rmseBuilder);


        } catch (Exception e) {
            e.printStackTrace();
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
}