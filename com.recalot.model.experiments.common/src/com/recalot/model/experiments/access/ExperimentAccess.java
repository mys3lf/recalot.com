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

package com.recalot.model.experiments.access;

import com.recalot.common.GenericServiceListener;
import com.recalot.common.Helper;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.context.Context;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.*;
import com.recalot.common.communication.Message;
import com.recalot.common.interfaces.model.data.DataAccess;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.Experiment;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.experiment.OnlineExperiment;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.model.rec.RecommenderAccess;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by matthaeus.schmedding on 16.04.2015.
 */
public class ExperimentAccess implements com.recalot.common.interfaces.model.experiment.ExperimentAccess {

    private final BundleContext context;

    private final ConcurrentHashMap<String, Experiment> experiments;
    private final ConcurrentHashMap<String, OnlineExperiment> onlineExperiments;
    private final ConcurrentHashMap<String, Thread> threads;
    private final GenericServiceListener<RecommenderBuilder> recommenderListener;
    private final GenericServiceListener<RecommenderAccess> recommenderAccess;
    private final Random random;
    private ConcurrentLinkedQueue<Experiment> queue;

    public ExperimentAccess(BundleContext context) {
        this.context = context;
        this.recommenderListener = new GenericServiceListener<>(context, RecommenderBuilder.class.getName());
        this.recommenderAccess = new GenericServiceListener<>(context, RecommenderAccess.class.getName());
        this.experiments = new ConcurrentHashMap<>();
        this.threads = new ConcurrentHashMap<>();
        this.queue = new ConcurrentLinkedQueue<>();
        this.onlineExperiments = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    @Override
    public Experiment getExperiment(String id) throws BaseException {
        // Lock list and return data source object.
        synchronized (experiments) {
            if (experiments.containsKey(id)) {
                return experiments.get(id);
            }
        }

        throw new NotFoundException("Experiment with id %s not found.", id);
    }

    @Override
    public Message deleteExperiment(String id) throws BaseException {
        // Lock list and add data source object.
        synchronized (experiments) {
            if (experiments.containsKey(id)) {
                experiments.remove(id);

                if (threads.containsKey(id)) {
                    threads.get(id).interrupt();
                    threads.remove(id);
                }
                return new Message("Delete successful", String.format("Experiment with id %s successful deleted.", id), Message.Status.INFO);
            }
        }

        throw new NotFoundException("Experiment with id %s not found.", id);
    }

    @Override
    public boolean containsExperiment(String experimentId) {
        synchronized (experiments) {
            return experiments.containsKey(experimentId);
        }
    }

    @Override
    public List<Experiment> getExperiments() throws BaseException {
        return new ArrayList<>(experiments.values());
    }

    @Override
    public OnlineExperiment createOnlineExperiment(DataSource dataSource, Map<String, String> param) throws BaseException {
        String experimentId = param.get(Helper.Keys.ExperimentId);
        String recommenderIds = param.get(Helper.Keys.RecommenderId);


        OnlineExperiment experiment = new OnlineExperiment();
        experiment.setDataSourceId(dataSource.getSourceId());
        experiment.setId(experimentId);

        Map<String, String> keyIds = Helper.splitIdKeyConfig(recommenderIds);

        //check if the percentage configuration is available
        for (String key : keyIds.keySet()) {
            if (!param.containsKey(key + "." + Helper.Keys.Percentage))
                throw new MissingArgumentException("The argument %s is missing. ", key + "." + Helper.Keys.Percentage);

            boolean nanOrWrongFormat;
            try {
                Double number = Double.parseDouble(param.get(key + "." + Helper.Keys.Percentage));

                nanOrWrongFormat = number.isNaN();

                if (!number.isNaN() && number <= 0.0) {
                    nanOrWrongFormat = false;
                }
            } catch (NumberFormatException e) {
                nanOrWrongFormat = true;
            }

            if (nanOrWrongFormat) {
                throw new WrongFormatException("The value %s for the key %s should be a double and greater than 0.", param.get(keyIds.get(key) + "." + Helper.Keys.Percentage), keyIds.get(key) + "." + Helper.Keys.Percentage);
            }
        }

        HashMap<String, Double> recommender = new HashMap<>();

        //initialize the recommenders
        for (String key : keyIds.keySet()) {
            if (recommenderIds != null) {
                Map<String, String> recommenderKeyIds = Helper.splitIdKeyConfig(recommenderIds);

                for (String recKey : recommenderKeyIds.keySet()) {
                    param.put(recommenderKeyIds.get(recKey) + "." + Helper.Keys.ID, recommenderKeyIds.get(recKey));
                    param.put(recommenderKeyIds.get(recKey) + "." + Helper.Keys.SourceId, param.get(Helper.Keys.SourceId));


                    Double number = Double.parseDouble(param.get(key + "." + Helper.Keys.Percentage));

                    Recommender rec = recommenderAccess.getFirstInstance().createRecommender(dataSource, recommenderKeyIds.get(recKey), param);
                    recommender.put(rec.getId(), number);
                }
            }
        }

        //normalize the recommender percentage
        Double sum = 0.0;
        for (String recId : recommender.keySet()) {
            sum += recommender.get(recId);
        }

        for (String recId : recommender.keySet()) {
            recommender.put(recId, recommender.get(recId) / sum);
        }

        experiment.setRecommender(recommender);

        return experiment;
    }

    @Override
    public Experiment createExperiment(Recommender[] recommender, DataSource dataSource, DataSplitter splitter, HashMap<String, Metric[]> metrics, ContextProvider context, Map<String, String> param) throws BaseException {

        String id = param.get(Helper.Keys.ExperimentId);
        if (id == null || id.isEmpty()) id = UUID.randomUUID().toString();

        if (threads.containsKey(id) || experiments.containsKey(id))
            throw new AlreadyExistsException("An experiment with the id %s already exists. Please first delete the experiment.", id);
        if (param.get(Helper.Keys.MetricIDs) == null)
            throw new MissingArgumentException("The argument %s is missing.", Helper.Keys.MetricIDs);

        Experiment experiment = new com.recalot.common.impl.experiment.Experiment(id, dataSource, splitter, recommender, metrics, context, param);


        //add to queue
        if (threads.size() > 0) {
            queue.add(experiment);
        } else { // run
            run(experiment);
        }

        experiments.put(experiment.getId(), experiment);

        return experiment;
    }

    private void runNext() {
        if (queue.size() > 0) {
            Experiment experiment = queue.poll();
            if (experiment != null) {
                run(experiment);
            }
        }
    }

    private void run(Experiment experiment) {
        if (experiment != null) {
            Thread thread = new Thread() {
                public void run() {

                    experiment.run();

                    threads.remove(experiment.getId());

                    runNext();
                }
            };

            threads.put(experiment.getId(), thread);
            thread.start();
        }
    }


    @Override
    public OnlineExperiment getOnlineExperiment(String id) throws BaseException {
        // Lock list and return data source object.
        synchronized (onlineExperiments) {
            if (onlineExperiments.containsKey(id)) {
                return onlineExperiments.get(id);
            }
        }

        throw new NotFoundException("Online experiment with id %s not found.", id);
    }

    @Override
    public String getNextRecommenderForOnlineExperiment(String experimentId, Map<String, String> param) throws BaseException {
        OnlineExperiment experiment = getOnlineExperiment(experimentId);

        Double r = random.nextDouble();
        String recommenderId = null;
        Map<String, Double> bounds =      experiment.getRecommenderBounds();
        for(String recId : bounds.keySet()) {
            if(r < bounds.get(recId)){
                recommenderId = recId;
                break;
            }
        }

        if(recommenderId != null && !recommenderId.isEmpty()) {
            return recommenderId;
        }

        throw new NotFoundException("Online experiment with id %s not found.", experimentId);
    }

    @Override
    public Message deleteOnlineExperiment(String id) throws BaseException {
        // Lock list and add data source object.
        synchronized (onlineExperiments) {
            if (onlineExperiments.containsKey(id)) {
                onlineExperiments.remove(id);

                return new Message("Delete successful", String.format("Online experiment with id %s successful deleted.", id), Message.Status.INFO);
            }
        }

        throw new NotFoundException("Online experiment with id %s not found.", id);
    }

    @Override
    public boolean containsOnlineExperiment(String experimentId) {
        synchronized (onlineExperiments) {
            return onlineExperiments.containsKey(experimentId);
        }
    }

    @Override
    public List<OnlineExperiment> getOnlineExperiments() throws BaseException {
        return new ArrayList<>(onlineExperiments.values());
    }


    @Override
    public String getKey() {
        return "experiment-access";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
