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
package com.recalot.common.impl.experiment;

import java.io.Serializable;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class StorageExperiment extends com.recalot.common.interfaces.model.experiment.Experiment implements Serializable {

    public StorageExperiment(Experiment exp) {
        this.recommenderIds = exp.getRecommenderIds();

        this.dataSourceId = exp.getDataSourceId();
        this.id = exp.getId();
        this.result = exp.getResults();
        this.info = exp.getInfo();
        this.state = exp.getState();
        this.params = exp.getParams();
    }

    public StorageExperiment(com.recalot.common.interfaces.model.experiment.Experiment exp) {
        this.recommenderIds = exp.getRecommenderIds();

        this.dataSourceId = exp.getDataSourceId();
        this.id = exp.getId();
        this.result = exp.getResults();
        this.info = exp.getInfo();
        this.state = exp.getState();
        this.params = exp.getParams();
    }


    @Override
    public void run() {
        //nothing to do
    }
}
