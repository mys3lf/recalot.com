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

package com.recalot.controller.recommendations;


import com.recalot.common.Helper;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Message;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.Context;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.common.exceptions.NotReadyException;
import com.recalot.common.interfaces.controller.RequestAction;
import com.recalot.common.interfaces.model.data.*;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.model.rec.RecommenderAccess;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;
import com.recalot.common.interfaces.template.RecommenderTemplate;
import com.recalot.common.GenericServiceListener;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author matthaeus.schmedding
 */
public class RecommenderController implements com.recalot.common.interfaces.controller.RecommenderController, Closeable {

    private final BundleContext context;
    private final GenericServiceListener recommenderAccess;
    private final GenericServiceListener templates;
    private final GenericServiceListener dataAccess;
    private final ContextProvider contextProvider;


    public RecommenderController(BundleContext context) {
        this.context = context;
        this.recommenderAccess = new GenericServiceListener<RecommenderAccess>(context, RecommenderAccess.class.getName());
        this.dataAccess = new GenericServiceListener<DataAccess>(context, DataAccess.class.getName());
        this.templates = new GenericServiceListener<RecommenderTemplate>(context, RecommenderTemplate.class.getName());
        this.contextProvider = new ContextProvider(context);

        this.context.addServiceListener(recommenderAccess);
        this.context.addServiceListener(dataAccess);
        this.context.addServiceListener(templates);
        this.context.addServiceListener(this.contextProvider);
    }

    @Override
    public TemplateResult process(RequestAction action, String templateKey, Map<String, String> param) throws BaseException {

        RecommenderTemplate template = (RecommenderTemplate) templates.getInstance(templateKey);
        TemplateResult result = null;

        try {

            switch ((RecommenderRequestAction) action) {
                case CreateRecommender: {
                    result = createRecommender(template, param);
                    break;
                }
                case Recommend: {
                    result = recommend(template, param);
                    break;
                }
                case GetRecommender: {
                    result = getRecommender(template, param);
                    break;
                }
                case GetRecommenderBuilder: {
                    result = getRecommenderBuilder(template, param);
                    break;
                }
                case GetRecommenders: {
                    result = getRecommenders(template, param);
                    break;
                }
                case UpdateRecommender: {
                    result = updateRecommender(template, param);
                    break;
                }
                case DeleteRecommender: {
                    result = deleteRecommender(template, param);
                    break;
                }
            }
        } catch (BaseException ex) {
            result = template.transform(ex);
        }

        return result;
    }

    private TemplateResult getRecommenders(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();

            RecommenderInformation[] recommenders = access.getRecommenders();

        if (param.containsKey(Helper.Keys.State)) {
            RecommenderInformation.RecommenderState state = RecommenderInformation.RecommenderState.valueOf(param.get(Helper.Keys.State));
            List<RecommenderInformation> temp = new ArrayList<>();
            for (RecommenderInformation info : recommenders) {
                if (info.getState() == state) {
                    temp.add(info);
                }
            }

            return template.transform(temp.toArray(new RecommenderInformation[temp.size()]));
        }

        return template.transform(recommenders);
    }

    private TemplateResult deleteRecommender(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();

        Message message = access.deleteRecommender(param.get(Helper.Keys.RecommenderId));

        return template.transform(message);
    }

    private TemplateResult getRecommender(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();
        Recommender recommender = access.getRecommender(param.get(Helper.Keys.RecommenderId));

        if(recommender == null) throw new NotFoundException("Recommender with the id %s not found.", param.get(Helper.Keys.RecommenderId));

        if(recommender.getConfiguration(Helper.Keys.UserId) == null) {
            recommender.setConfiguration(new ConfigurationItem(Helper.Keys.UserId, ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
        }

        if(recommender.getConfiguration(Helper.Keys.ItemId) == null) {
           recommender.setConfiguration(new ConfigurationItem(Helper.Keys.ItemId, ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Optional));
        }

        return template.transform(recommender);
    }

    private TemplateResult getRecommenderBuilder(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();
        RecommenderBuilder recommender = access.getRecommenderBuilder(param.get(Helper.Keys.RecommenderId));
        DataAccess dAccess = (DataAccess) dataAccess.getFirstInstance();

        if(recommender == null) throw new NotFoundException("Recommender with the id %s not found.", param.get(Helper.Keys.RecommenderId));
        if(recommender.getState() == RecommenderInformation.RecommenderState.AVAILABLE){
            ConfigurationItem config = new ConfigurationItem(Helper.Keys.SourceId, ConfigurationItem.ConfigurationItemType.Options, "", ConfigurationItem.ConfigurationItemRequirementType.Required);

            List<String> dataSources = new ArrayList<>();
            List<DataInformation> infos = dAccess.getDataInformations();
            for(DataInformation i : infos ){
                if(i.getState() == DataInformation.DataState.READY){
                    dataSources.add(i.getId());
                }
            }

            config.setOptions(dataSources);

            recommender.setConfiguration(config);
            recommender.setConfiguration(new ConfigurationItem(Helper.Keys.ID, ConfigurationItem.ConfigurationItemType.String, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            recommender.setConfiguration(new ConfigurationItem(Helper.Keys.RecommenderBuilderId, ConfigurationItem.ConfigurationItemType.String, recommender.getKey(), ConfigurationItem.ConfigurationItemRequirementType.Hidden));
        }

        return template.transform(recommender);
    }

    private TemplateResult recommend(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();

        Recommender recommender = access.getRecommender(param.get(Helper.Keys.RecommenderId));
        if(recommender == null) throw new NotFoundException("A recommender with the id %s can not be found.",param.get(Helper.Keys.RecommenderId));
        DataAccess dAccess = (DataAccess) dataAccess.getFirstInstance();
        DataSource source = dAccess.getDataSource(recommender.getDataSourceId());

        String userId = param.get(Helper.Keys.UserId);

        if(userId != null && !userId.isEmpty()) {

            for(Context c : contextProvider.getAll()) {
                if(c instanceof UserContext) {
                    ((UserContext)c).processContext(source.getSourceId(), userId, source, Helper.Keys.Context.DataSet);
                    ((UserContext)c).processContext(source.getSourceId(), userId, param, Helper.Keys.Context.Params);
                }
            }
        }

        return template.transform(recommender.recommend(userId, contextProvider, param));
    }

    private TemplateResult createRecommender(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();
        DataAccess dAccess = (DataAccess) dataAccess.getFirstInstance();

        DataSource source = getDataSource(dAccess, param.get(Helper.Keys.SourceId));
        if(source.getState() != DataInformation.DataState.READY) throw new NotReadyException("The data source %s is not ready so far. The current state is %s.", source.getId(), source.getState().toString());
        Recommender recommender = access.createRecommender(source, param);
        return template.transform(recommender);
    }

    private TemplateResult updateRecommender(RecommenderTemplate template, Map<String, String> param) throws BaseException {
        RecommenderAccess access = (RecommenderAccess) recommenderAccess.getFirstInstance();
        DataAccess dAccess = (DataAccess) dataAccess.getFirstInstance();

        DataSource source = getDataSource(dAccess, param.get(Helper.Keys.SourceId));
        if(source.getState() != DataInformation.DataState.READY) throw new NotReadyException("The data source %s is not ready so far. The current state is %s.", source.getId(), source.getState().toString());
        Recommender recommender = access.updateRecommender(param.get(Helper.Keys.RecommenderId), source, param);
        return template.transform(recommender.recommend(param.get(Helper.Keys.UserId)));
    }

    private DataSource getDataSource(DataAccess access, String sourceId) throws BaseException {
        return access.getDataSource(sourceId);
    }


    @Override
    public void close() throws IOException {
        if (recommenderAccess != null) {
            this.context.removeServiceListener(recommenderAccess);
        }

        if (dataAccess != null) {
            this.context.removeServiceListener(dataAccess);
        }

        if (templates != null) {
            this.context.removeServiceListener(templates);
        }

        if (contextProvider != null) {
            this.context.removeServiceListener(contextProvider);
        }
    }
}
