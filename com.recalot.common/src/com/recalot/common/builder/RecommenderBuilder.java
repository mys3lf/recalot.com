package com.recalot.common.builder;

import com.recalot.common.Helper;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;

import java.io.Closeable;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class RecommenderBuilder extends InstanceBuilder<Recommender> implements RecommenderInformation {

    public RecommenderBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);
    }

    @Override
    public RecommenderState getState() {
        return RecommenderState.AVAILABLE;
    }

    @Override
    public String getId() {
        return Helper.Keys.RecommenderBuilderIdPrefix + getKey();
    }
}
