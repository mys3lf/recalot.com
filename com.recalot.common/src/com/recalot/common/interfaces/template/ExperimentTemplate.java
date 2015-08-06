package com.recalot.common.interfaces.template;

import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.DataSplitterInformation;
import com.recalot.common.interfaces.model.experiment.Experiment;
import com.recalot.common.interfaces.model.experiment.MetricInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthaeus.schmedding
 */
public interface ExperimentTemplate extends BaseTemplate {
    public TemplateResult transform(Experiment experiment) throws BaseException;
    public TemplateResult transform(List<Experiment> experiment) throws BaseException;

    public TemplateResult transformMetrics(List<MetricBuilder> metrics) throws BaseException;
    public TemplateResult transform(MetricBuilder metric) throws BaseException;

    public TemplateResult transformSplitters(List<DataSplitterBuilder> splitters) throws BaseException;
    public TemplateResult transform(DataSplitterBuilder splitter) throws BaseException;
}
