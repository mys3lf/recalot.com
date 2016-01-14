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

package com.recalot.model.experiments;


import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.Initiator;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.experiments.access.ExperimentAccess;
import com.recalot.model.experiments.splitter.RandomNFoldDataSplitter;
import com.recalot.model.experiments.splitter.RandomPercentageDataSplitter;
import com.recalot.model.experiments.splitter.TimeBasedDataSplitter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, Initiator {


    private ExperimentAccess access;
    private ArrayList<DataSplitterBuilder> splitters;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        access = new ExperimentAccess(context);
        context.registerService(com.recalot.common.interfaces.model.experiment.ExperimentAccess.class.getName(), access, null);

        splitters = new ArrayList<>();

        try {
            DataSplitterBuilder splitterBuilder = new DataSplitterBuilder(this, RandomNFoldDataSplitter.class.getName(), "random-nfold", "Random N-Fold Data Splitter");
            splitterBuilder.setConfiguration(new ConfigurationItem("nbFolds", ConfigurationItem.ConfigurationItemType.Integer, "2", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            splitterBuilder.setConfiguration(new ConfigurationItem("seed", ConfigurationItem.ConfigurationItemType.Integer, "1", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            splitters.add(splitterBuilder);

        } catch (BaseException e) {

        }

        try {
            DataSplitterBuilder splitterBuilder = new DataSplitterBuilder(this, RandomPercentageDataSplitter.class.getName(), "random-percentage", "Random Percentage Data Splitter");
            splitterBuilder.setConfiguration(new ConfigurationItem("percentage", ConfigurationItem.ConfigurationItemType.Double, "0.7", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            splitterBuilder.setConfiguration(new ConfigurationItem("seed", ConfigurationItem.ConfigurationItemType.Integer, "1", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            splitters.add(splitterBuilder);

        } catch (BaseException e) {

        }

        try {
            DataSplitterBuilder splitterBuilder = new DataSplitterBuilder(this, TimeBasedDataSplitter.class.getName(), "timebased", "Time-based Data Splitter");
            splitterBuilder.setConfiguration(new ConfigurationItem("testPercentage", ConfigurationItem.ConfigurationItemType.Double, "0.3", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            splitters.add(splitterBuilder);

        } catch (BaseException e) {

        }

        for (DataSplitterBuilder splitter : splitters) {
            context.registerService(DataSplitterBuilder.class.getName(), splitter, null);
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
        if (access != null) {
            access.close();
            access = null;
        }

        if (splitters != null) {
            for (DataSplitterBuilder splitter : splitters) {
                splitter.close();
            }

            splitters = null;
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