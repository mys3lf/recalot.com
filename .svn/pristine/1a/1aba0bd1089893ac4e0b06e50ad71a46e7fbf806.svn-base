package com.recalot.common.impl.experiment;

import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.ListMetric;
import com.recalot.common.interfaces.model.experiment.Metric;
import com.recalot.common.interfaces.model.experiment.RatingMetric;
import com.recalot.common.interfaces.model.rec.Recommender;
import org.osgi.service.log.LogService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Experiment extends com.recalot.common.interfaces.model.experiment.Experiment {

    private final HashMap<String, Metric[]> metrics;
    private DataSet[] sets;

    public Experiment(String id, String dataSourceId, DataSet[] sets, Recommender[] recommenders, HashMap<String, Metric[]> metrics) {
        this.sets = sets;
        this.id = id;
        this.recommenders = recommenders;

        recommenderIds = new String[this.recommenders.length];
        for (int i = 0; i < recommenders.length; i++) {
            recommenderIds[i] = recommenders[i].getId();
        }

        this.dataSourceId = dataSourceId;

        this.metrics = metrics;
        this.result = new HashMap<>();
    }

    @Override
    public void run() {
        setState(ExperimentState.RUNNING);
        setPercentage(0);
        //do cross validation, train one set and test it with the users
        for (int i = 0; i < sets.length; i++) {
            for (Recommender r : recommenders) {
                try {
                    r.setDataSet(this.sets[i]);
                    r.train();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (this.logger != null) {
                        this.logger.log(LogService.LOG_ERROR, String.format("The training of recommender %s failed. Exception message: %s", r.getId(), e.getMessage()));
                    }
                }
            }

            //iterate over all sets
            for (int j = 0; j < sets.length; j++) {
                //check if the current set is not the trained set
                if (i != j) {
                    //iterate over all recommenders
                    for (Recommender r : recommenders) {
                        try {
                            User[] users = sets[j].getUsers();
                            //iterate over all users
                            for (User u : users) {
                                //get the interactions of the user in the test set
                                Interaction[] userInteractions = sets[j].getInteractions(u.getId());
                                //iterate over all metric for this recommender
                                for (Metric m : metrics.get(r.getId())) {

                                    if (m instanceof RatingMetric) {
                                        for (Interaction interaction : userInteractions) {
                                            Integer value = Integer.parseInt(interaction.getValue());
                                            Double predict = r.predict(interaction.getUserId(), interaction.getItemId());

                                            ((RatingMetric) m).addRating(value, predict);
                                        }
                                    } else if (m instanceof ListMetric) {
                                        ArrayList<String> interactions = new ArrayList();
                                        for (Interaction interaction : userInteractions) {
                                            interactions.add(interaction.getItemId());
                                        }

                                        ArrayList<String> result = new ArrayList();

                                        RecommendationResult rr = r.recommend(u.getId());

                                        for (RecommendedItem item : rr.getItems()) {
                                            result.add(item.getItemId());
                                        }

                                        ((ListMetric) m).addList(interactions, result);
                                    }
                                }
                            }
                        } catch (BaseException e) {
                            e.printStackTrace();
                        }

                        setPercentage(getPercentage() +  100.0 / (this.sets.length * (this.sets.length - 1) * this.recommenderIds.length));
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

        setState(ExperimentState.FINISHED);
    }
}
