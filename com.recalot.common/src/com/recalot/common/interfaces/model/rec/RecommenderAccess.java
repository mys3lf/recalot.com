package com.recalot.common.interfaces.model.rec;

import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.communication.Service;
import com.recalot.common.interfaces.model.data.DataSource;

import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface RecommenderAccess extends Service {
    public RecommenderInformation[] getRecommenders() throws BaseException;
    public Recommender getRecommender(String id) throws BaseException;
    public RecommenderBuilder getRecommenderBuilder(String id) throws BaseException;
    public Recommender createRecommender(DataSource dataSource, Map<String, String> param) throws BaseException;
    public Recommender updateRecommender(String id, DataSource dataSource, Map<String, String> param) throws BaseException;
    public Message deleteRecommender(String id);
}
