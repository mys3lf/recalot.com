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
import librec.data.DenseMatrix;
import librec.data.DenseVector;
import librec.data.MatrixEntry;
import librec.intf.GraphicRecommender;

/**
 * LDCC: Bayesian Co-clustering (BCC) with Gibbs sampling <br>
 * 
 * Wang et al., <strong>Latent Dirichlet Bayesian Co-Clustering</strong>, Machine Learning and Knowledge Discovery in
 * Databases, 2009.
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 *
 */

@Configuration(key = "Ku", type = ConfigurationItem.ConfigurationItemType.Integer, value = "20")
@Configuration(key = "Kv", type = ConfigurationItem.ConfigurationItemType.Integer, value = "10")
@Configuration(key = "au", type = ConfigurationItem.ConfigurationItemType.Double, value = "0.1", description = "alpha for user")
@Configuration(key = "av", type = ConfigurationItem.ConfigurationItemType.Double, value = "0.1", description = "alpha for item")
@Configuration(key = "bl", type = ConfigurationItem.ConfigurationItemType.Double, value = "0.1", description = "beta for rating levels")
@Configuration(key = "burnIn", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double, description = "burn-in period", value = "2000")
@Configuration(key = "sampleLag", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "500", description = "sample lag (if -1 only one sample taken)")
@Configuration(key = "numIters", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer, value = "5000")
public class LDCC extends GraphicRecommender {

	private Table<Integer, Integer, Integer> Zu, Zv;

	private DenseMatrix Nui, Nvj;
	private DenseVector Nv;

	private int[][][] Nijl;
	private DenseMatrix Nij;

	public int Ku, Kv;

    public double au, av, bl;

	// parameters
	private DenseMatrix PIu, PIv, PIuSum, PIvSum;
	private double[][][] Pijl, PijlSum;

	public LDCC() {
		super();
	}

	@Override
    public void initModel() throws Exception {
		Nui = new DenseMatrix(numUsers, Ku);
		Nu = new DenseVector(numUsers);

		Nvj = new DenseMatrix(numItems, Kv);
		Nv = new DenseVector(numItems);

		Nijl = new int[Ku][Kv][numLevels];
		Nij = new DenseMatrix(Ku, Kv);

		Zu = HashBasedTable.create();
		Zv = HashBasedTable.create();

		for (MatrixEntry me : trainMatrix) {
			int u = me.row();
			int v = me.column();
			double rate = me.get();
			int l = ratingScale.indexOf(rate);

			int i = (int) (Ku * Math.random());
			int j = (int) (Kv * Math.random());

			Nui.add(u, i, 1);
			Nu.add(u, 1);

			Nvj.add(v, j, 1);
			Nv.add(v, 1);

			Nijl[i][j][l]++;
			Nij.add(i, j, 1);

			Zu.put(u, v, i);
			Zv.put(u, v, j);
		}

		// parameters
		PIuSum = new DenseMatrix(numUsers, Ku);
		PIvSum = new DenseMatrix(numItems, Kv);
		Pijl = new double[Ku][Kv][numLevels];
		PijlSum = new double[Ku][Kv][numLevels];
	}

	@Override
	protected void eStep() {
		for (MatrixEntry me : trainMatrix) {
			int u = me.row();
			int v = me.column();
			double rate = me.get();
			int l = ratingScale.indexOf(rate);

			// user and item's factors
			int i = Zu.get(u, v);
			int j = Zv.get(u, v);

			// remove this observation
			Nui.add(u, i, -1);
			Nu.add(u, -1);

			Nvj.add(v, j, -1);
			Nv.add(v, -1);

			Nijl[i][j][l]--;
			Nij.add(i, j, -1);

			// compute P(i, j)
			DenseMatrix probs = new DenseMatrix(Ku, Kv);
			double sum = 0;
			for (int m = 0; m < Ku; m++) {
				for (int n = 0; n < Kv; n++) {
					// compute Pmn
					double v1 = (Nui.get(u, m) + au) / (Nu.get(u) + Ku * au);
					double v2 = (Nvj.get(v, n) + av) / (Nv.get(v) + Kv * av);
					double v3 = (Nijl[m][n][l] + bl) / (Nij.get(m, n) + numLevels * bl);

					double prob = v1 * v2 * v3;
					probs.set(m, n, prob);
					sum += prob;
				}
			}

			probs = probs.scale(1.0 / sum);

			// re-sample user factor
			double[] Pu = new double[Ku];
			for (int m = 0; m < Ku; m++) {
				Pu[m] = probs.sumOfRow(m);
			}
			for (int m = 1; m < Ku; m++) {
				Pu[m] += Pu[m - 1];
			}

			double rand = Math.random();
			for (i = 0; i < Ku; i++) {
				if (rand < Pu[i])
					break;
			}

			// re-sample item factor
			double[] Pv = new double[Kv];
			for (int n = 0; n < Kv; n++) {
				Pv[n] = probs.sumOfColumn(n);
			}
			for (int n = 1; n < Kv; n++) {
				Pv[n] += Pv[n - 1];
			}

			rand = Math.random();
			for (j = 0; j < Kv; j++) {
				if (rand < Pv[j])
					break;
			}

			// add statistics
			Nui.add(u, i, 1);
			Nu.add(u, 1);

			Nvj.add(v, j, 1);
			Nv.add(v, 1);

			Nijl[i][j][l]++;
			Nij.add(i, j, 1);

			Zu.put(u, v, i);
			Zv.put(u, v, j);
		}
	}

	@Override
	protected void readoutParams() {
		for (int u = 0; u < numUsers; u++) {
			for (int i = 0; i < Ku; i++) {
				PIuSum.add(u, i, (Nui.get(u, i) + au) / (Nu.get(u) + Ku * au));
			}
		}

		for (int v = 0; v < numItems; v++) {
			for (int j = 0; j < Kv; j++) {
				PIvSum.add(v, j, (Nvj.get(v, j) + av) / (Nv.get(v) + Kv * av));
			}
		}

		for (int i = 0; i < Ku; i++) {
			for (int j = 0; j < Kv; j++) {
				for (int l = 0; l < numLevels; l++) {
					PijlSum[i][j][l] += (Nijl[i][j][l] + bl) / (Nij.get(i, j) + numLevels * bl);
				}
			}
		}

		numStats++;
	}

	@Override
	protected void estimateParams() {
		PIu = PIuSum.scale(1.0 / numStats);
		PIv = PIvSum.scale(1.0 / numStats);

		for (int i = 0; i < Ku; i++) {
			for (int j = 0; j < Kv; j++) {
				for (int l = 0; l < numLevels; l++) {
					Pijl[i][j][l] = PijlSum[i][j][l] / numStats;
				}
			}
		}
	}

	@Override
	protected boolean isConverged(int iter) throws Exception {

		// get the parameters
		estimateParams();

		// compute the perplexity
		int N = 0;
		double sum = 0;
		for (MatrixEntry me : trainMatrix) {
			int u = me.row();
			int v = me.column();
			double ruv = me.get();

			sum += perplexity(u, v, ruv);
			N++;
		}

		double perp = Math.exp(sum / N);
		double delta = perp - loss; // perplexity should get smaller and smaller --> delta<0

		if (numStats > 1 && delta > 0)
			return true;

		loss = perp;
		return false;
	}

	@Override
	protected double perplexity(int u, int v, double pred) throws Exception {
		int l = (int) (pred / minRate - 1);

		// compute p(r|u,v)
		double prob = 0;
		for (int i = 0; i < Ku; i++) {
			for (int j = 0; j < Kv; j++) {
				prob += Pijl[i][j][l] * PIu.get(u, i) * PIv.get(v, j);
			}
		}
		return -Math.log(prob);
	}

	@Override
    public double predict(int u, int v) throws Exception {
		double pred = 0;

		for (int l = 0; l < numLevels; l++) {
			double rate = ratingScale.get(l);

			double prob = 0; // P(r|u,v)=\sum_{i,j} P(r|i,j)P(i|u)P(j|v)
			for (int i = 0; i < Ku; i++) {
				for (int j = 0; j < Kv; j++) {
					prob += Pijl[i][j][l] * PIu.get(u, i) * PIv.get(v, j);
				}
			}

			pred += rate * prob;
		}

		return pred;
	}
}
