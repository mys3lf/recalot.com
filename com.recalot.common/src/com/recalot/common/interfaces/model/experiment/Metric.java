package com.recalot.common.interfaces.model.experiment;

/**
 * @author Matthaeus.schmedding
 */
public abstract class Metric implements MetricInformation {
    private String id;
    private String name;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract double getResult();
}
