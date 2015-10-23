package com.recalot.common.interfaces.model.experiment;

import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.log.Loggable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 01.04.2015.
 */
public abstract class Experiment extends Loggable {

    protected HashMap<String, Map<String, Double>> result;
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

    public synchronized void addPercentage(double percentage) {
        this.percentage += percentage;
        if(this.percentage > 100) this.percentage = 100;
    }

    public synchronized void resetPercentage() {
        this.percentage = 0;
    }

    public HashMap<String, Map<String, Double>> getResults() {
        return result;
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

    public enum ExperimentState {
        RUNNING,
        FINISHED
    }
}
