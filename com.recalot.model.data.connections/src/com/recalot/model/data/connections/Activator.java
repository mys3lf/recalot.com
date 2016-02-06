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

package com.recalot.model.data.connections;

import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.builder.Initiator;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.data.connections.downloader.douban.DoubanDataSource;
import com.recalot.model.data.connections.downloader.filmtrust.FilmTrustDataSource;
import com.recalot.model.data.connections.downloader.flixster.FlixsterDataSource;
import com.recalot.model.data.connections.downloader.movielens.MovieLensDataSource;
import com.recalot.model.data.connections.mysql.MySQLDataSource;
import com.recalot.model.data.connections.reddit.RedditDataSource;
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
            ConfigurationItem config = new ConfigurationItem("source", ConfigurationItem.ConfigurationItemType.Options, "ml-100k", ConfigurationItem.ConfigurationItemRequirementType.Required);
            List<String> options = new ArrayList<>();
            options.add("ml-100k");
            options.add("ml-1m");
            options.add("ml-10m");
            options.add("ml-20m");
            options.add("ml-latest-small");
            options.add("ml-latest");
            config.setOptions(options);
            builder.setConfiguration(config);
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

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, FlixsterDataSource.class.getName(), "flixster", "");
         //   connections.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, FilmTrustDataSource.class.getName(), "filmtrust", "");
            connections.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, DoubanDataSource.class.getName(), "douban", "");
            connections.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, RedditDataSource.class.getName(), "reddit", "");
            builder.setConfiguration(new ConfigurationItem("dir", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
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