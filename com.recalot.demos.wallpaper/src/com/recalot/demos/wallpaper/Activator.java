package com.recalot.demos.wallpaper;


import com.recalot.demos.wallpaper.controller.DataAccessController;
import com.recalot.demos.wallpaper.view.Servlet;
import com.recalot.views.common.AbstractWebActivator;
import com.recalot.views.common.GenericControllerHandler;
import com.recalot.views.common.WebService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.http.HttpService;

import java.util.Dictionary;
import java.util.Hashtable;


/**
 * @author Matthaeus.schmedding
 */
public class Activator extends AbstractWebActivator {
    private DataAccessController controller;
    private ServiceTracker staticHttpTracker;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {

        controller = new DataAccessController(context);
        context.registerService(DataAccessController.class.getName(), controller, null);

        handler = new GenericControllerHandler<DataAccessController>(context, DataAccessController.class.getName());

        String pid = "com.recalot.demos.wallpaper";

        Dictionary config = new Hashtable();
        config.put(pid + ".path", new String("/sample/wallpaper-controller"));

        service = new WebService(pid, context,  new Servlet(handler), config);
        context.registerService(ManagedService.class.getName(), service, config);

        staticHttpTracker = new ServiceTracker(context, HttpService.class.getName(), null, "/web", "sample/wallpaper");
        staticHttpTracker.open();

        service.initialize();
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if(controller != null) controller.close();
        if(staticHttpTracker != null) staticHttpTracker.close();

        super.stop(context);
    }
}