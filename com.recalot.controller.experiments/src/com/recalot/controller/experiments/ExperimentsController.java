package com.recalot.controller.experiments;


import com.recalot.common.GenericServiceListener;
import com.recalot.common.Helper;
import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.impl.experiment.Experiment;
import com.recalot.common.interfaces.controller.RequestAction;
import com.recalot.common.interfaces.model.data.DataAccess;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.ExperimentAccess;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.template.ExperimentTemplate;
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


    public ExperimentsController(BundleContext context) {
        this.context = context;
        this.recommenderAccess = new GenericServiceListener(context, RecommenderBuilder.class.getName());
        this.dataAccess = new GenericServiceListener(context, DataAccess.class.getName());
        this.experimentsAccess = new GenericServiceListener(context, ExperimentAccess.class.getName());
        this.dataSplitterAccess = new GenericServiceListener(context, DataSplitterBuilder.class.getName());
        this.templates = new GenericServiceListener(context, ExperimentTemplate.class.getName());
        this.metricsListener = new GenericServiceListener(context, MetricBuilder.class.getName());

        this.context.addServiceListener(recommenderAccess);
        this.context.addServiceListener(dataAccess);
        this.context.addServiceListener(experimentsAccess);
        this.context.addServiceListener(dataSplitterAccess);
        this.context.addServiceListener(templates);
        this.context.addServiceListener(metricsListener);
    }

    @Override
    public TemplateResult process(RequestAction action, String templateKey, Map<String, String> param) throws BaseException {

        ExperimentTemplate template = templates.getInstance(templateKey);
        TemplateResult result = null;

        try {
            switch ((ExperimentsRequestAction) action) {
                case GetExperiments: {
                    result = getExperiments(template, param);
                    break;
                }
                case CreateExperiment: {
                    result = createExperiment(template, param);
                    break;
                }
                case DeleteExperiment: {
                    result = deleteExperiment(template, param);
                    break;
                }
                case GetExperiment: {
                    result = getExperiment(template, param);
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

    private TemplateResult createExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();
        DataAccess dAccess = dataAccess.getFirstInstance();

        List<Recommender> recommender = new ArrayList<>();

        String recommenderId = param.get(Helper.Keys.RecommenderId);

        if (recommenderId != null) {
            if (recommenderId.contains(",")) {
                String[] split = recommenderId.split(",");

                for (String s : split) {
                    //id should be something like mp@mp-test -> id@display-name
                    if (s != null && !s.isEmpty()) {
                        Recommender rec = null;

                        if (s.contains("@")) {
                            String[] idSplit = s.split("@");
                            if (idSplit.length > 1) {

                                param.put(idSplit[1] + "." +Helper.Keys.ID, idSplit[1]);
                                param.put(idSplit[1] + "." +Helper.Keys.SourceId, param.get(Helper.Keys.SourceId));

                                rec = recommenderAccess.getInstance(idSplit[0]).createInstance(idSplit[1], idSplit[1], param);
                            }
                        } else {

                            param.put(s + "." +Helper.Keys.ID, s);
                            param.put(s + "." +Helper.Keys.SourceId, param.get(Helper.Keys.SourceId));

                            rec = recommenderAccess.getInstance(s).createInstance(s, s, param);
                        }

                        recommender.add(rec);
                    }
                }
            } else {
                Recommender rec = null;

                if (recommenderId.contains("@")) {
                    String[] idSplit = recommenderId.split("@");
                    if (idSplit.length > 1) {

                        param.put(idSplit[1] + "." + Helper.Keys.ID, idSplit[1]);
                        param.put(idSplit[1] + "." + Helper.Keys.SourceId, param.get(Helper.Keys.SourceId));

                        rec = recommenderAccess.getInstance(idSplit[0]).createInstance(idSplit[1], idSplit[1], param);
                    }
                } else {

                    param.put(recommenderId + "." +Helper.Keys.ID, recommenderId);
                    param.put(recommenderId + "." +Helper.Keys.SourceId, param.get(Helper.Keys.SourceId));

                    rec = recommenderAccess.getInstance(recommenderId).createInstance(recommenderId, recommenderId, param);
                }

                recommender.add(rec);
            }
        }

        DataSource dataSource = dAccess.getDataSource(param.get(Helper.Keys.SourceId));

        String metricIds = param.get(Helper.Keys.MetricIDs);
        HashMap<String, Metric[]> metrics = new HashMap<>();

        if (metricIds != null) {
            String[] split = metricIds.split(",");
            for (Recommender r : recommender) {
                ArrayList<Metric> m = new ArrayList<>();
                for (String s : split) {
                    if (s != null && !s.isEmpty()) {
                        Metric metric = null;

                        if (s.contains("@")) {
                            String[] idSplit = s.split("@");
                            if (idSplit.length > 1) {
                                metric = metricsListener.getInstance(idSplit[0]).createInstance(idSplit[1], idSplit[1], param);
                            }
                        } else {
                            metric = metricsListener.getInstance(s).createInstance(s, s, param);
                        }

                        m.add(metric);
                    }

                    metrics.put(r.getId(), m.toArray(new Metric[m.size()]));
                }
            }
        }

        DataSplitterBuilder splitterBuilder = dataSplitterAccess.getInstance(param.get(Helper.Keys.DataSplitterId));

        DataSplitter splitter = splitterBuilder.createInstance(param.get(Helper.Keys.DataSplitterId), param.get(Helper.Keys.DataSplitterId), param);

        return template.transform(access.createExperiment(recommender.toArray(new Recommender[recommender.size()]), dataSource, splitter, metrics, param));
    }

    private TemplateResult deleteExperiment(ExperimentTemplate template, Map<String, String> param) throws BaseException {
        ExperimentAccess access = experimentsAccess.getFirstInstance();


        return template.transform(access.deleteExperiment(param.get(Helper.Keys.ExperimentId)));
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
