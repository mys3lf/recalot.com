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

package com.recalot.views.data.access;

import com.recalot.common.interfaces.controller.DataAccessController;
import com.recalot.views.common.AbstractWebActivator;
import com.recalot.views.common.GenericControllerHandler;
import com.recalot.views.common.WebService;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;


/**
 * @author Matthaeus.schmedding
 */
public class Activator extends AbstractWebActivator {

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    @Override
    public void start(BundleContext context) {
        handler = new GenericControllerHandler<DataAccessController>(context, DataAccessController.class.getName());

        String pid = "com.recalot.views.data.access";

        Dictionary config = new Hashtable();
        config.put(pid + ".path", new String("/data"));

        service = new WebService(pid, context,  new Servlet(handler), config);
        context.registerService(ManagedService.class.getName(), service, config);


        service.initialize();
    }
}