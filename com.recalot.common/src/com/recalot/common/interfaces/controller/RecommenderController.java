// Copyright (C) 2015 Matthäus Schmedding
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

package com.recalot.common.interfaces.controller;

/**
 * @author Matthaeus.schmedding
 */
public interface RecommenderController extends Controller {
    public enum RecommenderRequestAction implements RequestAction {
        Recommend (0),
        CreateRecommender (1),
        GetRecommender (2),
        GetRecommenders (3),
        UpdateRecommender (4),
        DeleteRecommender(5),
        GetRecommenderBuilder(6);

        private final int value;

        @Override
        public int getValue() {
            return this.value;
        }

        RecommenderRequestAction(int value) {
            this.value = value;
        }
    }
}
