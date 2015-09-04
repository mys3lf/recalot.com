package com.recalot.tests.data.dummypage;

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
