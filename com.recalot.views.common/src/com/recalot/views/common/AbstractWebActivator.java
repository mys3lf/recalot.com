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

package com.recalot.views.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by matthaeus.schmedding on 14.04.2015.
 */
public abstract class AbstractWebActivator implements BundleActivator {

    protected HttpServiceTracker httpTracker;
    protected GenericControllerHandler handler;
    protected WebService service;

    @Override
    public abstract void start(BundleContext bundleContext) throws Exception;

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (service == null) {
            try {
                service.close();
            } catch (Exception e) {
                //ignore the message
            }

            service = null;
        }

        if (handler != null) {
            try {
                handler.close();
            } catch (Exception e) {
                //ignore the message
            }

            handler = null;
        }

        if (httpTracker != null) {
            try {
                // stop tracking all HTTP services...
                httpTracker.close();
            } catch (Exception e) {
                //ignore the message
            }

            httpTracker = null;
        }
    }
}
