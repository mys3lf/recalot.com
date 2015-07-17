package com.example.web;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Matthaeus.schmedding
 */
public class ServiceTracker extends org.osgi.util.tracker.ServiceTracker {

    private final Controller controller;

    public ServiceTracker(BundleContext context, String reference, ServiceTrackerCustomizer customizer, Controller controller) {
        super(context, reference, customizer);
        this.controller = controller;
    }

    public void removedService(ServiceReference reference, Object service) {
        // HTTP service is no longer available, unregister our servlet...
        try {
          //  ((HttpService) service).unregister("/");
            ((HttpService) service).unregister("/cars");
        } catch (IllegalArgumentException exception) {
            // Ignore; servlet registration probably failed earlier on...
        }
    }

    public Object addingService(ServiceReference reference) {
        // HTTP service is available, register our servlet...
        HttpService httpService = (HttpService) this.context.getService(reference);
        try {

         //   httpService.registerServlet("/", new Servlet(controller), null, null);
            httpService.registerServlet("/cars", new Servlet(controller), null, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return httpService;
    }
}
