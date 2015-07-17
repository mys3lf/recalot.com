package com.recalot.common.configuration;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public abstract class ConfigurationService implements ManagedService {

    protected Dictionary config = new Hashtable();

    public void setPId(String pid) {
        config.put(Constants.SERVICE_PID, pid);
    }

    public String getPId() {
        return (String) config.get(Constants.SERVICE_PID);
    }

    public Dictionary getConfig() {
        return config;
    }

    public void setConfig(Dictionary config) {
        if (config != null) this.config = config;
        else this.config = new Hashtable();
    }

    @Override
    public void updated(Dictionary<String, ?> dictionary) throws ConfigurationException {
        if (dictionary != null) {
            setConfig(dictionary);
            onUpdate();
        }
    }

    public abstract void onUpdate();
}
