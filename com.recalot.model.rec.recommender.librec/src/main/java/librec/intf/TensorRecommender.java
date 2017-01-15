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

package librec.intf;

import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.model.rec.librec.DataDAO;
import librec.data.SparseTensor;

/**
 * Interface for tensor recommenders
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 */

//@Configuration("factors, lRate, maxLRate, reg, iters, boldDriver")

@Configuration(key = "reg", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "boldDriver", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Boolean)
@Configuration(key = "factors", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer)
@Configuration(key = "iters", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Integer)
@Configuration(key = "lRate", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
@Configuration(key = "maxLRate", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
public class TensorRecommender extends IterativeRecommender {

    /* for all tensors */
    public int numDimensions, userDimension, itemDimension;
	protected int[] dimensions;

	/* for a specific recommender */
	protected SparseTensor trainTensor;

	public TensorRecommender() throws Exception {
		super();
	}

    @Override
    public void setDao(DataDAO dataDAO) {
        super.setDao(dataDAO);

        try {
            this.dataDAO.readTensor();
            this.trainTensor = this.dataDAO.getRateTensor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        numDimensions = trainTensor.numDimensions();
        dimensions = trainTensor.dimensions();

        userDimension = trainTensor.getUserDimension();
        itemDimension = trainTensor.getItemDimension();
    }

	protected double predict(int[] keys, boolean bound) throws Exception {
		double pred = predict(keys);

		if (bound) {
			if (pred > maxRate)
				pred = maxRate;
			if (pred < minRate)
				pred = minRate;
		}

		return pred;
	}

	protected double predict(int[] keys) throws Exception {
		return predict(keys[userDimension], keys[itemDimension]);
	}
}
