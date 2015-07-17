package com.recalot.views.tracking;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.http.HttpService;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, ServiceListener {

    private ServiceTracker httpTracker;
    private ControllerHandler handler;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        handler = new ControllerHandler(context);
        context.addServiceListener(handler);

        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null, handler, "rec");
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
        if (handler != null) {
            try {
                context.removeServiceListener(handler);
            } catch (Exception e) {
                     //ignore the message
            }
        }

        if (httpTracker != null) {
            try {
                // stop tracking all HTTP services...
                httpTracker.close();
            } catch (Exception e) {
                //ignore the message
            }
        }

    }

    /**
     * Implements ServiceListener.serviceChanged().
     * Prints the details of any service event from the framework.
     *
     * @param event the fired service event.
     */
    public void serviceChanged(ServiceEvent event) {
        String[] objectClass = (String[]) event.getServiceReference().getProperty("objectClass");

        if (event.getType() == ServiceEvent.REGISTERED) {
            System.out.println(
                    "Ex1: Service of type " + objectClass[0] + " registered.");
        } else if (event.getType() == ServiceEvent.UNREGISTERING) {
            System.out.println(
                    "Ex1: Service of type " + objectClass[0] + " unregistered.");
        } else if (event.getType() == ServiceEvent.MODIFIED) {
            System.out.println(
                    "Ex1: Service of type " + objectClass[0] + " modified.");
        }
    }
}