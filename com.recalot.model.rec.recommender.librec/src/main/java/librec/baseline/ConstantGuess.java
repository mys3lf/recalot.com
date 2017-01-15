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

import com.recalot.model.rec.librec.DataDAO;
import librec.data.SparseMatrix;
import librec.intf.Recommender;

/**
 * Baseline: predict by a constant rating
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */
public class ConstantGuess extends Recommender {

	private double constant;

	public ConstantGuess() {
		super();

	}

    @Override
    public void setDao(DataDAO dataDAO) {
        super.setDao(dataDAO);

        constant = (minRate + maxRate) / 2.0; // can also use given constant
    }


	@Override
	public double predict(int u, int j) {
		return constant;
	}

}
