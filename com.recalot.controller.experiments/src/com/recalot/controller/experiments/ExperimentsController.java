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

package com.recalot.controller.experiments;


import com.recalot.common.GenericServiceListener;
import com.recalot.common.Helper;
import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.AlreadyExistsException;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.impl.experiment.Experiment;
import com.recalot.common.interfaces.controller.RecommenderController;
import com.recalot.common.interfaces.controller.RequestAction;
import com.recalot.common.interfaces.model.data.DataAccess;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.ExperimentAccess;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.experiment.OnlineExperiment;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.template.ExperimentTemplate;
import com.recalot.views.common.GenericControllerListener;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author matthaeus.schmedding
 */
public class ExperimentsController implements com.recalot.common.interfaces.controller.ExperimentsController, Closeable {

    private final BundleContext context;
    private final GenericServiceListener<RecommenderBuilder> recommenderAccess;
    private final GenericServiceListener<ExperimentTemplate> templates;
    private final GenericServiceListener<DataAccess> dataAccess;
    private final GenericServiceListener<ExperimentAccess> experimentsAccess;
    private final GenericServiceListener<DataSplitterBuilder> dataSplitterAccess;
    private final GenericServiceListener<MetricBuilder> metricsListener;
    private final ContextProvider contextProvider;
    private final GenericControllerListener recommenderController;


    public ExperimentsController(BundleContext context) {
        this.context = context;
        this.recommenderAccess = new GenericServiceListener(context, RecommenderBuilder.class.getName());
        this.recommenderController = new GenericControllerListener(context, RecommenderController.class.getName());
        this.dataAccess = new GenericServiceListener(context, DataAccess.class.getName());
        this.experimentsAccess = new GenericServiceListener(context, ExperimentAccess.class.getName());
        this.dataSplitterAccess = new GenericServiceListener(context, DataSplitterBuilder.class.getName());
        this.templates = new GenericServiceListener(context, ExperimentTemplate.class.getName());
        this.metricsListener = new GenericServiceListener(context, MetricBuilder.class.getName());
        this.contextProvider = new ContextProvider(context);

        this.context.addServiceListener(recommenderAccess);
        this.context.addServiceListener(recommenderController);
        this.context.addServiceListener(dataAccess);
        this.context.addServiceListener(experimentsAccess);
        this.context.addServiceListener(dataSplitterAccess);
        this.context.addServiceListener(templates);
        this.context.addServiceListener(metricsListener);
        this.context.addServiceListener(contextProvider);
    }

    @Override
    public TemplateResult process(RequestAction action, String templateKey, Map<String, String> param) throws BaseException {

        ExperimentTemplate template = templates.getInstance(templateKey);
        TemplateResult result = null;

        try {
            switch ((ExperimentsRequestAction) action) {
                case CreateExperiment: {
                    result = createExperiment(template, param);
                    break;
                }
                case CreateOnlineExperiment: {
                    result = createOnlineExperiment(template, param);
                    break;
                }
                case DeleteExperiment: {
                    result = deleteExperiment(template, param);
                    break;
                }
                case DeleteOnlineExperiment: {
                    result = deleteOnlineExperiment(template, param);
                    break;
                }
                case GetExperiments: {
                    result = getExperiments(template, param);
                    break;
                }
                case GetOnlineExperiments: {
                    result = getOnlineExperiments(template, param);
                    break;
                }
                case GetRecommendationForOnlineExperiment: {
                    result = getRecommendationForOnlineExperiment(templateKey, param);
                    break;
                }
                case GetExperiment: {
                    result = getExperiment(template, param);
                    break;
                }
                case GetOnlineExperiment: {
                    result = getOnlineExperiment(template, param);
                    break;
                }
                case GetExperimentConfiguration: {
                    result = getExperiment(template, param);
                    break;
                }
                case GetMetrics: {
                    result = getMetrics(template, param);
                    break;
                }
                case GetMetric: {
                    result = getMetric(template, param);
                    break;
                }
                case GetSplitters: {
                    result = getSplitters(template, param);
                    break;
                }
                case GetSplitter: {
                    result = getSplitter(template, param);
                    break;
                }
            }
        } catch (BaseException ex) {
            result = template.transform(ex);
        }

        return result;
    }

    private TemplateResult getRecommendationForOnlineExperiment(String templateKey, Map<String, String> param) throws BaseException {
        String experimentID = param.get(Helper.Keys.ExperimentId);
        ExperimentAccess access = experimentsAccess.getFirstInstance();
        String recommenderId = access.getNextRecommenderForOnlineExperiment(experimentID, param);
        param.put(Helper.Keys.RecommenderId, recommenderId);

        return recommenderController.getFirstInstance().process(RecommenderController.RecommenderRequestAction.Recommend, templateKey, param);
    }


    private TemplateResult createOnlineExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();
        DataAccess dAccess = dataAccess.getFirstInstance();
        DataSource dataSource = dAccess.getDataSource(param.get(Helper.Keys.SourceId));

        return template.transform(access.createOnlineExperiment(dataSource, param));
    }

    private TemplateResult getOnlineExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();
        DataAccess dAccess = dataAccess.getFirstInstance();
        DataSource dataSource = dAccess.getDataSource(param.get(Helper.Keys.SourceId));


        return template.transform(access.getOnlineExperiment(param.get(Helper.Keys.ExperimentId)));
    }

    private TemplateResult getExperiments(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();
        List<com.recalot.common.interfaces.model.experiment.Experiment> experiments = access.getExperiments();

        if (param.containsKey(Helper.Keys.State)) {
            Experiment.ExperimentState state = Experiment.ExperimentState.valueOf(param.get(Helper.Keys.State));
            List<com.recalot.common.interfaces.model.experiment.Experiment> temp = new ArrayList<>();
            for (com.recalot.common.interfaces.model.experiment.Experiment info : experiments) {
                if (info.getState() == state) {
                    temp.add(info);
                }
            }

            return template.transform(temp);
        }

        return template.transform(experiments);
    }

    private TemplateResult getOnlineExperiments(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();
        List<com.recalot.common.interfaces.model.experiment.OnlineExperiment> experiments = access.getOnlineExperiments();

        if (param.containsKey(Helper.Keys.State)) {
            OnlineExperiment.ExperimentState state = OnlineExperiment.ExperimentState.valueOf(param.get(Helper.Keys.State));
            List<OnlineExperiment> temp = new ArrayList<>();
            for (com.recalot.common.interfaces.model.experiment.OnlineExperiment info : experiments) {
                if (info.getState() == state) {
                    temp.add(info);
                }
            }

            return template.transformOnlineExperiments(temp);
        }

        return template.transformOnlineExperiments(experiments);
    }

    private TemplateResult createExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {

        ExperimentAccess access = experimentsAccess.getFirstInstance();
        String experimentId = param.get(Helper.Keys.ExperimentId);

        if (experimentId != null && !experimentId.isEmpty()) {
            if (access.containsExperiment(experimentId))
                throw new AlreadyExistsException("An experiment with the id %s already exists.", experimentId);
        }

        DataAccess dAccess = dataAccess.getFirstInstance();

        List<Recommender> recommender = new ArrayList<>();

        String recommenderId = param.get(Helper.Keys.RecommenderId);

        if (recommenderId != null) {
            Map<String, String> keyIds = Helper.splitIdKeyConfig(recommenderId);

            for (String key : keyIds.keySet()) {
                param.put(keyIds.get(key) + "." + Helper.Keys.ID, keyIds.get(key));
                param.put(keyIds.get(key) + "." + Helper.Keys.SourceId, param.get(Helper.Keys.SourceId));

                recommender.add(recommenderAccess.getInstance(key).createInstance(keyIds.get(key), keyIds.get(key), param));
            }
        }

        DataSource dataSource = dAccess.getDataSource(param.get(Helper.Keys.SourceId));

        String metricIds = param.get(Helper.Keys.MetricIDs);
        HashMap<String, Metric[]> metrics = new HashMap<>();

        if (metricIds != null) {
            Map<String, String> keyIds = Helper.splitIdKeyConfig(metricIds);

            for (Recommender r : recommender) {
                ArrayList<Metric> m = new ArrayList<>();
                for (String key : keyIds.keySet()) {
                    m.add(metricsListener.getInstance(key).createInstance(keyIds.get(key), keyIds.get(key), param));
                }

                metrics.put(r.getId(), m.toArray(new Metric[m.size()]));
            }
        }

        DataSplitterBuilder splitterBuilder = dataSplitterAccess.getInstance(param.get(Helper.Keys.DataSplitterId));

        DataSplitter splitter = splitterBuilder.createInstance(param.get(Helper.Keys.DataSplitterId), param.get(Helper.Keys.DataSplitterId), param);

        return template.transform(access.createExperiment(recommender.toArray(new Recommender[recommender.size()]), dataSource, splitter, metrics, contextProvider, param));
    }

    private TemplateResult deleteExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();


        return template.transform(access.deleteExperiment(param.get(Helper.Keys.ExperimentId)));
    }

    private TemplateResult deleteOnlineExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();


        return template.transform(access.deleteOnlineExperiment(param.get(Helper.Keys.ExperimentId)));
    }

    private TemplateResult getExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();

        return template.transform(access.getExperiment(param.get(Helper.Keys.ExperimentId)));
    }

    private TemplateResult getSplitter(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        return template.transform(dataSplitterAccess.getInstance(param.get(Helper.Keys.DataSplitterId)));
    }

    private TemplateResult getSplitters(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        return template.transformSplitters(dataSplitterAccess.getAll());
    }

    private TemplateResult getMetrics(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        return template.transformMetrics(metricsListener.getAll());
    }

    private TemplateResult getMetric(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        return template.transform(metricsListener.getInstance(param.get(Helper.Keys.MetricIDs)));
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

        if (experimentsAccess != null) {
            this.context.removeServiceListener(experimentsAccess);
        }

        if (dataSplitterAccess != null) {
            this.context.removeServiceListener(dataSplitterAccess);
        }
        if (metricsListener != null) {
            this.context.removeServiceListener(metricsListener);
        }
    }
}
