package com.recalot.controller.recommendations;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator {


    private RecommenderController controller;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        controller = new RecommenderController(context);
        context.registerService(com.recalot.common.interfaces.controller.RecommenderController.class.getName(), controller, null);
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    public void stop(BundleContext context) throws Exception {
       if(controller != null) controller.close();
    }
}