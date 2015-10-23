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

package com.recalot.demos.wallpaper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Matthaeus.schmedding
 */
public class ServiceTracker extends org.osgi.util.tracker.ServiceTracker {
    private final String filePath;
    private String path;

    public ServiceTracker(BundleContext context, String reference, ServiceTrackerCustomizer customizer, String filePath, String path) {
        super(context, reference, customizer);

        this.filePath = filePath;
        this.path = path;
    }

    public void removedService(ServiceReference reference, Object service) {
        // HTTP service is no longer available, unregister our servlet...
        try {
            ((HttpService) service).unregister("/" + path);
        } catch (IllegalArgumentException exception) {
            // Ignore; servlet registration probably failed earlier on...
        }
    }

    public Object addingService(ServiceReference reference) {
        // HTTP service is available, register our servlet...
        HttpService httpService = (HttpService) this.context.getService(reference);
        try {
            httpService.registerResources("/" + path, filePath, new HttpContext(httpService.createDefaultHttpContext()));


        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return httpService;
    }
}
