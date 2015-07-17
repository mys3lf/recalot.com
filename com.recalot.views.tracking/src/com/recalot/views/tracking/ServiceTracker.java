package com.recalot.views.tracking;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Matthaeus.schmedding
 */
public class ServiceTracker extends org.osgi.util.tracker.ServiceTracker {

    private final ControllerHandler handler;
    private String path;

    public ServiceTracker(BundleContext context, String reference, ServiceTrackerCustomizer customizer, ControllerHandler controller, String path) {
        super(context, reference, customizer);
        this.handler = controller;
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
            httpService.registerServlet("/" + path, new Servlet(handler), null, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return httpService;
    }
}
