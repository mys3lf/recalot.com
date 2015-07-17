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

        //TODO make the path configurable
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