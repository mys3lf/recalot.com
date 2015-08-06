package com.recalot.common.impl.experiment;

import com.recalot.common.Parallel;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;
import com.recalot.common.interfaces.model.experiment.ListMetric;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.RatingMetric;
import com.recalot.common.interfaces.model.rec.Recommender;
import org.osgi.service.log.LogService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Experiment extends com.recalot.common.interfaces.model.experiment.Experiment {

    private final HashMap<String, Metric[]> metrics;
    private final DataSource dataSource;
    private final DataSplitter splitter;

    public Experiment(String id, DataSource source, DataSplitter splitter, Recommender[] recommenders, HashMap<String, Metric[]> metrics) {

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
    }

    @Override
    public void run() {
        setState(ExperimentState.RUNNING);
        setPercentage(0);
        setInfo("Split data source");
//TODO add total time of experiment
        try {
            DataSet[] sets = splitter.split(dataSource);

            //do cross validation, train one set and test it with the users
            for (int i = 0; i < sets.length; i++) {
//TODO: make this parallel
                for (Recommender r : recommenders) {
                    try {
                        r.setDataSet(sets[i]);
                        setInfo(String.format("Train recommender %s with split %s", r.getId(), i));
                        r.train();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (this.logger != null) {
                            this.logger.log(LogService.LOG_ERROR, String.format("The training of recommender %s failed. Exception message: %s", r.getId(), e.getMessage()));
                            setInfo(String.format("The training of recommender %s failed. Exception message: %s", r.getId(), e.getMessage()));
                        }
                    }
                }

                //iterate over all sets
                for (int j = 0; j < sets.length; j++) {
                    //check if the current set is not the trained set
                    if (i != j) {
                        //iterate over all recommenders
                        for (Recommender r : recommenders) {

                            setInfo(String.format("Evaluate recommender %s trained with split %s against split %s", r.getId(), i, j));

                            try {
                                User[] users = sets[j].getUsers();

                                //iterate over all users

                                final int finalJ = j;
                                // make it parallel
                                Parallel.For(Arrays.asList(users),
                                        u -> {
                                            try {//get the interactions of the user in the test set
                                                Interaction[] userInteractions = new Interaction[0];

                                                userInteractions = sets[finalJ].getInteractions(u.getId());

                                                //iterate over all metric for this recommender
                                                for (Metric m : metrics.get(r.getId())) {

                                                    if (m instanceof RatingMetric) {
                                                        for (Interaction interaction : userInteractions) {
                                                            Integer value = Integer.parseInt(interaction.getValue());
                                                            Double predict = r.predict(interaction.getUserId(), interaction.getItemId());
                                                            if(!predict.isNaN()) {
                                                                ((RatingMetric) m).addRating(value, predict);
                                                            }
                                                        }
                                                    } else if (m instanceof ListMetric) {
                                                        ArrayList<String> interactions = new ArrayList();
                                                        for (Interaction interaction : userInteractions) {
                                                            interactions.add(interaction.getItemId());
                                                        }

                                                        ArrayList<String> result1 = new ArrayList();

                                                        RecommendationResult rr = r.recommend(u.getId());

                                                        for (RecommendedItem item : rr.getItems()) {
                                                            result1.add(item.getItemId());
                                                        }

                                                        ((ListMetric) m).addList(interactions, result1);
                                                    }
                                                }
                                            } catch (BaseException e) {
                                                e.printStackTrace();
                                            }
                                        });

                            } catch (BaseException e) {
                                e.printStackTrace();

                                setInfo(e.getMessage());
                            }

                            setPercentage(getPercentage() + 100.0 / (sets.length * (sets.length - 1) * this.recommenderIds.length));
                        }
                    }
                }
            }

            for (String key : metrics.keySet()) {
                Metric[] ms = metrics.get(key);
                HashMap<String, Double> r = new HashMap<>();
                for (Metric m : ms) {
                    r.put(m.getId(), m.getResult());
                }

                this.result.put(key, r);
            }
        } catch (BaseException e) {
            setInfo(e.getMessage());
        }

        setInfo("Done");
        setState(ExperimentState.FINISHED);
    }
}
