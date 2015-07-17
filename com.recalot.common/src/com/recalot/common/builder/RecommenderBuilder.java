package com.recalot.common.builder;

import com.recalot.common.Helper;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;

/**
 * Responsible for the initialization of Recommenders. Specification of the InstanceBuilder
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class RecommenderBuilder extends InstanceBuilder<Recommender> implements RecommenderInformation {

    /**
     * Constructor
     * @param initiator initiator instance
     * @param className class name of object that should be initialized
     * @param key key of the object
     * @param description description of the object
     * @throws BaseException
     */
    public RecommenderBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);
    }

    /**
     *
     * @return the current state (always "AVAILABLE")
     */
    @Override
    public RecommenderState getState() {
        return RecommenderState.AVAILABLE;
    }

    /**
     *
     * @return the id of the recommender builder
     */
    @Override
    public String getId() {
        return Helper.Keys.RecommenderBuilderIdPrefix + getKey();
    }
}
