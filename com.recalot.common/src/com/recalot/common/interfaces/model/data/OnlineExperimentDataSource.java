package com.recalot.common.interfaces.model.data;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.OnlineExperimentRecommendation;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public abstract class OnlineExperimentDataSource extends DataSource {
    public abstract OnlineExperimentRecommendation getRecommendation(String recId) throws BaseException;
    public abstract void createRecommendation(String recId) throws BaseException;
    public abstract void updateRecommendation(String recId) throws BaseException;
}
