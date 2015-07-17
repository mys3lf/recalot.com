package com.recalot.model.data.connections;


import com.recalot.common.Helper;
import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.builder.Initiator;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.data.connections.movielens.MovieLensDataSource;
import com.recalot.model.data.connections.mysql.MySQLDataSource;
import com.recalot.model.data.connections.text.TextDataSource;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, Initiator {


    private List<DataSourceBuilder> connections;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        connections = new ArrayList<>();

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, MovieLensDataSource.class.getName(), "ml", "");
            builder.setConfiguration(new ConfigurationItem(Helper.Keys.Dir, ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            connections.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, TextDataSource.class.getName(), "text", "");
            builder.setConfiguration(new ConfigurationItem("file", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            connections.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, MySQLDataSource.class.getName(), "mysql", "");
            builder.setConfiguration(new ConfigurationItem("sql-server", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            builder.setConfiguration(new ConfigurationItem("sql-username", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            builder.setConfiguration(new ConfigurationItem("sql-password", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            builder.setConfiguration(new ConfigurationItem("sql-database", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            connections.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        for (DataSourceBuilder c : connections) {
            context.registerService(DataSourceBuilder.class.getName(), c, null);
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
        if (connections != null) {
            for (DataSourceBuilder c : connections) {
                c.close();
            }
            connections = null;
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