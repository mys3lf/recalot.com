package com.recalot.views.common;

import com.recalot.common.interfaces.controller.Controller;
import com.recalot.common.interfaces.controller.DataAccessController;
import com.recalot.common.log.LogTracker;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author matthaeus.schmedding
 */
public class GenericControllerListener<T extends Controller> implements ServiceListener {
    private final BundleContext context;
    private String className;
    private final Map<Long, T> controller;
    private LogTracker logger;

    public GenericControllerListener(BundleContext context, String className) {
        this.context = context;
        this.className = className;
        this.controller = new LinkedHashMap<>();
        logger = new LogTracker(context);
        initialize();
    }

    private void initialize() {
        try {
            ServiceReference[] references = this.context.getServiceReferences(className, null);
            if (references != null) {
                for (ServiceReference ref : references) {
                    addController((Long) ref.getProperty(Constants.SERVICE_ID), (T) context.getService(ref));
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            logger.log(LogService.LOG_ERROR, e.getMessage());
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        String[] objectClass = (String[]) event.getServiceReference().getProperty(Constants.OBJECTCLASS);

        if (objectClass[0].equals(className)) {
            Long serviceId = (Long) event.getServiceReference().getProperty(Constants.SERVICE_ID);
            T instance = (T) context.getService(event.getServiceReference());

            if (event.getType() == ServiceEvent.REGISTERED) {
                addController(serviceId, instance);
            } else if (event.getType() == ServiceEvent.UNREGISTERING) {
                removeController(serviceId);
            } else if (event.getType() == ServiceEvent.MODIFIED) {
                updateController(serviceId, instance);
            }
        }
    }

    public T getFirstInstance() {
        // Lock list and add service object.
        synchronized (controller) {
            if (controller.values().size() > 0)
                return (T) controller.values().toArray()[0];
        }

        return null;
    }

    private void updateController(Long id, T instance) {
        // Lock list and add service object.
        synchronized (controller) {
            controller.put(id, instance);
            logger.log(LogService.LOG_INFO, "Update " + className + " controller with id" + id);
        }
    }

    private void removeController(Long id) {
        // Lock list and remove service object.
        synchronized (controller) {
            if (!controller.containsKey(id)) controller.remove(id);
            logger.log(LogService.LOG_INFO, "Remove " + className + " controller with id" + id);
        }
    }

    private void addController(Long id, T instance) {
        // Lock list and replace service object.
        synchronized (controller) {
            if (!controller.containsKey(id)) controller.put(id, instance);
            logger.log(LogService.LOG_INFO, "Add " + className + " controller with id" + id);
        }
    }
}
