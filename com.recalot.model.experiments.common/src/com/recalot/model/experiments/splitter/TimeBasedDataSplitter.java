package com.recalot.model.experiments.splitter;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.model.experiment.DataSplitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class splits the dataset into 2 parts according to the given percentage
 * and sorted using the timestamp in the interaction data .
 *
 * Created by matthaeus.schmedding on 16.04.2015.
 */
public class TimeBasedDataSplitter extends DataSplitter {

    private double testPercentage = 0.3;

    @Override
    public DataSet[] split(DataSource source) throws BaseException {
        FillableDataSet[] result = new FillableDataSet[2];
        result[0] = new FillableDataSet(); // train
        result[1] = new FillableDataSet(); // test

        //add all items to both splits
        for(Item item : source.getItems()){
            result[0].addItem(item);
            result[1].addItem(item);
        }

        for(User user : source.getUsers()){
            //add all users to both splits
            result[0].addUser(user);
            result[1].addUser(user);

            Interaction[] userInteractions = source.getInteractions(user.getId());

            // create a map for the user
            Map<Interaction, Long> userRatingsWithTimeStamp = new HashMap<Interaction, Long>();
            for (Interaction r : userInteractions) {
                userRatingsWithTimeStamp.put(r, r.getTimeStamp().getTime());
            }

            // Sort in descending order or timestamp
            Map<Interaction, Long> sortedInteractions = Helper.sortByValueDescending(userRatingsWithTimeStamp);

            int counter = 1;

            double testInteractionCount = userInteractions.length * testPercentage;

            //add interactions according to the given percentage
            for (Interaction r : sortedInteractions.keySet()) {
                if (counter <= testInteractionCount) {
                    // The newest go to the testset
                    result[1].addInteraction(r);
                }
                else {
                    result[0].addInteraction(r);
                }
                counter++;
            }
        }

        return result;
    }

    @Override
    public String getKey() {
        return "time";
    }

    @Override
    public String getId() {
        return "time";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    public double getTestPercentage() {
        return testPercentage;
    }

    public void setTestPercentage(double testPercentage) {
        this.testPercentage = testPercentage;
    }
}
