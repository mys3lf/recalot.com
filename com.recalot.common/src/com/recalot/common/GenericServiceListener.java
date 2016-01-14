// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.common;

import com.recalot.common.exceptions.NotFoundException;
import com.recalot.common.communication.Service;
import com.recalot.common.log.LogTracker;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;

import java.util.*;

/**
 * This class observes all changes in the OSGi container for instances of type T.
 * It register changes automatically and manage all instances of type T in the OSGi container.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class GenericServiceListener<T extends Service> implements ServiceListener {
    // The bundle context
    private BundleContext context;
    //Map with all instances of type T
    private final Map<String, T> instances;
    //The className of type T
    private String className;
    //Logger
    private LogTracker logger;

    /**
     * Initialize the logger and checks whether instances of type already exists within the bundle container.
     *
     * @param context the bundle context
     * @param className the className of type T
     */
    public GenericServiceListener(BundleContext context, String className) {
        this.context = context;
        this.instances = new LinkedHashMap<>();

        logger = new LogTracker(context);
        this.className = className;
        initialize();
    }

    /**
     * Checks if instances of type are already registered within the OSGi container and stores them.
     */
    private void initialize() {
        try {
            ServiceReference[] references = this.context.getServiceReferences(className, null);
            if (references != null) {
                for (ServiceReference ref : references) {
                    T instance = (T) context.getService(ref);
                    addInstance(instance.getKey(), instance);
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            logger.log(LogService.LOG_ERROR, e.getMessage());
        }
    }

    /**
     * Registers changes of instances of type T and add new instances, remove old instances or update existing instances.
     * @param event OSGi ServiceEvent
     */
    @Override
    public void serviceChanged(ServiceEvent event) {
        String[] objectClass = (String[]) event.getServiceReference().getProperty(Constants.OBJECTCLASS);

        if (className.equals(objectClass[0])) {
            T instance = (T) context.getService(event.getServiceReference());

            if (event.getType() == ServiceEvent.REGISTERED) {
                addInstance(instance.getKey(), instance);
            } else if (event.getType() == ServiceEvent.UNREGISTERING) {
                removeInstance(instance.getKey());
            } else if (event.getType() == ServiceEvent.MODIFIED) {
                updateInstance(instance.getKey(), instance);
            }
        }
    }

    /**
     * Returns the first instances of type T.
     * @return first instance of type T.
     */
    public T getFirstInstance() {
        // Lock list and add service object.
        synchronized (instances) {
            if (instances.values().size() > 0)
                return (T) instances.values().toArray()[0];
        }


        return null;
    }

    /**
     * Returns instances with the given key.
     * @param key service key.
     * @return instance with the given key.
     * @throws NotFoundException is no instance can be found.
     */
    public T getInstance(String key) throws NotFoundException {
        // Lock list and add service object.
        synchronized (instances) {
            if (instances.containsKey(key))
                return instances.get(key);
        }

        throw new NotFoundException("Service instance for key %s and class %s cannot be found.", key, className);
    }

    /**
     * Updates the instance with the given key.
     * @param key service key.
     * @param instance new instance.
     */
    private void updateInstance(String key, T instance) {
        // Lock list and add service object.
        synchronized (instances) {
            instances.put(key, instance);
            logger.log(LogService.LOG_INFO, "Update " + className + " with key " + key);
        }
    }

    /**
     * Remove the instance with the given key.
     * @param key service key.
     */
    private void removeInstance(String key) {
        // Lock list and remove service object.
        synchronized (instances) {
            if (!instances.containsKey(key)) instances.remove(key);
            logger.log(LogService.LOG_INFO, "Remove " + className + " with key " + key);
        }
    }

    /**
     * Remove the instance with the given key.
     * @param key service key.
     * @param instance registered instance of type T.
     */
    private void addInstance(String key, T instance) {
        // Lock list and replace service object.
        synchronized (instances) {
            if (!instances.containsKey(key)) instances.put(key, instance);
            logger.log(LogService.LOG_INFO, "Add " + className + " with key " + key);
        }
    }

    /**
     * Returns all instances of the given type.
     * @return all instances of T.
     */
    public ArrayList<T> getAll() {
        return new ArrayList<>(instances.values());
    }
}
