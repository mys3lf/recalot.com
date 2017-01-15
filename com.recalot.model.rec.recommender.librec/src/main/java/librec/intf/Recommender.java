// Copyright (C) 2014 Guibing Guo
//
// This file is part of LibRec.
//
// LibRec is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LibRec is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LibRec. If not, see <http://www.gnu.org/licenses/>.
//

package librec.intf;

import com.google.common.cache.LoadingCache;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.model.rec.librec.DataDAO;
import librec.data.SparseMatrix;
import librec.data.SparseVector;
import librec.data.SymmMatrix;
import librec.util.Sims;

import java.util.ArrayList;
import java.util.List;

/**
 * General recommenders
 * 
 * @author Guibing Guo
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 */
//@Configuration(key = "guavaCacheSpec", value = "maximumSize=200,expireAfterAccess=2m")
//@Configuration(key = "binThold", value = "-1.0", type = ConfigurationItem.ConfigurationItemType.Double, requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional, description = "threshold to binarize ratings")
public abstract class Recommender  {

	/************************************ Static parameters for all recommenders ***********************************/
	// matrix of rating data
	public SparseMatrix timeMatrix;

	// Guava cache configuration
	public String guavaCacheSpec = "maximumSize=200,expireAfterAccess=2m";

	// is ranking/rating prediction
	public boolean isRankingPred;

	// threshold to binarize ratings
	//public double binThold;

	// number of users, items, ratings
	public int numUsers, numItems, numRates;

	// a list of rating scales
	protected static List<Double> ratingScale;
	// number of rating levels
	public int numLevels;
	// Maximum, minimum values of rating scales
	public double maxRate, minRate;
	// minimum, maximum timestamp
	public long minTimestamp, maxTimestamp;

	// init mean and standard deviation
    public double initMean, initStd;
	// small value for initialization
    public double smallValue = 0.01;

	/************************************ Recommender-specific parameters ****************************************/

	// user-vector cache, item-vector cache
	protected LoadingCache<Integer, SparseVector> userCache;

	// user-items cache, item-users cache
	protected LoadingCache<Integer, List<Integer>> userItemsCache, itemUsersCache;

	// rating matrix for training
	protected SparseMatrix trainMatrix;

	// upper symmetric matrix of item-item correlations
	protected SymmMatrix corrs;

	// global average of training rates
	public double globalMean;
    protected DataDAO dataDAO;

    /**
	 * Constructor for Recommender
	 */
	public Recommender() {
			initMean = 0.0;
			initStd = 0.1;
	}

    public void setDao(DataDAO dataDAO) {
        this.dataDAO = dataDAO;

        try {
            SparseMatrix[] matrixes = dataDAO.readData();

            trainMatrix = matrixes[0];
            timeMatrix = matrixes[1];

            ratingScale = dataDAO.getRatingScale();
            minRate = ratingScale.get(0);
            maxRate = ratingScale.get(ratingScale.size() - 1);

            if(minRate == maxRate) minRate = 0;

            numLevels = ratingScale.size();

            numUsers = dataDAO.numUsers();
            numItems = dataDAO.numItems();

            // ratings' timestamps
            minTimestamp = dataDAO.getMinTimestamp();
            maxTimestamp = dataDAO.getMaxTimestamp();

            // global mean
            numRates = trainMatrix.size();
            globalMean = trainMatrix.sum() / numRates;

            // compute item-item correlations
            if (isRankingPred)
                corrs = new SymmMatrix(numItems);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * initilize recommender model
	 */
    public void initModel() throws Exception {
	}

	/**
	 * build user-user or item-item correlation matrix from training data
	 * 
	 * @param isUser
	 *            whether it is user-user correlation matrix
	 * 
	 * @return a upper symmetric matrix with user-user or item-item coefficients
	 * 
	 */
	protected SymmMatrix buildCorrs(boolean isUser, String similarityMeasure, int shrinkage) {
	//	Logs.debug("Build {} similarity matrix ...", isUser ? "user" : "item");

		int count = isUser ? numUsers : numItems;
		SymmMatrix corrs = new SymmMatrix(count);

		for (int i = 0; i < count; i++) {
			SparseVector iv = isUser ? trainMatrix.row(i) : trainMatrix.column(i);
			if (iv.getCount() == 0)
				continue;
			// user/item itself exclusive
			for (int j = i + 1; j < count; j++) {
				SparseVector jv = isUser ? trainMatrix.row(j) : trainMatrix.column(j);

				double sim = correlation(iv, jv, similarityMeasure, shrinkage);

				if (!Double.isNaN(sim))
					corrs.set(i, j, sim);
			}
		}

		return corrs;
	}


	/**
	 * Compute the correlation between two vectors for a specific method
	 * 
	 * @param iv
	 *            vector i
	 * @param jv
	 *            vector j
	 * @param method
	 *            similarity method
	 * @return the correlation between vectors i and j; return NaN if the correlation is not computable.
	 */
	protected double correlation(SparseVector iv, SparseVector jv, String method, int shrinkage) {

		// compute similarity
		List<Double> is = new ArrayList<>();
		List<Double> js = new ArrayList<>();

		for (Integer idx : jv.getIndex()) {
			if (iv.contains(idx)) {
				is.add(iv.get(idx));
				js.add(jv.get(idx));
			}
		}

		double sim = 0;
		switch (method.toLowerCase()) {
		case "cos":
			// for ratings along the overlappings
			sim = Sims.cos(is, js);
			break;
		case "cos-binary":
			// for ratings along all the vectors (including one-sided 0s)
			sim = iv.inner(jv) / (Math.sqrt(iv.inner(iv)) * Math.sqrt(jv.inner(jv)));
			break;
		case "msd":
			sim = Sims.msd(is, js);
			break;
		case "cpc":
			sim = Sims.cpc(is, js, (minRate + maxRate) / 2.0);
			break;
		case "exjaccard":
			sim = Sims.exJaccard(is, js);
			break;
		case "pcc":
		default:
			sim = Sims.pcc(is, js);
			break;
		}

		// shrink to account for vector size
		if (!Double.isNaN(sim)) {
			int n = is.size();
			if (shrinkage > 0)
				sim *= n / (n + shrinkage + 0.0);
		}

		return sim;
	}

	/**
	 * Learning method: override this method to build a model, for a model-based method. Default implementation is
	 * useful for memory-based methods.
	 * 
	 */
    public void buildModel() throws Exception {
	}

	/**
	 * After learning model: release some intermediate data to avoid memory leak
	 */
    public void postModel() throws Exception {
	}

	/**
	 * predict a specific rating for user u on item j. It is useful for evalution which requires predictions are
	 * bounded.
	 * 
	 * @param u
	 *            user id
	 * @param j
	 *            item id
	 * @param bound
	 *            whether to bound the prediction
	 * @return prediction
	 */
    public double predict(int u, int j, boolean bound) throws Exception {
		double pred = predict(u, j);

		if (bound) {
			if (pred > maxRate)
				pred = maxRate;
			if (pred < minRate)
				pred = minRate;
		}

		return pred;
	}

	/**
	 * predict a specific rating for user u on item j, note that the prediction is not bounded. It is useful for
	 * building models with no need to bound predictions.
	 * 
	 * @param u
	 *            user id
	 * @param j
	 *            item id
	 * @return raw prediction without bounded
	 */
    public double predict(int u, int j) throws Exception {
		return globalMean;
	}

	protected double perplexity(int u, int j, double r) throws Exception {
		return 0;
	}

	/**
	 * predict a ranking score for user u on item j: default case using the unbounded predicted rating values
	 * 
	 * @param u
	 *            user id
	 * 
	 * @param j
	 *            item id
	 * @return a ranking score for user u on item j
	 */
	public double ranking(int u, int j) throws Exception {
		return predict(u, j, false);
	}

	/**
	 * Below are a set of mathematical functions. As many recommenders often adopts them, for conveniency's sake, we put
	 * these functions in the base Recommender class, though they belong to Math class.
	 * 
	 */

	/**
	 * logistic function g(x)
	 */
	protected double g(double x) {
		return 1.0 / (1 + Math.exp(-x));
	}

	/**
	 * gradient value of logistic function g(x)
	 */
	protected double gd(double x) {
		return g(x) * g(-x);
	}

	/**
	 * @param x
	 *            input value
	 * @param mu
	 *            mean of normal distribution
	 * @param sigma
	 *            standard deviation of normation distribution
	 * 
	 * @return a gaussian value with mean {@code mu} and standard deviation {@code sigma};
	 */
	protected double gaussian(double x, double mu, double sigma) {
		return Math.exp(-0.5 * Math.pow(x - mu, 2) / (sigma * sigma));
	}

	/**
	 * normalize a rating to the region (0, 1)
	 */
	protected double normalize(double rate) {
        if(maxRate - minRate == 0){
            return rate;
        }
		return (rate - minRate) / (maxRate - minRate);
	}

	/**
	 * Check if ratings have been binarized; useful for methods that require binarized ratings;
	 */
	protected void checkBinary() {
	/*	if (binThold < 0) {

			//Logs.error("val.binary.threshold={}, ratings must be binarized first! Try set a non-negative value.", binThold);
			System.exit(-1);
		} */
	}

	/**
	 * 
	 * denormalize a prediction to the region (minRate, maxRate)
	 */
	protected double denormalize(double pred) {
		return minRate + pred * (maxRate - minRate);
	}
}
