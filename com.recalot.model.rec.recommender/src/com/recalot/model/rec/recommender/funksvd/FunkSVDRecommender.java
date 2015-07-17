package com.recalot.model.rec.recommender.funksvd;

import java.util.*;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Context;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.model.rec.recommender.helper.RecommenderHelper;
import com.recalot.model.rec.recommender.funksvd.helper.*;


/**
 * Implements a baseline SVD recommender
 * http://sifter.org/~simon/journal/20061211.html
 * Adapted from previous Apache Mahout implementation (0.4)
 */
public class FunkSVDRecommender extends Recommender {

    // Default parameter settings
    int numFeatures = 50;
    int initialSteps = 50;

    private HashMap<String, Integer> userMap = null;
    private HashMap<String, Integer> itemMap = null;
    private HashMap<String, Integer> interactionMap = null;
    private GradientDescentSVD emSvd = null;
    private List<Interaction> cachedPreferences = null;

    public FunkSVDRecommender(){
        setKey("funksvd");
    }

    // SVD-Specific things here
    public void train(int steps) {
        for (int i = 0; i < steps; i++) {
            nextTrainStep();
        }
    }

    // =====================================================================================

    private void nextTrainStep() {
        Collections.shuffle(cachedPreferences, random);

        for (int i = 0; i < numFeatures; i++) {
            for (Interaction rating : cachedPreferences) {
                int useridx = userMap.get(rating.getUserId());
                int itemidx = itemMap.get(rating.getItemId());
                int interactionidx = interactionMap.get(rating.getId());
                // System.out.println("Training useridx: " + useridx + ", itemidx: " + // itemidx);
                emSvd.train(useridx, itemidx, i, interactionidx);
            }
        }
    }

    // =====================================================================================
    private void recachePreferences() {
        cachedPreferences.clear();
        try {
            for (User user : getDataSet().getUsers()) {
                for (Interaction rating : getDataSet().getInteractions(user.getId())) {
                    cachedPreferences.add(rating);
                }
            }
        } catch (BaseException e) {

            e.printStackTrace();
        }
    }


    // --------------------------------------
    private static final Random random = RandomUtils.getRandom();


    /**
     * Returns the user vector in the latent space
     *
     * @param u the user id
     * @return the array with the weights
     */
    public double[] getUserVector(int u) {

        Integer user = this.userMap.get(u);
        if (user == null) {
         //   System.err.println("Cannot find internal ID for " + u);
            System.exit(1);
            return null;
        } else {
            return this.emSvd.getLeftVector(user);
        }
    }


    @Override
    public void train() throws BaseException {

        int numUsers = getDataSet().getUsersCount();
        userMap = new HashMap<>(numUsers);
        int idx = 0;

        for (User user : getDataSet().getUsers()) {
            userMap.put(user.getId(), idx++);
        }

        int numItems = getDataSet().getItemsCount();
        itemMap = new HashMap<>(numItems);

        idx = 0;
        for (Item item : getDataSet().getItems()) {
            itemMap.put(item.getId(), idx++);
        }

        int numInteractions = getDataSet().getInteractionsCount();
        interactionMap = new HashMap<>(numInteractions);

        idx = 0;
        for (Item item : getDataSet().getItems()) {
            itemMap.put(item.getId(), idx++);
        }

        double average = RecommenderHelper.getGlobalRatingAverage(getDataSet());
        double defaultValue = Math.sqrt((average - 1.0) / numFeatures);

        emSvd = new GradientDescentSVD(numUsers, numItems, numFeatures, defaultValue);
        cachedPreferences = new ArrayList<>(numUsers);
        recachePreferences();

        train(initialSteps);
    }


    @Override
    public RecommendationResult recommend(String userId, Context context, Map<String, String> param) {
        List<RecommendedItem> items =  new ArrayList<>();
        try {
            List<String> rec = recommendItemsByRatingPrediction(userId);

            for(String key: rec){
                items.add(new RecommendedItem(key, 0.0));
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        items =  Helper.applySubList(items, param, 10);

        return new RecommendationResult(getId(), items);
    }

    @Override
    public Double predict(String userId, String itemId, Context context, Map<String, String> param) {
        // return 0;
        Integer useridx = userMap.get(userId);
        Integer itemidx = itemMap.get(itemId);

        //return (float) emSvd.getDotProduct(useridx, itemidx);

        if (useridx != null) {
            return emSvd.getDotProduct(useridx, itemidx);
        } else {
            // This might happen during training test splits for super-sparse (test) data
            return Double.NaN;
        }
    }



    // =====================================================================================

    /**
     * Setter for factory
     *
     * @param n
     */
    public void setNumFeatures(String n) {
        this.numFeatures = Integer.parseInt(n);
    }

    // =====================================================================================

    /**
     * Setter for the initial steps
     *
     * @param n
     */
    public void setInitialSteps(String n) {
        this.initialSteps = Integer.parseInt(n);
    }

}
