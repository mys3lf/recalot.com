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

package librec.ext;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import librec.data.SparseVector;
import librec.intf.Recommender;
import librec.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zhou et al., <strong>Solving the apparent diversity-accuracy dilemma of recommender systems</strong>, Proceedings of
 * the National Academy of Sciences, 2010.
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */

@Configuration(key = "lambda", type = ConfigurationItem.ConfigurationItemType.Double)
public class Hybrid extends Recommender {

	Table<Integer, Integer, Double> userItemRanks = HashBasedTable.create();

	public double lambda;

	Map<Integer, Integer> itemDegrees = new HashMap<>();

    public Hybrid() {
		super();

		isRankingPred = true;
	}

	@Override
    public void initModel() throws Exception {
		for (int j = 0; j < numItems; j++)
			itemDegrees.put(j, trainMatrix.columnSize(j));
	}

    public double ranking(int u, int j) {

		// Note that in ranking, we first check a user u, and then check the
		// ranking score of each candidate items
		if (!userItemRanks.containsRow(u)) {
			// new user
			userItemRanks.clear();

			SparseVector uv = trainMatrix.row(u);
			List<Integer> items = Lists.toList(uv.getIndex());

			// distribute resources to users, including user u
			Map<Integer, Double> userResources = new HashMap<>();
			for (int v = 0; v < numUsers; v++) {
				SparseVector vv = trainMatrix.row(v);
				double sum = 0;
				int kj = vv.getCount();
				for (int item : vv.getIndex()) {
					if (items.contains(item))
						sum += 1.0 / Math.pow(itemDegrees.get(item), lambda);
				}

				if (kj > 0)
					userResources.put(v, sum / kj);
			}

			// redistribute resources to items
			for (int i = 0; i < numItems; i++) {
				if (items.contains(i))
					continue;

				SparseVector iv = trainMatrix.column(i);
				double sum = 0;
				for (int user : iv.getIndex())
					sum += userResources.containsKey(user) ? userResources.get(user) : 0.0;

				double score = sum / Math.pow(itemDegrees.get(i), 1 - lambda);
				userItemRanks.put(u, i, score);
			}
		}

		return userItemRanks.contains(u, j) ? userItemRanks.get(u, j) : 0.0;
	}
}
