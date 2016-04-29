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
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LibRec. If not, see <http://www.gnu.org/licenses/>.
//

package librec.baseline;

import librec.data.SparseVector;
import librec.intf.Recommender;

import java.util.HashMap;
import java.util.Map;

/**
 * Baseline: predict by the average of target item's ratings
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */
public class ItemAverage extends Recommender {

	private Map<Integer, Double> itemMeans;

	public ItemAverage() {
		super();

		itemMeans = new HashMap<>();
	}

	@Override
    public double predict(int u, int j) {
		if (!itemMeans.containsKey(j)) {
			SparseVector jv = trainMatrix.column(j);
			double mean = jv.getCount() > 0 ? jv.mean() : globalMean;
			itemMeans.put(j, mean);
		}

		return itemMeans.get(j);
	}
}
