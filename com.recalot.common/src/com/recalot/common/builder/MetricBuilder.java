package com.recalot.common.builder;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.MetricInformation;

/**
 * Responsible for the initialization of metrics. Specification of the InstanceBuilder
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class MetricBuilder extends InstanceBuilder<Metric> implements MetricInformation {

    /**
     * Constructor
     * @param initiator initiator instance
     * @param className class name of object that should be initialized
     * @param key key of the object
     * @param description description of the object
     * @throws BaseException
     */
    public MetricBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);
    }

    /**
     *
     * @return the id of the metric
     */
    @Override
    public String getId() {
        return getKey();
    }
}
