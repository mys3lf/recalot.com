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

package com.recalot.common.interfaces.model.experiment;

import com.recalot.common.communication.RecommendationResult;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class OnlineExperimentRecommendation {
    private String experimentId;
    private String userId;
    private String itemId;
    private RecommendationResult result;

    public OnlineExperimentRecommendation(String experimentId, String userId, String itemId, RecommendationResult result) {
        this.experimentId = experimentId;
        this.userId = userId;
        this.itemId = itemId;
        this.result = result;
    }

    public String getId() {
        return experimentId;
    }

    public void setId(String experimentId) {
        this.experimentId = experimentId;
    }

    public RecommendationResult getResult() {
        return result;
    }

    public void setResult(RecommendationResult result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
