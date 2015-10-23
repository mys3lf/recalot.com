// Copyright (C) 2015 Matthäus Schmedding
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

package com.recalot.common.interfaces.model.experiment;

import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.communication.Service;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author matthaeus.schmedding
 */
public interface ExperimentAccess extends Service {
    public Experiment getExperiment(String id) throws BaseException;
    public Message deleteExperiment(String id)  throws BaseException;
    public List<Experiment> getExperiments()  throws BaseException;
    public Experiment createExperiment(Recommender[] recommender, DataSource source, DataSplitter splitter, HashMap<String, Metric[]> metrics, ContextProvider context, Map<String, String> param) throws BaseException;
}
