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

package com.recalot.model.experiments.splitter;

import com.recalot.common.communication.*;
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
public class RandomPercentageDataSplitter extends DataSplitter {
    private int seed = 1;
    private double percentage = 0.7;

    @Override
    public DataSet[] split(DataSource source) throws BaseException {
        List<FillableDataSet> result = new ArrayList<>();

        // Create empty lists
        result.add(new FillableDataSet()); //train
        result.add(new FillableDataSet()); //test

        Interaction[] allInteractions = source.getInteractions();

        //split interactions
        Random r = new Random(seed);
        for (Interaction i : allInteractions) {

            double next = r.nextDouble();

            if(next > percentage) {
                result.get(1).addInteraction(i); //add to test
            } else {
                result.get(0).addInteraction(i); //add to train
            }

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


    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }
}
