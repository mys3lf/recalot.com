package com.recalot.model.experiments.splitter;

import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configurable;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by matthaeus.schmedding on 16.04.2015.
 */
public class RandomDataSplitter extends DataSplitter {

    /**
     * A min-ratings per user constraint for the test set
     */
    private int minRatingsPerUser = -1;

    /**
     * A min-ratings per item constraint for the test set
     */
    private int minRatingsPerItem = -1;


    @Override
    public DataSet[] split(DataSource source) throws BaseException {
        List<FillableDataSet> result = new ArrayList<>();
        // Create the empty lists
        for (int i = 0; i < this.getNbFolds(); i++) {
            result.add(new FillableDataSet());
        }

        Interaction[] allInteractions = source.getInteractions();

        Random r = new Random();
        for (Interaction i : allInteractions) {
            int next = r.nextInt(this.getNbFolds());

            if (result.get(next).getUser(i.getUserId()) == null) {
                User user = source.getUser(i.getUserId());
                if (user != null) result.get(next).addUser(user);
            }

            if (result.get(next).getItem(i.getItemId()) == null) {
                Item item = source.getItem(i.getItemId());
                if (item != null) result.get(next).addItem(item);
            }

            result.get(next).addInteraction(i);
        }

        return result.toArray(new DataSet[result.size()]);
    }

    @Override
    public String getKey() {
        return "random";
    }

    @Override
    public String getId() {
        return "random";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    public int getMinRatingsPerItem() {
        return minRatingsPerItem;
    }

    public void setMinRatingsPerItem(int minRatingsPerItem) {
        this.minRatingsPerItem = minRatingsPerItem;
    }

    public int getMinRatingsPerUser() {
        return minRatingsPerUser;
    }

    public void setMinRatingsPerUser(int minRatingsPerUser) {
        this.minRatingsPerUser = minRatingsPerUser;
    }
}
