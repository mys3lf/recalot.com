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
package com.recalot.common.interfaces.model.experiment;

import com.recalot.common.Helper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class OnlineExperiment {
    private String dataSourceId;
    private String id;
    private Map<String, Double> recommender;
    private Map<String, Double> bounds;
    private ExperimentState state;


    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Double> getRecommender() {
        return recommender;
    }

    public Map<String, Double> getRecommenderBounds() {
        if(bounds == null && recommender != null) {
            bounds = new LinkedHashMap<>();
            Double sum = 0.0;
            for(String recId: recommender.keySet()) {
                sum += recommender.get(recId);

                bounds.put(recId, sum);
            }

            bounds = Helper.sortByValueDescending(bounds);
        }

        return bounds;
    }

    public void setRecommender(Map<String, Double> recommender) {
        this.bounds = null;
        this.recommender = recommender;
    }

    public OnlineExperiment.ExperimentState getState() {
        return state;
    }

    public void setState(ExperimentState state) {
        this.state = state;
    }

    public enum ExperimentState {
        TRAINING,
        RUNNING
    }
}
