package com.recalot.common.builder;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.MetricInformation;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class MetricBuilder extends InstanceBuilder<Metric> implements MetricInformation {

    public MetricBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);
    }

    @Override
    public String getId() {
        return getKey();
    }
}
