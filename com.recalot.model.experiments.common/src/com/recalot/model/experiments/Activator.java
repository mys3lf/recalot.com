package com.recalot.model.experiments;


import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.Initiator;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.model.experiments.access.ExperimentAccess;
import com.recalot.model.experiments.splitter.RandomDataSplitter;
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
            DataSplitterBuilder splitterBuilder = new DataSplitterBuilder(this, RandomDataSplitter.class.getName(), "random", "Random Data Splitter");
            splitterBuilder.setConfiguration(new ConfigurationItem("nbFolds", ConfigurationItem.ConfigurationItemType.Integer, "2", ConfigurationItem.ConfigurationItemRequirementType.Optional));

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