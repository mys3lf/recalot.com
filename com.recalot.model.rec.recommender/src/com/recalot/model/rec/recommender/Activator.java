package com.recalot.model.rec.recommender;


import com.recalot.common.builder.Initiator;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.rec.recommender.mostpopular.MostPopularRecommender;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;


/**
 * Register the recommender builder services. This class is called automatically when the bundle is activated.
 *
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, Initiator {


    private List<RecommenderBuilder> recommenders;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        recommenders = new ArrayList<>();

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, MostPopularRecommender.class.getName(), "mp", "");
       //     builder.setConfiguration(new ConfigurationItem("topN", ConfigurationItem.ConfigurationItemType.Integer, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.wallpaper.mostpopular.MostPopularRecommender.class.getName(), "wallpaper-mp", "");
        //    builder.setConfiguration(new ConfigurationItem("topN", ConfigurationItem.ConfigurationItemType.Integer, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.wallpaper.survey.SurveyRecommender.class.getName(), "wallpaper-survey", "");
           // builder.setConfiguration(new ConfigurationItem("topN", ConfigurationItem.ConfigurationItemType.Integer, "", ConfigurationItem.ConfigurationItemRequirementType.Required));
            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.knn.UserBasedCosineNearestNeighborsRecommender.class.getName(), "cosine-user-knn", "");
            builder.setConfiguration(new ConfigurationItem("minOverlap", ConfigurationItem.ConfigurationItemType.Integer, "3", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("maxNeighbors", ConfigurationItem.ConfigurationItemType.Integer, "10", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("minSimilarity", ConfigurationItem.ConfigurationItemType.Integer, "0.0", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.funksvd.FunkSVDRecommender.class.getName(), "funk-svd", "");
            builder.setConfiguration(new ConfigurationItem("numFeatures", ConfigurationItem.ConfigurationItemType.Integer, "50", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("initialSteps", ConfigurationItem.ConfigurationItemType.Integer, "50", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.bprmf.BPRMFRecommender.class.getName(), "bprmf", "");
            builder.setConfiguration(new ConfigurationItem("uniformUserSampling", ConfigurationItem.ConfigurationItemType.Boolean, "true", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("biasReg", ConfigurationItem.ConfigurationItemType.Double, "0", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("numFeatures", ConfigurationItem.ConfigurationItemType.Integer, "100", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("initialSteps", ConfigurationItem.ConfigurationItemType.Integer, "100", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("learnRate", ConfigurationItem.ConfigurationItemType.Double, "0.05", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("regU", ConfigurationItem.ConfigurationItemType.Double, "0.0025", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("regI", ConfigurationItem.ConfigurationItemType.Double, "0.0025", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("regJ", ConfigurationItem.ConfigurationItemType.Double, "0.00025", ConfigurationItem.ConfigurationItemRequirementType.Optional));
            builder.setConfiguration(new ConfigurationItem("updateJ", ConfigurationItem.ConfigurationItemType.Boolean, "true", ConfigurationItem.ConfigurationItemRequirementType.Optional));

            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.experiments.GlobalAverageRatingRecommender.class.getName(), "global-average-rating", "This recommender is used for comparison in experiments and provides a global average as prediction.");

            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }
        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.experiments.GlobalItemAverageRatingRecommender.class.getName(), "item-average-rating", "This recommender is used for comparison in experiments and provides a item average as prediction.");

            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }
        try {
            RecommenderBuilder builder = new RecommenderBuilder(this, com.recalot.model.rec.recommender.experiments.GlobalUserAverageRatingRecommender.class.getName(), "user-average-rating", "This recommender is used for comparison in experiments and provides a user average as prediction.");

            recommenders.add(builder);
        } catch (BaseException e) {
            e.printStackTrace();
        }


        for (RecommenderBuilder c : recommenders) {
            context.registerService(RecommenderBuilder.class.getName(), c, null);
        }
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    public void stop(BundleContext context) throws Exception {
        if (recommenders != null) {
            for (RecommenderBuilder c : recommenders) {
                c.close();
            }
            recommenders = null;
        }
    }

    @Override
    public Object createInstance(String className) {
        try {
            Class c = Class.forName(className);
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}