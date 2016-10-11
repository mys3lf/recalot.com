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

package com.recalot.common.interfaces.template;

import com.recalot.common.builder.DataSplitterBuilder;
import com.recalot.common.builder.MetricBuilder;
import com.recalot.common.communication.TemplateResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.*;

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
    public TemplateResult transform(OnlineExperiment onlineExperiment);
    public TemplateResult transformOnlineExperiments(List<OnlineExperiment> experiments);
}
