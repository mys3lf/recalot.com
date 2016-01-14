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

package com.recalot.repo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator {

    private ServiceTracker httpTracker;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {

        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null, "/web", "repo");
        // start tracking all HTTP services...
        httpTracker.open();
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    public void stop(BundleContext context) throws Exception {
        if (httpTracker != null) {
            try {
                // stop tracking all HTTP services...
                httpTracker.close();
            } catch (Exception e) {
                //ignore the message
            }
        }

    }
}