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
