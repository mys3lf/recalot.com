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

import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.log.Loggable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 01.04.2015.
 */
public abstract class Experiment extends Loggable implements Serializable{

    protected HashMap<String, Map<String, Double>> result;
    protected Map<String, String> params;
    protected String dataSourceId;
    protected String[] recommenderIds;
    protected ExperimentState state;
    protected String id;
    protected String info;
    protected double percentage;
    protected Recommender[] recommenders;
    protected Map<String, String> config;

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double p) {
        percentage = p;
    }

    public synchronized void addPercentage(double percentage) {
        this.percentage += percentage;
        if (this.percentage > 100) this.percentage = 100;
    }

    public synchronized void resetPercentage() {
        this.percentage = 0;
    }

    public HashMap<String, Map<String, Double>> getResults() {
        return result;
    }

    public Map<String,String> getParams() {
        return params;
    }


    public String getDataSourceId() {
        return dataSourceId;
    }

    public String[] getRecommenderIds() {
        return recommenderIds;
    }

    public ExperimentState getState() {
        return state;
    }

    public void setState(ExperimentState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public abstract void run();

    public void setId(String id) {
        this.id = id;
    }

    public enum ExperimentState {
        WAITING,
        RUNNING,
        FINISHED
    }
}
