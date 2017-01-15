// Copyright (C) 2014-2015 Guibing Guo
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

package librec.rating;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import librec.data.*;
import librec.intf.GraphicRecommender;
import librec.util.Gaussian;
import librec.util.Randoms;
import librec.util.Stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thomas Hofmann, <strong>Collaborative Filtering via Gaussian Probabilistic Latent Semantic Analysis</strong>, SIGIR
 * 2003. <br>
 * 
 * <strong>Tempered EM:</strong> Thomas Hofmann, <strong>Unsupervised Learning by Probabilistic Latent Semantic
 * Analysis</strong>, Machine Learning, 42, 177�C196, 2001.
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 *
 */

@Configuration(key = "q", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, description = "smoothing weight", value = "10")
@Configuration(key = "b", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, description = "tempered EM parameter beta, suggested by Wu Bin", value = "1.0")
@Configuration(key = "burnIn", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, description = "burn-in period", value = "1400")
@Configuration(key = "sampleLag", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "10", description = "sample lag (if -1 only one sample taken)")
@Configuration(key = "numFactors", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "10")
@Configuration(key = "numIters", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "100")
public class GPLSA extends GraphicRecommender {

	// {user, item, {factor z, probability}}
	private Table<Integer, Integer, Map<Integer, Double>> Q;
	private DenseMatrix Mu, Sigma;

	private DenseVector mu, sigma;
	public double q; // smoothing weight
    public double b; // tempered EM parameter beta, suggested by Wu Bin

	public GPLSA() {
		super();
	}

	@Override
    public void initModel() throws Exception {

		// Pz_u
		Puk = new DenseMatrix(numUsers, numFactors);
		for (int u = 0; u < numUsers; u++) {
			double[] probs = Randoms.randProbs(numFactors);
			for (int k = 0; k < numFactors; k++) {
				Puk.set(u, k, probs[k]);
			}
		}

		// normalize ratings
		double mean = globalMean;
		double sd = Stats.sd(trainMatrix.getData(), mean);

		mu = new DenseVector(numUsers);
		sigma = new DenseVector(numUsers);
		for (int u = 0; u < numUsers; u++) {
			SparseVector ru = trainMatrix.row(u);
			int Nu = ru.size();
			if (Nu < 1)
				continue;

			// compute mu_u
			double mu_u = (ru.sum() + q * mean) / (Nu + q);
			mu.set(u, mu_u);

			// compute sigma_u
			double sum = 0;
			for (VectorEntry ve : ru) {
				sum += Math.pow(ve.get() - mu_u, 2);
			}
			sum += q * Math.pow(sd, 2);
			double sigma_u = Math.sqrt(sum / (Nu + q));
			sigma.set(u, sigma_u);
		}

		// initialize Q
		Q = HashBasedTable.create();

		for (MatrixEntry me : trainMatrix) {
			int u = me.row();
			int i = me.column();
			double rate = me.get();

			double r = (rate - mu.get(u)) / sigma.get(u); // continuous ratings
			me.set(r);

			Q.put(u, i, new HashMap<>());
		}

		// initialize Mu, Sigma
		Mu = new DenseMatrix(numItems, numFactors);
		Sigma = new DenseMatrix(numItems, numFactors);
		for (int i = 0; i < numItems; i++) {
			SparseVector ci = trainMatrix.column(i);
			int Ni = ci.size();

			if (Ni < 1)
				continue;

			double mu_i = ci.mean();

			double sum = 0;
			for (VectorEntry ve : ci) {
				sum += Math.pow(ve.get() - mu_i, 2);
			}
			double sd_i = Math.sqrt(sum / Ni);

			for (int z = 0; z < numFactors; z++) {
				Mu.set(i, z, mu_i + smallValue * Math.random());
				Sigma.set(i, z, sd_i + smallValue * Math.random());
			}
		}
	}

	@Override
	protected void eStep() {
		// variational inference to compute Q
		for (MatrixEntry me : trainMatrix) {
			int u = me.row();
			int i = me.column();
			double r = me.get();

			double denominator = 0;
			double[] numerator = new double[numFactors];
			for (int z = 0; z < numFactors; z++) {
				double pdf = Gaussian.pdf(r, Mu.get(i, z), Sigma.get(i, z));
				double val = Math.pow(Puk.get(u, z) * pdf, b); // Tempered EM

				numerator[z] = val;
				denominator += val;
			}

			Map<Integer, Double> factorProbs = Q.get(u, i);
			for (int z = 0; z < numFactors; z++) {
				double prob = (denominator > 0 ? numerator[z] / denominator : 0);
				factorProbs.put(z, prob);
			}
		}
	}

	@Override
	protected void mStep() {

		// theta_u,z
		for (int u = 0; u < numUsers; u++) {
			List<Integer> items = trainMatrix.getColumns(u);
			if (items.size() < 1)
				continue;

			double[] numerator = new double[numFactors];
			double denominator = 0;
			for (int z = 0; z < numFactors; z++) {

				for (int i : items) {
					numerator[z] += Q.get(u, i).get(z);
				}

				denominator += numerator[z];
			}

			for (int z = 0; z < numFactors; z++) {
				Puk.set(u, z, numerator[z] / denominator);
			}
		}

		// Mu, Sigma
		for (int i = 0; i < numItems; i++) {
			List<Integer> users = trainMatrix.getRows(i);
			if (users.size() < 1)
				continue;

			for (int z = 0; z < numFactors; z++) {
				double numerator = 0, denominator = 0;

				for (int u : users) {
					double r = trainMatrix.get(u, i);
					double prob = Q.get(u, i).get(z);

					numerator += r * prob;
					denominator += prob;
				}

				double mu = denominator > 0 ? numerator / denominator : 0;
				Mu.set(i, z, mu);

				numerator = 0;
				denominator = 0;
				for (int u : users) {
					double r = trainMatrix.get(u, i);
					double prob = Q.get(u, i).get(z);

					numerator += Math.pow(r - mu, 2) * prob;
					denominator += prob;
				}

				double sigma = denominator > 0 ? Math.sqrt(numerator / denominator) : 0;
				Sigma.set(i, z, sigma);
			}
		}
	}

	@Override
    public double predict(int u, int i) throws Exception {

		double sum = 0;
		for (int z = 0; z < numFactors; z++) {
			sum += Puk.get(u, z) * Mu.get(i, z);
		}

		return mu.get(u) + sigma.get(u) * sum;
	}
}
