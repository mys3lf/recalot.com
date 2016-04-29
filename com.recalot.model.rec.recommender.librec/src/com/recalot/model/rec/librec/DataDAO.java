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

package com.recalot.model.rec.librec;

import com.google.common.collect.*;
import com.recalot.common.communication.*;
import librec.data.SparseMatrix;
import librec.data.SparseTensor;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class DataDAO {

    // store rate data as {user, item, rate} matrix
    private SparseMatrix rateMatrix;
    // store time data as {user, item, timestamp} matrix
    private SparseMatrix timeMatrix;

    // store time data as {user1, user1, relationship} matrix
    private SparseMatrix relationshipMatrix;
    // store rate data as a sparse tensor
    private SparseTensor rateTensor;

    // data scales
    private List<Double> ratingScale;
    // scale distribution
    private Multiset<Double> scaleDist;

    // number of rates
    private int numRatings;

    // user/item {raw id, inner id} map
    private BiMap<Integer, Integer> userIds, itemIds;

    // inverse views of userIds, itemIds
    private BiMap<Integer, Integer> idUsers, idItems;

    // minimum/maximum rating timestamp
    private long minTimestamp, maxTimestamp;
    private DataSet dataSet;

    /**
     * Constructor for a data DAO object
     */
    public DataDAO() {
        this.userIds = HashBiMap.create();

        this.itemIds = HashBiMap.create();
        scaleDist = HashMultiset.create();
    }


    /**
     * Read data from the data file. Note that we didn't take care of the duplicated lines.
     *
     * @return a sparse matrix storing all the relevant data
     */
    public SparseMatrix[] readData(String... relationshipType) throws Exception {

        // Table {row-id, col-id, rate}
        Table<Integer, Integer, Double> dataTable = HashBasedTable.create();
        // Table {row-id, col-id, timestamp}
        Table<Integer, Integer, Long> timeTable = null;
        // Table {row-id, col-id, relationship}
        Table<Integer, Integer, Double> relationshipTable = null;
        // Map {col-id, multiple row-id}: used to fast build a rating matrix
        Multimap<Integer, Integer> rColMap = null;
        // Map {col-id, multiple row-id}: used to fast build a rating matrix
        Multimap<Integer, Integer> colMap = HashMultimap.create();

        minTimestamp = Long.MAX_VALUE;
        maxTimestamp = Long.MIN_VALUE;

        //read user ids
        for (User user : dataSet.getUsers()){
            // inner id starting from 0
            int row = userIds.containsKey(InnerIds.getId(user.getId())) ? userIds.get(InnerIds.getId(user.getId())) : userIds.size();
            userIds.put(InnerIds.getId(user.getId()), row);
        }

        //read item ids
        for (Item item : dataSet.getItems()){
            // inner id starting from 0
            int col = itemIds.containsKey(InnerIds.getId(item.getId())) ? itemIds.get(InnerIds.getId(item.getId())) : itemIds.size();
            itemIds.put(InnerIds.getId(item.getId()), col);
        }


        if(dataSet.getRelationCount() > 0 ) {

            relationshipTable = HashBasedTable.create();

            rColMap = HashMultimap.create();
            //read social information
            for (Relation relation: dataSet.getRelations()){
                if(relationshipType.length == 0 || relation.getType().toLowerCase().equals(relationshipType[0])) {

                    // inner id starting from 0
                    if(!userIds.containsKey(InnerIds.getId(relation.getFromId()))) {
                        userIds.put(InnerIds.getId(relation.getFromId()), userIds.size());
                    }

                    // inner id starting from 0
                    if(!userIds.containsKey(InnerIds.getId(relation.getToId()))) {
                        userIds.put(InnerIds.getId(relation.getToId()), userIds.size());
                    }

                    int row = userIds.get(InnerIds.getId(relation.getToId()));
                    int col = userIds.get(InnerIds.getId(relation.getToId()));

                    relationshipTable.put(row, col, 1.0);
                    rColMap.put(col, row);
                }
            }
        }

        //read interactions
        for (Interaction interaction : dataSet.getInteractions()){

            String itemId = interaction.getItemId();
            String userId = interaction.getUserId();
            String rating = interaction.getValue();


            int row = userIds.containsKey(InnerIds.getId(userId)) ? userIds.get(InnerIds.getId(userId)) : userIds.size();
            userIds.put(InnerIds.getId(userId), row);

            int col = itemIds.containsKey(InnerIds.getId(itemId)) ? itemIds.get(InnerIds.getId(itemId)) : itemIds.size();
            itemIds.put(InnerIds.getId(itemId), col);

            Double r = Double.valueOf(rating);
            if(r.isNaN()) r = 1.0;

            /*
            // binarize the rating for item recommendation task
            if (binThold >= 0)
                r = r > binThold ? 1.0 : 0.0;
*/
            dataTable.put(row, col, r);
            colMap.put(col, row);

            scaleDist.add(r);

            // record rating's issuing time
            if (interaction.getTimeStamp() != null) {
                if (timeTable == null) timeTable = HashBasedTable.create();

                long timestamp = interaction.getTimeStamp().getTime();

                if (minTimestamp > timestamp)
                    minTimestamp = timestamp;

                if (maxTimestamp < timestamp)
                    maxTimestamp = timestamp;

                timeTable.put(row, col, timestamp);
            }
        }

        numRatings = scaleDist.size();
        ratingScale = new ArrayList<>(scaleDist.elementSet());
        Collections.sort(ratingScale);

        int numRows = numUsers(), numCols = numItems();

        // if min-rate = 0.0, shift upper a scale
        double minRate = ratingScale.get(0).doubleValue();
        double epsilon = minRate == 0.0 ? ratingScale.get(1).doubleValue() - minRate : 0;
        if (epsilon > 0) {
            // shift upper a scale
            for (int i = 0, im = ratingScale.size(); i < im; i++) {
                double val = ratingScale.get(i);
                ratingScale.set(i, val + epsilon);
            }
            // update data table
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    if (dataTable.contains(row, col))
                        dataTable.put(row, col, dataTable.get(row, col) + epsilon);
                }
            }
        }

        // build rating matrix
        rateMatrix = new SparseMatrix(numRows, numCols, dataTable, colMap);

        if (timeTable != null)
            timeMatrix = new SparseMatrix(numRows, numCols, timeTable, colMap);

        if (relationshipTable != null)
            relationshipMatrix = new SparseMatrix(numRows , numRows, relationshipTable, rColMap);

        // release memory of data table
        dataTable = null;
        timeTable = null;
        relationshipTable = null;

        return new SparseMatrix[]{rateMatrix, timeMatrix, relationshipMatrix};
    }

    /**
     * Read data from the data file. Note that we didn't take care of the duplicated lines.
     *
     * @return a sparse tensor storing all the relevant data
     */
    @SuppressWarnings("unchecked")
    public SparseMatrix[] readTensor() throws Exception {

        int[] dims = null;
        int numDims = 3;
        List<Integer>[] ndLists = null;
        Set<Integer>[] ndSets = null;
        List<Double> vals = new ArrayList<Double>();


        for(Interaction interaction: dataSet.getInteractions()) {

            // initialization
            if (dims == null) {
                numDims = 2;
                dims = new int[numDims];
                ndLists = (List<Integer>[]) new List<?>[numDims];
                ndSets = (Set<Integer>[]) new Set<?>[numDims];

                for (int d = 0; d < numDims; d++) {
                    ndLists[d] = new ArrayList<>();
                    ndSets[d] = new HashSet<>();
                }
            }

            // inner id starting from 0
            int row = userIds.containsKey(InnerIds.getId(interaction.getUserId())) ? userIds.get(InnerIds.getId(interaction.getUserId())) : userIds.size();
            userIds.put(InnerIds.getId(interaction.getUserId()), row);
            ndLists[0].add(row);
            ndSets[0].add(row);

            // inner id starting from 0
            int col = itemIds.containsKey(InnerIds.getId(interaction.getItemId())) ? itemIds.get(InnerIds.getId(interaction.getItemId())) : itemIds.size();
            itemIds.put(InnerIds.getId(interaction.getItemId()), col);

            ndLists[1].add(col);
            ndSets[1].add(col);

            double rate = Double.parseDouble(interaction.getValue());
            // binarize the rating for item recommendation task
          /*  if (binThold >= 0)
                rate = rate > binThold ? 1.0 : 0.0;
*/
            vals.add(rate);
            scaleDist.add(rate);
        }

        numRatings = scaleDist.size();
        ratingScale = new ArrayList<>(scaleDist.elementSet());
        Collections.sort(ratingScale);

        // if min-rate = 0.0, shift upper a scale
        double minRate = ratingScale.get(0).doubleValue();
        double epsilon = minRate == 0.0 ? ratingScale.get(1).doubleValue() - minRate : 0;
        if (epsilon > 0) {
            // shift upper a scale
            for (int i = 0, im = ratingScale.size(); i < im; i++) {
                double val = ratingScale.get(i);
                ratingScale.set(i, val + epsilon);
            }
            // update rating values
            for (int i = 0; i < vals.size(); i++) {
                vals.set(i, vals.get(i) + epsilon);
            }
        }

        for (int d = 0; d < numDims; d++) {
            dims[d] = ndSets[d].size();
        }

        rateTensor = new SparseTensor(dims, ndLists, vals);
    //    rateTensor.setUserDimension(cols[0]);
      //  rateTensor.setItemDimension(cols[1]);

        return new SparseMatrix[]{rateTensor.rateMatrix(), null};
    }

    /**
     * @return number of users
     */
    public int numUsers() {
        return userIds.size();
    }

    /**
     * @return number of items
     */
    public int numItems() {
        return itemIds.size();
    }

    /**
     * @return number of rates
     */
    public int numRatings() {
        return numRatings;
    }

    /**
     * @return number of days
     */
    public int numDays() {
        return (int) TimeUnit.MILLISECONDS.toDays(maxTimestamp - minTimestamp);
    }

    /**
     * @param rawId raw user id as String
     * @return inner user id as int
     */
    public int getUserId(String rawId) {
        return userIds.get(InnerIds.getId(rawId));
    }

    /**
     * @param innerId inner user id as int
     * @return raw user id as String
     */
    public int getUserId(int innerId) {

        if (idUsers == null)
            idUsers = userIds.inverse();

        return idUsers.get(innerId);
    }

    /**
     * @param rawId raw item id as String
     * @return inner item id as int
     */
    public int getItemId(String rawId) {
        return itemIds.get(InnerIds.getId(rawId));
    }

    /**
     * @param innerId inner user id as int
     * @return raw item id as String
     */
    public int getItemId(int innerId) {

        if (idItems == null)
            idItems = itemIds.inverse();

        return idItems.get(innerId);
    }

    /**
     * @return the rate matrix
     */
    public SparseMatrix getRateMatrix() {
        return rateMatrix;
    }

    public SparseMatrix getRelationshipMatrix() {
        return relationshipMatrix;
    }
    /**
     * @return rating scales
     */
    public List<Double> getRatingScale() {
        return ratingScale;
    }

    /**
     * @return user {rawid, inner id} mappings
     */
    public BiMap<Integer, Integer> getUserIds() {
        return userIds;
    }

    /**
     * @return item {rawid, inner id} mappings
     */
    public BiMap<Integer, Integer> getItemIds() {
        return itemIds;
    }

    /**
     * @return the minimum timestamp
     */
    public long getMinTimestamp() {
        return minTimestamp;
    }

    /**
     * @return the maximum timestamp
     */
    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    public SparseTensor getRateTensor() {
        return rateTensor;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public DataSet getDataSet() {
        return dataSet;
    }
}
