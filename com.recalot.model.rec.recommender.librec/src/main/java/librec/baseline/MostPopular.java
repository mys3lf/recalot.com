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

import java.util.HashMap;
import java.util.Map;

import librec.data.SparseMatrix;
import librec.intf.Recommender;

/**
 * Baseline: items are weighted by the number of ratings they received.
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */
public class MostPopular extends Recommender {

	private Map<Integer, Integer> itemPops;

	public MostPopular() {
		super();

		// force to set as the ranking prediction method
		isRankingPred = true;
	}

	@Override
    public void initModel() {
        itemPops = new HashMap<>();

        for(int j : trainMatrix.columns()) {
            itemPops.put(j, trainMatrix.columnSize(j));
        }

	}

	@Override
    public double ranking(int u, int j) {
        try {
            return itemPops.containsKey(j) ? itemPops.get(j) : 0;
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return 0;
	}

}
