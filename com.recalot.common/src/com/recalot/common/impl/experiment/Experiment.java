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

package com.recalot.common.impl.experiment;

import com.recalot.common.Helper;
import com.recalot.common.Parallel;
import com.recalot.common.communication.*;
import com.recalot.common.context.Context;
import com.recalot.common.context.UserContext;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.ListMetric;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.RatingMetric;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;
import org.osgi.service.log.LogService;

import java.util.*;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Experiment extends com.recalot.common.interfaces.model.experiment.Experiment {

    private final HashMap<String, Metric[]> metrics;
    private final DataSource dataSource;
    private final DataSplitter splitter;

    private final boolean runThroughAllItems;

    private ContextProvider context;
    private Map<String, String> param;
    private int maxRelevantItemCount = Integer.MAX_VALUE;

    public Experiment(String id, DataSource source, DataSplitter splitter, Recommender[] recommenders, HashMap<String, Metric[]> metrics, ContextProvider context, Map<String, String> param) {
        this.context = context;
        this.param = param;

        this.id = id;
        this.recommenders = recommenders;

        recommenderIds = new String[this.recommenders.length];
        for (int i = 0; i < recommenders.length; i++) {
            recommenderIds[i] = recommenders[i].getId();
        }

        this.dataSourceId = source.getId();
        this.dataSource = source;
        this.splitter = splitter;
        this.metrics = metrics;
        this.result = new HashMap<>();

        this.runThroughAllItems = param != null && param.containsKey("runThroughAllItems") && param.get("runThroughAllItems").equals("true");

        if (param != null && param.containsKey("maxRelevantItemCount")) {
            try {
                Integer result = Integer.parseInt(param.get("maxRelevantItemCount"));
                if (result != null && result >= 1) {
                    this.maxRelevantItemCount = result;
                }
            } catch (NumberFormatException e) {

            }
        }
    }

    @Override
    public void run() {
        setState(ExperimentState.RUNNING);

        resetPercentage();
        setInfo("Split data source");

        //TODO add total time of experiment
        try {

            DataSet[] sets = splitter.split(dataSource);

            if (!param.containsKey(Helper.Keys.SplitType) || param.get(Helper.Keys.SplitType).equals("simple")) {
                //check test data set against train data set

                setInfo("Started training recommenders with first split");

                trainRecommenders(sets[0]);
                addPercentage(50);
                setInfo("Started testing of the recommenders with second split");
                performTest(recommenders, sets[1], 1.0 * 50 / recommenders.length);

            } else if (param.get(Helper.Keys.SplitType).equals("n-fold")) {

                // steps for completion:
                // train each recommender with each data set
                // test each trained recommender with the data that is not in the train data set
                double percentageSteps = 100.00 / (recommenders.length * sets.length * 2);

                //do cross validation, train one set and test it with the users
                for (int i = 0; i < sets.length; i++) {
                    setInfo(String.format("Started training recommenders with split %s", i));

                    DataSet trainDataSet = FillableDataSet.createDataSet(dataSource.getDataSet(), sets[i].getInteractions());

                    trainRecommenders(trainDataSet);

                    addPercentage(recommenders.length * percentageSteps);

                    performTest(recommenders, sets[i], percentageSteps);
                }
            }
        } catch (BaseException e) {
            setInfo(e.getMessage());
        }

        setInfo("Done");
        setState(ExperimentState.FINISHED);
    }

    private void performTest(Recommender[] recommenders, DataSet test, double percentage) {
        //iterate over all recommenders
        for (Recommender r : recommenders) {

            setInfo(String.format("Evaluate trained recommender %s", r.getId()));

            try {
                User[] users = test.getUsers();
                //iterate over all users
                // make it parallel


                boolean runOverAllItems = false;

                for (Metric m : metrics.get(r.getId())) {
                    if (m instanceof RatingMetric) {
                        runOverAllItems = true;
                    }
                }

                if (!runOverAllItems) {
                    runOverAllItems = runThroughAllItems;
                }

                final boolean allItems = runOverAllItems;


                double percentagePerUser = percentage / users.length;
                Parallel.For(Arrays.asList(users),
                        u -> {

                            ArrayList<Interaction> userInteractions = new ArrayList<>();

                            //get the interactions of the user in the test set
                            try {
                                Interaction[] interactions = test.getInteractions(u.getId());

                                userInteractions = new ArrayList<>(Arrays.asList(interactions));

                                Collections.sort(userInteractions, (a2, a1) -> a2.getTimeStamp().compareTo(a1.getTimeStamp()));

                            } catch (BaseException e) {
                                e.printStackTrace();
                                this.logger.log(LogService.LOG_ERROR, e.getMessage());
                            }

                            //run over all items
                            if (allItems) {
                                for (int i = 0; i < userInteractions.size() - 1; i++) {

                                    //TODO add relevant item filter
                                    // if(userInteractions.get(i).getItemId().length() <= 2) continue;

                                    ArrayList<String> previous = new ArrayList();
                                    ArrayList<String> subsequent = new ArrayList();

                                    for (int j = 0; j < userInteractions.size(); j++) {
                                        if (i > j) {
                                            previous.add(userInteractions.get(j).getItemId());
                                        } else {
                                            subsequent.add(userInteractions.get(j).getItemId());
                                        }
                                    }

                                    //fill context
                                    for (Context c : context.getAll()) {
                                        if (c instanceof UserContext) {
                                            ((UserContext) c).processContext(this.getId() + ":" + this.getDataSourceId(), u.getId(), previous, Helper.Keys.Context.LastConsumed);

                                            if (subsequent.size() > 0) {
                                                try {
                                                    ((UserContext) c).processContext(this.getId() + ":" + this.getDataSourceId(), u.getId(), test.getItem(subsequent.get(0)), Helper.Keys.Context.Item);
                                                } catch (BaseException e) {
                                                    e.printStackTrace();
                                                    this.logger.log(LogService.LOG_ERROR, e.getMessage());
                                                }
                                            }
                                        }
                                    }

                                    //iterate over all metric for this recommender
                                    for (Metric m : metrics.get(r.getId())) {
                                        //if the metric is a rating metric, run through all consumed items in the test set and predict the rating
                                        if (m instanceof RatingMetric) {

                                            Integer value = Integer.parseInt(userInteractions.get(i).getValue());

                                            try {
                                                Double predict = r.predict(userInteractions.get(i).getUserId(), userInteractions.get(i).getItemId(), context);

                                                if (!predict.isNaN()) {
                                                    ((RatingMetric) m).addRating(value, predict);
                                                }
                                            } catch (Exception e) {
                                                this.logger.log(LogService.LOG_ERROR, e.getMessage());
                                            }

                                        } else if (m instanceof ListMetric) {
                                            // There are 2 possibilities for list metrics.
                                            // 1. Run through all items and use the subsequent items as relevant items.
                                            // 2. Use all consumed items as relevant items.
                                            // The relevant items can be limited in both cases.
                                            // This is the first case
                                            try {

                                                ArrayList<String> result1 = new ArrayList();

                                                RecommendationResult rr = r.recommend(u.getId(), context);

                                                for (RecommendedItem item : rr.getItems()) {
                                                    result1.add(item.getItemId());
                                                }

                                                List<String> relevant = Helper.applySubList(subsequent, maxRelevantItemCount);
                                                ((ListMetric) m).addList(relevant, result1);

                                            } catch (Exception e) {
                                                this.logger.log(LogService.LOG_ERROR, e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } else if (!runThroughAllItems) {

                                //iterate over all metric for this recommender
                                for (Metric m : metrics.get(r.getId())) {
                                    if (m instanceof ListMetric) {
                                        // 2. possibility for list metrics.
                                        // Use all consumed items as relevant items.
                                        // The relevant items can be limited in both cases.

                                        try {
                                            ArrayList<String> interactions = new ArrayList();
                                            for (Interaction interaction : userInteractions) {
                                                interactions.add(interaction.getItemId());
                                            }

                                            ArrayList<String> result1 = new ArrayList();

                                            RecommendationResult rr = r.recommend(u.getId(), context);

                                            for (RecommendedItem item : rr.getItems()) {
                                                result1.add(item.getItemId());
                                            }

                                            ((ListMetric) m).addList(Helper.applySubList(interactions, maxRelevantItemCount), result1);

                                        } catch (Exception e) {
                                            this.logger.log(LogService.LOG_ERROR, e.getMessage());
                                        }

                                    }
                                }
                            }

                            addPercentage(percentagePerUser);
                        });
            } catch (BaseException e) {
                e.printStackTrace();

                setInfo(e.getMessage());
            }

            //copy results to output
            for (String key : metrics.keySet()) {
                Metric[] ms = metrics.get(key);
                HashMap<String, Double> res = new HashMap<>();
                for (Metric m : ms) {
                    res.put(m.getId(), m.getResult());
                }

                this.result.put(key, res);
            }
        }
    }

    private void trainRecommenders(DataSet train) {

        Parallel.For(Arrays.asList(recommenders), r -> {
            try {
                r.setDataSourceId(this.getId() + ":" + this.getDataSourceId());
                r.setDataSet(train);

                r.train();
            } catch (Exception e) {
                e.printStackTrace();
                if (this.logger != null) {
                    this.logger.log(LogService.LOG_ERROR, String.format("The training of recommender %s failed. Exception message: %s", r.getId(), e.getMessage()));
                    setInfo(String.format("The training of recommender %s failed. Exception message: %s", r.getId(), e.getMessage()));
                }
            }
        });
    }
}
