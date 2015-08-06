package com.recalot.common.builder;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.DataSplitterInformation;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.MetricInformation;

/**
 * Responsible for the initialization of data splitter. Specification of the InstanceBuilder
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class DataSplitterBuilder extends InstanceBuilder<DataSplitter> implements DataSplitterInformation {

    /**
     * Constructor
     * @param initiator initiator instance
     * @param className class name of object that should be initialized
     * @param key key of the object
     * @param description description of the object
     * @throws com.recalot.common.exceptions.BaseException
     */
    public DataSplitterBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);
    }

    /**
     *
     * @return the id of the data splitter
     */
    @Override
    public String getId() {
        return getKey();
    }
}
