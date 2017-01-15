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

package librec.rating;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import librec.data.DenseMatrix;
import librec.data.MatrixEntry;
import librec.data.SparseVector;
import librec.intf.SocialRecommender;

/**
 * Hao Ma, Dengyong Zhou, Chao Liu, Michael R. Lyu and Irwin King, <strong>Recommender systems with social
 * regularization</strong>, WSDM 2011.<br>
 * 
 * <p>
 * In the original paper, this method is named as "SR2_pcc". For consistency, we rename it as "SoReg" as used by some
 * other papers such as: Tang et al., <strong>Exploiting Local and Global Social Context for Recommendation</strong>,
 * IJCAI 2013.
 * </p>
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */


@Configuration(key = "beta", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "similarityMeasure", type = ConfigurationItem.ConfigurationItemType.Options, options = {"cos", "cos-binary", "msd", "cpc", "exjaccard", "pcc"}, value = "pcc", description = "similarity measure")
@Configuration(key = "similarityShrinkage", type = ConfigurationItem.ConfigurationItemType.Integer, description = "similarity shrinkage")
public class SoReg extends SocialRecommender {

	private Table<Integer, Integer, Double> userCorrs;
	public double beta;
    // similarity measure
    public String similarityMeasure;
    // similarity shrinkage
    public int similarityShrinkage;

	public SoReg() {
		super();

		initByNorm = false;
	}

	@Override
    public void initModel() throws Exception {
		super.initModel();

		userCorrs = HashBasedTable.create();
	}

	/**
	 * compute similarity between users u and v
	 */
	protected double similarity(Integer u, Integer v) {
		if (userCorrs.contains(u, v))
			return userCorrs.get(u, v);

		if (userCorrs.contains(v, u))
			return userCorrs.get(v, u);

		double sim = Double.NaN;

		if (u < trainMatrix.numRows() && v < trainMatrix.numRows()) {
			SparseVector uv = trainMatrix.row(u);
			if (uv.getCount() > 0) {
				SparseVector vv = trainMatrix.row(v);
				sim = correlation(uv, vv, similarityMeasure, similarityShrinkage); // could change to other measures

				if (!Double.isNaN(sim))
					sim = (1.0 + sim) / 2;
			}
		}

		userCorrs.put(u, v, sim);

		return sim;
	}

	@Override
    public void buildModel() throws Exception {
		for (int iter = 1; iter <= numIters; iter++) {

			loss = 0;

			// temp data
			DenseMatrix PS = new DenseMatrix(numUsers, numFactors);
			DenseMatrix QS = new DenseMatrix(numItems, numFactors);

			// ratings
			for (MatrixEntry me : trainMatrix) {
				int u = me.row();
				int j = me.column();
				double ruj = me.get();

				double pred = predict(u, j);
				double euj = pred - ruj;

				loss += euj * euj;

				for (int f = 0; f < numFactors; f++) {
					double puf = P.get(u, f);
					double qjf = Q.get(j, f);

					PS.add(u, f, euj * qjf + regU * puf);
					QS.add(j, f, euj * puf + regI * qjf);

					loss += regU * puf * puf + regI * qjf * qjf;
				}
			}

			// friends
			for (int u = 0; u < numUsers; u++) {
				// out links: F+
				SparseVector uos = socialMatrix.row(u);

				for (int k : uos.getIndex()) {
					double suk = similarity(u, k);
					if (!Double.isNaN(suk)) {
						for (int f = 0; f < numFactors; f++) {
							double euk = P.get(u, f) - P.get(k, f);
							PS.add(u, f, beta * suk * euk);

							loss += beta * suk * euk * euk;
						}
					}
				}

				// in links: F-
				SparseVector uis = socialMatrix.column(u);
				for (int g : uis.getIndex()) {
					double sug = similarity(u, g);
					if (!Double.isNaN(sug)) {
						for (int f = 0; f < numFactors; f++) {
							double eug = P.get(u, f) - P.get(g, f);
							PS.add(u, f, beta * sug * eug);
						}
					}
				}

			} // end of for loop

			P = P.add(PS.scale(-lRate));
			Q = Q.add(QS.scale(-lRate));

			loss *= 0.5;

			if (isConverged(iter))
				break;
		}
	}
}
