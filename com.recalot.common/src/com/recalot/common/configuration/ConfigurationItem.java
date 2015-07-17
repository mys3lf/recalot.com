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
    private ConfigurationItemRequirementType requirement;

    public ConfigurationItem()
    {
        key = "";
        type = ConfigurationItemType.String;
        value = "";
        requirement = ConfigurationItemRequirementType.Required;
        options = new ArrayList<>();
    }

    public ConfigurationItem(String key, ConfigurationItemType type, String value, ConfigurationItemRequirementType requirement)
    {
        this.key = key;
        this.type = type;
        this.value = value;
        this.requirement = requirement;
        this.options = new ArrayList<>();
    }

    public ConfigurationItem(String key, ConfigurationItemType type, String value, ConfigurationItemRequirementType requirement, ArrayList<String> options)
    {
        this.key = key;
        this.type = type;
        this.value = value;
        this.requirement = requirement;
        this.options = options;
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

    public enum ConfigurationItemType{
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
