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

package com.recalot.demos.wallpaper;


import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.builder.Initiator;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.demos.wallpaper.controller.DataAccessController;
import com.recalot.demos.wallpaper.mysql.MySQLDataSource;
import com.recalot.demos.wallpaper.view.Servlet;
import com.recalot.views.common.AbstractWebActivator;
import com.recalot.views.common.GenericControllerHandler;
import com.recalot.views.common.WebService;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.http.HttpService;

import java.util.Dictionary;
import java.util.Hashtable;


/**
 * @author Matthaeus.schmedding
 */
public class Activator extends AbstractWebActivator implements Initiator {
    private DataAccessController controller;
    private ServiceTracker staticHttpTracker;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {

        controller = new DataAccessController(context);
        context.registerService(DataAccessController.class.getName(), controller, null);

        handler = new GenericControllerHandler<DataAccessController>(context, DataAccessController.class.getName());

        String pid = "com.recalot.demos.wallpaper";

        Dictionary config = new Hashtable();
        config.put(pid + ".path", new String("/sample/wallpaper-controller"));

        service = new WebService(pid, context,  new Servlet(handler), config);
        context.registerService(ManagedService.class.getName(), service, config);

        staticHttpTracker = new ServiceTracker(context, HttpService.class.getName(), null, "/web", "sample/wallpaper");
        staticHttpTracker.open();

        service.initialize();


        try {
            DataSourceBuilder builder = new DataSourceBuilder(this, MySQLDataSource.class.getName(), "wallpaper-mysql", "");
            builder.setConfiguration(new ConfigurationItem("sql-server", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            builder.setConfiguration(new ConfigurationItem("sql-username", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            builder.setConfiguration(new ConfigurationItem("sql-password", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            builder.setConfiguration(new ConfigurationItem("sql-database", ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));

            context.registerService(DataSourceBuilder.class.getName(), builder, null);
        } catch (BaseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if(controller != null) controller.close();
        if(staticHttpTracker != null) staticHttpTracker.close();

        super.stop(context);
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