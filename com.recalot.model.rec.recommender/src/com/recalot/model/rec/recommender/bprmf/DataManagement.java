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

package com.recalot.model.rec.recommender.bprmf;

import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.User;
import com.recalot.common.exceptions.BaseException;
import com.recalot.model.rec.recommender.funksvd.helper.RandomUtils;
import com.recalot.model.rec.recommender.helper.matrix.SparseByteMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Manages the data-objects of BPR-MF
 * 
 */
public class DataManagement  {
    // HashMaps to convert from mapped ids to real ones
    public HashMap<Integer, String> userMap;
    public HashMap<Integer, String> itemMap;


    // HashMaps to convert from real ids to mapped ones
    public HashMap<String, Integer> userIndices;
	public HashMap<String, Integer> itemIndices;

	// Itembias Array
	public double[] item_bias;

	// start values for mapping
	public int userid;
	public int itemid;

	// values for initialization of the latent matrices
	public static double sqrt_e_div_2_pi = Math.sqrt(Math.E / (2 * Math.PI));
	public static final Random random = RandomUtils.getRandom();

	private double initMean = 0;
	private double initStDev = 0.1;

	public double[][] latentUserVector;
	public double[][] latentItemVector;

	// HashMap containing for each user a list with seen item
	public HashMap<Integer, ArrayList<Integer>> userMatrix;

	// matrix containing for each user/item - combination a bool value,
	// indicating whether the user has seen the item
	// was in original: public boolean[][] boolMatrix;
	SparseByteMatrix boolMatrix;
	int boolMatrix_numUsers;
	int boolMatrix_numItems;

	// number of positive entries in then boolmatrix
	public int numPosentries;

	public DataSet dataSet;

	// How to interpret rating data as binary data
	// Default: No (as done in original implementation)
	// If set to yes, the global item relevance threshold is applied
	public boolean useRatingThreshold = false;
	

	/**
	 * initializes all the needed objects
	 * 
	 * @param dataSet
	 *            DataModel - the dataModel of the current training data
	 * @param numUsers
	 *            Number - number of users
	 * @param numItems
	 *            Number - number of items
	 * @param numFeatures
	 *            Number - number of columns in the latent matrices
	 */
	public void init(DataSet dataSet, int numUsers, int numItems,
			int numFeatures) throws BaseException {
		
		this.dataSet = dataSet;

		userMap = new HashMap<>();
		itemMap = new HashMap<>();
		userIndices = new HashMap<>();
		itemIndices = new HashMap<>();
		userid = 0;
		itemid = 0;
		numPosentries = 0;
		userMatrix = new HashMap<>();
		
		for (User user : dataSet.getUsers()) {
			this.addUser(user);
		}

		for (Item item : dataSet.getItems()) {
			this.addItem(item);
		}

		
		
		latentUserVector = new double[numUsers][numFeatures];
		latentItemVector = new double[numItems][numFeatures];

		initLatentmatrix(latentUserVector);
		initLatentmatrix(latentItemVector);

		item_bias = new double[numItems];

		boolMatrix = new SparseByteMatrix(numUsers,numItems);
		boolMatrix_numUsers = numUsers;
		boolMatrix_numItems = numItems;

		this.booleanRatings();
	}

	/**
	 * initializes the user/item-matrix with booleans instead of ratings
	 */
	public void booleanRatings() throws BaseException {
		for (Interaction r : dataSet.getInteractions()) {
			Integer user = userIndices.get(r.getUserId().toLowerCase());
			Integer item = itemIndices.get(r.getItemId().toLowerCase());

            try{
                boolMatrix.setBool(user, item, true);
            } catch (Exception e){
                e.printStackTrace();
             //   System.out.println(e.getMessage());
            }

			
			ArrayList<Integer> ratingsOfUser = userMatrix.get(user);
			if (ratingsOfUser == null) {
				ratingsOfUser = new ArrayList<>();
				userMatrix.put(user, ratingsOfUser);
			}
			ratingsOfUser.add(item);
			numPosentries++;
		}
	}

	/**
	 * initiates the given latent matrix with random values
	 * 
	 * @param matrix
	 *            double[][] - the given latent matrix
	 */
	private void initLatentmatrix(double[][] matrix) {
		for (int k = 0; k < matrix.length; k++) {
			for (int l = 0; l < matrix[k].length; l++) {
				matrix[k][l] = this.nextNormal(initMean, initStDev);
            }
		}
	}

	/**
	 * calculates the scalarproduct with rowdifference for the given parameters
	 * 
	 * @param user
	 *            Number - the mapped userID
	 * @param item1
	 *            Number - the mapped itemID of a viewed item
	 * @param item2
	 *            Number - the mapped itemID of an unviewed item
	 * @return result Number - the scalarproduct with rowdifference
	 */
	public double rowScalarProductWithRowDifference(int user, int item1,
			int item2) {

		if (user >= latentUserVector.length)
			throw new IllegalArgumentException("i too big: " + user
					+ ", dim1 is " + latentUserVector.length);
		if (item1 >= latentItemVector.length)
			throw new IllegalArgumentException("item1 too big: " + item1
					+ ", dim1 is " + latentItemVector.length);
		if (item2 >= latentItemVector.length)
			throw new IllegalArgumentException("j too big: " + item2
					+ ", dim1 is " + latentItemVector.length);
		if (latentUserVector[user].length != latentItemVector[item1].length)
			throw new IllegalArgumentException("wrong row size: "
					+ latentUserVector[user].length + " vs. "
					+ latentItemVector[item1].length);
		if (latentUserVector[user].length != latentItemVector[item2].length)
			throw new IllegalArgumentException("wrong row size: "
					+ latentUserVector[user].length + " vs. "
					+ latentItemVector[item2].length);

		double result = 0.0;
		for (int c = 0; c < latentUserVector[user].length; c++)
			result += latentUserVector[user][c]
					* ((Double) latentItemVector[item1][c] - (Double) latentItemVector[item2][c]);
		return result;
	}

	/**
	 * calculates the scalarproduct for the given parameters
	 * 
	 * @param user
	 *            Number - the mapped userID
	 * @param item
	 *            Number - the mapped itemID of a viewed item
	 * @return result Number - the scalarproduct
	 */
	public double rowScalarProduct(int user, int item) {
		if (user >= latentUserVector.length)
			throw new IllegalArgumentException("i too big: " + user
					+ ", dim1 is " + latentUserVector.length);
		if (item >= latentItemVector.length)
			throw new IllegalArgumentException("j too big: " + item
					+ ", dim1 is " + latentItemVector.length);
		if (latentUserVector[user].length != latentItemVector[item].length)
			throw new IllegalArgumentException("wrong row size: "
					+ latentUserVector[user].length + " vs. "
					+ latentItemVector[item].length);

		Double result = 0.0;
		for (int c = 0; c < latentUserVector[user].length; c++)
			result += (Double) latentUserVector[user][c]
					* ((Double) latentItemVector[item][c]);
		return result;
	}

	/**
	 * adds the given user to the userMap and the userIndices
	 *
     * @param user
     *            Number - unmapped userID
     */
	public void addUser(User user) {
		userMap.put(userid, user.getId().toLowerCase());
		userIndices.put(user.getId().toLowerCase(), userid);
		userid++;
	}

	/**
	 * adds the given item to the itemMap and the itemIndices
	 *
     * @param item
     *            Number - unmapped itemID
     */
	public void addItem(Item item) {
		itemMap.put(itemid, item.getId().toLowerCase());
		itemIndices.put(item.getId().toLowerCase(), itemid);
		itemid++;
	}

	public double nextNormal(double mean, double stdev) {
		return mean + stdev * nextNormal();
	}

	public double nextNormal() {
		double y;
		double x;
		do {
			double u = random.nextDouble();
			x = nextExp(1);
			y = 2 * u * sqrt_e_div_2_pi * Math.exp(-x);
		} while (y < (2 / (2 * Math.PI)) * Math.exp(-0.5 * x * x));
		if (random.nextDouble() < 0.5) {
			return x;
		} else {
			return -x;
		}
	}

	public double nextExp(double lambda) {
		double u = random.nextDouble();
		return -(1 / lambda) * Math.log(1 - u);
	}
}
