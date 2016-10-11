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
