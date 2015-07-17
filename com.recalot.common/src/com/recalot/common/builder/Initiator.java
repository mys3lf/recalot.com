package com.recalot.common.builder;

/**
 * Interface which is necessary for the initialization of instance in the different Builder classes
 * Every bundle has to initialize its classes. Otherwise this bundle would need all dependencies, which is impossible.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface Initiator {
    /**
     * Initialize an object with of the given class name
     * @param className class name of the instance that should be initialized
     * @return initialized object
     */
    public Object createInstance(String className);
}
