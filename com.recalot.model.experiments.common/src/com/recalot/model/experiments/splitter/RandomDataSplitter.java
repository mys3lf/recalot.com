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
    @Override
    public DataSet[] split(DataSource source) throws BaseException {
        List<FillableDataSet> result = new ArrayList<>();
        // Create the empty lists
        for (int i = 0; i < this.getNbFolds(); i++) {
            result.add(new FillableDataSet());
        }

        Interaction[] allInteractions = source.getInteractions();

        //split interactions
        Random r = new Random();
        for (Interaction i : allInteractions) {
            int next = r.nextInt(this.getNbFolds());

            result.get(next).addInteraction(i);
        }

        User[] allUsers = source.getUsers();
        Item[] allItems = source.getItems();

        // but every data set gets all users and items
        for(User user: allUsers) {
            for(FillableDataSet dataSet : result) {
                dataSet.addUser(user);
            }
        }

        for(Item item: allItems) {
            for(FillableDataSet dataSet : result) {
                dataSet.addItem(item);
            }
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
}
