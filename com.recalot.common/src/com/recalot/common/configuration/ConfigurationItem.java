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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class ConfigurationItem {
    private List<String> options;
    private String key;
    private ConfigurationItemType type;
    private String value;
    private String description;
    private ConfigurationItemRequirementType requirement;

    public ConfigurationItem()
    {
        key = "";
        type = ConfigurationItemType.String;
        value = "";
        requirement = ConfigurationItemRequirementType.Required;
        options = new ArrayList<>();
        description = "";
    }

    public ConfigurationItem(String key, ConfigurationItemType type)
    {
        this.key = key;
        this.type = type;
        this.value = "";
        this.requirement = ConfigurationItemRequirementType.Required;
        this.options = new ArrayList<>();
        this.description = "";
    }

    public ConfigurationItem(String key, ConfigurationItemType type, String value)
    {
        this.key = key;
        this.type = type;
        this.value = value;
        this.requirement = ConfigurationItemRequirementType.Required;
        this.options = new ArrayList<>();
        this.description = "";
    }

    public ConfigurationItem(String key, ConfigurationItemType type, String value, ConfigurationItemRequirementType requirement)
    {
        this.key = key;
        this.type = type;
        this.value = value;
        this.requirement = requirement;
        this.options = new ArrayList<>();
        this.description = "";
    }

    public ConfigurationItem(String key, ConfigurationItemType type, String value, ConfigurationItemRequirementType requirement, String description)
    {
        this.key = key;
        this.type = type;
        this.value = value;
        this.requirement = requirement;
        this.options = new ArrayList<>();
        this.description = description;
    }

    public ConfigurationItem(String key, ConfigurationItemType type, String value, ConfigurationItemRequirementType requirement, String description, ArrayList<String> options)
    {
        this.key = key;
        this.type = type;
        this.value = value;
        this.requirement = requirement;
        this.options = options;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ConfigurationItemType getType() {
        return type;
    }

    public void setType(ConfigurationItemType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ConfigurationItemRequirementType getRequirement() {
        return requirement;
    }

    public void setRequirement(ConfigurationItemRequirementType requirement) {
        this.requirement = requirement;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum ConfigurationItemType {
        String,
        Options,
        Boolean,
        Integer,
        Double
    }

    public enum ConfigurationItemRequirementType{
        Required,
        Optional,
        Hidden
    }
}
