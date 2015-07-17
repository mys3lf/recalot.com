package com.recalot.common.builder;

import com.recalot.common.communication.Service;
import com.recalot.common.configuration.Configurable;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * Applies the configuration to the given instances
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class InstanceBuilder<T> extends Configurable implements Service {

    //Actual instance builder
    private Initiator initiator;
    //class name of the instances that should be build
    private String className;
    //key of the instance builder
    private String key;
    //description of the instance builder
    private String description;

    /**
     * Default constructor. Stores the given values.
     *
     * @param initiator the Initiator actually initialize the instance
     * @param className class name of the instance that should be build
     * @param key key of the instance builder
     * @param description description of the instance builder
     * @throws BaseException
     */
    public InstanceBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        this.initiator = initiator;
        this.className = className;
        this.key = key;
        this.description = description;
    }

    /**
     *  Builds an instance without a prefix for the configuration.
     *
     * @param id the id of the instance
     * @param params a map that contains configuration information
     * @return the instance with the applied configuration
     * @throws BaseException
     */
    public T createInstance(String id, Map<String, String> params) throws BaseException {
        return createInstance(id, "", params);
    }

    /**
     * Builds and instance and applies all configuration values.
     *
     * @param configPrefix prefix for the configuration
     * @param id the id of the instance
     * @param params a map that contains configuration information
     * @return the instance with the applied configuration
     * @throws BaseException
     */
    public T createInstance(String id, String configPrefix, Map<String, String> params) throws BaseException {
        checkConfiguration(configPrefix + (configPrefix == null || configPrefix.isEmpty() ? "" : "."), params);
        try {
            Object instance = initiator.createInstance(className);
            Class c = instance.getClass();

            Method idm = getMethod(c, "setId", String.class);
            if (idm != null) {
                idm.invoke(instance, id);
            }

            applyConfiguration(instance, c, getConfiguration());

            return (T) instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return the key of the instance builder
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * @return the description of the instance builder
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        //nothing to do here
    }

    /**
     * necessary for flexjson
     * flexjson is not able to access getter that are in a super super class
     * @return the current configuration
     */
    public ConfigurationItem[] getConfiguration() {
        return super.getConfiguration();
    }
}
