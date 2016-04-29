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

import com.google.common.cache.LoadingCache;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.model.rec.librec.DataDAO;
import librec.data.SparseMatrix;

import java.util.List;

/**
 * Recommenders in which social information is used
 * 
 * @author Guo Guibing
 * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 * 
 */
//@Configuration("factors, lRate, maxLRate, regB, regU, regI, regS, iters, boldDriver")
@Configuration(key = "regS", value="0.1", requirement = ConfigurationItem.ConfigurationItemRequirementType.Required, type = ConfigurationItem.ConfigurationItemType.Double)
public abstract class SocialRecommender extends IterativeRecommender {

	// socialMatrix: social rate matrix, indicating a user is connecting to a number of other users
	// trSocialMatrix: inverse social matrix, indicating a user is connected by a number of other users
	protected static SparseMatrix socialMatrix;

	// social regularization
	public double regS;

	// shared social cache for all social recommenders
	protected LoadingCache<Integer, List<Integer>> userFriendsCache;

    @Override
    public void setDao(DataDAO dataDAO) {
        super.setDao(dataDAO);

        socialMatrix = dataDAO.getRelationshipMatrix();
        this.numUsers = dataDAO.numUsers();
    }

	public SocialRecommender() {
		super();
	}
}
