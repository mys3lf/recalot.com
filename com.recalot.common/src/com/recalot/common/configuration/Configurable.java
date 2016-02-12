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

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.MissingArgumentException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public abstract class Configurable {

    protected HashMap<String, ConfigurationItem> configurationMap = new HashMap<>();

    public ConfigurationItem[] getConfiguration() {
        return configurationMap.values().toArray(new ConfigurationItem[configurationMap.size()]);
    }

    public void setConfiguration(List<ConfigurationItem> configuration) {
        configurationMap = new HashMap<>();
        if (configuration != null) {
            for (ConfigurationItem item : configuration) {
                configurationMap.put(item.getKey(), item);
            }
        }
    }

    public ConfigurationItem getConfiguration(String key) {
        return configurationMap.get(key);
    }

    public void setConfiguration(ConfigurationItem configuration) {
        configurationMap.put(configuration.getKey(), configuration);
    }

    public void checkConfiguration(Map<String, String> config) throws BaseException {
        checkConfiguration("", config);
    }

    public void checkConfiguration(String prefix, Map<String, String> config) throws BaseException {
        if (this.configurationMap != null && config != null) {
            for (ConfigurationItem item : this.configurationMap.values()) {
                if (item.getRequirement() == ConfigurationItem.ConfigurationItemRequirementType.Required && !config.containsKey(prefix + item.getKey())) {
                    throw new MissingArgumentException("The argument %s is missing.", prefix + item.getKey());
                }

                if (config.containsKey(prefix + item.getKey())) {
                    item.setValue(config.get(prefix + item.getKey()));
                }
            }
        }
    }

    public static void applyConfiguration(Object instance, Class c, ConfigurationItem[] config) throws Exception {

        for (ConfigurationItem item : config) {
            if (item.getValue() != null) {
                String methodName = item.getKey();

                methodName = adjustMethodName(methodName);
                //   System.out.println(methodName + ":" + item.getValue());
                boolean found = false;

                switch (item.getType()) {
                    case Integer: {

                        Method m = getMethod(c, methodName, Integer.class);
                        if (m == null) m = getMethod(c, methodName, int.class);
                        if (m != null) {
                            m.invoke(instance, Integer.parseInt(item.getValue()));
                            found = true;
                        }

                        break;
                    }
                    case Double: {
                        Method m = getMethod(c, methodName, Double.class);
                        if (m == null) m = getMethod(c, methodName, double.class);
                        if (m != null) {
                            m.invoke(instance, Double.parseDouble(item.getValue()));
                            found = true;
                        }
                        break;
                    }
                    case Boolean: {
                        Method m = getMethod(c, methodName, Boolean.class);
                        if (m == null) m = getMethod(c, methodName, boolean.class);
                        if (m != null) {
                            m.invoke(instance, Boolean.parseBoolean(item.getValue()));
                            found = true;
                        }
                        break;
                    }
                    case Options:
                    case String: {
                        Method m = getMethod(c, methodName, String.class);
                        if (m != null) {
                            m.invoke(instance, item.getValue());
                            found = true;
                        }
                        break;
                    }
                }


                if (!found) {
                    //the instance does not handle the configuration values individually but global in one method
                    Method m = getMethod(c, "setConfigurationValue", ConfigurationItem.class);

                    if (m != null) {
                        m.invoke(instance, item);
                    }
                }
            }
        }
    }


    protected static Method getMethod(Class c, String methodName, Class... cl) {
        Method m = null;
        Class tempClass = c;
        while (tempClass != null && m == null) {
            try {
                m = tempClass.getMethod(methodName, cl);
            } catch (NoSuchMethodException e) {
                tempClass = tempClass.getSuperclass();
            }
        }

        return m;
    }

    protected static Field getField(Class c, String fieldName, Class cl) {
        Field f = null;
        Class tempClass = c;
        while (tempClass != null && f == null) {
            try {
                f = tempClass.getDeclaredField(fieldName);
                if (!f.getType().equals(cl)) {
                    f = null;
                }

                if (!f.isAccessible()) {
                    f = null;
                }
            } catch (NoSuchFieldException e) {
                tempClass = tempClass.getSuperclass();
            }
        }

        return f;
    }

    private static String adjustMethodName(String methodName) {
        StringBuilder builder = new StringBuilder();
        builder.append("set");
        String[] split = methodName.split("-");

        for (String s : split) {
            builder.append(s.substring(0, 1).toUpperCase() + s.substring(1));
        }

        return builder.toString();
    }


    public void appendConfiguration(ArrayList<ConfigurationItem> items) {
        if (items != null) {
            if (items != null) {
                for (ConfigurationItem item : items) {
                    configurationMap.put(item.getKey(), item);
                }
            }
        }
    }
}
