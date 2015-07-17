package com.recalot.views.recommend;

import com.recalot.common.interfaces.controller.RecommenderController;
import com.recalot.views.common.AbstractWebActivator;
import com.recalot.views.common.GenericControllerHandler;
import com.recalot.views.common.WebService;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;


/**
 * @author Matthaeus.schmedding
 */
public class Activator extends AbstractWebActivator {

    @Override
    public void start(BundleContext context) {
        handler = new GenericControllerHandler<RecommenderController>(context, RecommenderController.class.getName());

        String pid = "com.recalot.views.recommend";

        Dictionary config = new Hashtable();
        config.put(pid + ".path", new String("/rec"));

        service = new WebService(pid, context, new Servlet(handler), config);
        context.registerService(ManagedService.class.getName(), service, config);
        service.initialize();

    }
}