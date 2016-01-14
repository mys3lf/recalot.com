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

package com.recalot.templates.experiments;

import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.Experiment;
import com.recalot.common.interfaces.template.ExperimentTemplate;
import com.recalot.templates.base.JsonBaseTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by matthaeus.schmedding on 01.04.2015.
 */
public class JsonExperimentTemplate extends JsonBaseTemplate implements ExperimentTemplate {
    @Override
    public TemplateResult transform(Experiment experiment) throws BaseException {
        String result = getSerializer().include("id", "state", "percentage", "results", "results.*", "recommenderIds", "dataSourceId", "info").exclude("*").serialize(experiment);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(List<Experiment> experiments) throws BaseException {
        String result = getSerializer().include("state", "id").exclude("*").serialize(experiments);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transformMetrics(List<MetricBuilder> metrics) throws BaseException {
        String result = getSerializer().serialize(metrics);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(MetricBuilder metric) throws BaseException {
        String result = getSerializer().include("configuration").serialize(metric);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
        public TemplateResult transformSplitters(List<DataSplitterBuilder> splitters) throws BaseException {
        String result = getSerializer().serialize(splitters);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    @Override
    public TemplateResult transform(DataSplitterBuilder splitter) throws BaseException {

        String result = getSerializer().include("configuration").serialize(splitter);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }
}
