package com.recalot.demos.wallpaper;


import com.recalot.demos.wallpaper.controller.DataAccessController;
import com.recalot.demos.wallpaper.view.Servlet;
import com.recalot.views.common.AbstractWebActivator;
import com.recalot.views.common.GenericControllerHandler;
import com.recalot.views.common.WebService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;


/**
 * @author Matthaeus.schmedding
 */
public class Activator extends AbstractWebActivator {
    private DataAccessController controller;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {

        controller = new DataAccessController(context);
        context.registerService(com.recalot.common.interfaces.controller.DataAccessController.class.getName(), controller, null);

        handler = new GenericControllerHandler<DataAccessController>(context, DataAccessController.class.getName());

        String pid = "com.recalot.demos.wallpaper";

        Dictionary config = new Hashtable();
        config.put(pid + ".path", new String("/wallpaper"));

        service = new WebService(pid, context,  new Servlet(handler), config);
        context.registerService(ManagedService.class.getName(), service, config);

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

        super.stop(context);
    }
}