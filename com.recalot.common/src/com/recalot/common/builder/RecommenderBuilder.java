// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.common.builder;

import com.recalot.common.Helper;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.common.interfaces.model.rec.RecommenderInformation;

/**
 * Responsible for the initialization of Recommenders. Specification of the InstanceBuilder
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class RecommenderBuilder extends InstanceBuilder<Recommender> implements RecommenderInformation {

    /**
     * Constructor
     * @param initiator initiator instance
     * @param className class name of object that should be initialized
     * @param key key of the object
     * @param description description of the object
     * @throws BaseException
     */
    public RecommenderBuilder(Initiator initiator, String className, String key, String description) throws BaseException {
        super(initiator, className, key, description);
    }

    /**
     *
     * @return the current state (always "AVAILABLE")
     */
    @Override
    public RecommenderState getState() {
        return RecommenderState.AVAILABLE;
    }

    /**
     *
     * @return the id of the recommender builder
     */
    @Override
    public String getId() {
        return Helper.Keys.RecommenderBuilderIdPrefix + getKey();
    }
}
