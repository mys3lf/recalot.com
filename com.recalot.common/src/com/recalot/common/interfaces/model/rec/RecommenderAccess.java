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

package com.recalot.common.interfaces.model.rec;

import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.communication.Service;
import com.recalot.common.interfaces.model.data.DataSource;

import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface RecommenderAccess extends Service {
    public RecommenderInformation[] getRecommenders() throws BaseException;
    public Recommender getRecommender(String id) throws BaseException;
    public RecommenderBuilder getRecommenderBuilder(String id) throws BaseException;
    public Recommender createRecommender(DataSource dataSource, Map<String, String> param) throws BaseException;
    public Recommender updateRecommender(String id, DataSource dataSource, Map<String, String> param) throws BaseException;
    public Message deleteRecommender(String id);
}
