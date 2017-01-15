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

import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import librec.data.DenseMatrix;
import librec.data.DenseVector;

/**
 * Recommenders using iterative learning techniques
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */
//@Configuration("factors, lRate, maxLRate, regB, regU, regI, iters, boldDriver")
@Configuration(key = "isBoldDriver", value = "true", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Boolean)

@Configuration(key = "numFactors", value="100", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer)
@Configuration(key = "numIters",  value="100", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer)
@Configuration(key = "lRate", value = "0.005", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "maxLRate", value = "-1", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)

@Configuration(key = "decay", value = "-1.0", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "regI", value = "0.1", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "regU", value = "0.1", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "regB", value = "0.1", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "reg", value = "0.1", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
public abstract class IterativeRecommender extends Recommender {

	/************************************ parameters for all recommenders ***********************************/
    // init, maximum learning rate
    public double  maxLRate;
    // number of factors
    public int numFactors;

    // whether to adjust learning rate automatically
    public boolean isBoldDriver;

    // number of iterations
    public int numIters;
    // user, item and bias regularization
    public double regU, regI, regB, reg;

    // decay of learning rate
    public float decay;

    // indicator of static field initialization
	public static boolean resetStatics = true;

	/************************************ Recommender-specific parameters ****************************************/
	// factorized user-factor matrix
	protected DenseMatrix P;

	// factorized item-factor matrix
	protected DenseMatrix Q;

	// user biases
	protected DenseVector userBias;
	// item biases
	protected DenseVector itemBias;

	// adaptive learn rate
    public double lRate;
	// objective loss
    public double loss, last_loss = 0;
	// predictive measure
    public double measure, last_measure = 0;

	// initial models using normal distribution
	protected boolean initByNorm;

	public IterativeRecommender() {
		super();

		initByNorm = true;
	}

	/**
	 * default prediction method
	 */
	@Override
	public double predict(int u, int j) throws Exception {
		return DenseMatrix.rowMult(P, u, Q, j);
	}

	/**
	 * Post each iteration, we do things:
	 * 
	 * <ol>
	 * <li>print debug information</li>
	 * <li>check if converged</li>
	 * <li>if not, adjust learning rate</li>
	 * </ol>
	 * 
	 * @param iter
	 *            current iteration
	 * 
	 * @return boolean: true if it is converged; false otherwise
	 * 
	 */
	protected boolean isConverged(int iter) throws Exception {
		float delta_measure = (float) (last_measure - measure);

		if (Double.isNaN(loss) || Double.isInfinite(loss)) {
		    //	Logs.error("Loss = NaN or Infinity: current settings does not fit the recommender! Change the settings and try again!");
	        //THIS SHOULD NEVER BE PERFORMED WITHIN RECALOT		System.exit(-1);
		}

		// check if converged
		boolean cond1 = Math.abs(loss) < 1e-5;
		boolean cond2 = (delta_measure > 0) && (delta_measure < 1e-5);
		boolean converged = cond1 || cond2;

		// if not converged, update learning rate
		if (!converged)
			updateLRate(iter);

		last_loss = loss;
		last_measure = measure;

		return converged;
	}

	/**
	 * Update current learning rate after each epoch <br/>
	 * 
	 * <ol>
	 * <li>bold driver: Gemulla et al., Large-scale matrix factorization with distributed stochastic gradient descent,
	 * KDD 2011.</li>
	 * <li>constant decay: Niu et al, Hogwild!: A lock-free approach to parallelizing stochastic gradient descent, NIPS
	 * 2011.</li>
	 * <li>Leon Bottou, Stochastic Gradient Descent Tricks</li>
	 * <li>more ways to adapt learning rate can refer to: http://www.willamette.edu/~gorr/classes/cs449/momrate.html</li>
	 * </ol>
	 * 
	 * @param iter
	 *            the current iteration
	 */
	protected void updateLRate(int iter) {

		if (lRate <= 0)
			return;

		if (isBoldDriver && iter > 1)
			lRate = Math.abs(last_loss) > Math.abs(loss) ? lRate * 1.05 : lRate * 0.5;
		else if (decay > 0 && decay < 1)
			lRate *= decay;

		// limit to max-learn-rate after update
		if (maxLRate > 0 && lRate > maxLRate)
			lRate = maxLRate;
	}

	@Override
    public void initModel() throws Exception {
		P = new DenseMatrix(numUsers, numFactors);
		Q = new DenseMatrix(numItems, numFactors);

		// initialize model
		if (initByNorm) {
			P.init(initMean, initStd);
			Q.init(initMean, initStd);
		} else {
			P.init(); // P.init(smallValue);
			Q.init(); // Q.init(smallValue);
		}
	}
}
