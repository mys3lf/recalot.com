package librec.intf;

import com.google.common.collect.Table;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import librec.data.DenseMatrix;
import librec.data.DenseVector;

/**
 * Probabilistic Graphic Models
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */

@Configuration(key = "burnIn", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, description = "burn-in period", value = "30")
@Configuration(key = "sampleLag", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "10", description = "sample lag (if -1 only one sample taken)")
@Configuration(key = "numFactors", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "10")
@Configuration(key = "numIters", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "30")
@Configuration(key = "lRate", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, value="0.005")
@Configuration(key = "initAlpha", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, value = "0.01")
@Configuration(key = "initBeta", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, value = "0.01")
public class GraphicRecommender extends Recommender {

	/**
	 * number of topics
	 */
	public int numFactors;

	/**
	 * Dirichlet hyper-parameters of user-topic distribution: typical value is 50/K
	 */
	public double initAlpha;

	/**
	 * Dirichlet hyper-parameters of topic-item distribution, typical value is 0.01
	 */
    public double initBeta;
	/**
	 * burn-in period
	 */
    public int burnIn;

	/**
	 * sample lag (if -1 only one sample taken)
	 */
    public int sampleLag;

	/**
	 * maximum number of iterations
	 */
	public int numIters;

	/**
	 * intervals for printing verbose information
	 */
    public static int numIntervals;


	/*********************************** Method-specific Parameters ************************/

	/**
	 * entry[u, i, k]: topic assignment as sparse structure
	 */
	protected Table<Integer, Integer, Integer> z;

	/**
	 * entry[i, k]: number of tokens assigned to topic k, given item i.
	 */
	protected DenseMatrix Nik;
	
	/**
	 * entry[k, i]: number of tokens assigned to topic k, given item i.
	 */
	protected DenseMatrix Nki;

	/**
	 * entry[u, k]: number of tokens assigned to topic k, given user u.
	 */
	protected DenseMatrix Nuk;

	/**
	 * entry[k]: number of tokens assigned to topic t.
	 */
	protected DenseVector Nk;

	/**
	 * entry[u]: number of tokens rated by user u.
	 */
	protected DenseVector Nu;

	/**
	 * entry[i]: number of tokens rating item i.
	 */
	protected DenseVector Ni;

	/**
	 * vector of hyperparameters for alpha and beta
	 */
	protected DenseVector alpha, beta;

	/**
	 * cumulative statistics of theta, phi
	 */
	protected DenseMatrix PukSum, PikSum, PkiSum;

	/**
	 * posterior probabilities of parameters
	 * 
	 */
	protected DenseMatrix Puk, Pki, Pik;

	/**
	 * size of statistics
	 */
	protected int numStats = 0;

	/**
	 * objective loss
	 */
	protected double loss, lastLoss;

	public GraphicRecommender() {
		super();
	}

	@Override
    public void buildModel() throws Exception {

		for (int iter = 1; iter <= numIters; iter++) {

			// E-step: infer parameters
			eStep();

			// M-step: update hyper-parameters
			mStep();

			// get statistics after burn-in
			if ((iter > burnIn) && (iter % sampleLag == 0)) {
				readoutParams();

				if (isConverged(iter))
					break;
			}

		}

		// retrieve posterior probability distributions
		estimateParams();

	}

	/**
	 * update the hyper-parameters
	 */
	protected void mStep() {
	}

	/**
	 * employing early stopping criteria
	 * 
	 * @param iter
	 *            current iteration
	 */
	protected boolean isConverged(int iter) throws Exception {
		return false;
	}

	/**
	 * estimate the model parameters
	 */
	protected void estimateParams() {
	}

	/**
	 * parameters estimation: used in the training phase
	 */
	protected void eStep() {
    }
	/**
	 * read out parameters for each iteration
	 */
	protected void readoutParams() {
	}
}
